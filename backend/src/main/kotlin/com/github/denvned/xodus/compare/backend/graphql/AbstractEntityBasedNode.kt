package com.github.denvned.xodus.compare.backend.graphql

import graphql.relay.Relay
import jetbrains.exodus.entitystore.Entity
import kotlinx.graphql.annotations.GraphQLID

abstract class AbstractEntityBasedNode(protected val entity: Entity) : Node {

  @GraphQLID
  override val id get() = Relay().toGlobalId(this::class.java.simpleName, java.lang.Long.toString(entity.id.localId))!!
}
