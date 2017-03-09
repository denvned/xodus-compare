package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.xodus.compare.ComparisonStoreNames
import jetbrains.exodus.entitystore.Entity

class Blob(private val entity: Entity) {

  val name get() = entity.getProperty(ComparisonStoreNames.Blob.NAME) as String

  val oldSize get() = getSize(ComparisonStoreNames.Blob.OLD_VALUE)

  val newSize get() = getSize(ComparisonStoreNames.Blob.NEW_VALUE)

  private fun getSize(valuePropName: String) = entity.getBlobSize(valuePropName).takeIf { it >= 0 }
}
