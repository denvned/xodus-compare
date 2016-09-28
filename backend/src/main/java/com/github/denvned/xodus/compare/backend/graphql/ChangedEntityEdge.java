package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.Entity;

public final class ChangedEntityEdge extends AbstractEntityBasedEdge {
    public ChangedEntityEdge(Entity entity) {
        super(entity);
    }

    @GraphQLField @GraphQLNonNull
    public ChangedEntity getNode() {
        return new ChangedEntity(entity);
    }
}
