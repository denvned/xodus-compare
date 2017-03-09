package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.graphql.PageInfo
import jetbrains.exodus.entitystore.Entity
import jetbrains.exodus.entitystore.EntityIterable
import java.util.*

abstract class AbstractEntityBasedConnection(
    private val iterable: EntityIterable,
    private val first: Int?,
    private val after: Long?) {

  protected val page by lazy {
    val entities = ArrayList<Entity>(first ?: 8)
    var hasPrev = false
    var hasNext = false

    for (entity in iterable) {
      if (entities.size == first ?: Int.MAX_VALUE) {
        hasNext = true
        break
      }

      if (entity.id.localId > after ?: -1) {
        entities += entity
      } else {
        hasPrev = true
      }
    }

    Page(entities, PageInfo(hasPrev, hasNext))
  }

  val pageInfo get() = page.info

  val totalCount get() = iterable.size()

  protected class Page(val entities: List<Entity>, val info: PageInfo)
}
