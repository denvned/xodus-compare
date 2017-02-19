package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import com.github.denvned.xodus.compare.ComparisonStoreNames;
import jetbrains.exodus.entitystore.Entity;

public final class LinkTargetEdge extends AbstractEntityBasedEdge {
    public LinkTargetEdge(Entity entity) {
        super(entity);
    }

    @GraphQLField @GraphQLNonNull
    public long getNode() {
        return (long)entity.getProperty(ComparisonStoreNames.LinkTarget.ENTITY_ID);
    }
}
