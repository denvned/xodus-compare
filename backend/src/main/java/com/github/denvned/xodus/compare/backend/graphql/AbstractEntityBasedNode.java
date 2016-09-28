package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.Node;
import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLID;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import graphql.relay.Relay;
import jetbrains.exodus.entitystore.Entity;

public abstract class AbstractEntityBasedNode implements Node {
    protected final Entity entity;

    public AbstractEntityBasedNode(Entity entity) {
        this.entity = entity;
    }

    @GraphQLField @GraphQLID @GraphQLNonNull @Override
    public String getId() {
        return new Relay().toGlobalId(
            getClass().getSimpleName(),
            Long.toString(entity.getId().getLocalId())
        );
    }
}
