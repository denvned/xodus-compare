package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.xodus.compare.ComparisonStoreNames
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider
import jetbrains.exodus.entitystore.Entity

abstract class AbstractEntity(entity: Entity) : AbstractEntityBasedNode(entity), EntityInterface {

  override val type get() = EntityType(entity.getLink(ComparisonStoreNames.Entity.TYPE)!!)

  override val localId get() = entity.id.localId

  override val entityId get() = entity.getProperty(ComparisonStoreNames.Entity.ID) as Long

  override val properties get() = propertyIterable.map(::Property).sortedBy(Property::name)

  override val blobs get() = blobIterable.map(::Blob).sortedBy(Blob::name)

  override val links get() = linkIterable.map(::Link).sortedBy(Link::name)

  override val propertyCount get() = propertyIterable.size()

  override val blobCount get() = blobIterable.size()

  override val linkCount get() = linkIterable.size()

  private val propertyIterable get() = getIterable(ComparisonStoreNames.PROPERTY, ComparisonStoreNames.Property.ENTITY)

  private val blobIterable get() = getIterable(ComparisonStoreNames.BLOB, ComparisonStoreNames.Blob.ENTITY)

  private val linkIterable get() = getIterable(ComparisonStoreNames.LINK, ComparisonStoreNames.Link.ENTITY)

  private fun getIterable(typeName: String, linkName: String) =
    ComparisonStoreProvider.store.currentTransaction!!.findLinks(typeName, entity, linkName)
}
