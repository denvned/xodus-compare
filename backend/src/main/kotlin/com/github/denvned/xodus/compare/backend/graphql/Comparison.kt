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

public final class Comparison extends AbstractEntityBasedNode {
    public Comparison(Entity entity) {
        super(entity);
    }

    @GraphQLField @GraphQLNonNull
    public long getLocalId() {
        return entity.getId().getLocalId();
    }

    @GraphQLField @GraphQLNonNull
    public String getOldStoreDir() {
        return (String)entity.getProperty(ComparisonStoreNames.Comparison.OLD_STORE_DIR);
    }

    @GraphQLField @GraphQLNonNull
    public String getOldStoreName() {
        return (String)entity.getProperty(ComparisonStoreNames.Comparison.OLD_STORE_NAME);
    }

    @GraphQLField @GraphQLNonNull
    public String getNewStoreDir() {
        return (String)entity.getProperty(ComparisonStoreNames.Comparison.NEW_STORE_DIR);
    }

    @GraphQLField @GraphQLNonNull
    public String getNewStoreName() {
        return (String)entity.getProperty(ComparisonStoreNames.Comparison.NEW_STORE_NAME);
    }

    @GraphQLField @GraphQLNonNull
    public long getDate() {
        return (long)entity.getProperty(ComparisonStoreNames.Comparison.DATE);
    }

    @GraphQLField @GraphQLNonNull
    public long getOldEntityCount() {
        return (long)entity.getProperty(ComparisonStoreNames.Comparison.OLD_ENTITY_COUNT);
    }

    @GraphQLField @GraphQLNonNull
    public long getNewEntityCount() {
        return (long)entity.getProperty(ComparisonStoreNames.Comparison.NEW_ENTITY_COUNT);
    }

    @GraphQLField @GraphQLNonNull
    public long getOldEntitiesProcessed() {
        return (long)entity.getProperty(ComparisonStoreNames.Comparison.OLD_ENTITIES_PROCESSED);
    }

    @GraphQLField @GraphQLNonNull
    public long getNewEntitiesProcessed() {
        return (long)entity.getProperty(ComparisonStoreNames.Comparison.NEW_ENTITIES_PROCESSED);
    }

    @GraphQLField @GraphQLNonNull
    public List<@GraphQLNonNull EntityType> getEntityTypes() {
        List<EntityType> result = new ArrayList<>();

        for (Entity entityType : getEntityTypeIterable()) {
            EntityType entity = new EntityType(entityType);
            if (entity.getAddedEntities(null, null).getTotalCount() > 0
                || entity.getChangedEntities(null, null).getTotalCount() > 0
                || entity.getDeletedEntities(null, null).getTotalCount() > 0
            ) {
                result.add(entity);
            }
        }

        Collections.sort(result, Comparator.comparing(type -> {
            String newName = type.getNewName();
            return newName != null ? newName : type.getOldName();
        }));

        return result;
    }

    @GraphQLField @GraphQLNonNull
    public long addedEntityCount() {
        long count = 0;
        for (Entity entityType : getEntityTypeIterable()) {
            count += new EntityType(entityType).getAddedEntities(null, null).getTotalCount();
        }
        return count;
    }

    @GraphQLField @GraphQLNonNull
    public long changedEntityCount() {
        long count = 0;
        for (Entity entityType : getEntityTypeIterable()) {
            count += new EntityType(entityType).getChangedEntities(null, null).getTotalCount();
        }
        return count;
    }

    @GraphQLField @GraphQLNonNull
    public long deletedEntityCount() {
        long count = 0;
        for (Entity entityType : getEntityTypeIterable()) {
            count += new EntityType(entityType).getDeletedEntities(null, null).getTotalCount();
        }
        return count;
    }

    private EntityIterable getEntityTypeIterable() {
        StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();
        return txn.findLinks(ComparisonStoreNames.ENTITY_TYPE, entity, ComparisonStoreNames.EntityType.COMPARISON);
    }
}
