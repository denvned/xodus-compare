package com.github.denvned.graphql

import graphql.ExecutionResult

interface GraphQLExecutionWrapper {
  fun wrapExecution(execute: () -> ExecutionResult): ExecutionResult
}
