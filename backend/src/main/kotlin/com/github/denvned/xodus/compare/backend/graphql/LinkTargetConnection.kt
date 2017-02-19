package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.EntityIterable;

import java.util.AbstractList;
import java.util.List;

public final class LinkTargetConnection extends AbstractEntityBasedConnection {
    public LinkTargetConnection(EntityIterable entities, Integer first, Long after) {
        super(entities, first, after);
    }

    @GraphQLField @GraphQLNonNull
    public List<@GraphQLNonNull LinkTargetEdge> getEdges() {
        init();

        return new AbstractList<LinkTargetEdge>() {
            @Override
            public LinkTargetEdge get(int index) {
                return new LinkTargetEdge(entities.get(index));
            }

            @Override
            public int size() {
                return entities.size();
            }
        };
    }
}
