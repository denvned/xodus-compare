package com.github.denvned.graphql

import com.github.denvned.graphql.annotations.MutationProvider
import com.github.denvned.graphql.annotations.QueryProvider
import graphql.ExceptionWhileDataFetching
import graphql.GraphQL
import graphql.schema.*
import graphql.InvalidSyntaxError
import graphql.validation.ValidationError
import java.util.logging.Level
import java.util.logging.Logger
import javax.enterprise.inject.Instance
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultGraphQLExecutor @Inject constructor(
    private val typeBuilder: GraphQLTypeBuilder,
    @QueryProvider private val queryProviders: Instance<Any>,
    @MutationProvider private val mutationProviders: Instance<Any>,
    private val executionWrapper: GraphQLExecutionWrapper) : GraphQLExecutor {

  private val LOGGER = Logger.getLogger(GraphQLServlet::class.qualifiedName)
  private val graphql: GraphQL

  init {
    fun buildType(name: String, providers: Iterable<Any>) = GraphQLObjectType.newObject().apply {
      name(name)
      providers.forEach { fields(typeBuilder.buildFields(it::class, DataFetcher { _ -> it })) }
    }.build()

    val queryType = buildType("Query", queryProviders)
    val mutationType = buildType("Mutation", mutationProviders)

    graphql = GraphQL(GraphQLSchema.newSchema().query(queryType).apply {
      mutationType.takeUnless { it.fieldDefinitions.isEmpty() }?.let { mutation(it) }
    }.build(typeBuilder.allObjectTypes))
  }

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
