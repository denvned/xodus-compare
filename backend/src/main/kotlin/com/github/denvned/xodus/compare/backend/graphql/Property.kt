package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import com.github.denvned.xodus.compare.ComparisonStoreNames;
import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.entitystore.Entity;

public final class Property {
    private final Entity entity;

    public Property(Entity entity) {
        this.entity = entity;
    }

    @GraphQLField @GraphQLNonNull
    public String getName() {
        return (String)entity.getProperty(ComparisonStoreNames.Property.NAME);
    }

    @GraphQLField
    public String getOldValueType() {
        return getValueType(ComparisonStoreNames.Property.OLD_VALUE);
    }

    @GraphQLField
    public String getNewValueType() {
        return getValueType(ComparisonStoreNames.Property.NEW_VALUE);
    }

    @GraphQLField
    public String getOldValue() {
        return getValue(ComparisonStoreNames.Property.OLD_VALUE);
    }

    @GraphQLField
    public String getNewValue() {
        return getValue(ComparisonStoreNames.Property.NEW_VALUE);
    }

    private String getValueType(String valuePropName) {
        Comparable property = entity.getProperty(valuePropName);
        if (property == null) {
            return null;
        }

        Class<?> clazz = property.getClass();
        return clazz != ArrayByteIterable.class ? clazz.getSimpleName() : "Unknown";
    }

    private String getValue(String valuePropName) {
        Comparable property = entity.getProperty(valuePropName);
        return property != null ? property.toString() : null;
    }
}
