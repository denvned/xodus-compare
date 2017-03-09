package com.github.denvned.xodus.compare.backend.graphql

import jetbrains.exodus.entitystore.Entity

abstract class AbstractEntityBasedEdge(protected val entity: Entity) {

  val cursor get() = entity.id.localId.toString()
}
