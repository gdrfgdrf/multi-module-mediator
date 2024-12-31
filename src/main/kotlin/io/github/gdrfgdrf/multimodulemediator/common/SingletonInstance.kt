package io.github.gdrfgdrf.multimodulemediator.common

import io.github.gdrfgdrf.multimodulemediator.Mediator

open class SingletonInstance<T> : TypeGetter<T>() {
    fun instance(): T = Mediator.get<T>(type)!!
}