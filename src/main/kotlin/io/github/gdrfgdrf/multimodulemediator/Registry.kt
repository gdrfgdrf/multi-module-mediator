package io.github.gdrfgdrf.multimodulemediator

import io.github.gdrfgdrf.multimodulemediator.annotation.EnumService
import io.github.gdrfgdrf.multimodulemediator.annotation.EnumServiceImpl
import io.github.gdrfgdrf.multimodulemediator.annotation.Service
import io.github.gdrfgdrf.multimodulemediator.annotation.ServiceImpl
import io.github.gdrfgdrf.multimodulemediator.utils.ClassUtils
import kotlin.streams.toList

object Registry {
    fun register(
        classLoader: ClassLoader,
        path: String,
        ignoredPackageNames: Array<String>? = null,
        filter: ((Class<*>) -> Boolean)? = null
    ) {
        val finalFilter = if (filter != null) {
            filter
        } else {
            {
                true
            }
        }

        val serviceClasses = LinkedHashSet<Class<*>>()
        val serviceImplClasses = LinkedHashSet<Class<*>>()

        ClassUtils.searchJar(
            classLoader,
            path,
            ignoredPackageNames,
            {
                return@searchJar it.isAnnotationPresent(Service::class.java) && finalFilter(it)
            },
            serviceClasses,
        )
        ClassUtils.searchJar(
            classLoader,
            path,
            ignoredPackageNames,
            {
                return@searchJar it.isAnnotationPresent(ServiceImpl::class.java) && finalFilter(it)
            },
            serviceImplClasses
        )

        if (serviceClasses.size != serviceImplClasses.size) {
            val array = arrayListOf<Class<*>>()

            serviceClasses.forEach { clazz ->
                val list = serviceImplClasses.stream()
                    .filter {
                        return@filter it.interfaces.contains(clazz) || it.superclass == clazz
                    }
                    .toList()
                if (list.isEmpty()) {
                    array.add(clazz)
                }
            }
            serviceImplClasses.forEach { clazz ->
                val list = serviceClasses.stream()
                    .filter {
                        return@filter clazz.interfaces.contains(it) || clazz.superclass == it
                    }
                    .toList()
                if (list.isEmpty()) {
                    array.add(clazz)
                }
            }
            array.distinct()
            array.forEach {
                println(it)
            }

            throw IllegalArgumentException("service classes set's size is not equals service impl classes set's size")
        }

        serviceClasses.forEach { clazz ->
            val service = clazz.getAnnotation(Service::class.java)
            val value = service.value

            val optional = serviceImplClasses.stream()
                .filter {
                    val serviceImpl = it.getAnnotation(ServiceImpl::class.java)
                    return@filter value == serviceImpl.value
                }
                .findAny()
            if (!optional.isPresent) {
                throw IllegalArgumentException("Unable to find the implementation of service $clazz")
            }

            val serviceImpl = optional.get()
            Mediator.register(clazz, serviceImpl)
        }

        val enumServiceClasses = LinkedHashSet<Class<*>>()
        val enumServiceImplClasses = LinkedHashSet<Class<*>>()

        ClassUtils.searchJar(
            classLoader,
            path,
            ignoredPackageNames,
            {
                return@searchJar it.isAnnotationPresent(EnumService::class.java)
            },
            enumServiceClasses
        )
        ClassUtils.searchJar(
            classLoader,
            path,
            ignoredPackageNames,
            {
                return@searchJar it.isAnnotationPresent(EnumServiceImpl::class.java)
            },
            enumServiceImplClasses
        )

        if (enumServiceClasses.size != enumServiceImplClasses.size) {
            val array = arrayListOf<Class<*>>()

            enumServiceClasses.forEach { clazz ->
                val list = enumServiceImplClasses.stream()
                    .filter {
                        return@filter (it.interfaces.contains(clazz) || it.superclass == clazz) && finalFilter(it)
                    }
                    .toList()
                if (list.isEmpty()) {
                    array.add(clazz)
                }
            }
            enumServiceImplClasses.forEach { clazz ->
                val list = enumServiceClasses.stream()
                    .filter {
                        return@filter (clazz.interfaces.contains(it) || it.superclass == clazz) && finalFilter(it)
                    }
                    .toList()
                if (list.isEmpty()) {
                    array.add(clazz)
                }
            }
            array.distinct()
            array.forEach {
                println(it)
            }

            throw IllegalArgumentException("enum service classes set's size is not equals enum service impl classes set's size")
        }

        enumServiceClasses.forEach { clazz ->
            val enumService = clazz.getAnnotation(EnumService::class.java)
            val value = enumService.value

            val optional = enumServiceImplClasses.stream()
                .filter {
                    val enumServiceImpl = it.getAnnotation(EnumServiceImpl::class.java)
                    return@filter value == enumServiceImpl.value
                }
                .findAny()
            if (!optional.isPresent) {
                throw IllegalArgumentException("Unable to find the implementation of enum service $clazz")
            }

            val enumServiceImpl = optional.get()
            Mediator.registerEnum(clazz, enumServiceImpl)
        }
    }
}