package repository

import repository.access.DataAccessor

/**
 * Configuration for RepositoryContext.
 * Allows you to set default strategies, logging, and factories.
 */
data class RepositoryConfig(

    /**
     * The default factory that will be used to create repositories
     * if no specific factory is specified.
     */
    val defaultFactory: RepositoryFactory? = null,

    /**
     * Strategy for generating names for fields and tables.
     * For example, from camelCase to snake_case.
     */
    val namingStrategy: ((String) -> String?)? = null,

    /**
     * Logger for context and factory operations.
     * You can pass your own implementation.
     */
    val logger: ((String) -> Unit)? = null,

    /**
     * Whether to throw an exception when attempting to register
     * an already existing repository.
     * If true, duplicate registration will cause an error.
     * If false, the existing repository will be skipped.
     */
    val strictRegistration: Boolean = false,

    /**
     * Enables debug mode with additional logging and diagnostic information.
     */
    val debug: Boolean = false
)