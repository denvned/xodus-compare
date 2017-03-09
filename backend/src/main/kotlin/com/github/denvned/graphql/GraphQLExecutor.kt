package com.github.denvned.graphql

interface GraphQLExecutor {

  fun execute(request: GraphQLRequest): GraphQLResponse

  fun execute(query: String) = execute(GraphQLRequest(query))
}
