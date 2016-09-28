package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.Node;
import com.github.denvned.graphql.annotations.*;
import graphql.relay.Relay;

import javax.inject.Inject;

@QueryProvider
public final class Queries {
    @GraphQLField @GraphQLNonNull
    public final Viewer viewer;

    @Inject
    public Queries(Viewer viewer) {
        this.viewer = viewer;
    }

    @GraphQLField
    public Node getNode(@GraphQLID @GraphQLName("id") @GraphQLNonNull String id) {
        Relay.ResolvedGlobalId globalId = new Relay().fromGlobalId(id);
        String type = globalId.getType();
        long localId = Long.parseLong(globalId.getId());

        if (type.equals(Viewer.class.getSimpleName())) {
            return viewer;
        }
        if (type.equals(Comparison.class.getSimpleName())) {
            return viewer.getComparison(localId);
        }
        if (type.equals(EntityType.class.getSimpleName())) {
            return viewer.getEntityType(localId);
        }
        if (type.equals(AddedEntity.class.getSimpleName())) {
            return viewer.getAddedEntity(localId);
        }
        if (type.equals(ChangedEntity.class.getSimpleName())) {
            return viewer.getChangedEntity(localId);
        }
        if (type.equals(DeletedEntity.class.getSimpleName())) {
            return viewer.getDeletedEntity(localId);
        }
        if (type.equals(LinkTargetType.class.getSimpleName())) {
            return viewer.getLinkTargetType(localId);
        }

        throw new IllegalArgumentException("Unknown node type");
    }
}
