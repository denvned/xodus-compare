package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.xodus.compare.ComparisonStoreNames
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider
import graphql.relay.Relay
import kotlinx.graphql.annotations.GraphQLID
import javax.inject.Singleton

@Singleton
class Viewer : Node {

  @GraphQLID
  override val id = Relay().toGlobalId(Viewer::class.java.simpleName, "0")!!

  val comparisons: List<Comparison>
    get() {
      val txn = ComparisonStoreProvider.store.currentTransaction!!
      val comparisons = txn.getAll(ComparisonStoreNames.COMPARISON)
      return comparisons.map(::Comparison).sortedWith(compareByDescending<Comparison> { it.date }.thenBy { it.localId })
    }

  fun getComparison(localId: Long): Comparison? {
    val txn = ComparisonStoreProvider.store.currentTransaction!!
    return txn.findIds(ComparisonStoreNames.COMPARISON, localId, localId).first?.let(::Comparison)
  }

  fun getEntityType(localId: Long): EntityType? {
    val txn = ComparisonStoreProvider.store.currentTransaction!!
    return txn.findIds(ComparisonStoreNames.ENTITY_TYPE, localId, localId).first?.let(::EntityType)
  }

  fun getAddedEntity(localId: Long): AddedEntity? {
    val txn = ComparisonStoreProvider.store.currentTransaction!!
    return txn.findIds(ComparisonStoreNames.ADDED_ENTITY, localId, localId).first?.let(::AddedEntity)
  }

  fun getChangedEntity(localId: Long): ChangedEntity? {
    val txn = ComparisonStoreProvider.store.currentTransaction!!
    return txn.findIds(ComparisonStoreNames.CHANGED_ENTITY, localId, localId).first?.let(::ChangedEntity)
  }

  fun getDeletedEntity(localId: Long): DeletedEntity? {
    val txn = ComparisonStoreProvider.store.currentTransaction!!
    return txn.findIds(ComparisonStoreNames.DELETED_ENTITY, localId, localId).first?.let(::DeletedEntity)
  }

  fun getLinkTargetType(localId: Long): LinkTargetType? {
    val txn = ComparisonStoreProvider.store.currentTransaction!!
    return txn.findIds(ComparisonStoreNames.LINK_TARGET_TYPE, localId, localId).first?.let(::LinkTargetType)
  }
}
