package com.github.denvned.graphql;

import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;

public final class PageInfo {
    @GraphQLField @GraphQLNonNull
    public final boolean hasPreviousPage;

    @GraphQLField @GraphQLNonNull
    public final boolean hasNextPage;

    public PageInfo(boolean hasPreviousPage, boolean hasNextPage) {
        this.hasPreviousPage = hasPreviousPage;
        this.hasNextPage = hasNextPage;
    }
}
