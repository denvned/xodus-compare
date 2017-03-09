package com.github.denvned.xodus.compare

object ComparisonStoreNames {
  const val COMPARISON = "Comparison"
  const val ENTITY_TYPE = "EntityType"
  const val ADDED_ENTITY = "AddedEntity"
  const val CHANGED_ENTITY = "ChangedEntity"
  const val DELETED_ENTITY = "DeletedEntity"
  const val PROPERTY = "Property"
  const val BLOB = "Blob"
  const val LINK = "Link"
  const val LINK_TARGET_TYPE = "LinkTargetType"
  const val ADDED_LINK_TARGET = "AddedLinkTarget"
  const val DELETED_LINK_TARGET = "DeletedLinkTarget"

  object Comparison {
    const val OLD_STORE_DIR = "oldStoreDir"
    const val OLD_STORE_NAME = "oldStoreName"
    const val NEW_STORE_DIR = "newStoreDir"
    const val NEW_STORE_NAME = "newStoreName"
    const val DATE = "date"
    const val OLD_ENTITY_COUNT = "oldEntityCount"
    const val NEW_ENTITY_COUNT = "newEntityCount"
    const val OLD_ENTITIES_PROCESSED = "oldEntitiesProcessed"
    const val NEW_ENTITIES_PROCESSED = "newEntitiesProcessed"
  }

  object EntityType {
    const val COMPARISON = "comparison"
    const val ID = "id"
    const val NEW_NAME = "newName"
    const val OLD_NAME = "oldName"
  }

  object Entity {
    const val TYPE = "type"
    const val ID = "id"
  }

  object Property {
    const val ENTITY = "entity"
    const val NAME = "name"
    const val OLD_VALUE = "oldValue"
    const val NEW_VALUE = "newValue"
  }

  object Blob {
    const val ENTITY = "entity"
    const val NAME = "name"
    const val OLD_VALUE = "oldValue"
    const val NEW_VALUE = "newValue"
  }

  object Link {
    const val ENTITY = "entity"
    const val NAME = "name"
  }

  object LinkTargetType {
    const val LINK = "link"
    const val ENTITY_TYPE = "entityType"
  }

  object LinkTarget {
    const val TYPE = "type"
    const val ENTITY_ID = "entityId"
  }
}
