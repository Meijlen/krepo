package repository

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
)