package kotlinx.graphql

import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

interface GraphQLSchemaBuildingStrategy {
  val queryRootTypeName: String
  val mutationRootTypeName: String

  fun isIgnored(callable: KCallable<*>): Boolean

  fun isInterface(clazz: KClass<*>): Boolean

  fun getName(clazz: KClass<*>): String
  fun getName(callable: KCallable<*>): String
  fun getName(parameter: KParameter): String

  fun isId(callable: KCallable<*>): Boolean
  fun isId(parameter: KParameter): Boolean

  fun getDefaultValue(parameter: KParameter): Any
}
