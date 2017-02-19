package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.Node;
import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLName;
import com.github.denvned.graphql.annotations.GraphQLNonNull;

import java.util.List;

@GraphQLName("Entity")
public interface EntityInterface extends Node {
    @GraphQLField @GraphQLNonNull
    EntityType getType();

    @GraphQLField @GraphQLNonNull
    long getLocalId();

    @GraphQLField @GraphQLNonNull
    long getEntityId();

    @GraphQLField @GraphQLNonNull
    List<@GraphQLNonNull Property> getProperties();

    @GraphQLField @GraphQLNonNull
    List<@GraphQLNonNull Blob> getBlobs();

    @GraphQLField @GraphQLNonNull
    List<@GraphQLNonNull Link> getLinks();

    @GraphQLField @GraphQLNonNull
    long getPropertyCount();

    @GraphQLField @GraphQLNonNull
    long getBlobCount();

    @GraphQLField @GraphQLNonNull
    long getLinkCount();
}
