package com.github.denvned.graphql

import com.github.denvned.graphql.annotations.MutationProvider
import com.github.denvned.graphql.annotations.QueryProvider
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import kotlinx.graphql.GraphQLSchemaBuilder
import javax.enterprise.inject.Instance
import javax.enterprise.inject.Produces

class GraphQLProvider {

  @Produces
  fun getGraphQL(schema: GraphQLSchema) = GraphQL(schema)

  @Produces
  fun getGraphQLSchema(schemaBuilder: GraphQLSchemaBuilder,
      @QueryProvider queryProviders: Instance<Any>,
      @MutationProvider mutationProviders: Instance<Any>) = schemaBuilder.run {

    queryProviders.forEach{ addQueries(it) }
    mutationProviders.forEach{ addMutations(it) }
    build()
  }

  @Produces
  fun getGraphQLTypeBuilder() = GraphQLSchemaBuilder()
}
