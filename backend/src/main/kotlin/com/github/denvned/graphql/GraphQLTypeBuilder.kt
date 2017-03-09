package com.github.denvned.graphql

import com.github.denvned.graphql.annotations.GraphQLRelayMutation
import graphql.Scalars
import graphql.relay.Relay
import graphql.schema.*
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaMethod

@Singleton
class GraphQLTypeBuilder @Inject constructor(private val strategy: GraphQLTypeBuildingStrategy) {

  val allObjectTypes: Set<GraphQLObjectType>
    get() = objectTypeMap.values.asSequence().filterIsInstance<GraphQLObjectType>().toHashSet()

  private val inputObjectTypeMap = HashMap<KClass<*>, GraphQLInputObjectType>()
  private val interfaceMap = HashMap<KClass<*>, GraphQLOutputType>()
  private val objectTypeMap = HashMap<KClass<*>, GraphQLOutputType>()

  private fun getInputType(type: KType, isId: Boolean): GraphQLInputType = type.clazz.let { clazz ->
    when {
      clazz == Boolean::class -> Scalars.GraphQLBoolean
      clazz == Int::class -> Scalars.GraphQLInt
      clazz == Long::class -> Scalars.GraphQLLong
      clazz == Float::class || clazz == Double::class -> Scalars.GraphQLFloat
      clazz == String::class -> if (isId) Scalars.GraphQLID else Scalars.GraphQLString
      clazz.isSubclassOf(Iterable::class) && clazz.isSuperclassOf(List::class) && type.arguments.size == 1 ->
          GraphQLList(getInputType(type.arguments[0].type!!, isId = isId))
      else -> getInputObjectType(clazz)
    }.let { if (type.isMarkedNullable) it as GraphQLInputType else GraphQLNonNull(it) }
  }

  private fun getOutputType(type: KType, isId: Boolean): GraphQLOutputType = type.clazz.let { clazz ->
    when {
      clazz == Boolean::class -> Scalars.GraphQLBoolean
      clazz == Int::class -> Scalars.GraphQLInt
      clazz == Long::class -> Scalars.GraphQLLong
      clazz == Float::class || clazz == Double::class -> Scalars.GraphQLFloat
      clazz == String::class -> if (isId) Scalars.GraphQLID else Scalars.GraphQLString
      clazz == BooleanArray::class -> GraphQLList(Scalars.GraphQLBoolean)
      clazz == IntArray::class -> GraphQLList(Scalars.GraphQLInt)
      clazz == LongArray::class -> GraphQLList(Scalars.GraphQLLong)
      (clazz == Array<Any>::class || clazz.isSubclassOf(Iterable::class)) && type.arguments.size == 1 ->
        GraphQLList(getOutputType(type.arguments[0].type!!, isId))
      strategy.isInterface(clazz) -> getInterface(clazz)
      else -> getObjectType(clazz)
    }.let { if (type.isMarkedNullable) it else GraphQLNonNull(it) }
  }

  private fun getInputObjectType(clazz: KClass<*>) = inputObjectTypeMap.getOrPut(clazz) {
    clazz.toGraphQLInputObjectType()
  }

  private fun getObjectType(clazz: KClass<*>) = objectTypeMap.getOrPut(clazz) {
    objectTypeMap[clazz] = GraphQLTypeReference(strategy.getName(clazz))
    clazz.toGraphQLObjectType()
  }

  private fun getInterface(clazz: KClass<*>) = interfaceMap.getOrPut(clazz) {
    interfaceMap[clazz] = GraphQLTypeReference(strategy.getName(clazz))
    clazz.toGraphQLInterfaceType()
  }

  private fun KClass<*>.toGraphQLInputObjectType() = let { clazz ->
    GraphQLInputObjectType.newInputObject().run {
      name(strategy.getName(clazz))
      fields(buildInputFields(primaryConstructor!!))
    }.build()
  }

  private fun KClass<*>.toGraphQLObjectType() = let { clazz ->
    GraphQLObjectType.newObject().apply {
      name(strategy.getName(clazz))
      fields(buildFields(clazz, sourceFetcher))

      allSuperclasses.asSequence().filter(strategy::isInterface).forEach {
        withInterface(getInterface(it) as GraphQLInterfaceType)
      }
    }.build()
  }

  private fun KClass<*>.toGraphQLInterfaceType() = let { clazz ->
    GraphQLInterfaceType.newInterface().apply {
      name(strategy.getName(clazz))
      fields(buildFields(clazz, sourceFetcher))
      typeResolver {
        it::class.allSuperclasses.asSequence().map { objectTypeMap[it::class] }.filterNotNull().first()
          as GraphQLObjectType
      }
    }.build()
  }

