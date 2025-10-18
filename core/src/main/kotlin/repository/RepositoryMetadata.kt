package repository

import kotlin.reflect.KClass

/**
 * Metadata about the repository.
 * Used by RepositoryContext and RepositoryFactory to create and manage repositories.
 */
data class RepositoryMetadata(

    /** Repository interface class (e.g., UserRepository::class) */
    val repositoryClass: KClass<*>,

    /** The entity class to which the repository is bound (e.g., User::class) */
    val entityClass: KClass<*>,

    /** Entity identifier class (e.g., Int::class or UUID::class) */
    val idClass: KClass<*>,

    /** Methods declared in the repository interface */
    val methods: List<RepositoryMethod> = emptyList(),


    /** Additional properties or annotations, if needed */
    val annotations: List<Annotation> = emptyList(),
)