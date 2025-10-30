package repository

import exception.RepositoryException
import query.MethodNameParser
import query.ParsedMethod
import repository.access.DataAccessor
import utils.ReflectionUtils
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

/**
 * Central container for managing the lifecycle of repositories.
 * Independent of specific ORM implementations (Exposed, JDBC, etc.).
 * Stores factories, cache repositories, and metadata.
 */
class RepositoryContext(
    val config: RepositoryConfig,
    val dataAccessorProvider: (metadata: RepositoryMetadata) -> DataAccessor<*, *>,
) {
    private val factories: MutableList<RepositoryFactory> = mutableListOf()

    private val repositories: MutableMap<KClass<*>, KtorRepository<*, *>> = mutableMapOf()

    val metadata: MutableMap<KClass<*>, RepositoryMetadata> = mutableMapOf()

    private var initialized: Boolean = false
    private var closed: Boolean = false


    fun addFactory(factory: RepositoryFactory) {
        factories.add(factory)
    }

    fun removeFactory(factory: RepositoryFactory) {
        factories.remove(factory)
    }

    private val defaultFactory: RepositoryFactory?
        get() = config.defaultFactory ?: factories.firstOrNull()



    /**
     * Registers the repository in the context.
     * Creates metadata and adds it to the cache.
     */
    fun <R: KtorRepository<*,*>> registerRepository(repositoryClass: KClass<R>, factory: RepositoryFactory? = null) {
        if (repositories.containsKey(repositoryClass)) {
            val message = "Repository ${repositoryClass.simpleName} already registered!"
            if (config.strictRegistration) throw RepositoryException(message)
            else config.logger?.invoke("[RepositoryContext] $message — skipping.")
            return
        }

        val chosenFactory = factory ?: defaultFactory
            ?: throw RepositoryException("No factory available to create repository $repositoryClass")

        val repository = chosenFactory.createRepository(repositoryClass, this)
        repositories[repositoryClass] = repository

        val entityClass = ReflectionUtils.findEntityClass(repositoryClass)
        val idClass = ReflectionUtils.findIdClass(repositoryClass)
        val methods = ReflectionUtils.getRepositoryMethods(repositoryClass)
        val baseMethodNames = CrudRepository::class.declaredMemberFunctions.map { it.name }.toSet()
        val parsedMethods: Map<String, ParsedMethod> = methods.associate { repositoryMethod ->
            val methodName = repositoryMethod.function.name

            methodName to MethodNameParser.parse(methodName)
        }
        val annotations = repositoryClass.annotations
        val entityMetadata = ReflectionUtils.extractEntityMetadata(entityClass)

        metadata[repositoryClass] = RepositoryMetadata(
            repositoryClass,
            entityClass,
            idClass,
            methods,
            baseMethodNames,
            parsedMethods,
            annotations,
            entityMetadata
        )

        config.logger?.invoke("[RepositoryContext] Registered repository ${repositoryClass.simpleName} for entity ${entityClass.simpleName}")
    }

    /**
     * Gets an already created repository.
     * If it does not exist yet, creates it via the factory.
     */
    @Suppress("UNCHECKED_CAST")
    fun <R : KtorRepository<*, *>> getRepository(repositoryClass: KClass<R>, factory: RepositoryFactory? = null): R {
        repositories[repositoryClass]?.let { return it as R }

        registerRepository(repositoryClass, factory)

        return repositories[repositoryClass] as? R
            ?: throw RepositoryException("Failed to retrieve repository ${repositoryClass.simpleName}")
    }

    inline fun <reified R: KtorRepository<*, *>> getRepository(): R {
        return getRepository(R::class)
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