package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.graphql.GraphQLExecutionWrapper
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider
import graphql.ExecutionResult

class TransactionalGraphQLExecutionWrapper : GraphQLExecutionWrapper {
  override fun wrapExecution(execute: () -> ExecutionResult): ExecutionResult {
    ComparisonStoreProvider.store.run {
      beginReadonlyTransaction()
      try {
        return execute()
      } finally {
        currentTransaction!!.abort()
      }
    }
  }
}
