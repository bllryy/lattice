package xyz.meowing.lattice.event

import org.jetbrains.annotations.ApiStatus
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

open class EventBus {
    val subscribers: MutableMap<Class<*>, MutableList<PrioritizedCallback<*>>> = ConcurrentHashMap()

    inline fun <reified T : Event> register(
        priority: Int = 0,
        add: Boolean = true,
        noinline callback: (T) -> Unit
    ): EventCall {
        return register(T::class.java, priority, add, callback)
    }

    @JvmOverloads
    fun <T : Event> register(
        eventClass: Class<T>,
        priority: Int = 0,
        add: Boolean = true,
        callback: (T) -> Unit
    ): EventCall {
        val handlers = subscribers.getOrPut(eventClass) { CopyOnWriteArrayList() }
        val prioritizedCallback = PrioritizedCallback(priority, callback)
        if (add) addSorted(handlers, prioritizedCallback)
        return EventCallImpl(prioritizedCallback, handlers, this, add)
    }

    fun <T : Event> post(event: T): Boolean {
        val handlers = subscribers[event::class.java] ?: return false
        if (handlers.isEmpty()) return false

        for (handler in handlers) {
            if (event is CancellableEvent && event.cancelled) return true

            try {
                @Suppress("UNCHECKED_CAST")
                (handler.callback as (T) -> Unit)(event)
            } catch (e: Exception) {
                handleException(event, e)
            }
        }

        return event is CancellableEvent && event.cancelled
    }

    /** Builds and posts the event only if it has subscribers. */
    inline fun <reified T : Event> post(supplier: () -> T): Boolean {
        if (!hasSubscribers(T::class.java)) return false
        return post(supplier())
    }

    open fun handleException(event: Any, exception: Exception) {
        exception.printStackTrace()
    }

    fun hasSubscribers(eventClass: Class<*>): Boolean {
        return subscribers[eventClass]?.isNotEmpty() == true
    }

    inline fun <reified T : Event> hasSubscribers(): Boolean = hasSubscribers(T::class.java)

    fun clear() {
        subscribers.clear()
    }

    @ApiStatus.Internal
    fun <T : Event> addSorted(
        list: MutableList<PrioritizedCallback<*>>,
        callback: PrioritizedCallback<T>
    ) {
        val index = list.binarySearch { it.priority.compareTo(callback.priority) }
        val insertIndex = if (index < 0) -(index + 1) else index
        list.add(insertIndex, callback)
    }
}
