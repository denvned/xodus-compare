package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.graphql.Node
import com.github.denvned.graphql.annotations.GraphQLID
import graphql.relay.Relay
import jetbrains.exodus.entitystore.Entity

abstract class AbstractEntityBasedNode(protected val entity: Entity) : Node {

  @GraphQLID
  override val id get() = Relay().toGlobalId(this::class.java.simpleName, java.lang.Long.toString(entity.id.localId))!!
}
