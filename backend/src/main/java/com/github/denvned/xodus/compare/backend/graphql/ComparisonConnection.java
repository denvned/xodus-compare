package com.github.denvned.xodus.compare.backend.graphql;

import com.github.denvned.graphql.PageInfo;
import com.github.denvned.graphql.annotations.GraphQLField;
import com.github.denvned.graphql.annotations.GraphQLNonNull;
import com.github.denvned.xodus.compare.ComparisonStoreNames;
import com.github.denvned.xodus.compare.backend.ComparisonStoreProvider;
import jetbrains.exodus.entitystore.Entity;
import jetbrains.exodus.entitystore.EntityIterable;
import jetbrains.exodus.entitystore.StoreTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ComparisonConnection {
    private final Integer first;
    private final Long after;
    private EntityIterable comparisons;
    private boolean hasPrev;
    private boolean hasNext;

    public ComparisonConnection(Integer first, Long after) {
        this.first = first;
        this.after = after;
    }

    private void init() {
        if (comparisons == null) {
            StoreTransaction txn = ComparisonStoreProvider.getStore().getCurrentTransaction();

            EntityIterable all = txn.getAll(ComparisonStoreNames.COMPARISON);

            long count;
            if (after != null) {
                comparisons = txn.findIds(ComparisonStoreNames.COMPARISON, 0, after - 1);
                count = comparisons.size();
                if (count < all.size()) {
                    hasPrev = true;
                }
            } else {
                comparisons = all;
                count = comparisons.size();
            }

            if (first != null) {
                if (first < count) {
                    comparisons = comparisons.skip((int)Math.min(count - first, Integer.MAX_VALUE));
                    hasNext = true;
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
    public List<@GraphQLNonNull ComparisonEdge> getEdges() {
        init();

        List<ComparisonEdge> edges = new ArrayList<>();

        for (Entity comparison : comparisons) {
            edges.add(new ComparisonEdge(comparison));
        }

        Collections.reverse(edges);
        return edges;
    }
}
