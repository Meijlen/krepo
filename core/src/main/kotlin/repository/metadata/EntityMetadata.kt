package repository.metadata

import kotlin.reflect.KClass

/**
 * Complete metadata descriptor for an entity class.
 *
 * This class contains all information needed to map a Kotlin data class to a database table,
 * including the table name and all column mappings. It serves as the central metadata store
 * used throughout the repository framework.
 *
 * Used by:
 * - [DataAccessor] implementations to perform database operations with proper table/column names
 * - [CrudRepositoryDelegate] to pass entity structure information to the data accessor
 * - [RepositoryInvocationHandler] to resolve custom query method field references
 * - Schema generation tools to create/migrate database tables
 *
 * Example usage:
 * ```kotlin
 * val userMetadata = EntityMetadata(
 *     entityClass = User::class,
 *     tableName = "users",
 *     columns = listOf(
 *         ColumnProperty("id", Long::class),
 *         ColumnProperty("name", String::class).length(255),
 *         ColumnProperty("email", String::class).unique()
 *     )
 * )
 * ```
 *
 * @property entityClass the Kotlin class that represents this entity
 * @property tableName the database table name this entity maps to
 * @property columns list of all column mappings for this entity (empty list if not configured)
 */
data class EntityMetadata(
    val entityClass: KClass<*>,
    val tableName: String,
    val columns: List<ColumnProperty> = emptyList(),
)
