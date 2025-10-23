package query

/**
 * Represents the type of query action to perform on a repository.
 *
 * These actions correspond to the prefix of repository method names
 * and determine the type of database operation to execute.
 */
enum class QueryAction {
    FIND,
    DELETE,
    COUNT,
    EXISTS,
    UPDATE,
}