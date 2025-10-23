package query

/**
 * Represents logical operators used to combine multiple query conditions.
 *
 * These operators determine how conditions are joined in the WHERE clause
 * of a query.
 */
enum class LogicalOperator {
    /** Logical AND - all conditions must be true */
    AND,

    /** Logical OR - at least one condition must be true */
    OR
}