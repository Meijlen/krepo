package utils

import exception.RepositoryException
import repository.KtorRepository
import repository.RepositoryMethod
import repository.annotations.Column
import repository.annotations.Entity
import repository.annotations.Id
import repository.annotations.Transient
import repository.metadata.ColumnProperty
import repository.metadata.EntityMetadata
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

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

    /**
     * Returns all properties of the given [kClass] that are annotated with the specified [annotationClass].
     *
     * @param kClass the Kotlin class to inspect
     * @param annotationClass the annotation to search for
     * @return a list of [KProperty1] representing the annotated properties
     */
    fun getAnnotatedProperties(kClass: KClass<*>, annotationClass: KClass<out Annotation>): List<KProperty1<out Any, *>> {
        return kClass.memberProperties.filter { props ->
            props.annotations.any { it.annotationClass == annotationClass } }
    }

    /**
     * Extracts metadata from a given entity class annotated with [Entity].
     *
     * This function analyzes the class to determine:
     * - its table name (from the @Entity annotation or class name)
     * - all column properties (from @Column annotations, excluding @Transient)
     * - the ID property (from @Id, if present)
     *
     * @param entityClass the entity class to extract metadata from
     * @return an [EntityMetadata] object describing the entity
     * @throws RepositoryException if:
     *  - the class is missing an @Entity annotation
     *  - multiple @Id fields are found
     *  - a property type cannot be resolved
     */
    fun extractEntityMetadata(entityClass: KClass<*>): EntityMetadata {
        val entityAnn = entityClass.findAnnotation<Entity>()
            ?: throw RepositoryException("Missing @Entity annotation on ${entityClass.simpleName}")

        val tableName = entityAnn.tableName.ifEmpty { entityClass.simpleName!!.lowercase() }

        val columns = getAnnotatedProperties(entityClass, Column::class)
            .filterNot { it.annotations.any { a -> a.annotationClass == Transient::class } }
            .map { prop ->
                val colAnn = prop.findAnnotation<Column>()!!
                val name = colAnn.name.ifEmpty { prop.name }
                val type = prop.returnType.classifier as? KClass<*>
                    ?: throw RepositoryException("Cannot resolve type for property ${prop.name}")

                ColumnProperty(
                    name = name,
                    type = type,
                    nullable = colAnn.nullable,
                    unique = colAnn.unique,
                    defaultValue = colAnn.defaultValue.ifEmpty { null },
                    length = colAnn.length.takeIf {it > 0 },
                    precision = colAnn.precision.takeIf {it > 0},
                    scale = colAnn.scale.takeIf {it > 0},
                )
            }

        val idProps = getAnnotatedProperties(entityClass, Id::class)
        if (idProps.size > 1) {
            throw RepositoryException("Multiple @Id fields found in ${entityClass.simpleName}")
        }

        return EntityMetadata(
            entityClass = entityClass,
            tableName = tableName,
            columns = columns,
        )
    }

    fun getIdValue(entity: Any): Any? {
        val entityClass = entity::class

        val idProps = getAnnotatedProperties(entityClass, Id::class)

        if (idProps.size > 1) {
            throw RepositoryException("Multiple @Id fields found in ${entityClass.simpleName}")
        }

        val idProp = idProps.first()

        idProp.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        return (idProp as KProperty1<Any, *>).get(idProp)
    }
}
