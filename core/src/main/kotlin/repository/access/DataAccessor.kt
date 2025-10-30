package repository.access

import query.ParsedMethod
import repository.metadata.EntityMetadata

interface DataAccessor<E: Any, ID: Any> {

    suspend fun findById(id: ID, metadata: EntityMetadata): E?

    suspend fun save(entity: E, metadata: EntityMetadata): E

    suspend fun deleteById(id: ID, metadata: EntityMetadata)

    fun executeQuery(
        parsedMethod: ParsedMethod,
        args: List<Any>?,
        metadata: EntityMetadata
    ): List<E>

    suspend fun findAll(metadata: EntityMetadata): List<E>
}