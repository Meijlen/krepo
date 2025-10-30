package repository

import repository.access.DataAccessor
import repository.metadata.EntityMetadata
import utils.ReflectionUtils

class CrudRepositoryDelegate<E : Any, ID : Any>(
    private val dataAccessor: DataAccessor<E, ID>,
    private val metadata: EntityMetadata
) : CrudRepository<E, ID> {

    override suspend fun findAll(): List<E> {
        return dataAccessor.findAll(metadata)
    }

    override suspend fun findById(id: ID): E? {
        return dataAccessor.findById(id, metadata)
    }

    override suspend fun save(entity: E): E {
        return dataAccessor.save(entity, metadata)
    }

    override suspend fun delete(entity: E): Boolean {
        val id = ReflectionUtils.getIdValue(entity) as? ID
            ?: throw IllegalStateException("Cannot delete entity ${entity::class.simpleName}: ID value is null or wrong type.")

        return deleteById(id)
    }

    override suspend fun deleteById(id: ID): Boolean {
        // Делегируем DataAccessor
        return try {
            dataAccessor.deleteById(id, metadata)
            true
        } catch (e: Exception) {
            // Обработка ошибок
            false
        }
    }
}