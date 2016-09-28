package com.github.denvned.xodus.compare.backend.graphql;

import jetbrains.exodus.entitystore.Entity;

public final class DeletedEntity extends AbstractEntity {
    public DeletedEntity(Entity entity) {
        super(entity);
    }
}
