package com.github.denvned.graphql

import com.github.denvned.graphql.annotations.*
import com.github.denvned.graphql.annotations.GraphQLNonNull
import graphql.Scalars
import graphql.relay.Relay
import graphql.schema.*
import java.lang.reflect.*
import java.util.*
import javax.inject.Singleton

@Singleton
class GraphQLTypeBuilder {
    val allObjectTypes: Set<GraphQLObjectType>
        get() {
            return objectTypeMap.values.toHashSet()
        }

    private val inputTypeMap = hashMapOf<Class<*>, (AnnotatedType) -> GraphQLInputType>(
        Boolean::class.java to { annotatedType ->
            Scalars.GraphQLBoolean
        },
        java.lang.Boolean::class.java to { annotatedType ->
            Scalars.GraphQLBoolean
        },
        Int::class.java to { annotatedType ->
            Scalars.GraphQLInt
        },
        Integer::class.java to { annotatedType ->
            Scalars.GraphQLInt
        },
        Long::class.java to { annotatedType ->
            Scalars.GraphQLLong
        },
        java.lang.Long::class.java to { annotatedType ->
            Scalars.GraphQLLong
        },
        String::class.java to { annotatedType ->
            if (annotatedType.isAnnotationPresent(GraphQLID::class.java)) Scalars.GraphQLID else Scalars.GraphQLString
        },
        List::class.java to { annotatedType ->
            GraphQLList(getInputType((annotatedType as AnnotatedParameterizedType).annotatedActualTypeArguments[0]))
        }
    )
    private val outputTypeMap = hashMapOf<Class<*>, (AnnotatedType) -> GraphQLOutputType>(
        Boolean::class.java to { annotatedType ->
            Scalars.GraphQLBoolean
        },
        java.lang.Boolean::class.java to { annotatedType ->
            Scalars.GraphQLBoolean
        },
        Int::class.java to { annotatedType ->
            Scalars.GraphQLInt
        },
        Integer::class.java to { annotatedType ->
            Scalars.GraphQLInt
        },
        Long::class.java to { annotatedType ->
            Scalars.GraphQLLong
        },
        java.lang.Long::class.java to { annotatedType ->
            Scalars.GraphQLLong
        },
        String::class.java to { annotatedType ->
            if (annotatedType.isAnnotationPresent(GraphQLID::class.java)) Scalars.GraphQLID else Scalars.GraphQLString
        },
        List::class.java to { annotatedType ->
            GraphQLList(getOutputType((annotatedType as AnnotatedParameterizedType).annotatedActualTypeArguments[0]))
        }
    )
    private val objectTypeMap = HashMap<Class<*>, GraphQLObjectType>()

    private fun getGraphQLName(clazz: Class<*>) =
        clazz.getAnnotation(GraphQLName::class.java)?.value ?: clazz.simpleName

    private fun getClass(annotatedType: AnnotatedType) =
        annotatedType.type.let { if (it is ParameterizedType) it.rawType else it } as Class<*>

    private fun getInputType(annotatedType: AnnotatedType): GraphQLInputType =
        inputTypeMap[getClass(annotatedType)]!!(annotatedType).let {
            if (annotatedType.isAnnotationPresent(GraphQLNonNull::class.java)) {
                graphql.schema.GraphQLNonNull(it)
            } else {
                it
            }
        }

    private fun getOutputType(annotatedType: AnnotatedType): GraphQLOutputType =
        getClass(annotatedType).let { clazz ->
            outputTypeMap[clazz]?.invoke(annotatedType) ?: buildOutputType(clazz)
        }.let {
            if (annotatedType.isAnnotationPresent(GraphQLNonNull::class.java)) {
                graphql.schema.GraphQLNonNull(it)
            } else {
                it
            }
        }

    private fun buildOutputType(clazz: Class<*>): GraphQLOutputType {
        for (typeDependency in clazz.getAnnotationsByType(GraphQLTypeDependency::class.java)) {
            typeDependency.value.java.let {
                if (it !in objectTypeMap) {
                    buildOutputType(it)
                }
            }
        }

        outputTypeMap[clazz] = { GraphQLTypeReference(getGraphQLName(clazz)) }

        return (if (clazz.isInterface) {
            buildInterface(clazz)
        } else {
            buildObjectType(clazz).apply {
                objectTypeMap[clazz] = this
            }
        } as GraphQLOutputType).apply {
            outputTypeMap[clazz] = { this }
        }
    }

