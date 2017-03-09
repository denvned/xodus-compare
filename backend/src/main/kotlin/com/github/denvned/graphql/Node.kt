package com.github.denvned.graphql

import com.github.denvned.graphql.annotations.GraphQLID
import com.github.denvned.graphql.annotations.GraphQLInterface

@GraphQLInterface
interface Node {
  @GraphQLID val id: String
}
