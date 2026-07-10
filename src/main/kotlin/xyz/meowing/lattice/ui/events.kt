package xyz.meowing.lattice.ui

sealed class MouseEvent {
    class Move(val x: Float, val y: Float, val element: Element<*>) {
        class Enter(val x: Float, val y: Float, val element: Element<*>)
        class Exit(val x: Float, val y: Float, val element: Element<*>)
    }

    class Scroll(val x: Float, val y: Float, val horizontal: Double, val vertical: Double, val element: Element<*>?)

    class Click(val x: Float, val y: Float, val button: Int, val element: Element<*>?)

    class Release(val x: Float, val y: Float, val button: Int, val element: Element<*>?)
}

sealed class KeyEvent {
    class Type(val keyCode: Int, val scanCode: Int, val char: Char, val element: Element<*>)
}
