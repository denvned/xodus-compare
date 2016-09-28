package com.github.denvned.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLID;
import com.github.denvned.graphql.annotations.GraphQLNonNull;

public interface Node {
    @GraphQLField @GraphQLID @GraphQLNonNull
    String getId();
}
