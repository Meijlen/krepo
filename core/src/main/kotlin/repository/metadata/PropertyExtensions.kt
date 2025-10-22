package repository.metadata

fun ColumnProperty.nullable(value: Boolean = true): ColumnProperty = copy(nullable = value)

fun ColumnProperty.unique(value: Boolean = true): ColumnProperty = copy(nullable = value)

fun ColumnProperty.default(value: Any?): ColumnProperty = copy(defaultValue = value)

fun ColumnProperty.length(value: Int): ColumnProperty = copy(length = value)

fun ColumnProperty.precision(value: Int): ColumnProperty = copy(precision = value)

fun ColumnProperty.scale(value: Int): ColumnProperty = copy(scale = value)