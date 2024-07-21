package io.homeassistant.companion.android.common

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class InstanceCountTracker<ThisType : Any> {

    private lateinit var receiver: ThisType
    private lateinit var receiverType: KClass<out ThisType>

    operator fun getValue(thisRef: ThisType, property: KProperty<*>): Int {
        if (!::receiver.isInitialized) {
            receiver = thisRef
            receiverType = thisRef::class
            val set = alreadyCountedInstances.getValue(receiverType)
            set += thisRef
            alreadyCountedInstances[receiverType] = set
        }
        return alreadyCountedInstances.getValue(receiverType).size
    }

    companion object {
        private val alreadyCountedInstances = mutableMapOf<KClass<out Any>, MutableSet<Any>>().withDefault { mutableSetOf() }
    }
}