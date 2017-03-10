package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.graphql.annotations.QueryProvider
import graphql.relay.Relay
import kotlinx.graphql.annotations.GraphQLID
import javax.inject.Inject

@QueryProvider
class Queries @Inject constructor(val viewer: Viewer) {

  fun getNode(@GraphQLID id: String): Node? {
    val globalId = Relay().fromGlobalId(id)
    val localId = globalId.getId().toLong()

    return when (globalId.getType()) {
      Viewer::class.simpleName -> viewer
      Comparison::class.simpleName -> viewer.getComparison(localId)
      EntityType::class.simpleName -> viewer.getEntityType(localId)
      AddedEntity::class.simpleName -> viewer.getAddedEntity(localId)
      ChangedEntity::class.simpleName -> viewer.getChangedEntity(localId)
      DeletedEntity::class.simpleName -> viewer.getDeletedEntity(localId)
      LinkTargetType::class.simpleName -> viewer.getLinkTargetType(localId)
      else -> throw IllegalArgumentException("Unknown node type")
    }
  }
}
