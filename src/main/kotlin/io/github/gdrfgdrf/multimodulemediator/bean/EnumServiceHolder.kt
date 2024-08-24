package io.github.gdrfgdrf.multimodulemediator.bean

import io.github.gdrfgdrf.multimodulemediator.annotation.EnumServiceImpl
import io.github.gdrfgdrf.multimodulemediator.enums.Operations

class EnumServiceHolder(clazz: Class<*>, implClass: Class<*>) : ServiceHolder(clazz, implClass) {
    override fun initialize() {
        singleton = false
        singletonInstance = null
    }

    override fun get(operations: Operations, argumentSet: ArgumentSet?): Any? {
        if (operations == Operations.GET) {
            return null
        }

        if (argumentSet == null && operations == Operations.VALUES) {
            val declaredMethod = implClass.getDeclaredMethod("values")
            return declaredMethod.invoke(null)
        }
        if (argumentSet != null && operations == Operations.VALUE_OF) {
            val name = argumentSet.args[0] as String

            val valueOf = implClass.getDeclaredMethod("valueOf", java.lang.String::class.java)
            val any = valueOf.invoke(null, name)

            return any
        }
        if (argumentSet != null && operations == Operations.SEARCH) {
            val enumServiceImpl = implClass.getAnnotation(EnumServiceImpl::class.java)
            val searcher = enumServiceImpl.searcher
            if (searcher.isBlank()) {
                return null
            }

            val name = argumentSet.args[0] as String
            val searcherMethod = implClass.getDeclaredMethod(searcher, java.lang.String::class.java)
            val any = searcherMethod.invoke(null, name)

            return any
        }
        return null
    }
}