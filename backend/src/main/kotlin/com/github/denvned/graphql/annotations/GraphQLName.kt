package com.github.denvned.graphql.annotations

@Target(AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER)
annotation class GraphQLName(val value: String)
