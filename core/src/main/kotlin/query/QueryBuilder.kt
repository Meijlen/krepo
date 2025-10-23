package query

import repository.metadata.EntityMetadata

/**
 * Interface for building queries from parsed method metadata.
 *
 * Implementations of this interface are responsible for translating
 * parsed repository method information into actual database queries
 * specific to the underlying data store (SQL, NoSQL, etc.).
 */
interface QueryBuilder {

    /**
     * Builds a query from parsed method metadata and runtime arguments.
     *
     * @param entityMetadata Metadata about the entity being queried (table name, columns, etc.)
     * @param parsedMethod The parsed repository method containing action and conditions
     * @param args Runtime arguments passed to the repository method
     * @return A query object appropriate for the underlying data store
     *
     * @example
     * ```
     * val query = queryBuilder.buildQuery(
     *   entityMetadata = userMetadata,
     *   parsedMethod = ParsedMethod(QueryAction.FIND, conditions),
     *   args = arrayOf("John", 25)
     * )
     * ```
     */
    fun buildQuery(entityMetadata: EntityMetadata, parsedMethod: ParsedMethod, args: Array<Any?>): Any
}