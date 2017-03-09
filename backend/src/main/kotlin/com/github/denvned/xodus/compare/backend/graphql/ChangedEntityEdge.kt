package com.github.denvned.xodus.compare.backend.graphql

import jetbrains.exodus.entitystore.Entity

class ChangedEntityEdge(entity: Entity) : AbstractEntityBasedEdge(entity) {

  val node get() = ChangedEntity(entity)
}
