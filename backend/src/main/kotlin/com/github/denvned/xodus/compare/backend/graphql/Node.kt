package com.github.denvned.xodus.compare.backend.graphql

import kotlinx.graphql.annotations.GraphQLID
import kotlinx.graphql.annotations.GraphQLInterface

@GraphQLInterface
interface Node {
  @GraphQLID val id: String
}
