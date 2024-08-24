package io.github.gdrfgdrf.multimodulemediator

import io.github.gdrfgdrf.multimodulemediator.bean.ArgumentSet
import io.github.gdrfgdrf.multimodulemediator.bean.EnumServiceHolder
import io.github.gdrfgdrf.multimodulemediator.bean.ServiceHolder
import io.github.gdrfgdrf.multimodulemediator.enums.Operations

object Mediator {
    private val map = HashMap<Class<*>, ServiceHolder>()

    fun register(clazz: Class<*>, implClass: Class<*>) {
        val serviceHolder = ServiceHolder(clazz, implClass)
        map[clazz] = serviceHolder
    }

    fun registerEnum(clazz: Class<*>, implClass: Class<*>) {
        val enumServiceHolder = EnumServiceHolder(clazz, implClass)
        map[clazz] = enumServiceHolder
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(clazz: Class<*>, argumentSet: ArgumentSet? = null): T? {
        val serviceHolder = map[clazz] ?: return null
        return serviceHolder.get(Operations.GET, argumentSet) as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> valueOf(clazz: Class<*>, name: String): T? {
        val serviceHolder = map[clazz] ?: return null
        return serviceHolder.get(Operations.VALUE_OF, ArgumentSet(arrayOf(name))) as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> values(clazz: Class<*>): T? {
        val serviceHolder = map[clazz] ?: return null
        return serviceHolder.get(Operations.VALUES, null) as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <E> search(clazz: Class<*>, name: String): List<E>? {
        val serviceHolder = map[clazz] ?: return null
        return serviceHolder.get(Operations.SEARCH, ArgumentSet(arrayOf(name))) as List<E>
    }
}