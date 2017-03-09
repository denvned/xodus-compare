package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.xodus.compare.ComparisonStoreNames
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityIterable

class EntityType(entity: Entity) : AbstractEntityBasedNode(entity) {

  val localId get() = entity.id.localId

  val comparison get() = Comparison(entity.getLink(ComparisonStoreNames.EntityType.COMPARISON)!!)

  val typeId get() = entity.getProperty(ComparisonStoreNames.EntityType.ID) as Int

  val oldName get() = entity.getProperty(ComparisonStoreNames.EntityType.OLD_NAME) as String?

  val newName get() = entity.getProperty(ComparisonStoreNames.EntityType.NEW_NAME) as String?

  fun getAddedEntities(first: Int?, after: String?) =
      AddedEntityConnection(getEntities(ComparisonStoreNames.ADDED_ENTITY), first, after?.toLong())

  fun getChangedEntities(first: Int?, after: String?) =
    ChangedEntityConnection(getEntities(ComparisonStoreNames.CHANGED_ENTITY), first, after?.toLong())

  fun getDeletedEntities(first: Int?, after: String?) =
    DeletedEntityConnection(getEntities(ComparisonStoreNames.DELETED_ENTITY), first, after?.toLong())

  private fun getEntities(changeType: String): EntityIterable {
    val txn = ComparisonStoreProvider.store.currentTransaction!!
    return txn.findLinks(changeType, entity, ComparisonStoreNames.Entity.TYPE)
  }
}
