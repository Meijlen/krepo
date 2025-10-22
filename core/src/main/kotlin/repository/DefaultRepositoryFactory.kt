package repository

import exception.RepositoryException
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

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