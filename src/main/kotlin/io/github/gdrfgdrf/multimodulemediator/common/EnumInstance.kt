package io.github.gdrfgdrf.multimodulemediator.common

import io.github.gdrfgdrf.multimodulemediator.Mediator

open class EnumInstance<T> : TypeGetter<T>() {
    fun valueOf(name: String): T {
        return Mediator.valueOf<T>(type, name)!!
    }

    fun find(name: String): T? {
        return Mediator.valueOf<T>(type, name)
    }

    fun values(): Array<T> {
        return Mediator.values<T>(type) as Array<T>
    }

    fun search(name: String): List<T> {
        return Mediator.search<T>(type, name)!!
    }
}