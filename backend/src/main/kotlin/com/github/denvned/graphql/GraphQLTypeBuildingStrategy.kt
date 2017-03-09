package com.github.denvned.graphql

import kotlin.reflect.*

interface GraphQLTypeBuildingStrategy {
  fun isIgnored(callable: KCallable<*>): Boolean

  fun isInterface(clazz: KClass<*>): Boolean

  fun getName(clazz: KClass<*>): String
  fun getName(callable: KCallable<*>): String
  fun getName(parameter: KParameter): String

  fun isId(callable: KCallable<*>): Boolean
  fun isId(parameter: KParameter): Boolean

  fun getDefaultValue(parameter: KParameter): Any
}
