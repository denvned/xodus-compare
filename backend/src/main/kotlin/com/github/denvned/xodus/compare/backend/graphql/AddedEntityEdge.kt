package com.github.denvned.xodus.compare.backend.graphql

import jetbrains.exodus.entitystore.Entity

class AddedEntityEdge(entity: Entity) : AbstractEntityBasedEdge(entity) {

  val node get() = AddedEntity(entity)
}
