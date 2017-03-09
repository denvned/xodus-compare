package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.xodus.compare.ComparisonStoreNames
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityIterable

class Comparison(entity: Entity) : AbstractEntityBasedNode(entity) {

  val localId get() = entity.id.localId

  val oldStoreDir get() = entity.getProperty(ComparisonStoreNames.Comparison.OLD_STORE_DIR) as String

  val oldStoreName get() = entity.getProperty(ComparisonStoreNames.Comparison.OLD_STORE_NAME) as String

  val newStoreDir get() = entity.getProperty(ComparisonStoreNames.Comparison.NEW_STORE_DIR) as String

  val newStoreName get() = entity.getProperty(ComparisonStoreNames.Comparison.NEW_STORE_NAME) as String

  val date get() = entity.getProperty(ComparisonStoreNames.Comparison.DATE) as Long

  val oldEntityCount get() = entity.getProperty(ComparisonStoreNames.Comparison.OLD_ENTITY_COUNT) as Long

  val newEntityCount get() = entity.getProperty(ComparisonStoreNames.Comparison.NEW_ENTITY_COUNT) as Long

  val oldEntitiesProcessed get() = entity.getProperty(ComparisonStoreNames.Comparison.OLD_ENTITIES_PROCESSED) as Long

  val newEntitiesProcessed get() = entity.getProperty(ComparisonStoreNames.Comparison.NEW_ENTITIES_PROCESSED) as Long

  val entityTypes get() = entityTypeIterable.map(::EntityType).filter {
    it.getAddedEntities(null, null).totalCount > 0
      || it.getChangedEntities(null, null).totalCount > 0
      || it.getDeletedEntities(null, null).totalCount > 0
  }.sortedBy { it.newName ?: it.oldName }

  val addedEntityCount get() = entityTypeIterable.map { EntityType(it).getAddedEntities(null, null).totalCount }.sum()

  val changedEntityCount get() =
      entityTypeIterable.map { EntityType(it).getChangedEntities(null, null).totalCount }.sum()

  val deletedEntityCount get() =
      entityTypeIterable.map { EntityType(it).getDeletedEntities(null, null).totalCount }.sum()

  private val entityTypeIterable: EntityIterable
    get() {
      val txn = ComparisonStoreProvider.store.currentTransaction!!
      return txn.findLinks(ComparisonStoreNames.ENTITY_TYPE, entity, ComparisonStoreNames.EntityType.COMPARISON)
    }
}
