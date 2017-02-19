package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.Entity;

public final class DeletedEntityEdge extends AbstractEntityBasedEdge {
    public DeletedEntityEdge(Entity entity) {
        super(entity);
    }

    @GraphQLField @GraphQLNonNull
    public DeletedEntity getNode() {
        return new DeletedEntity(entity);
    }
}
