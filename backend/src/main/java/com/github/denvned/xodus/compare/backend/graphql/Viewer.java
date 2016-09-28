package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.Node;
import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLID;
import com.github.denvned.graphql.annotations.GraphQLName;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import com.github.denvned.xodus.compare.ComparisonStoreNames;
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider;
import graphql.relay.Relay;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.StoreTransaction;

import javax.inject.Singleton;

@Singleton
public final class Viewer implements Node {
    private final static String ID = new Relay().toGlobalId(Viewer.class.getSimpleName(), "0");

    @GraphQLField @GraphQLID @GraphQLNonNull @Override
    public String getId() {
        return ID;
    }

    @GraphQLField @GraphQLNonNull
    public ComparisonConnection getComparisons(
        @GraphQLName("first") Integer first,
        @GraphQLName("after") String after
    ) {
        return new ComparisonConnection(first, after != null ? Long.parseLong(after) : null);
    }

    @GraphQLField
    public Comparison getComparison(@GraphQLName("localId") @GraphQLNonNull long localId) {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        Entity entity = txn.findIds(ComparisonStoreNames.COMPARISON, localId, localId).getFirst();
        return entity != null ? new Comparison(entity) : null;
    }

    @GraphQLField
    public EntityType getEntityType(@GraphQLName("localId") @GraphQLNonNull long localId) {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        Entity entity = txn.findIds(ComparisonStoreNames.ENTITY_TYPE, localId, localId).getFirst();
        return entity != null ? new EntityType(entity) : null;
    }

    @GraphQLField
    public AddedEntity getAddedEntity(@GraphQLName("localId") @GraphQLNonNull long localId) {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        Entity entity = txn.findIds(ComparisonStoreNames.ADDED_ENTITY, localId, localId).getFirst();
        return entity != null ? new AddedEntity(entity) : null;
    }

    @GraphQLField
    public ChangedEntity getChangedEntity(@GraphQLName("localId") @GraphQLNonNull long localId) {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        Entity entity = txn.findIds(ComparisonStoreNames.CHANGED_ENTITY, localId, localId).getFirst();
        return entity != null ? new ChangedEntity(entity) : null;
    }

    @GraphQLField
    public DeletedEntity getDeletedEntity(@GraphQLName("localId") @GraphQLNonNull long localId) {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        Entity entity = txn.findIds(ComparisonStoreNames.DELETED_ENTITY, localId, localId).getFirst();
        return entity != null ? new DeletedEntity(entity) : null;
    }

    @GraphQLField
    public LinkTargetType getLinkTargetType(@GraphQLName("localId") long localId) {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        Entity entity = txn.findIds(ComparisonStoreNames.LINK_TARGET_TYPE, localId, localId).getFirst();
        return entity != null ? new LinkTargetType(entity) : null;
    }
}
