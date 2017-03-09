package com.github.denvned.xodus.compare.backend.graphql

import jetbrains.exodus.entitystore.EntityIterable
import java.util.*

class AddedEntityConnection(entities: EntityIterable, first: Int?, after: Long?)
    : AbstractEntityBasedConnection(entities, first, after) {

  val edges: List<AddedEntityEdge>
    get() = object : AbstractList<AddedEntityEdge>() {
      override val size get() = page.entities.size
      override fun get(index: Int) = AddedEntityEdge(page.entities[index])
    }
}
