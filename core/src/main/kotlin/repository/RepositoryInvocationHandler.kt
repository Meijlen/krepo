package repository

import repository.access.DataAccessor
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

/**
 * Dynamic proxy handler for repository interfaces.
 *
 * This handler intercepts method calls on repository proxy instances and routes them
 * to the appropriate implementation:
 * - Base CRUD methods (findAll, findById, save, delete, deleteById) are delegated to CrudRepositoryDelegate
 * - Custom query methods (e.g., findByName, deleteByStatus) are parsed and executed by DataAccessor
 * - Object methods (toString, equals, hashCode) are handled by this handler itself
 *
 * The handler supports only suspend functions, as all repository methods must be coroutine-safe.
 *
 * @param E the entity type managed by the repository
 * @param ID the type of the entity's primary key
 * @param metadata metadata about the repository interface including parsed method information
 * @param crudDelegate the delegate that handles standard CRUD operations
 * @param dataAccessor the data accessor that executes custom query methods
 */
@Suppress("UNCHECKED_CAST")
class RepositoryInvocationHandler<E: Any, ID: Any>(
    private val metadata: RepositoryMetadata,
    private val crudDelegate: CrudRepositoryDelegate<E, ID>,
    private val dataAccessor: DataAccessor<E, ID>
): InvocationHandler {

    /**
     * Handles method invocations on the repository proxy.
     *
     * Routing logic:
     * 1. If the method is from Any (Object), invoke it directly on this handler
     * 2. Extract the Continuation from the method arguments (last parameter for suspend functions)
     * 3. For base CRUD methods, delegate to crudDelegate
     * 4. For parsed custom methods, execute via dataAccessor
     * 5. Throw UnsupportedOperationException for unrecognized methods
     *
     * @param proxy the proxy instance the method was invoked on
     * @param method the Method instance corresponding to the interface method
     * @param args the arguments passed to the method call, including the Continuation
     * @return COROUTINE_SUSPENDED for suspend functions, or the actual result if available
     * @throws UnsupportedOperationException if the method is not a suspend function or cannot be handled
     */
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val nonNullArgs = args ?: emptyArray()

        if (method.declaringClass ==  Any::class.java) {
            return method.invoke(this, *nonNullArgs)
        }

        val continuation = nonNullArgs.lastOrNull() as? Continuation<Any?>
            ?: throw UnsupportedOperationException("Non-suspend methods not supported: ${method.name}")

        val wrappedContinuation = object : Continuation<Any?> {
            override val context: CoroutineContext get() = continuation.context
            override fun resumeWith(result: Result<Any?>) {
                continuation.resume(result)
            }
        }

        return invokeSuspend(wrappedContinuation) outer@{
            val arguments = nonNullArgs.dropLast(1)

            if(metadata.baseMethodNames.contains(method.name)) {
                val newArgs = arguments.toTypedArray() + wrappedContinuation
                try {
                    method.invoke(crudDelegate, *newArgs)
                } catch (exception: InvocationTargetException) {
                    throw exception.cause ?: exception
                }
            } else if(metadata.parsedMethods.containsKey(method.name)) {
                val parsedMethod = metadata.parsedMethods[method.name]!!

                dataAccessor.executeQuery(
                    parsedMethod,
                    arguments,
                    metadata.entityMetadata
                )
            } else {
                throw UnsupportedOperationException("Method ${method.name} is not valid repository method or cannot be handled.")
            }

        }

    }

    /**
     * Invokes a suspend block and handles the coroutine suspension protocol.
     *
     * This utility method properly handles the COROUTINE_SUSPENDED marker that indicates
     * the coroutine has suspended and will resume later. It also catches exceptions
     * and resumes the continuation with the exception.
     *
     * @param T the return type of the suspend block
     * @param continuation the continuation to resume when the block completes
     * @param block the suspend function to execute
     * @return COROUTINE_SUSPENDED if the coroutine suspended, or the actual result otherwise
     */
    private fun <T> invokeSuspend(
        continuation: Continuation<Any?>,
        block: suspend () -> T
    ): Any? {
        @Suppress("UNCHECKED_CAST")
        val suspendBlock = block as (Continuation<Any?>) -> Any?
        val result = try {
            suspendBlock(continuation)
        } catch (e: Exception) {
            continuation.resumeWithException(e)
            return COROUTINE_SUSPENDED
        }
        return if (result == COROUTINE_SUSPENDED) COROUTINE_SUSPENDED else result
    }
}

