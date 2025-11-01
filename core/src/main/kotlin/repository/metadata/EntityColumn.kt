package repository.metadata

import kotlin.reflect.KProperty1

data class EntityColumn(
    val kotlinProperty: KProperty1<out Any, *>,
    val columnMetadata: ColumnProperty,
    val isId: Boolean
)
