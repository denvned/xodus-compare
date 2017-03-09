package com.github.denvned.xodus.compare.backend.graphql

import jetbrains.exodus.entitystore.EntityIterable
import java.util.*

class DeletedEntityConnection(entities: EntityIterable, first: Int?, after: Long?)
    : AbstractEntityBasedConnection(entities, first, after) {

  val edges: List<DeletedEntityEdge>
    get() = object : AbstractList<DeletedEntityEdge>() {
      override val size get() = page.entities.size
      override fun get(index: Int) = DeletedEntityEdge(page.entities[index])
    }
}
