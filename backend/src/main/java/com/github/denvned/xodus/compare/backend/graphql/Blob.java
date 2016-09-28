package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import com.github.denvned.xodus.compare.ComparisonStoreNames;
import jetbrains.exodus.entitystore.Entity;

public final class Blob {
    private final Entity entity;

    public Blob(Entity entity) {
        this.entity = entity;
    }

    @GraphQLField @GraphQLNonNull
    public String getName() {
        return (String)entity.getProperty(ComparisonStoreNames.Blob.NAME);
    }

    @GraphQLField
    public Long getOldSize() {
        return getSize(ComparisonStoreNames.Blob.OLD_VALUE);
    }

    @GraphQLField
    public Long getNewSize() {
        return getSize(ComparisonStoreNames.Blob.NEW_VALUE);
    }

    private Long getSize(String valuePropName) {
        long size = entity.getBlobSize(valuePropName);
        return size >= 0 ? size : null;
    }
}
