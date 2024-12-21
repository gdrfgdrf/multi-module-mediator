package io.github.gdrfgdrf.multimodulemediator.common

import io.github.gdrfgdrf.multimodulemediator.Mediator
import java.lang.reflect.ParameterizedType

open class SingletonInstance<T> {
    fun instance(): T = Mediator.get<T>(type)!!

    private val type: Class<T>
    init {
        val type = this::class.java.genericSuperclass
        if (type is ParameterizedType) {
            val rawType = type.rawType
            val actualTypeArguments = type.actualTypeArguments
            if (rawType is Class<*> && actualTypeArguments.isNotEmpty()) {
                this.type = actualTypeArguments[0] as Class<T>
            } else {
                throw UnsupportedOperationException()
            }
        } else {
            throw UnsupportedOperationException()
        }
    }
}