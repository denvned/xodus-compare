package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.*;
import com.github.denvned.xodus.compare.EntityStoreComparator;
import com.github.denvned.xodus.compare.EntityStoreLocation;
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.PersistentEntityStore;

import javax.inject.Inject;

@MutationProvider
public final class CompareStoresMutation {
    @Inject
    private Viewer viewer;

    @GraphQLField @GraphQLRelayMutation
    public Payload compareStores(
        @GraphQLName("oldStoreDir") @GraphQLNonNull String oldStoreDir,
        @GraphQLName("oldStoreName") @GraphQLNonNull String oldStoreName,
        @GraphQLName("newStoreDir") @GraphQLNonNull String newStoreDir,
        @GraphQLName("newStoreName") @GraphQLNonNull String newStoreName
    ) {
        PersistentEntityStore store = ComparisonStoreProvider.getStore();

        Entity comparison = EntityStoreComparator.INSTANCE.compareStores(
            new EntityStoreLocation(oldStoreDir, oldStoreName),
            new EntityStoreLocation(newStoreDir, newStoreName),
            store
        );

        if (comparison == null) {
            return new Payload(null, viewer);
        }

        store.getCurrentTransaction().abort();
        store.beginReadonlyTransaction();

        return new Payload(new Comparison(comparison), viewer);
    }

    public static class Payload {
        @GraphQLField
        public final Comparison comparison;

        @GraphQLField @GraphQLNonNull
        public final Viewer viewer;

        Payload(Comparison comparison, Viewer viewer) {
            this.comparison = comparison;
            this.viewer = viewer;
        }
    }
}
