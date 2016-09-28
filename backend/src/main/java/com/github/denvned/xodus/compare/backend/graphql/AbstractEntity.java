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

public abstract class AbstractEntity extends AbstractEntityBasedNode implements EntityInterface {
    public AbstractEntity(Entity entity) {
        super(entity);
    }

    @GraphQLField @GraphQLNonNull
    public EntityType getType() {
        return new EntityType(entity.getLink(ComparisonStoreNames.Entity.TYPE));
    }

    @GraphQLField @GraphQLNonNull
    public long getLocalId() {
        return entity.getId().getLocalId();
    }

    @GraphQLField @GraphQLNonNull
    public long getEntityId() {
        return (long)entity.getProperty(ComparisonStoreNames.Entity.ID);
    }

    @GraphQLField @GraphQLNonNull
    public List<@GraphQLNonNull Property> getProperties() {
        List<Property> result = new ArrayList<>();

        for (Entity property : getPropertyIterable()) {
            result.add(new Property(property));
        }

        Collections.sort(result, Comparator.comparing(Property::getName));

        return result;
    }

    @GraphQLField @GraphQLNonNull
    public List<@GraphQLNonNull Blob> getBlobs() {
        List<Blob> result = new ArrayList<>();

        for (Entity blob : getBlobIterable()) {
            result.add(new Blob(blob));
        }

        Collections.sort(result, Comparator.comparing(Blob::getName));

        return result;
    }

    @GraphQLField @GraphQLNonNull
    public List<@GraphQLNonNull Link> getLinks() {
        List<Link> result = new ArrayList<>();

        for (Entity link : getLinkIterable()) {
            result.add(new Link(link));
        }

        Collections.sort(result, Comparator.comparing(Link::getName));

        return result;
    }

    @GraphQLField @GraphQLNonNull
    public long getPropertyCount() {
        return getPropertyIterable().size();
    }

    @GraphQLField @GraphQLNonNull
    public long getBlobCount() {
        return getBlobIterable().size();
    }

    @GraphQLField @GraphQLNonNull
    public long getLinkCount() {
        return getLinkIterable().size();
    }

    private EntityIterable getPropertyIterable() {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        return getIterable(ComparisonStoreNames.PROPERTY, ComparisonStoreNames.Property.ENTITY);
    }

    private EntityIterable getBlobIterable() {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        return getIterable(ComparisonStoreNames.BLOB, ComparisonStoreNames.Blob.ENTITY);
    }

    private EntityIterable getLinkIterable() {
        return getIterable(ComparisonStoreNames.LINK, ComparisonStoreNames.Link.ENTITY);
    }

    private EntityIterable getIterable(String typeName, String linkName) {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        return txn.findLinks(typeName, entity, linkName);
    }
}
