package query


/**
 * Represents a single condition in a query with its field, operator, and parameter binding.
 *
 * Query conditions are extracted from repository method names and represent
 * individual WHERE clause predicates.
 *
 * @property field The entity field name to apply the condition to (e.g., "name", "age")
 * @property operator The comparison operator to use (EQ, GT, LIKE, etc.)
 * @property parameterIndex The index of the method parameter that provides the value for this condition
 * @property logical The logical operator connecting this condition to the previous one (AND/OR)
 *
 * @example
 * For method `findByNameAndAgeGreaterThan(name: String, age: Int)`:
 * ```
 * QueryCondition(
 *   field = "name",
 *   operator = Operator.EQ,
 *   parameterIndex = 0,  // maps to 'name' parameter
 *   logical = LogicalOperator.AND
 * )
 * ```
 */

data class QueryCondition(
    val field: String,
    val operator: Operator,
    val parameterIndex: Int,
    val logical: LogicalOperator = LogicalOperator.AND
)
