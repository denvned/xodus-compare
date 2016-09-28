package com.github.denvned.xodus.compare

import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityStoreException
import jetbrains.exodus.entitystore.PersistentEntityStore
import org.apache.commons.io.IOUtils
import java.util.*
import kotlin.comparisons.compareBy

internal class EntityComparator(
    val oldEntity: Entity?,
    val newEntity: Entity?,
    val store: PersistentEntityStore,
    val getEntity: () -> Entity,
    val entityTypes: Map<Int, Entity>
) {
    private val MAX_TRANSACTION_BATCH_SIZE = 1024

    fun compareEntities() {
        compareProps()
        compareBlobs()
        compareLinks()
    }

    private fun compareProps() {
        val propChanges = ArrayList<Triple<String, Comparable<*>?, Comparable<*>?>>()

        val oldPropNames = oldEntity?.propertyNames ?: emptyList()
        val newPropNames = newEntity?.propertyNames ?: emptyList()

        for (propName in (oldPropNames + newPropNames).distinct()) {
            fun getProp(entity: Entity?) = try {
                    entity?.getProperty(propName)
                } catch (e: EntityStoreException) {
                    ArrayByteIterable(entity!!.getRawProperty(propName)!!)
                }

            val oldValue = getProp(oldEntity)
            val newValue = getProp(newEntity)

            if (oldValue != newValue) {
                propChanges += Triple(propName, oldValue, newValue)
            }
        }

        if (propChanges.isNotEmpty()) {
            store.executeInTransaction {
                for ((propName, oldValue, newValue) in propChanges) {
                    ComparisonEntityCreators.newProperty(
                        txn = it,
                        entity = getEntity(),
                        name = propName,
                        oldValue = oldValue,
                        newValue = newValue
                    )
                }
            }
        }
    }

    private fun compareBlobs() {
        val oldBlobNames = oldEntity?.blobNames ?: emptyList()
        val newBlobNames = newEntity?.blobNames ?: emptyList()

        for (blobName in (oldBlobNames + newBlobNames).distinct()) {
            val oldSize = oldEntity?.getBlobSize(blobName) ?: -1
            val newSize = newEntity?.getBlobSize(blobName) ?: -1

            if (oldSize < 0 || newSize < 0 || oldSize != newSize
                || !IOUtils.contentEquals(oldEntity!!.getBlob(blobName), newEntity!!.getBlob(blobName))
            ) {
                store.executeInTransaction {
                    ComparisonEntityCreators.newBlob(
                        txn = it,
                        entity = getEntity(),
                        name = blobName,
                        oldValue = oldEntity?.getBlob(blobName),
                        newValue = newEntity?.getBlob(blobName)
                    )
                }
            }
        }
    }

    private fun compareLinks() {
        val oldLinkNames = oldEntity?.linkNames ?: emptyList()
        val newLinkNames = newEntity?.linkNames ?: emptyList()

        for (linkName in (oldLinkNames + newLinkNames).distinct()) {
            var link: Entity? = null
            val addedLinkTargetBatch = ArrayList<Long>()
            val deletedLinkTargetBatch = ArrayList<Long>()
            var typeId: Int? = null
            var linkTargetType: Entity? = null

            fun flushLinkTargetBatch() {
                if (addedLinkTargetBatch.isNotEmpty() || deletedLinkTargetBatch.isNotEmpty()) {
                    store.executeInTransaction {
                        for (linkTarget in addedLinkTargetBatch) {
                            ComparisonEntityCreators.newAddedLinkTarget(
                                txn = it,
                                linkTargetType = linkTargetType!!,
                                entityId = linkTarget
                            )
                        }
                        for (linkTarget in deletedLinkTargetBatch) {
                            ComparisonEntityCreators.newDeletedLinkTarget(
                                txn = it,
                                linkTargetType = linkTargetType!!,
                                entityId = linkTarget
                            )
                        }
                    }

                    addedLinkTargetBatch.clear()
                    deletedLinkTargetBatch.clear()
                }
            }

            val oldLinkTargets = oldEntity?.getLinks(linkName)
            val newLinkTargets = newEntity?.getLinks(linkName)

            for ((oldLinkTarget, newLinkTarget) in MatchingIterator(oldLinkTargets, newLinkTargets, compareBy { it.id })) {
                if (oldLinkTarget == null || newLinkTarget == null) {
                    val linkTarget = (oldLinkTarget ?: newLinkTarget)!!.id

                    if (link == null) {
                        link = store.computeInTransaction {
                            ComparisonEntityCreators.newLink(
                                txn = it,
                                entity = getEntity(),
                                name = linkName
                            )
                        }
                    }

                    if (linkTarget.typeId != typeId) {
                        flushLinkTargetBatch()

                        typeId = linkTarget.typeId

                        linkTargetType = store.computeInTransaction {
                            ComparisonEntityCreators.newLinkTargetType(
                                txn = it,
                                link = link!!,
                                entityType = entityTypes[typeId!!]!!
                            )
                        }
                    }

                    (if (oldLinkTarget == null) addedLinkTargetBatch else deletedLinkTargetBatch) += linkTarget.localId

                    if (addedLinkTargetBatch.size + deletedLinkTargetBatch.size >= MAX_TRANSACTION_BATCH_SIZE) {
                        flushLinkTargetBatch()
                    }
                }
            }

            flushLinkTargetBatch()
        }
    }
}
