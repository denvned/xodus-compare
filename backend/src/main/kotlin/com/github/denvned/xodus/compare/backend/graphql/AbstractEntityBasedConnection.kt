package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.PageInfo;
import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEntityBasedConnection {
    private EntityIterable iterable;
    private final Integer first;
    private final Long after;
    protected List<Entity> entities;
    private boolean hasPrev;
    private boolean hasNext;

    public AbstractEntityBasedConnection(EntityIterable entities, Integer first, Long after) {
        iterable = entities;
        this.after = after;
        this.first = first;
    }

    protected void init() {
        if (entities == null) {
            entities = new ArrayList<>(first != null ? first : 8);

            for (Entity entity : iterable) {
                if (first != null && entities.size() >= first) {
                    hasNext = true;
                    break;
                }

                if (after == null || entity.getId().getLocalId() > after) {
                    entities.add(entity);
                } else {
                    hasPrev = true;
                }
            }
        }
    }

    @GraphQLField @GraphQLNonNull
    public PageInfo getPageInfo() {
        init();
        return new PageInfo(hasPrev, hasNext);
    }

    @GraphQLField @GraphQLNonNull
    public long getTotalCount() {
        return iterable.size();
    }
}
