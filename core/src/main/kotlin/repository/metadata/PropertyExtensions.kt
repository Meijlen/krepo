package repository.metadata

/**
 * Sets the nullable constraint for a column.
 *
 * Creates a copy of the column property with the nullable flag updated.
 * Used in fluent column definition chains.
 *
 * Example:
 * ```kotlin
 * val column = ColumnProperty("email", String::class)
 *     .nullable()
 *     .length(255)
 * ```
 *
 * @param value true to allow NULL values, false for NOT NULL constraint (default: true)
 * @return a new ColumnProperty instance with updated nullable flag
 */
fun ColumnProperty.nullable(value: Boolean = true): ColumnProperty = copy(nullable = value)

/**
 * Sets the unique constraint for a column.
 *
 * Creates a copy of the column property with the unique flag updated.
 * Used in fluent column definition chains.
 *
 * Example:
 * ```kotlin
 * val column = ColumnProperty("username", String::class)
 *     .unique()
 *     .length(50)
 * ```
 *
 * @param value true to enforce UNIQUE constraint, false otherwise (default: true)
 * @return a new ColumnProperty instance with updated unique flag
 */
fun ColumnProperty.unique(value: Boolean = true): ColumnProperty = copy(unique = value)

/**
 * Sets the default value for a column.
 *
 * Creates a copy of the column property with the specified default value.
 * The default value is used when inserting records without providing this field.
 *
 * Example:
 * ```kotlin
 * val column = ColumnProperty("status", String::class)
 *     .default("active")
 * ```
 *
 * @param value the default value to use (must match the column type)
 * @return a new ColumnProperty instance with the specified default value
 */
fun ColumnProperty.default(value: Any?): ColumnProperty = copy(defaultValue = value)

/**
 * Sets the maximum length for string/varchar columns.
 *
 * Creates a copy of the column property with the specified length constraint.
 * Typically used for VARCHAR or CHAR column types.
 *
 * Example:
 * ```kotlin
 * val column = ColumnProperty("name", String::class)
 *     .length(255)
 * ```
 *
 * @param value the maximum length in characters
 * @return a new ColumnProperty instance with the specified length
 */
fun ColumnProperty.length(value: Int): ColumnProperty = copy(length = value)

/**
 * Sets the precision for decimal/numeric columns.
 *
 * Creates a copy of the column property with the specified precision (total number of digits).
 * Used together with [scale] to define DECIMAL or NUMERIC column types.
 *
 * Example:
 * ```kotlin
 * val column = ColumnProperty("price", BigDecimal::class)
 *     .precision(10)
 *     .scale(2)  // DECIMAL(10,2)
 * ```
 *
 * @param value the total number of digits (before and after decimal point)
 * @return a new ColumnProperty instance with the specified precision
 */
fun ColumnProperty.precision(value: Int): ColumnProperty = copy(precision = value)

/**
 * Sets the scale for decimal/numeric columns.
 *
 * Creates a copy of the column property with the specified scale (digits after decimal point).
 * Must be used together with [precision] to properly define DECIMAL or NUMERIC columns.
 *
 * Example:
 * ```kotlin
 * val column = ColumnProperty("amount", BigDecimal::class)
 *     .precision(15)
 *     .scale(4)  // DECIMAL(15,4)
 * ```
 *
 * @param value the number of digits after the decimal point
 * @return a new ColumnProperty instance with the specified scale
 */
fun ColumnProperty.scale(value: Int): ColumnProperty = copy(scale = value)