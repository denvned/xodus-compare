package com.github.denvned.xodus.compare.backend.graphql

import jetbrains.exodus.entitystore.EntityIterable
import java.util.*

class ChangedEntityConnection(entities: EntityIterable, first: Int?, after: Long?)
    : AbstractEntityBasedConnection(entities, first, after) {

  val edges: List<ChangedEntityEdge>
    get() = object : AbstractList<ChangedEntityEdge>() {
      override val size get() = page.entities.size
      override fun get(index: Int) = ChangedEntityEdge(page.entities[index])
    }
}
