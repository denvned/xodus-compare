package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.Entity;

public final class AddedEntityEdge extends AbstractEntityBasedEdge {
    public AddedEntityEdge(Entity entity) {
        super(entity);
    }

    @GraphQLField @GraphQLNonNull
    public AddedEntity getNode() {
        return new AddedEntity(entity);
    }
}
