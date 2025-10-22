package repository

import kotlin.reflect.KClass

/**
 * Factory for creating repositories.
 * Responsible for binding the repository interface to a specific implementation.
 */
interface RepositoryFactory {

    /**
     * Creates a repository for the specified interface.
     * Context and generic types will be retrieved via reflection inside the factory.
     */
    fun <R : KtorRepository<*, *>> createRepository(
        repositoryClass: KClass<R>,
        context: RepositoryContext
    ): R
}