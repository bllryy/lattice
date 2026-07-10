package xyz.meowing.lattice.event

interface EventCall {
    fun unregister(): Boolean
    fun register(): Boolean
    fun isRegistered(): Boolean
}

data class PrioritizedCallback<T : Event>(
    val priority: Int,
    val callback: (T) -> Unit
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrioritizedCallback<*>) return false
        return callback === other.callback
    }

    override fun hashCode(): Int = callback.hashCode()
}

internal class EventCallImpl<T : Event>(
    private val callback: PrioritizedCallback<T>,
    private val handlers: MutableList<PrioritizedCallback<*>>,
    private val eventBus: EventBus,
    initiallyRegistered: Boolean
) : EventCall {
    private var registered = initiallyRegistered

    override fun unregister(): Boolean {
        if (!registered) return false
        val removed = handlers.remove(callback)
        if (removed) registered = false
        return removed
    }

    override fun register(): Boolean {
        if (registered) return false
        eventBus.addSorted(handlers, callback)
        registered = true
        return true
    }

    override fun isRegistered(): Boolean = registered
}
