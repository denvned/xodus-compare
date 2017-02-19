package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import com.github.denvned.xodus.compare.ComparisonStoreNames;
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class Link {
    private final Entity entity;

    public Link(Entity entity) {
        this.entity = entity;
    }

    @GraphQLField @GraphQLNonNull
    public String getName() {
        return (String)entity.getProperty(ComparisonStoreNames.Link.NAME);
    }

    @GraphQLField @GraphQLNonNull
    public List<@GraphQLNonNull LinkTargetType> getTargetTypes() {
        List<LinkTargetType> result = new ArrayList<>();

        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();

        EntityIterable targetTypes =
            txn.findLinks(ComparisonStoreNames.LINK_TARGET_TYPE, entity, ComparisonStoreNames.LinkTargetType.LINK);
        for (Entity targetType : targetTypes) {
            result.add(new LinkTargetType(targetType));
        }

        Collections.sort(result, Comparator.comparing(targetType -> {
            EntityType type = targetType.getEntityType();
            String newName = type.getNewName();
            return newName != null ? newName : type.getOldName();
        }));

        return result;
    }
}
