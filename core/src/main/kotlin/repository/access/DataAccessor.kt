package repository.access

import query.ParsedMethod
import repository.metadata.EntityMetadata

/**
 * Core interface for low-level data access operations.
 *
 * DataAccessor serves as the bridge between the repository layer and the actual data source
 * (database, cache, file system, etc.). It handles both basic CRUD operations and complex
 * custom queries parsed from repository method names.
 *
 * This interface is used by:
 * - [CrudRepositoryDelegate] for standard CRUD operations (findAll, findById, save, delete)
 * - [RepositoryInvocationHandler] for executing custom query methods (e.g., findByName, deleteByStatus)
 *
 * All methods are suspend functions to support asynchronous, non-blocking data access.
 *
 * @param E the entity type that this accessor manages
 * @param ID the type of the entity's primary key
 */
interface DataAccessor<E: Any, ID: Any> {

    /**
     * Retrieves a single entity by its primary key.
     *
     * @param id the primary key value to search for
     * @param metadata entity metadata containing table name, column mappings, and ID field information
     * @return the entity if found, null if no entity exists with the given ID
     */
    suspend fun findById(id: ID, metadata: EntityMetadata): E?

    /**
     * Persists or updates an entity in the data source.
     *
     * Used by [CrudRepositoryDelegate.save] to handle both insert and update operations.
     * The implementation should:
     * - Insert a new record if the entity has no ID or the ID doesn't exist
     * - Update the existing record if the ID already exists
     * - Return the entity with any auto-generated values (e.g., database-generated IDs)
     *
     * @param entity the entity to save or update
     * @param metadata entity metadata containing table name, column mappings, and ID field information
     * @return the saved entity, potentially with updated fields (e.g., auto-generated ID)
     */
    suspend fun save(entity: E, metadata: EntityMetadata): E

    /**
     * Deletes an entity by its primary key.
     *
     * @param id the primary key of the entity to delete
     * @param metadata entity metadata containing table name, column mappings, and ID field information
     * @return true if the entity was successfully deleted, false if no entity with the given ID exists
     */
    suspend fun deleteById(id: ID, metadata: EntityMetadata): Boolean

    /**
     * Executes a custom query parsed from a repository method name.
     *
     * Used exclusively by [RepositoryInvocationHandler] to handle custom query methods like:
     * - findByName(name: String)
     * - findByStatusAndAge(status: String, age: Int)
     * - deleteByCreatedDateBefore(date: LocalDate)
     *
     * The implementation should:
     * - Translate the parsed method into the appropriate data source query
     * - Bind the provided arguments to the query parameters
     * - Execute the query and return the results
     *
     * @param parsedMethod the parsed method metadata containing operation type, field conditions, and sorting
     * @param args the method arguments to bind to the query, in the order they appear in the method signature
     * @param metadata entity metadata containing table name, column mappings, and ID field information
     * @return a list of entities matching the query criteria, or an empty list if no matches found
     */
    suspend fun executeQuery(
        parsedMethod: ParsedMethod,
        args: List<Any>?,
        metadata: EntityMetadata
    ): List<E>

    /**
     * Retrieves all entities from the data source.
     *
     * @param metadata entity metadata containing table name, column mappings, and ID field information
     * @return a list of all entities, or an empty list if none exist
     */
    suspend fun findAll(metadata: EntityMetadata): List<E>
}