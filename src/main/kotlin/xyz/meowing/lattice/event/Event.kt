package xyz.meowing.lattice.event

abstract class Event

abstract class CancellableEvent : Event() {
    var cancelled = false
        private set

    fun cancel() {
        cancelled = true
    }
}
