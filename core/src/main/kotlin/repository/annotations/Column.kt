package repository.annotations

/**
 * Specifies column configuration for entity properties.
 *
 * @property name The column name in the database.
 *                If empty, the property name will be used.
 * @property nullable Whether the column allows NULL values.
 * @property unique Whether the column should have a UNIQUE constraint.
 * @property defaultValue Default value for the column (as SQL expression).
 * @property length Maximum length for string/varchar columns (0 = no limit).
 * @property precision Total number of digits for numeric columns.
 * @property scale Number of digits after decimal point for numeric columns.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(
    val name: String = "",
    val nullable: Boolean = false,
    val unique: Boolean = false,
    val defaultValue: String = "",
    val length: Int = 0,
    val precision: Int = 0,
    val scale: Int = 0,
)
