package repository

import kotlin.reflect.KFunction

/**
 * Wrapper over KFunction with additional metadata for repository method handling.
 *
 * This class encapsulates repository method information and provides support
 * for coroutine-based (suspend) functions, allowing the framework to properly
 * handle both blocking and non-blocking repository operations.
 *
 * @property function The Kotlin function reference from the repository interface.
 * @property isSuspend Whether this method is a suspend function (coroutine).
 *                     Suspend methods require special invocation handling.
 */
data class RepositoryMethod(
    val function: KFunction<*>,
    val isSuspend: Boolean,
)
