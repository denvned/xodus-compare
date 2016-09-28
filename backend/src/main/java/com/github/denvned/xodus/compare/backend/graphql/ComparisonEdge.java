package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.Entity;

public final class ComparisonEdge extends AbstractEntityBasedEdge {
    public ComparisonEdge(Entity entity) {
        super(entity);
    }

    @GraphQLField @GraphQLNonNull
    public Comparison getNode() {
        return new Comparison(entity);
    }
}
