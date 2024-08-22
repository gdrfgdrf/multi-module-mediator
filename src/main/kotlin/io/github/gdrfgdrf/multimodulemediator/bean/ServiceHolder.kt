package io.github.gdrfgdrf.multimodulemediator.bean

import io.github.gdrfgdrf.multimodulemediator.annotation.KotlinSingleton
import io.github.gdrfgdrf.multimodulemediator.annotation.Service
import io.github.gdrfgdrf.multimodulemediator.annotation.ServiceImpl

open class ServiceHolder(
    val clazz: Class<*>,
    val implClass: Class<*>
) {
    protected var singleton: Boolean = false
    protected var singletonInstance: Any? = null

    init {
        val service = clazz.getAnnotation(Service::class.java)
        this.singleton = service.singleton
    }

    open fun get(argumentSet: ArgumentSet? = null): Any {
        if (singleton && singletonInstance != null) {
            return singletonInstance!!
        }

        if (clazz.isAnnotationPresent(KotlinSingleton::class.java)) {
            val declaredField = implClass.getDeclaredField("INSTANCE")
            declaredField.isAccessible = true
            val instance = declaredField.get(null)

            if (singleton) {
                singletonInstance = instance
            }
            return instance
        }

        val serviceImpl = implClass.getAnnotation(ServiceImpl::class.java)
        if (serviceImpl.instanceGetter.isEmpty()) {
            val instance = if (serviceImpl.needArgument && argumentSet != null) {
                serviceImpl::class.java.getConstructor(ArgumentSet::class.java).newInstance(argumentSet)
            } else {
                serviceImpl::class.java.getConstructor().newInstance()
            }

            if (singleton) {
                singletonInstance = instance
            }
            return instance
        } else {
            val instance = if (serviceImpl.needArgument && argumentSet != null) {
                serviceImpl::class.java.getMethod(serviceImpl.instanceGetter, ArgumentSet::class.java).invoke(argumentSet)
            } else {
                serviceImpl::class.java.getMethod(serviceImpl.instanceGetter).invoke(null)
            }

            if (singleton) {
                singletonInstance = instance
            }
            return instance
        }
    }

}