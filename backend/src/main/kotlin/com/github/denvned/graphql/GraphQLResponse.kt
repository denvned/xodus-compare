package com.github.denvned.graphql

import com.fasterxml.jackson.annotation.JsonInclude
import graphql.GraphQLError

class GraphQLResponse(
    val data: Any? = null,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) val errors: List<GraphQLError>? = emptyList())
