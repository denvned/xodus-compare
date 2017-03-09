package com.github.denvned.xodus.compare.backend.graphql

import jetbrains.exodus.entitystore.Entity

class DeletedEntityEdge(entity: Entity) : AbstractEntityBasedEdge(entity) {

  val node get() = DeletedEntity(entity)
}
