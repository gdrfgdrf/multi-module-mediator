package io.github.gdrfgdrf.multimodulemediator.bean

class EnumServiceHolder(clazz: Class<*>, implClass: Class<*>) : ServiceHolder(clazz, implClass) {
    init {
        singleton = false
        singletonInstance = null
    }

    override fun get(argumentSet: ArgumentSet?): Any {
        if (argumentSet == null) {
            val declaredMethod = implClass.getDeclaredMethod("values")
            return declaredMethod.invoke(null)
        }

        val name = argumentSet.args[0] as String

        val valueOf = implClass.getDeclaredMethod("valueOf", java.lang.String::class.java)
        val any = valueOf.invoke(null, name)

        return any
    }
}