package repository

import repository.access.DataAccessor
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class RepositoryInvocationHandler<E: Any, ID: Any>(
    private val metadata: RepositoryMetadata,
    private val crudDelegate: CrudRepositoryDelegate<E, ID>,
    private val dataAccessor: DataAccessor<E, ID>
): InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        val arguments = args ?: emptyArray()

        if (method.declaringClass ==  Any::class.java) {
            return method.invoke(this, *arguments)
        }

        if(metadata.baseMethodNames.contains(method.name)) {
            return method.invoke(crudDelegate, *arguments)
        }

        if (metadata.parsedMethods.containsKey(method.name)) {
            val parsedMethod = metadata.parsedMethods[method.name]!!

            return dataAccessor.executeQuery(
                parsedMethod,
                arguments.toList(),
                metadata.entityMetadata
            )
        }

        throw UnsupportedOperationException("Method ${method.name} is not valid repository method or cannot be handled.")

    }
}