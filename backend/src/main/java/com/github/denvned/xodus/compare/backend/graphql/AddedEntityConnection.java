package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.EntityIterable;

import java.util.AbstractList;
import java.util.List;

public final class AddedEntityConnection extends AbstractEntityBasedConnection {
    public AddedEntityConnection(EntityIterable entities, Integer first, Long after) {
        super(entities, first, after);
    }

    @GraphQLField @GraphQLNonNull
    public List<@GraphQLNonNull AddedEntityEdge> getEdges() {
        init();

        return new AbstractList<AddedEntityEdge>() {
            @Override
            public AddedEntityEdge get(int index) {
                return new AddedEntityEdge(entities.get(index));
            }

            @Override
            public int size() {
                return entities.size();
            }
        };
    }
}
