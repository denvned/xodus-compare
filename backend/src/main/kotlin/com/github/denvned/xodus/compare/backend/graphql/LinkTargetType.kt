package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.xodus.compare.ComparisonStoreNames
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityIterable

class LinkTargetType(entity: Entity) : AbstractEntityBasedNode(entity) {

  val link get() = Link(entity.getLink(ComparisonStoreNames.LinkTargetType.LINK)!!)

  val entityType get() = EntityType(entity.getLink(ComparisonStoreNames.LinkTargetType.ENTITY_TYPE)!!)

  fun getAddedTargets(first: Int?, after: String?) =
      LinkTargetConnection(getTargets(ComparisonStoreNames.ADDED_LINK_TARGET), first, after?.toLong())

  fun getDeletedTargets(first: Int?, after: String?) =
      LinkTargetConnection(getTargets(ComparisonStoreNames.DELETED_LINK_TARGET), first, after?.toLong())

  private fun getTargets(changeType: String): EntityIterable {
    val txn = ComparisonStoreProvider.store.currentTransaction!!
    return txn.findLinks(changeType, entity, ComparisonStoreNames.LinkTarget.TYPE)
  }
}
