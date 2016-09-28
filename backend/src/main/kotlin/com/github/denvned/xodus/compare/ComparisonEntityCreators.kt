package com.github.denvned.xodus.compare

import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.StoreTransaction
import java.io.InputStream
import java.util.*

internal object ComparisonEntityCreators {
    fun newComparison(
        txn: StoreTransaction,
        oldStore: PersistentEntityStore,
        newStore: PersistentEntityStore,
        date: Date,
        oldEntityCount: Long,
        newEntityCount: Long
    ): Entity {
        return txn.newEntity(ComparisonStoreNames.COMPARISON).apply {
            setProperty(ComparisonStoreNames.Comparison.OLD_STORE_DIR, oldStore.location)
            setProperty(ComparisonStoreNames.Comparison.OLD_STORE_NAME, oldStore.name)
            setProperty(ComparisonStoreNames.Comparison.NEW_STORE_DIR, newStore.location)
            setProperty(ComparisonStoreNames.Comparison.NEW_STORE_NAME, newStore.name)
            setProperty(ComparisonStoreNames.Comparison.DATE, date.time)
            setProperty(ComparisonStoreNames.Comparison.OLD_ENTITY_COUNT, oldEntityCount)
            setProperty(ComparisonStoreNames.Comparison.NEW_ENTITY_COUNT, newEntityCount)
            setProperty(ComparisonStoreNames.Comparison.OLD_ENTITIES_PROCESSED, 0L)
            setProperty(ComparisonStoreNames.Comparison.NEW_ENTITIES_PROCESSED, 0L)
        }
    }

    fun newEntityType(
        txn: StoreTransaction,
        comparison: Entity,
        id: Int,
        oldName: String?,
        newName: String?
    ): Entity {
        return txn.newEntity(ComparisonStoreNames.ENTITY_TYPE).apply {
            setLink(ComparisonStoreNames.EntityType.COMPARISON, comparison)
            setProperty(ComparisonStoreNames.EntityType.ID, id)
            oldName?.let { setProperty(ComparisonStoreNames.EntityType.OLD_NAME, it) }
            newName?.let { setProperty(ComparisonStoreNames.EntityType.NEW_NAME, it) }
        }
    }

    fun newAddedEntity(txn: StoreTransaction, entityType: Entity, entityId: Long): Entity {
        return txn.newEntity(ComparisonStoreNames.ADDED_ENTITY).apply {
            setLink(ComparisonStoreNames.Entity.TYPE, entityType)
            setProperty(ComparisonStoreNames.Entity.ID, entityId)
        }
    }

    fun newChangedEntity(txn: StoreTransaction, entityType: Entity, entityId: Long): Entity {
        return txn.newEntity(ComparisonStoreNames.CHANGED_ENTITY).apply {
            setLink(ComparisonStoreNames.Entity.TYPE, entityType)
            setProperty(ComparisonStoreNames.Entity.ID, entityId)
        }
    }

    fun newDeletedEntity(txn: StoreTransaction, entityType: Entity, entityId: Long): Entity {
        return txn.newEntity(ComparisonStoreNames.DELETED_ENTITY).apply {
            setLink(ComparisonStoreNames.Entity.TYPE, entityType)
            setProperty(ComparisonStoreNames.Entity.ID, entityId)
        }
    }

    fun newProperty(
        txn: StoreTransaction,
        entity: Entity,
        name: String,
        oldValue: Comparable<*>?,
        newValue: Comparable<*>?
    ): Entity {
        return txn.newEntity(ComparisonStoreNames.PROPERTY).apply {
            setLink(ComparisonStoreNames.Property.ENTITY, entity)
            setProperty(ComparisonStoreNames.Property.NAME, name)
            oldValue?.let { setProperty(ComparisonStoreNames.Property.OLD_VALUE, oldValue) }
            newValue?.let { setProperty(ComparisonStoreNames.Property.NEW_VALUE, newValue) }
        }
    }

    fun newBlob(
        txn: StoreTransaction,
        entity: Entity,
        name: String,
        oldValue: InputStream?,
        newValue: InputStream?
    ): Entity {
        return txn.newEntity(ComparisonStoreNames.BLOB).apply {
            setLink(ComparisonStoreNames.Blob.ENTITY, entity)
            setProperty(ComparisonStoreNames.Blob.NAME, name)
            oldValue?.let { setBlob(ComparisonStoreNames.Blob.OLD_VALUE, it) }
            newValue?.let { setBlob(ComparisonStoreNames.Blob.NEW_VALUE, it) }
        }
    }

    fun newLink(txn: StoreTransaction, entity: Entity, name: String): Entity {
        return txn.newEntity(ComparisonStoreNames.LINK).apply {
            setLink(ComparisonStoreNames.Link.ENTITY, entity)
            setProperty(ComparisonStoreNames.Link.NAME, name)
        }
    }

    fun newLinkTargetType(txn: StoreTransaction, link: Entity, entityType: Entity): Entity {
        return txn.newEntity(ComparisonStoreNames.LINK_TARGET_TYPE).apply {
            setLink(ComparisonStoreNames.LinkTargetType.LINK, link)
            setLink(ComparisonStoreNames.LinkTargetType.ENTITY_TYPE, entityType)
        }
    }

    fun newAddedLinkTarget(
        txn: StoreTransaction,
        linkTargetType: Entity,
        entityId: Long
    ): Entity {
        return txn.newEntity(ComparisonStoreNames.ADDED_LINK_TARGET).apply {
            setLink(ComparisonStoreNames.LinkTarget.TYPE, linkTargetType)
            setProperty(ComparisonStoreNames.LinkTarget.ENTITY_ID, entityId)
        }
    }

    fun newDeletedLinkTarget(
        txn: StoreTransaction,
        linkTargetType: Entity,
        entityId: Long
    ): Entity {
        return txn.newEntity(ComparisonStoreNames.DELETED_LINK_TARGET).apply {
            setLink(ComparisonStoreNames.LinkTarget.TYPE, linkTargetType)
            setProperty(ComparisonStoreNames.LinkTarget.ENTITY_ID, entityId)
        }
    }
}
