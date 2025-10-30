package repository

import repository.access.DataAccessor
import repository.metadata.EntityMetadata
import utils.ReflectionUtils

/**
 * Default implementation of CrudRepository that delegates operations to a DataAccessor.
 *
 * This class acts as a bridge between the repository interface and the underlying
 * data access layer. It handles the conversion between high-level repository operations
 * and low-level data accessor calls.
 *
 * @param E the entity type managed by this repository
 * @param ID the type of the entity's primary key
 * @param dataAccessor the underlying data accessor that performs actual database operations
 * @param metadata metadata describing the entity structure and mappings
 */
class CrudRepositoryDelegate<E : Any, ID : Any>(
    private val dataAccessor: DataAccessor<E, ID>,
    private val metadata: EntityMetadata
) : CrudRepository<E, ID> {

    /**
     * Retrieves all entities from the data source.
     *
     * @return a list containing all entities, or an empty list if none exist
     */
    override suspend fun findAll(): List<E> {
        return dataAccessor.findAll(metadata)
    }

    /**
     * Finds a single entity by its primary key.
     *
     * @param id the primary key of the entity to find
     * @return the entity if found, null otherwise
     */
    override suspend fun findById(id: ID): E? {
        return dataAccessor.findById(id, metadata)
    }

    /**
     * Persists or updates an entity in the data source.
     *
     * If the entity has no ID or the ID doesn't exist in the database,
     * a new record is created. Otherwise, the existing record is updated.
     *
     * @param entity the entity to save
     * @return the saved entity, potentially with an updated ID if it was newly created
     */
    override suspend fun save(entity: E): E {
        return dataAccessor.save(entity, metadata)
    }

    /**
     * Deletes the given entity from the data source.
     *
     * Extracts the entity's ID using reflection and delegates to deleteById.
     *
     * @param entity the entity to delete
     * @return true if the deletion was successful, false if the entity had no ID
     *         or the deletion failed
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun delete(entity: E): Boolean {
        val id = ReflectionUtils.getIdValue(entity) as? ID
            ?: return false

        return deleteById(id)
    }

    /**
     * Deletes an entity by its primary key.
     *
     * @param id the primary key of the entity to delete
     * @return true if the deletion was successful, false if an exception occurred
     */
    override suspend fun deleteById(id: ID): Boolean {
        return try {
            dataAccessor.deleteById(id, metadata)
            true
        } catch (e: Exception) {
            false
        }
    }
}