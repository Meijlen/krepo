package query

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue


class MethodNameParserTest {

    @Test
    fun `when method name is simple find, then action should be find and no conditions`() {
        val methodName = "find"
        val parsedMethod = MethodNameParser.parse(methodName)
        assertEquals(QueryAction.FIND, parsedMethod.action)
        assertTrue(parsedMethod.conditions.isEmpty())
    }

    @Test
    fun `when method name contains By and condition, then conditions list must not be empty`() {
        val methodName = "findByUsername"
        val parsedMethod = MethodNameParser.parse(methodName)
        assertEquals(QueryAction.FIND, parsedMethod.action)
        assertEquals(1, parsedMethod.conditions.size)

        val condition = parsedMethod.conditions.first()
        assertEquals("username",condition.field)
        assertEquals(Operator.EQ, condition.operator)
    }

    @Test
    fun `when method name is complex with And and GreaterThan, it parses correctly`() {
        val methodName = "findByEmailAndAgeGreaterThan"

        val parsedMethod = MethodNameParser.parse(methodName)

        assertEquals(QueryAction.FIND, parsedMethod.action)
        assertEquals(2, parsedMethod.conditions.size)

        val emailCondition = parsedMethod.conditions[0]
        assertEquals("email", emailCondition.field)
        assertEquals(Operator.EQ, emailCondition.operator) // Предполагаем, что "By" означает EQUALS

        val ageCondition = parsedMethod.conditions[1]
        assertEquals("age", ageCondition.field)
        assertEquals(Operator.GT, ageCondition.operator)
        assertEquals(LogicalOperator.AND, ageCondition.logical) // Проверка, что после And стоит AND
    }

    @Test
    fun `when method name contains simple Or, then conditions are linked with OR`() {

        val methodName = "findByCityOrZipCode"

        val parsedMethod = MethodNameParser.parse(methodName)

        assertEquals(QueryAction.FIND, parsedMethod.action)
        assertEquals(2, parsedMethod.conditions.size)

        val cityCondition = parsedMethod.conditions[0]
        assertEquals("city", cityCondition.field)

        val zipCodeCondition = parsedMethod.conditions[1]
        assertEquals("zipCode", zipCodeCondition.field)

        assertEquals(LogicalOperator.OR, zipCodeCondition.logical)
    }

    @Test
    fun `when method name mixes And and Or sequentially, all links are parsed correctly`() {
        val methodName = "findByStatusAndAgeOrCity"

        val parsedMethod = MethodNameParser.parse(methodName)

        assertEquals(3, parsedMethod.conditions.size)

        val statusCondition = parsedMethod.conditions[0]
        assertEquals("status", statusCondition.field)

        val ageCondition = parsedMethod.conditions[1]
        assertEquals("age", ageCondition.field)
        assertEquals(LogicalOperator.AND, ageCondition.logical)

        val cityCondition = parsedMethod.conditions[2]
        assertEquals("city", cityCondition.field)
        assertEquals(LogicalOperator.OR, cityCondition.logical)
    }

    @Test
    fun `when method name includes Between and Like operators, the conditions are correctly parsed`() {
        val methodName = "findByDateBetweenAndDescriptionLike"

        val parsedMethod = MethodNameParser.parse(methodName)

        val dateCondition = parsedMethod.conditions.find { it.field == "date" }
        assertNotNull(dateCondition)
        assertEquals(Operator.BETWEEN, dateCondition!!.operator)

        val descCondition = parsedMethod.conditions.find { it.field == "description" }
        assertNotNull(descCondition)
        assertEquals(Operator.LIKE, descCondition!!.operator)
        assertEquals(LogicalOperator.AND, descCondition.logical)
    }
}