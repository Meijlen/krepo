package query

import exception.QueryParseException

/**
 * Parser for repository method names that converts method names into structured query representations.
 *
 * Supports parsing method names in the format: `{action}[By{field}{operator}[And|Or{field}{operator}]*]`
 *
 * Example method names:
 * - `findByName` - finds entities where name equals the parameter
 * - `deleteByAgeGreaterThan` - deletes entities where age is greater than the parameter
 * - `countByStatusAndCreatedAtBetween` - counts entities matching both conditions
 */
object MethodNameParser {
    private val actionPrefixes = mapOf(
        "find" to QueryAction.FIND,
        "delete" to QueryAction.DELETE,
        "count" to QueryAction.COUNT,
        "exists" to QueryAction.EXISTS,
        "update" to QueryAction.UPDATE,
    )

    private val operatorSuffixes = mapOf(
        "GreaterThan" to Operator.GT,
        "GreaterThanEqual" to Operator.GTE,
        "LessThan" to Operator.LT,
        "LessThanEqual" to Operator.LTE,
        "NotIn" to Operator.NOT_IN,
        "In" to Operator.IN,
        "Between" to Operator.BETWEEN,
        "IsNull" to Operator.IS_NULL,
        "IsNotNull" to Operator.IS_NOT_NULL,
        "Like" to Operator.LIKE,
        "NotLike" to Operator.LIKE,
        "Not" to Operator.NE
    )

    /**
     * Parses a repository method name into a structured [ParsedMethod] object.
     *
     * @param methodName The method name to parse (e.g., "findByNameAndAgeGreaterThan")
     * @return A [ParsedMethod] containing the action and list of query conditions
     * @throws QueryParseException if the method name has an unknown or invalid action prefix
     *
     * @example
     * ```
     * val parsed = MethodNameParser.parse("findByEmailAndActiveTrue")
     * // Returns: ParsedMethod(action=FIND, conditions=[...])
     * ```
     */
    fun parse(methodName: String): ParsedMethod {
        val action = extractAction(methodName)
        val afterBy = methodName.substringAfter("By", missingDelimiterValue = "")

        if (afterBy.isEmpty()) {
            return ParsedMethod(action, emptyList())
        }

        val (tokens, logicalOperators) = splitByLogicalOperators(afterBy)

        val conditions = tokens.mapIndexed { index, token ->
            val (field, operator) = extractFieldAndOperator(token)
            val logical = when (index) {
                0 -> LogicalOperator.AND
                else -> logicalOperators.getOrElse(index - 1) { LogicalOperator.AND }
            }
            QueryCondition(
                field = field,
                operator = operator,
                parameterIndex = index,
                logical = logical
            )
        }

        return ParsedMethod(
            action = action,
            conditions = conditions,
            logicalOperators = logicalOperators,
        )
    }

    /**
     * Extracts the query action from the method name prefix.
     *
     * @param name The full method name
     * @return The corresponding [QueryAction]
     * @throws QueryParseException if no valid action prefix is found
     */
    private fun extractAction(name: String): QueryAction {
        val prefix = actionPrefixes.keys.firstOrNull { name.startsWith(it, ignoreCase = true) }
            ?: throw QueryParseException("Unknown repository method prefix in '$name'")

        return actionPrefixes[prefix]!!
    }

    /**
     * Splits a condition expression by logical operators (And/Or).
     *
     * @param expression The expression after "By" (e.g., "NameAndAgeGreaterThanOrStatus")
     * @return A pair of field tokens and their connecting logical operators
     */
    private fun splitByLogicalOperators(expression: String): Pair<List<String>, List<LogicalOperator>> {
        val result = mutableListOf<String>()
        val logicalOperators = mutableListOf<LogicalOperator>()

        val regex = Regex("And|Or")
        var lastIndex = 0

        regex.findAll(expression).forEach { match ->
            result += expression.substring(lastIndex, match.range.first)
            logicalOperators += if (match.value == "And") LogicalOperator.AND else LogicalOperator.OR
            lastIndex = match.range.last + 1
        }

        result += expression.substring(lastIndex)
        return result to logicalOperators
    }

    /**
     * Extracts the field name and operator from a condition token.
     *
     * If no operator suffix is found, defaults to equality (EQ).
     * Converts the first character of the field name to lowercase.
     *
     * @param part A single condition token (e.g., "AgeGreaterThan" or "Name")
     * @return A pair of field name and operator
     *
     * @example
     * Input: "AgeGreaterThan" -> ("age", Operator.GT)
     * Input: "Name" -> ("name", Operator.EQ)
     */
    private fun extractFieldAndOperator(part: String): Pair<String, Operator> {
        val operator = operatorSuffixes.entries.firstOrNull { part.endsWith(it.key) }?.value
        return if (operator != null) {
            val field = part.removeSuffix(operatorSuffixes.entries.first { it.value == operator }.key)
                .replaceFirstChar { it.lowercase() }
            field to operator
        } else {
            part.replaceFirstChar { it.lowercase() } to Operator.EQ
        }
    }

}