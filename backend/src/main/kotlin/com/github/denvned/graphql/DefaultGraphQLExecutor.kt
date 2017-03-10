package com.github.denvned.graphql

import graphql.ExceptionWhileDataFetching
import graphql.GraphQL
import graphql.InvalidSyntaxError
import graphql.validation.ValidationError
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject

class DefaultGraphQLExecutor @Inject constructor(
  private val graphql: GraphQL,
  private val executionWrapper: GraphQLExecutionWrapper) : GraphQLExecutor {

  private val LOGGER = Logger.getLogger(DefaultGraphQLExecutor::class.qualifiedName)

  override fun execute(request: GraphQLRequest): GraphQLResponse =
      executionWrapper.wrapExecution { graphql.execute(request.query, object {}, request.variables) }.run {
        errors.asSequence().filterIsInstance<ExceptionWhileDataFetching>().forEach {
          LOGGER.log(Level.WARNING, "Exception while data fetching.", it.exception)
        }

        GraphQLResponse(
            data = data.takeIf { errors.isEmpty() },
            errors = errors.takeIf { it.isNotEmpty() }?.filter { it is InvalidSyntaxError || it is ValidationError })
      }
}
