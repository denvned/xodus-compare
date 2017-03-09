package com.github.denvned.xodus.compare

import jetbrains.exodus.entitystore.*
import jetbrains.exodus.env.Environment
import jetbrains.exodus.env.EnvironmentConfig
import jetbrains.exodus.env.Environments
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

object EntityStoreComparator {
  private const val THREAD_POOL_SIZE = 8
  private const val UPDATE_PROCESSED_BATCH_SIZE = 256
  private val threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

  fun compareStores(
    oldStoreLocation: EntityStoreLocation,
    newStoreLocation: EntityStoreLocation,
    store: PersistentEntityStore
  ): Entity {
    val futureComparison = CompletableFuture<Entity>()

    threadPool.execute {
      var oldEnv: Environment? = null
      var newEnv: Environment? = null
      var oldStore: PersistentEntityStore? = null
      var newStore: PersistentEntityStore? = null

      try {
        fun getEnv(storeDir: String) = Environments.newInstance(
          storeDir,
          EnvironmentConfig().setEnvIsReadonly(true).setGcEnabled(false)
        )

        fun getStore(env: Environment, storeName: String) = PersistentEntityStores.newInstance(
          PersistentEntityStoreConfig().setRefactoringSkipAll(true),
          env,
          storeName
        )

        oldEnv = getEnv(oldStoreLocation.dir)
        newEnv = getEnv(newStoreLocation.dir)
        oldStore = getStore(oldEnv, oldStoreLocation.storeName)
        newStore = getStore(newEnv, newStoreLocation.storeName)

        doCompareStores(
          oldStore = oldStore,
          newStore = newStore,
          store = store,
          futureComparison = futureComparison
        )
      } catch (e: Throwable) {
        futureComparison.completeExceptionally(e)
      } finally {
        oldStore?.close() ?: oldEnv?.close()
        newStore?.close() ?: newEnv?.close()
      }
    }

    return futureComparison.get()
  }

  private fun doCompareStores(
    oldStore: PersistentEntityStore,
    newStore: PersistentEntityStore,
    store: PersistentEntityStore,
    futureComparison: CompletableFuture<Entity>
  ) {
    oldStore.executeInReadonlyTransaction { oldTxn ->
      newStore.executeInReadonlyTransaction { newTxn ->
        val date = Date()

        fun countEntities(txn: StoreTransaction): Long {
          var count = 0L
          txn.entityTypes.forEach { count += txn.getAll(it).size() }
          return count
        }

        val comparison = store.computeInTransaction {
          ComparisonEntityCreators.newComparison(
            txn = it,
            oldStoreDir = oldStore.location,
            oldStoreName = oldStore.name,
            newStoreDir = newStore.location,
            newStoreName = newStore.name,
            date = date,
            oldEntityCount = countEntities(oldTxn),
            newEntityCount = countEntities(newTxn)
          )
        }
        futureComparison.complete(comparison)

        var oldEntitiesProcessed = 0L
        var newEntitiesProcessed = 0L

        fun updateProcessed() {
          store.executeInTransaction {
            ComparisonEntityCreators.updateComparisonProcessedCounts(
              comparison = comparison,
              oldEntitiesProcessed = oldEntitiesProcessed,
              newEntitiesProcessed = newEntitiesProcessed
            )
          }
        }

        val oldTypeNames = oldTxn.entityTypes.associate { oldStore.getEntityTypeId(it) to it }
        val newTypeNames = newTxn.entityTypes.associate { newStore.getEntityTypeId(it) to it }

        val entityTypes = store.computeInTransaction {
          (oldTypeNames.keys + newTypeNames.keys).associate { typeId ->
            val oldName = oldTypeNames[typeId]
            val newName = newTypeNames[typeId]

            typeId to ComparisonEntityCreators.newEntityType(
              txn = it,
              comparison = comparison,
              id = typeId,
              oldName = oldName,
              newName = newName
            )
          }
        }

        var i = 0L
        for (typeId in entityTypes.keys) {
          val oldEntities = oldTypeNames[typeId]?.let { oldTxn.getAll(it) }
          val newEntities = newTypeNames[typeId]?.let { newTxn.getAll(it) }

          for ((oldEntity, newEntity) in MatchingIterator(oldEntities, newEntities, compareBy { it.id })) {
            EntityComparator(
              oldEntity = oldEntity,
              newEntity = newEntity,
              store = store,
              entityTypes = entityTypes
            ).compareEntities()

            if (oldEntity != null) {
              oldEntitiesProcessed++
            }
            if (newEntity != null) {
              newEntitiesProcessed++
            }

            if (++i % UPDATE_PROCESSED_BATCH_SIZE == 0L) {
              updateProcessed()
            }
          }
        }

        updateProcessed()
      }
    }
  }
}