  private fun KFunction<*>.toRelayMutation(objectFetcher: DataFetcher): GraphQLFieldDefinition {
    return Relay().mutationWithClientMutationId(
        name.let { it[0].toUpperCase() + it.substring(1) },
        name,
        buildInputFields(this),
        buildFields(returnType.classifier as KClass<*>, sourceOutputFetcher)) { env ->
      val input = env.getArgument<Map<String, Any>>("input")
      MutationResult(
        clientMutationId = input["clientMutationId"] as String,
        output = callBy(convertArguments(valueParameters, input).apply {
          put(instanceParameter!!, objectFetcher.get(env))
        })!!)
    }
  }

  private fun buildInputFields(func: KFunction<*>) = func.valueParameters.map { it.toGraphQLInputObjectField() }

  fun buildFields(clazz: KClass<*>, objectFetcher: DataFetcher) = (
      clazz.memberProperties.asSequence().filter { it.isIncluded }.map { it.toGraphQLField(objectFetcher) }
        + clazz.memberFunctions.asSequence().filter { it.isIncluded }.map {
          if (it.findAnnotation<GraphQLRelayMutation>() != null) {
            it.toRelayMutation(objectFetcher)
          } else {
            it.toGraphQLField(objectFetcher)
          }
        }
      ).toList()

  private val KCallable<*>.isIncluded get() = !strategy.isIgnored(this) && visibility == KVisibility.PUBLIC

  private val KFunction<*>.isIncluded get() = (this as KCallable<*>).isIncluded && javaMethod !in OBJECT_METHODS

  private fun <T> KProperty1<T, *>.toGraphQLField(objectFetcher: DataFetcher) = let { property ->
    GraphQLFieldDefinition.newFieldDefinition().apply {
      name(strategy.getName(property))
      type(getOutputType(returnType, strategy.isId(property)))
      dataFetcher { env -> get(objectFetcher.get(env) as T) }
    }.build()
  }

  private fun KFunction<*>.toGraphQLField(objectFetcher: DataFetcher) = let { function ->
    GraphQLFieldDefinition.newFieldDefinition().apply {
      name(strategy.getName(function))
      type(getOutputType(returnType, strategy.isId(function)))
      valueParameters.forEach { argument(it.toGraphQLArgument()) }
      dataFetcher { env -> call(objectFetcher.get(env), *env.arguments.values.toTypedArray()) }
    }.build()
  }

  private fun KParameter.toGraphQLArgument() = let { parameter ->
    GraphQLArgument.newArgument().apply {
      name(strategy.getName(parameter))
      type(getInputType(type, isId = strategy.isId(parameter)))
      if (isOptional) {
        defaultValue(DefaultValue(strategy.getDefaultValue(parameter)))
      }
    }.build()
  }

  private fun KParameter.toGraphQLInputObjectField() = let { parameter ->
    GraphQLInputObjectField.newInputObjectField().apply {
      name(strategy.getName(parameter))
      type(getInputType(type, isId = strategy.isId(parameter)))
      if (isOptional) {
        defaultValue(DefaultValue(strategy.getDefaultValue(parameter)))
      }
    }.build()
  }

  private fun convertArgumentValue(type: KType, value: Any?): Any? = type.clazz.let { clazz ->
    when {
      clazz == Boolean::class || clazz == Int::class || clazz == Long::class || clazz == String::class -> value
      clazz == Float::class -> (value as? Double)?.toFloat() ?: value
      clazz == Double::class -> (value as? Float)?.toDouble() ?: value
      clazz.isSuperclassOf(List::class) ->
        (value as List<*>).map { convertArgumentValue(type.arguments[0].type!!, it) }
      else -> clazz.primaryConstructor!!.run { callBy(convertArguments(valueParameters, value as Map<String, *>)) }
    }
  }

  private fun convertArguments(parameters: List<KParameter>, values: Map<String, *>) =
      HashMap<KParameter, Any?>().apply {
        parameters.forEach { param ->
          values[param.name].takeIf { it !is DefaultValue }?.let {
            put(param, convertArgumentValue(param.type, it))
          }
        }
      }

  companion object {
    private val OBJECT_METHODS =
      sequenceOf(Any::equals, Any::hashCode, Any::toString).map { it.javaMethod }.toHashSet()

    class MutationResult(val clientMutationId: String, val output: Any)

    private class DefaultValue(val value: Any) {
      override fun toString() = value.toString()
    }

    private val sourceFetcher = DataFetcher { it.source }
    private val sourceOutputFetcher = DataFetcher { (it.source as MutationResult).output }

    private val KType.clazz get() = classifier as KClass<*>
  }
}
