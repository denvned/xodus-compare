package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.EntityIterable;

import java.util.AbstractList;
import java.util.List;

public final class DeletedEntityConnection extends AbstractEntityBasedConnection {
    public DeletedEntityConnection(EntityIterable entities, Integer first, Long after) {
        super(entities, first, after);
    }

    @GraphQLField @GraphQLNonNull
    public List<@GraphQLNonNull DeletedEntityEdge> getEdges() {
        init();

        return new AbstractList<DeletedEntityEdge>() {
            @Override
            public DeletedEntityEdge get(int index) {
                return new DeletedEntityEdge(entities.get(index));
            }

            @Override
            public int size() {
                return entities.size();
            }
        };
    }
}
