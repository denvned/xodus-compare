package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.EntityIterable;

import java.util.AbstractList;
import java.util.List;

public final class ChangedEntityConnection extends AbstractEntityBasedConnection {
    public ChangedEntityConnection(EntityIterable entities, Integer first, Long after) {
        super(entities, first, after);
    }

    @GraphQLField @GraphQLNonNull
    public List<@GraphQLNonNull ChangedEntityEdge> getEdges() {
        init();

        return new AbstractList<ChangedEntityEdge>() {
            @Override
            public ChangedEntityEdge get(int index) {
                return new ChangedEntityEdge(entities.get(index));
            }

            @Override
            public int size() {
                return entities.size();
            }
        };
    }
}
