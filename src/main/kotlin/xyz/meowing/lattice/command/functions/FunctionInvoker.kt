package xyz.meowing.lattice.command.functions

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.ParameterizedType

/**
 * # FunctionInvoker
 *
 * Interface to simplify invoking functions, using Java reflection.
 *
 * @see LambdaInvoker
 * @author Stivais
 */
sealed interface FunctionInvoker<T> {

    /**
     * List of parameters from the function.
     */
    val parameters: List<Parameter<*>>

    /**
     * Invokes the function, with a list of arguments.
     *
     * This can error, due to incorrect input.
     */
    fun invoke(arguments: List<Any?>): T

    /**
     * [FunctionInvoker] implementation, that is used for anonymous [functions][Function], like lambdas.
     *
     * This class uses [MethodHandle] to invoke the function.
     *
     * It is recommended to use [FunctionInvoker.from] instead of directly initializing this class.
     */
    class LambdaInvoker<T> internal constructor(
        lambda: Function<T>,
        parameterTypes: Array<out Class<*>>
    ) : FunctionInvoker<T> {
        override val parameters: List<Parameter<*>>

        /**
         * Used for invoking the function.
         */
        private val mHandle: MethodHandle

        init {
            try {
                val lambdaClass = lambda.javaClass

                val invokeMethod = lambdaClass.declaredMethods.firstOrNull { method ->
                    !method.isSynthetic &&
                            !method.isBridge &&
                            method.name != "equals" &&
                            method.name != "hashCode" &&
                            method.name != "toString"
                } ?: throw IllegalStateException("No invoke method found in lambda")

                if (!invokeMethod.isAccessible) invokeMethod.isAccessible = true
                mHandle = MethodHandles.lookup().unreflect(invokeMethod).bindTo(lambda)

                if (parameterTypes.isEmpty()) {
                    val rawTypes = invokeMethod.parameterTypes
                    // Kotlin K2/IR may compile lambdas so the only non-bridge invoke method
                    // has erased Object types. Recover concrete types from the FunctionN
                    // generic interface that the lambda class still declares.
                    val actualTypes = if (rawTypes.isNotEmpty() && rawTypes.all { it == Any::class.java }) {
                        recoverFromFunctionInterface(lambdaClass, rawTypes.size)
                            ?: throw IllegalStateException(
                                "Cannot recover parameter types for lambda ${lambdaClass.name}. " +
                                "Interfaces: ${lambdaClass.genericInterfaces.toList()}. " +
                                "Use a typed runs { p: Type -> } overload instead of Function<Unit>."
                            )
                    } else {
                        rawTypes
                    }
                    parameters = actualTypes.mapIndexed { index, type ->
                        Parameter("param$index", type, false)
                    }
                } else {
                    parameters = parameterTypes.mapIndexed { index, type ->
                        Parameter("param$index", type, false)
                    }
                }
            } catch (e: Exception) {
                throw Exception("[Lattice-Commodore] Error creating Function Invoker.", e)
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun invoke(arguments: List<Any?>): T {
            return mHandle.invokeWithArguments(arguments) as T
        }

        private companion object {
            fun recoverFromFunctionInterface(lambdaClass: Class<*>, paramCount: Int): Array<Class<*>>? {
                for (iface in lambdaClass.genericInterfaces) {
                    val pt = iface as? ParameterizedType ?: continue
                    val raw = pt.rawType as? Class<*> ?: continue
                    if (!raw.name.startsWith("kotlin.jvm.functions.Function")) continue
                    val args = pt.actualTypeArguments
                    // FunctionN<P1,...,Pn,R> — last arg is return type
                    if (args.size != paramCount + 1) continue
                    return Array(paramCount) { i ->
                        when (val arg = args[i]) {
                            is Class<*> -> arg
                            is ParameterizedType -> arg.rawType as? Class<*> ?: Any::class.java
                            else -> Any::class.java
                        }
                    }
                }
                return null
            }
        }
    }

    companion object {
        /**
         * Creates a function invoker with explicit parameter types.
         * If no parameter types are provided, will attempt to extract them from the lambda's method signature.
         */
        fun <T> from(function: Function<T>, vararg parameterTypes: Class<*>): FunctionInvoker<T> {
            return LambdaInvoker(function, parameterTypes)
        }
    }
}