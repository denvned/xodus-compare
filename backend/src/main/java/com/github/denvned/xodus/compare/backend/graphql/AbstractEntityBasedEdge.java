package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.Entity;

public abstract class AbstractEntityBasedEdge {
    protected final Entity entity;

    public AbstractEntityBasedEdge(Entity entity) {
        this.entity = entity;
    }

    @GraphQLField @GraphQLNonNull
    public String getCursor() {
        return Long.toString(entity.getId().getLocalId());
    }
}
