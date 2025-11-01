package metadata

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import repository.annotations.Column
import repository.annotations.Entity
import repository.annotations.Id
import utils.ReflectionUtils

@Entity("products")
data class ProductEntity(

    @Column(
        name = "product_pk",
        nullable = false,
        unique = true
    )
    @Id
    val id: Int,


    @Column(
        name = "product_title",
        unique = false,
        defaultValue = "Untitled",
        length = 255,
        precision = 10,
        scale = 2
    )
    val title: String?,

    @Transient
    val temp: String = "test"
)

class EntityMetadataTest {

    @Test
    fun `metadata should correctly map all annotation parameters to ColumnProperty`() {
        val metadata = ReflectionUtils.extractEntityMetadata(ProductEntity::class)

        val titleProp = metadata.columns.find { it.kotlinProperty.name == "title" }
        assertNotNull(titleProp)

        val columnProperty = titleProp.columnMetadata// Предполагаем, что EntityMetadata хранит ColumnProperty

        assertEquals("product_title", columnProperty.name, "Column name from annotation is incorrect.")

        assertTrue(columnProperty.nullable, "Nullable must be true from annotation.")

        assertEquals(false, columnProperty.unique, "Unique must be false from annotation.")

        assertEquals("Untitled", columnProperty.defaultValue, "Default value mismatch.")

        assertEquals(255, columnProperty.length, "Length mismatch.")
        assertEquals(10, columnProperty.precision, "Precision mismatch.")
        assertEquals(2, columnProperty.scale, "Scale mismatch.")

        assertEquals(String::class, columnProperty.type, "Type must be String.")
    }
}