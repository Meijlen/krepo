package query

/**
 * Represents comparison and logical operators used in query conditions.
 *
 * These operators are used to build dynamic queries from repository method names.
 */
enum class Operator {
    EQ, NE,
    GT, LT,
    GTE, LTE,
    LIKE, NOT_LIKE,
    IN, NOT_IN,
    BETWEEN,
    IS_NULL, IS_NOT_NULL,
}