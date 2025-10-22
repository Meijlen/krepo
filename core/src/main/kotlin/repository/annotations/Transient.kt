package repository.annotations

/**
 * Marks a property as transient, excluding it from database persistence.
 * Transient properties are not mapped to any database column and are ignored
 * during save/update operations.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Transient