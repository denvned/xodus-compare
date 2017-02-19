package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLName;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import com.github.denvned.xodus.compare.ComparisonStoreNames;
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

public final class EntityType extends AbstractEntityBasedNode {
    public EntityType(Entity entity) {
        super(entity);
    }

    @GraphQLField @GraphQLNonNull
    public long getLocalId() {
        return entity.getId().getLocalId();
    }

    @GraphQLField @GraphQLNonNull
    public Comparison getComparison() {
        return new Comparison(entity.getLink(ComparisonStoreNames.EntityType.COMPARISON));
    }

    @GraphQLField @GraphQLNonNull
    public int getTypeId() {
        return (int)entity.getProperty(ComparisonStoreNames.EntityType.ID);
    }

    @GraphQLField
    public String getOldName() {
        return (String)entity.getProperty(ComparisonStoreNames.EntityType.OLD_NAME);
    }

    @GraphQLField
    public String getNewName() {
        return (String)entity.getProperty(ComparisonStoreNames.EntityType.NEW_NAME);
    }

    @GraphQLField @GraphQLNonNull
    public AddedEntityConnection getAddedEntities(
        @GraphQLName("first") Integer first,
        @GraphQLName("after") String after
    ) {
        return new AddedEntityConnection(getEntities(
            ComparisonStoreNames.ADDED_ENTITY),
            first,
            after != null ? Long.parseLong(after) : null
        );
    }

    @GraphQLField @GraphQLNonNull
    public ChangedEntityConnection getChangedEntities(
        @GraphQLName("first") Integer first,
        @GraphQLName("after") String after
    ) {
        return new ChangedEntityConnection(getEntities(
            ComparisonStoreNames.CHANGED_ENTITY),
            first,
            after != null ? Long.parseLong(after) : null
        );
    }

    @GraphQLField @GraphQLNonNull
    public DeletedEntityConnection getDeletedEntities(
        @GraphQLName("first") Integer first,
        @GraphQLName("after") String after
    ) {
        return new DeletedEntityConnection(getEntities(
            ComparisonStoreNames.DELETED_ENTITY),
            first,
            after != null ? Long.parseLong(after) : null
        );
    }

    private EntityIterable getEntities(String changeType) {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();

        return txn.findLinks(
            changeType,
            entity,
            ComparisonStoreNames.Entity.TYPE
        );
    }
}