    private fun buildInterface(clazz: Class<*>) =
        GraphQLInterfaceType.newInterface().apply {
            name(getGraphQLName(clazz))
            fields(buildFields(clazz, DataFetcher { it -> it.source }))
            typeResolver { objectTypeMap[it.javaClass]!! }
        }.build()

    private fun buildObjectType(clazz: Class<*>) =
        GraphQLObjectType.newObject().apply {
            name(getGraphQLName(clazz))
            fields(buildFields(clazz, DataFetcher { it -> it.source }))

            fun addInterfaces(c: Class<*>) {
                for (annotatedInterface in c.annotatedInterfaces) {
                    withInterface(getOutputType(annotatedInterface) as GraphQLInterfaceType)
                }

                for (`interface` in c.interfaces) {
                    addInterfaces(`interface`)
                }

                c.superclass?.let { addInterfaces(it) }
            }

            addInterfaces(clazz)
        }.build()

    private fun buildMutation(method: Method, objectFetcher: DataFetcher): GraphQLFieldDefinition {
        val inputFields = buildInputObjectFields(method)

        class MutationResult(val clientMutationId: String, val output: Any)

        return Relay().mutationWithClientMutationId(
            method.name.let { it[0].toUpperCase() + it.substring(1) },
            method.name,
            inputFields,
            buildFields(method.returnType, DataFetcher { it -> (it.source as MutationResult).output })
        ) { env ->
            val input = env.getArgument<Map<String, Any>>("input")
            MutationResult(
                clientMutationId = input["clientMutationId"] as String,
                output = method.invoke(objectFetcher.get(env), *Array(method.parameterCount) {
                    input[inputFields[it].name]
                })
            )
        }
    }

    fun buildFields(clazz: Class<*>, objectFetcher: DataFetcher) =
        ArrayList<GraphQLFieldDefinition>().apply {
            for (field in clazz.fields) {
                if (field.isAnnotationPresent(GraphQLField::class.java)) {
                    this += buildField(field, objectFetcher)
                }
            }

            for (method in clazz.methods) {
                if (method.isAnnotationPresent(GraphQLRelayMutation::class.java)) {
                    this += buildMutation(method, objectFetcher)
                } else if (method.isAnnotationPresent(GraphQLField::class.java)) {
                    this += buildField(method, objectFetcher)
                }
            }
        }

    private fun buildField(field: Field, objectFetcher: DataFetcher) =
        GraphQLFieldDefinition.newFieldDefinition().apply {
            name(field.name)

            type(getOutputType(field.annotatedType))

            dataFetcher { env ->
                field.get(objectFetcher.get(env))
            }
        }.build()

    private fun buildField(method: Method, objectFetcher: DataFetcher) =
        GraphQLFieldDefinition.newFieldDefinition().apply {
            name(method.name.replaceFirst(Regex("^(?:get|is|set)([A-Z])"), "$1").let {
                it[0].toLowerCase() + it.substring(1)
            })
            type(getOutputType(method.annotatedReturnType))

            for (parameter in method.parameters) {
                argument(buildArgument(parameter))
            }

            dataFetcher { env ->
                method.invoke(objectFetcher.get(env), *env.arguments.values.toTypedArray())
            }
        }.build()

    private fun buildArgument(parameter: Parameter) =
        GraphQLArgument.newArgument().apply {
            name(parameter.getAnnotation(GraphQLName::class.java)?.value ?: parameter.name)
            type(getInputType(parameter.annotatedType))
        }.build()

    private fun buildInputObjectFields(method: Method) =
        ArrayList<GraphQLInputObjectField>().apply {
            for (parameter in method.parameters) {
                this += buildInputObjectField(parameter)
            }
        }

    private fun buildInputObjectField(parameter: Parameter) =
        GraphQLInputObjectField.newInputObjectField().apply {
            name(parameter.getAnnotation(GraphQLName::class.java)?.value ?: parameter.name)
            type(getInputType(parameter.annotatedType))
        }.build()
}
