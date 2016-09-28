package com.github.denvned.xodus.compare

import jetbrains.exodus.entitystore.*
import jetbrains.exodus.env.EnvironmentConfig
import jetbrains.exodus.env.Environments
import java.util.*
import kotlin.comparisons.compareBy

object EntityStoreComparator {
    const val PROCESSED_ENTITIES_UPDATE_BATCH_COUNT = 256

    fun compareStores(
        oldStoreLocation: EntityStoreLocation,
        newStoreLocation: EntityStoreLocation,
        store: PersistentEntityStore
    ): Entity {
        fun getStore(storeLocation: EntityStoreLocation) = PersistentEntityStores.newInstance(
            PersistentEntityStoreConfig().setRefactoringSkipAll(true),
            Environments.newInstance(storeLocation.dir, EnvironmentConfig().setEnvIsReadonly(true).setGcEnabled(false)),
            storeLocation.storeName
        )

        val oldStore = getStore(oldStoreLocation)
        val newStore = getStore(newStoreLocation)

        try {
            return compareStores(
                oldStore = oldStore,
                newStore = newStore,
                store = store
            )
        } finally {
            oldStore.close()
            newStore.close()
        }
    }

    fun compareStores(
        oldStore: PersistentEntityStore,
        newStore: PersistentEntityStore,
        store: PersistentEntityStore
    ): Entity {
        val date = Date()
        val oldTxn = oldStore.beginReadonlyTransaction()
        val newTxn = newStore.beginReadonlyTransaction()

        try {
            fun countEntities(txn: StoreTransaction): Long {
                var count = 0L
                txn.entityTypes.forEach { count += txn.getAll(it).size() }
                return count
            }

            val comparison = store.computeInTransaction {
                ComparisonEntityCreators.newComparison(
                    txn = it,
                    oldStore = oldStore,
                    newStore = newStore,
                    date = date,
                    oldEntityCount = countEntities(oldTxn),
                    newEntityCount = countEntities(newTxn)
                )
            }

            var oldEntitiesProcessed = 0L
            var newEntitiesProcessed = 0L

            fun updateProcessed() {
                store.executeInTransaction {
                    comparison.setProperty(ComparisonStoreNames.Comparison.OLD_ENTITIES_PROCESSED, oldEntitiesProcessed)
                    comparison.setProperty(ComparisonStoreNames.Comparison.NEW_ENTITIES_PROCESSED, newEntitiesProcessed)
                }
            }

            val oldTypeNames = oldTxn.entityTypes.associate { oldStore.getEntityTypeId(it) to it }
            val newTypeNames = newTxn.entityTypes.associate { newStore.getEntityTypeId(it) to it }

            val entityTypes = store.computeInTransaction {
                (oldTypeNames.keys + newTypeNames.keys).associate { typeId ->
                    val oldName = oldTypeNames[typeId]
                    val newName = newTypeNames[typeId]

                    typeId to ComparisonEntityCreators.newEntityType(
                        txn = it,
                        comparison = comparison,
                        id = typeId,
                        oldName = oldName,
                        newName = newName
                    )
                }
            }

            for ((typeId, entityType) in entityTypes) {
                val oldEntities = oldTypeNames[typeId]?.let { oldTxn.getAll(it) }
                val newEntities = newTypeNames[typeId]?.let { newTxn.getAll(it) }

                for ((oldEntity, newEntity) in MatchingIterator(oldEntities, newEntities, compareBy { it.id })) {
                    var entity: Entity? = null

                    EntityComparator(
                        oldEntity = oldEntity,
                        newEntity = newEntity,
                        store = store,
                        getEntity = {
                            if (entity == null) {
                                entity = store.computeInTransaction {
                                    val entityId = (oldEntity ?: newEntity)!!.id.localId
                                    when {
                                        oldEntity == null -> ComparisonEntityCreators.newAddedEntity(
                                            txn = it,
                                            entityType = entityType,
                                            entityId = entityId
                                        )
                                        newEntity == null -> ComparisonEntityCreators.newDeletedEntity(
                                            txn = it,
                                            entityType = entityType,
                                            entityId = entityId
                                        )
                                        else -> ComparisonEntityCreators.newChangedEntity(
                                            txn = it,
                                            entityType = entityType,
                                            entityId = entityId
                                        )
                                    }
                                }
                            }
                            entity!!
                        },
                        entityTypes = entityTypes
                    ).compareEntities()

                    if (oldEntity != null) {
                        oldEntitiesProcessed++
                    }
                    if (newEntity != null) {
                        newEntitiesProcessed++
                    }

                    if ((oldEntitiesProcessed + newEntitiesProcessed) % PROCESSED_ENTITIES_UPDATE_BATCH_COUNT == 0L) {
                        updateProcessed()
                    }
                }
            }

            updateProcessed()

            return comparison
        } finally {
            oldTxn.abort()
            newTxn.abort()
        }
    }
}
