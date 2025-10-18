package repository

import exception.RepositoryException
import utils.ReflectionUtils
import kotlin.reflect.KClass

/**
 * Central container for managing the lifecycle of repositories.
 * Independent of specific ORM implementations (Exposed, JDBC, etc.).
 * Stores factories, cache repositories, and metadata.
 */
class RepositoryContext(
    val config: RepositoryConfig,
    val  databaseContext: Any? = null,
) {
    private val factories: MutableList<RepositoryFactory> = mutableListOf()

    private val repositories: MutableMap<KClass<*>, KtorRepository<*, *>> = mutableMapOf()

    private val metadata: MutableMap<KClass<*>, RepositoryMetadata> = mutableMapOf()

    private var initialized: Boolean = false
    private var closed: Boolean = false

    /**
     * Registers the repository factory.
     */
    fun registerFactory(factory: RepositoryFactory) {
        factories.add(factory)
        config.logger?.invoke("Registered repository factory: ${factory::class.simpleName}")
    }


    /**
     * Registers the repository in the context.
     * Creates metadata and adds it to the cache.
     */
    fun registerRepository(repositoryClass: KClass<out KtorRepository<*, *>>) {
        if (repositories.containsKey(repositoryClass)) return

        val factory = config.defaultFactory ?: factories.firstOrNull()
            ?: throw RepositoryException("No factory available to create repository $repositoryClass")

        val repository = factory.createRepository(repositoryClass, this)
        repositories[repositoryClass] = repository

        val entityClass = ReflectionUtils.findEntityClass(repositoryClass)
        val idClass = ReflectionUtils.findIdClass(repositoryClass)
        val methods = ReflectionUtils.getRepositoryMethods(repositoryClass)
        val annotations = repositoryClass.annotations

        metadata[repositoryClass] = RepositoryMetadata(
            repositoryClass,
            entityClass,
            idClass,
            methods,
            annotations
        )

        config.logger?.invoke("Registered repository ${repositoryClass.simpleName} for entity ${entityClass.simpleName}")

    }

    /**
     * Gets an already created repository.
     * If it does not exist yet, creates it via the factory.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getRepository(repositoryClass: KClass<out KtorRepository<*, *>>): T {
        return repositories[repositoryClass] as? T ?: run {
            registerRepository(repositoryClass)
            repositories[repositoryClass] as T
        }
    }

    /**
     * Initialize context (e.g., lazy creation of repositories or configuration checks)
     */
    fun initialize() {
        if (initialized) return
        initialized = true
        config.logger?.invoke("RepositoryContext initialized")
    }

    /**
     * Context completion
     */
    fun close() {
        if (closed) return
        closed = true
        repositories.clear()
        factories.clear()
        metadata.clear()
        config.logger?.invoke("RepositoryContext closed")
    }
}