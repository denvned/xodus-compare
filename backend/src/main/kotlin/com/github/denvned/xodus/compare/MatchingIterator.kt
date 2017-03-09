package com.github.denvned.xodus.compare

import java.util.*

/**
 * The [firstIterator] and [secondIterator] have to iterate elements in the ascending order
 * according to the [comparator].
 */
internal class MatchingIterator<T>(
  val firstIterator: Iterator<T>,
  val secondIterator: Iterator<T>,
  val comparator: Comparator<T>
) : Iterator<Pair<T?, T?>> {
  private var nextFirst = firstIterator.nextOrNull()
  private var nextSecond = secondIterator.nextOrNull()

  constructor(first: Iterable<T>?, second: Iterable<T>?, comparator: Comparator<T>)
    : this(
    (first ?: emptyList()).iterator(),
    (second ?: emptyList()).iterator(),
    comparator
  )

  override fun hasNext() = nextFirst != null || nextSecond != null

  override fun next(): Pair<T?, T?> {
    val first = nextFirst
    val second = nextSecond

    if (first == null && second == null) {
      throw NoSuchElementException()
    }

    val compare = when {
      first === second -> 0
      first == null -> 1
      second == null -> -1
      else -> comparator.compare(first, second)
    }

    return if (compare < 0) {
      nextFirst = firstIterator.nextOrNull()
      first to null
    } else if (compare > 0) {
      nextSecond = secondIterator.nextOrNull()
      null to second
    } else {
      nextFirst = firstIterator.nextOrNull()
      nextSecond = secondIterator.nextOrNull()
      first to second
    }
  }

  private fun Iterator<T>.nextOrNull() = if (hasNext()) next() else null
}
