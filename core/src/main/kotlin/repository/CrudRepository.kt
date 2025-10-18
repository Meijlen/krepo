package repository

/**
 * Basic CRUD interface for all repositories.
 *
 * Provides standard operations for working with entities:
 * - read (find)
 * - save
 * - delete
 *
 * Does not depend on a specific ORM (Exposed, JDBC, etc.)
 *
 * @param E entity type
 * @param ID primary key type
 */
interface CrudRepository<E : Any, ID : Any> : KtorRepository<E, ID> {

    /**
     * Returns all entities.
     */
    suspend fun findAll(): List<E>

    /**
     * Finds an entity by its ID.
     */
    suspend fun findById(id: ID): E?

    /**
     * Saves or updates an entity.
     * Returns the saved entity (possibly with an updated ID).
     */
    suspend fun save(entity: E): E

    /**
     * Deletes an entity.
     * Returns true if the deletion was successful.
     */
    suspend fun delete(entity: E): Boolean

    /**
     * Deletes an entity by ID.
     * Returns true if the deletion was successful.
     */
    suspend fun deleteById(id: ID): Boolean
}