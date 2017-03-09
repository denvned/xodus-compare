package com.github.denvned.graphql

import com.github.denvned.graphql.annotations.GraphQLID
import com.github.denvned.graphql.annotations.GraphQLIgnore
import com.github.denvned.graphql.annotations.GraphQLInterface
import com.github.denvned.graphql.annotations.GraphQLName
import javax.inject.Singleton
import kotlin.reflect.*
import kotlin.reflect.full.findAnnotation

@Singleton
class DefaultGraphQLTypeBuildingStrategy : GraphQLTypeBuildingStrategy {

  override fun isIgnored(callable: KCallable<*>) = callable.isIgnoredInGraphQL

  override fun isInterface(clazz: KClass<*>) = clazz.findAnnotation<GraphQLInterface>() != null

  override fun getName(clazz: KClass<*>) = clazz.graphQLNameByAnnotation ?: clazz.simpleName!!

  override fun getName(callable: KCallable<*>) = callable.run {
    graphQLNameByAnnotation ?: if (this is KFunction<*>) propertyName else name
  }

  override fun getName(parameter: KParameter) = parameter.graphQLNameByAnnotation ?: parameter.name!!

  override fun isId(callable: KCallable<*>) = callable.isGraphQLId

  override fun isId(parameter: KParameter) = parameter.isGraphQLId

  private val KAnnotatedElement.isIgnoredInGraphQL get() = findAnnotation<GraphQLIgnore>() != null

  private val KAnnotatedElement.graphQLNameByAnnotation get() = findAnnotation<GraphQLName>()?.value

  private val KAnnotatedElement.isGraphQLId get() = findAnnotation<GraphQLID>() != null

  override fun getDefaultValue(parameter: KParameter) = "<default-value>"

  companion object {
    private val KFunction<*>.propertyName get() = name.replaceFirst("^(?:get|is|set)([A-Z])".toRegex(), "$1").let {
      it[0].toLowerCase() + it.substring(1)
    }
  }
}
