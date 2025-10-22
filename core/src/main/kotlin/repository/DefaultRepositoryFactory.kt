package repository

import exception.RepositoryException
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

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
    override fun <R : KtorRepository<*, *>> createRepository(
        repositoryClass: KClass<R>,
        context: RepositoryContext
    ): R {
        val constructor = repositoryClass.primaryConstructor
            ?: throw RepositoryException("No primary constructor found for ${repositoryClass.simpleName}")

        return constructor.call(context)
    }
}