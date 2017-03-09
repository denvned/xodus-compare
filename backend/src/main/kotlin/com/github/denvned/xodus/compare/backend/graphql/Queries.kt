package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.graphql.Node
import com.github.denvned.graphql.annotations.*
import graphql.relay.Relay

import javax.inject.Inject

@QueryProvider
class Queries @Inject constructor(val viewer: Viewer) {

  fun getNode(@GraphQLID id: String): Node? {
    val globalId = Relay().fromGlobalId(id)
    val localId = globalId.getId().toLong()

    return when (globalId.getType()) {
      Viewer::class.java.simpleName -> viewer
      Comparison::class.java.simpleName -> viewer.getComparison(localId)
      EntityType::class.java.simpleName -> viewer.getEntityType(localId)
      AddedEntity::class.java.simpleName -> viewer.getAddedEntity(localId)
      ChangedEntity::class.java.simpleName -> viewer.getChangedEntity(localId)
      DeletedEntity::class.java.simpleName -> viewer.getDeletedEntity(localId)
      LinkTargetType::class.java.simpleName -> viewer.getLinkTargetType(localId)
      else -> throw IllegalArgumentException("Unknown node type")
    }
  }
}
