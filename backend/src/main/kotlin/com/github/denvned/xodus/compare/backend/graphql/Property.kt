package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.xodus.compare.ComparisonStoreNames
import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.entitystore.Entity

class Property(private val entity: Entity) {

  val name get() = entity.getProperty(ComparisonStoreNames.Property.NAME) as String

  val oldValueType get() = getValueType(ComparisonStoreNames.Property.OLD_VALUE)

  val newValueType get() = getValueType(ComparisonStoreNames.Property.NEW_VALUE)

  val oldValue get() = getValue(ComparisonStoreNames.Property.OLD_VALUE)

  val newValue get() = getValue(ComparisonStoreNames.Property.NEW_VALUE)

  private fun getValueType(valuePropName: String) = entity.getProperty(valuePropName)?.let { value ->
    value::class.java.takeIf { it != ArrayByteIterable::class.java }?.simpleName ?: "Unknown"
  }

  private fun getValue(valuePropName: String) = entity.getProperty(valuePropName)?.toString()
}
