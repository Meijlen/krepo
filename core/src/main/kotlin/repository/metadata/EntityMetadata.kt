package repository.metadata

import kotlin.reflect.KClass

data class EntityMetadata(
    val entityClass: KClass<*>,
    val tableName: String,
    val columns: List<ColumnProperty> = emptyList(),
)
