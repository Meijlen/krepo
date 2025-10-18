package repository


/**
 * Base interface for all repositories in the Ktor Repository API.
 *
 * @param E entity type (Entity)
 * @param ID identifier type (Primary Key)
 *
 * Analogous to JpaRepository in Spring, but abstracted from a specific ORM.
 */
interface KtorRepository<E : Any, ID : Any>