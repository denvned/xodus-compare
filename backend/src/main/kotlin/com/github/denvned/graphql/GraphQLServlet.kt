package com.github.denvned.graphql

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.denvned.graphql.annotations.MutationProvider
import com.github.denvned.graphql.annotations.QueryProvider
import graphql.ExceptionWhileDataFetching
import graphql.GraphQL
import graphql.InvalidSyntaxError
import graphql.schema.DataFetcher
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema.newSchema
import graphql.validation.ValidationError
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.enterprise.inject.Instance
import javax.inject.Inject
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/")
class GraphQLServlet : HttpServlet() {
    private val LOGGER = Logger.getLogger(GraphQLServlet::class.java.name)

    @Inject
    private lateinit var typeBuilder: GraphQLTypeBuilder

    @Inject @QueryProvider
    private lateinit var queryProviders: Instance<Any>

    @Inject @MutationProvider
    private lateinit var mutationProviders: Instance<Any>

    @Inject
    private lateinit var executionWrapper: GraphQLExecutionWrapper

    private lateinit var graphql: GraphQL

    @PostConstruct
    private fun buildSchema() {
        fun buildType(name: String, providers: Iterable<Any>) =
            GraphQLObjectType.newObject().apply {
                name(name)

                for (provider in providers) {
                    fields(typeBuilder.buildFields(provider.javaClass, DataFetcher { provider }))
                }
            }.build()

        val queryType = buildType("Query", queryProviders)
        val mutationType = buildType("Mutation", mutationProviders)

        graphql = GraphQL(newSchema().query(queryType).apply {
            if (!mutationType.fieldDefinitions.isEmpty()) {
                mutation(mutationType)
            }
        }.build(typeBuilder.allObjectTypes))
    }

    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        query(req.getParameter("q"), HashMap<String, Any>(), res)
    }

    private class Request {
        lateinit var query: String
        lateinit var variables: Map<String, Any>
    }

    override fun doPost(req: HttpServletRequest, res: HttpServletResponse) {
        val request = ObjectMapper().readValue(req.reader, Request::class.java)

        query(request.query, request.variables, res)
    }

    private fun query(query: String, variables: Map<String, Any>, res: HttpServletResponse) {
        val result = executionWrapper.wrapExecution { graphql.execute(query, object {}, variables) }

        res.contentType = "application/json"

        val payload = HashMap<String, Any>()

        if (result.errors.isEmpty()) {
            payload["data"] = result.data
        } else {
            res.status = 500

            payload["errors"] = result.errors.filter {
                it is InvalidSyntaxError || it is ValidationError
            }

            for (error in result.errors) {
                if (error is ExceptionWhileDataFetching) {
                    LOGGER.log(Level.WARNING, "Exception while data fetching.", error.exception)
                }
            }
        }

        res.writer.write(ObjectMapper().writeValueAsString(payload))
    }
}
