package utils

import exception.RepositoryException
import repository.KtorRepository
import repository.RepositoryMethod
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.memberProperties

/**
 * Utility object for reflection operations on repository classes.
 * Provides methods to extract generic type arguments and analyze repository methods.
 */
object ReflectionUtils {

    /**
     * Extracts the Entity type from a repository class.
     *
     * @param repositoryClass The repository class to analyze
     * @return The KClass representing the Entity type parameter
     * @throws RepositoryException if the Entity type cannot be resolved
     */
    fun findEntityClass(repositoryClass: KClass<out KtorRepository<*,*>>): KClass<*> {
        return getGenericTypeArguments(repositoryClass).first
    }

    /**
     * Extracts the ID type from a repository class.
     *
     * @param repositoryClass The repository class to analyze
     * @return The KClass representing the ID type parameter
     * @throws RepositoryException if the ID type cannot be resolved
     */
    fun findIdClass(repositoryClass: KClass<out KtorRepository<*, *>>): KClass<*> {
        return getGenericTypeArguments(repositoryClass).second
    }

    /**
     * Discovers custom query methods defined in a repository.
     * Filters methods by naming conventions (findBy*, deleteBy*, existBy*, countBy*)
     * and excludes base KtorRepository methods.
     *
     * @param repositoryClass The repository class to scan for custom methods
     * @return List of RepositoryMethod objects representing custom query methods
     */
    fun getRepositoryMethods(repositoryClass: KClass<out KtorRepository<*, *>>): List<RepositoryMethod> {
        // Collect base method names to exclude them from custom methods
        val baseMethods = KtorRepository::class.members.map { it.name }.toSet()

        return repositoryClass.declaredMemberFunctions
            .filter { function ->
                val name = function.name
                (name.startsWith("findBy") ||
                        name.startsWith("deleteBy") ||
                        name.startsWith("existBy") ||
                        name.startsWith("countBy")) &&
                        !baseMethods.contains(name)
            }
            .map { function ->
                RepositoryMethod(
                    function = function,
                    isSuspend = function.isSuspend,
                )
            }
    }

    /**
     * Resolves the Entity and ID type parameters from a repository class hierarchy.
     * Recursively searches through supertypes to find KtorRepository<Entity, ID> declaration.
     *
     * @param repositoryClass The repository class to analyze
     * @return Pair of (Entity KClass, ID KClass)
     * @throws RepositoryException if KtorRepository supertype is not found or type parameters cannot be resolved
     */
    fun getGenericTypeArguments(repositoryClass: KClass<out KtorRepository<*, *>>): Pair<KClass<*>, KClass<*>> {

        /**
         * Recursively searches for KtorRepository type in the type hierarchy.
         *
         * @param type The current type being examined
         * @return KType of KtorRepository if found, null otherwise
         */
        fun findKtorRepositoryType(type: KType): KType? {
            val classifier = type.classifier as? KClass<*> ?: return null
            if (classifier == KtorRepository::class) return type

            // Recursively search in supertypes
            return classifier.supertypes.firstNotNullOfOrNull { findKtorRepositoryType(it) }
        }

        // Find the KtorRepository type in the inheritance hierarchy
        val ktorRepoType = repositoryClass.supertypes.firstNotNullOfOrNull { findKtorRepositoryType(it) }
            ?: throw RepositoryException("KtorRepository supertype not found for ${repositoryClass.simpleName}")

        // Extract type arguments
        val arguments = ktorRepoType.arguments
        if (arguments.size < 2) {
            throw RepositoryException("KtorRepository must declare two type parameters: <Entity, ID>")
        }

        // Resolve Entity type
        val entityClass = arguments[0].type?.classifier as? KClass<*>
            ?: throw RepositoryException("Cannot resolve Entity type for ${repositoryClass.simpleName}")

        // Resolve ID type
        val idClass = arguments[1].type?.classifier as? KClass<*>
            ?: throw RepositoryException("Cannot resolve ID type for ${repositoryClass.simpleName}")

        return entityClass to idClass
    }

    fun getAnnotatedProperties(kClass: KClass<*>, annotationClass: KClass<out Annotation>): List<KProperty1<out Any, *>> {
        return kClass.memberProperties.filter { props ->
            props.annotations.any { it.annotationClass == annotationClass } }
    }
}
