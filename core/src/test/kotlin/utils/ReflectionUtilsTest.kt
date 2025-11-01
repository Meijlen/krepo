package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

annotation class IdAnnotation
annotation class ColumnAnnotation

class TestData(
    @IdAnnotation val id: Int,
    @ColumnAnnotation val name: String,
    val description: String,
    @IdAnnotation @ColumnAnnotation val combo: String
)

class ReflectionUtilsTest {

    @Test
    fun `should return only properties annotated with IdAnnotation`() {
        val kClass: KClass<*> = TestData::class

        val properties = ReflectionUtils.getAnnotatedProperties(kClass, IdAnnotation::class)

        assertEquals(2, properties.size)

        val names = properties.map { it.name }.toSet()
        assertTrue(names.contains("id"))
        assertTrue(names.contains("combo"))
        assertTrue(names.none { it == "name" || it == "description" })
    }

    @Test
    fun `should return only properties annotated with ColumnAnnotation`() {
        val kClass: KClass<*> = TestData::class

        val properties = ReflectionUtils.getAnnotatedProperties(kClass, ColumnAnnotation::class)

        assertEquals(2, properties.size)

        val names = properties.map { it.name }.toSet()
        assertTrue(names.contains("name"))
        assertTrue(names.contains("combo"))
    }
}