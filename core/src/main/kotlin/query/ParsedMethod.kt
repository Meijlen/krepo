package query


/**
 * Represents a parsed repository method with its action and conditions.
 *
 * This data class holds the structured representation of a repository method name
 * after parsing, containing the action to perform and all query conditions.
 *
 * @property action The query action to perform (FIND, DELETE, COUNT, etc.)
 * @property conditions List of query conditions extracted from the method name
 * @property logicalOperators List of logical operators (AND/OR) connecting the conditions
 *
 * @example
 * For method `findByNameAndAgeGreaterThan`:
 * ```
 * ParsedMethod(
 *   action = QueryAction.FIND,
 *   conditions = [
 *     QueryCondition("name", Operator.EQ, 0, LogicalOperator.AND),
 *     QueryCondition("age", Operator.GT, 1, LogicalOperator.AND)
 *   ],
 *   logicalOperators = [LogicalOperator.AND]
 * )
 * ```
 */
data class ParsedMethod(
    val action: QueryAction,
    val conditions: List<QueryCondition>,
    val logicalOperators: List<LogicalOperator> = emptyList(),
)
