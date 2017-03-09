package com.github.denvned.xodus.compare.backend.graphql

import jetbrains.exodus.entitystore.EntityIterable
import java.util.*

class LinkTargetConnection(entities: EntityIterable, first: Int?, after: Long?)
    : AbstractEntityBasedConnection(entities, first, after) {

  val edges: List<LinkTargetEdge>
    get() = object : AbstractList<LinkTargetEdge>() {
      override val size get() = page.entities.size
      override fun get(index: Int) = LinkTargetEdge(page.entities[index])
    }
}
