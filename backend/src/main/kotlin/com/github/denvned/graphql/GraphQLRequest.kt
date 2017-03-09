package com.github.denvned.graphql

import com.fasterxml.jackson.annotation.JsonInclude

class GraphQLRequest(
    val query: String,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) val variables: Map<String, Any> = emptyMap())
