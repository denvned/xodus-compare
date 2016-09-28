package com.github.denvned.xodus.compare.backend

import com.github.denvned.xodus.compare.ArrayByteIterableBinding
import jetbrains.exodus.ArrayByteIterable
import jetbrains.exodus.entitystore.PersistentEntityStore
import jetbrains.exodus.entitystore.PersistentEntityStores
import jetbrains.exodus.env.Environments
import javax.annotation.PostConstruct
import javax.annotation.Resource
import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import javax.servlet.annotation.WebListener

object ComparisonStoreProvider {
    @JvmStatic
    lateinit var store: PersistentEntityStore
        // private set (commented because of https://youtrack.jetbrains.com/issue/KT-11585#comment=27-1621942)

    @WebListener
    class ContextListener : ServletContextListener {
        @Resource(name = "comparisonStoreDir")
        private lateinit var comparisonStoreDir: String
        @Resource(name = "comparisonStoreName")
        private lateinit var comparisonStoreName: String

        @PostConstruct
        private fun init() {
            store =
                PersistentEntityStores.newInstance(Environments.newInstance(comparisonStoreDir), comparisonStoreName)

            store.executeInTransaction {
                store.registerCustomPropertyType(it, ArrayByteIterable::class.java, ArrayByteIterableBinding)
            }
        }

        override fun contextInitialized(sce: ServletContextEvent) {
        }

        override fun contextDestroyed(sce: ServletContextEvent) {
            store.close()
        }
    }
}
