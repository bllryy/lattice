package xyz.meowing.lattice.event

sealed class ClientEvent {
    class Start : Event()
    class Stop : Event()
}

sealed class TickEvent {
    class Start : Event()
    class End : Event()
}

sealed class RenderEvent {
    /** Posted every frame after vanilla GUI rendering, inside a renderer frame. */
    class Gui : CancellableEvent()
}
