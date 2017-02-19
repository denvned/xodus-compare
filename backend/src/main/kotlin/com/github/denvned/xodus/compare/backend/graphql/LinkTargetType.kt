package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLName;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import com.github.denvned.xodus.compare.ComparisonStoreNames;
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

public final class LinkTargetType extends AbstractEntityBasedNode {
    public LinkTargetType(Entity entity) {
        super(entity);
    }

    @GraphQLField @GraphQLNonNull
    public Link getLink() {
        return new Link(entity.getLink(ComparisonStoreNames.LinkTargetType.LINK));
    }

    @GraphQLField @GraphQLNonNull
    public EntityType getEntityType() {
        return new EntityType(entity.getLink(ComparisonStoreNames.LinkTargetType.ENTITY_TYPE));
    }

    @GraphQLField @GraphQLNonNull
    public LinkTargetConnection getAddedTargets(
        @GraphQLName("first") Integer first,
        @GraphQLName("after") String after
    ) {
        return new LinkTargetConnection(getTargets(
            ComparisonStoreNames.ADDED_LINK_TARGET),
            first,
            after != null ? Long.parseLong(after) : null
        );
    }

    @GraphQLField @GraphQLNonNull
    public LinkTargetConnection getDeletedTargets(
        @GraphQLName("first") Integer first,
        @GraphQLName("after") String after
    ) {
        return new LinkTargetConnection(getTargets(
            ComparisonStoreNames.DELETED_LINK_TARGET),
            first,
            after != null ? Long.parseLong(after) : null
        );
    }

    private EntityIterable getTargets(String changeType) {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();

        return txn.findLinks(
            changeType,
            entity,
            ComparisonStoreNames.LinkTarget.TYPE
        );
    }
}
