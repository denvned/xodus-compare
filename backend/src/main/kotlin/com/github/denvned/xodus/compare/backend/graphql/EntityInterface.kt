package com.github.denvned.xodus.compare.backend.graphql

import kotlinx.graphql.annotations.GraphQLInterface
import kotlinx.graphql.annotations.GraphQLName

@GraphQLInterface
@GraphQLName("Entity")
interface EntityInterface : Node {
  val type: EntityType
  val localId: Long
  val entityId: Long
  val properties: List<Property>
  val blobs: List<Blob>
  val links: List<Link>
  val propertyCount: Long
  val blobCount: Long
  val linkCount: Long
}
