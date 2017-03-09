package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.xodus.compare.ComparisonStoreNames
import jetbrains.exodus.entitystore.Entity

class LinkTargetEdge(entity: Entity) : AbstractEntityBasedEdge(entity) {

  val node get() = entity.getProperty(ComparisonStoreNames.LinkTarget.ENTITY_ID) as Long
}
