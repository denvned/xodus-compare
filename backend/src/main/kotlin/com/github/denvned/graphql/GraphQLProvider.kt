package com.github.denvned.graphql

import com.github.denvned.graphql.annotations.MutationProvider
import com.github.denvned.graphql.annotations.QueryProvider
import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import kotlinx.graphql.GraphQLTypeBuilder
import javax.enterprise.inject.Instance
import javax.enterprise.inject.Produces

class GraphQLProvider {
  companion object {
    private const val QUERY_TYPE_NAME = "Query"
    private const val MUTATION_TYPE_NAME = "Mutation"
  }

  @Produces
  fun getGraphQL(schema: GraphQLSchema) = GraphQL(schema)

  @Produces
  fun getGraphQLSchema(typeBuilder: GraphQLTypeBuilder,
      @QueryProvider queryProviders: Instance<Any>,
      @MutationProvider mutationProviders: Instance<Any>): GraphQLSchema {

    fun buildType(name: String, providers: Iterable<Any>) = GraphQLObjectType.newObject().apply {
      name(name)
      providers.forEach { fields(typeBuilder.buildFields(it::class, DataFetcher { _ -> it })) }
    }.build()

    val queryType = buildType(QUERY_TYPE_NAME, queryProviders)
    val mutationType = buildType(MUTATION_TYPE_NAME, mutationProviders)

    return GraphQLSchema.newSchema().query(queryType).apply {
      mutationType.takeUnless { it.fieldDefinitions.isEmpty() }?.let { mutation(it) }
    }.build(typeBuilder.allObjectTypes)
  }

  @Produces
  fun getGraphQLTypeBuilder() = GraphQLTypeBuilder()
}
