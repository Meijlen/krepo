package repository.annotations

/**
 * Marks a class as a database entity.
 *
 * @property tableName The name of the database table.
 *                     If empty, the table name will be derived from the class name.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Entity(
    val tableName: String = ""
)