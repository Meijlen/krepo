package repository

import exception.RepositoryException
import repository.access.DataAccessor

import java.lang.reflect.Proxy
import kotlin.reflect.KClass

/**
 * Default implementation of RepositoryFactory that creates dynamic proxy instances.
 *
 * This factory generates repository implementations at runtime using Java's Proxy mechanism.
 * Instead of requiring concrete repository classes, it creates proxy objects that intercept
 * method calls and route them to the appropriate handler.
 *
 * The factory performs the following steps:
 * 1. Retrieves metadata for the repository interface from the context
 * 2. Creates a DataAccessor instance for database operations
 * 3. Instantiates a CrudRepositoryDelegate for standard CRUD operations
 * 4. Creates a RepositoryInvocationHandler to route method calls
 * 5. Generates a dynamic proxy that implements the repository interface
 *
 * This approach allows for flexible repository definitions without boilerplate code,
 * supporting both predefined CRUD methods and custom query methods derived from
 * method names (e.g., findByEmail, deleteByStatus).
 */
class DefaultRepositoryFactory: RepositoryFactory {

    /**
     * Creates a repository instance for the given interface.
     *
     * The created repository is a dynamic proxy that implements all methods declared
     * in the repository interface. Base CRUD methods are handled by CrudRepositoryDelegate,
     * while custom query methods are parsed and executed by DataAccessor.
     *
     * @param R the repository interface type, must extend KtorRepository
     * @param repositoryClass the KClass representing the repository interface
     * @param context the repository context containing metadata and configuration
     * @return a proxy instance implementing the repository interface
     * @throws RepositoryException if metadata for the repository class is not found
     */
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