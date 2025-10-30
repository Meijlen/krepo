package repository.metadata

import kotlin.reflect.KClass

/**
 * Describes a single column/property mapping in an entity.
 *
 * This class holds metadata about how a Kotlin property maps to a database column,
 * including type information, constraints, and database-specific attributes.
 *
 * Used by:
 * - [EntityMetadata] to store the complete column structure of an entity
 * - [DataAccessor] implementations to generate proper SQL DDL and DML statements
 * - Query builders to construct type-safe queries with proper column references
 *
 * @property name the column name in the database (may differ from the property name)
 * @property type the Kotlin type of the property (e.g., String::class, Int::class)
 * @property nullable whether the column accepts NULL values (default: false for NOT NULL)
 * @property unique whether the column has a UNIQUE constraint (default: false)
 * @property defaultValue the default value for the column if not provided (default: null)
 * @property length maximum length for string/varchar columns (default: null for database default)
 * @property precision total number of digits for decimal/numeric columns (default: null)
 * @property scale number of digits after decimal point for decimal/numeric columns (default: null)
 */
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
