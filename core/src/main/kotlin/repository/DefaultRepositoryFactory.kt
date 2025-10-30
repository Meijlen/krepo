package repository

import exception.RepositoryException
import repository.access.DataAccessor

import java.lang.reflect.Proxy
import kotlin.reflect.KClass

/**
 * Default implementation of RepositoryFactory.
 *
 * Creates repository instances by invoking their primary constructor with the provided context.
 * This factory assumes that all repository classes have a primary constructor that accepts
 * a single RepositoryContext parameter.
 *
 * @throws RepositoryException if the repository class has no primary constructor.
 */
class DefaultRepositoryFactory: RepositoryFactory {

    @Suppress("UNCHECKED_CAST")
    override fun <R : KtorRepository<*, *>> createRepository(
        repositoryClass: KClass<R>,
        context: RepositoryContext
    ): R {
        val metadata = context.metadata[repositoryClass]
            ?: throw RepositoryException("Metadata for ${repositoryClass.simpleName} not found")

        val dataAccessor = context.dataAccessorProvider(metadata) as DataAccessor<Any, Any>

        val crudDelegate = CrudRepositoryDelegate(
            dataAccessor,
            metadata.entityMetadata
        )

        val handler = RepositoryInvocationHandler(
            metadata,
            crudDelegate,
            dataAccessor
        )
        
        return Proxy.newProxyInstance(
            repositoryClass.java.classLoader,
            arrayOf(repositoryClass.java),
            handler
        ) as R
    }
}