package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.xodus.compare.ComparisonStoreNames
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider
import jetbrains.exodus.entitystore.Entity

class Link(private val entity: Entity) {

  val name get() = entity.getProperty(ComparisonStoreNames.Link.NAME) as String

  val targetTypes: List<LinkTargetType>
    get() {
      val txn = ComparisonStoreProvider.store.currentTransaction!!
      val targetTypes =
        txn.findLinks(ComparisonStoreNames.LINK_TARGET_TYPE, entity, ComparisonStoreNames.LinkTargetType.LINK)
      return targetTypes.map(::LinkTargetType).sortedBy {
        it.entityType.run { newName ?: oldName }
      }
    }
}
