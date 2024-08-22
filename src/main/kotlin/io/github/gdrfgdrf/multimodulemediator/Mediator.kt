package io.github.gdrfgdrf.multimodulemediator

import io.github.gdrfgdrf.multimodulemediator.bean.ArgumentSet
import io.github.gdrfgdrf.multimodulemediator.bean.EnumServiceHolder
import io.github.gdrfgdrf.multimodulemediator.bean.ServiceHolder

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
        return serviceHolder.get(argumentSet) as T
    }
}