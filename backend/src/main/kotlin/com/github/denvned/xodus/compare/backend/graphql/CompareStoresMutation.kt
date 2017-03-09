package com.github.denvned.xodus.compare.backend.graphql

import com.github.denvned.graphql.annotations.*
import com.github.denvned.xodus.compare.EntityStoreComparator
import com.github.denvned.xodus.compare.EntityStoreLocation
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider
import javax.inject.Inject

@MutationProvider
class CompareStoresMutation @Inject constructor(private val viewer: Viewer) {

  @GraphQLRelayMutation
  fun compareStores(oldStoreDir: String, oldStoreName: String, newStoreDir: String, newStoreName: String): Payload {
    val store = ComparisonStoreProvider.store

    val comparison = EntityStoreComparator.compareStores(
        EntityStoreLocation(oldStoreDir, oldStoreName),
        EntityStoreLocation(newStoreDir, newStoreName),
        store)

    store.currentTransaction!!.abort()
    store.beginReadonlyTransaction()

    return Payload(Comparison(comparison), viewer)
  }

  class Payload internal constructor(val comparison: Comparison, val viewer: Viewer)
}
