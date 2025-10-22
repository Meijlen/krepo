package repository.metadata

import kotlin.reflect.KClass

data class ColumnProperty(
    val name: String,
    val type: KClass<*>,
    val nullable: Boolean = false,
    val unique: Boolean = false,
    val defaultValue: Any? = null,
    val length: Int? = null,
    val precision: Int? = null,
    val scale: Int? = null,
)
