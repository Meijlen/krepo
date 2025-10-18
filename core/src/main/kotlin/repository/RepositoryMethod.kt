package repository

import kotlin.reflect.KFunction

data class RepositoryMethod(
    val function: KFunction<*>,
    val isSuspend: Boolean,
)
