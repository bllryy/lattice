package xyz.meowing.lattice.ui

import xyz.meowing.lattice.animation.AnimationManager
import xyz.meowing.lattice.input.Mouse

class Window {
    val children: MutableList<Element<*>> = mutableListOf()

    private val mouseX: Float get() = Mouse.Raw.x.toFloat()
    private val mouseY: Float get() = Mouse.Raw.y.toFloat()

    fun addChild(element: Element<*>) {
        element.parent = this
        children.add(element)
    }

    fun removeChild(element: Element<*>) {
        element.parent = null
        children.remove(element)
    }

    fun draw() {
        children.forEach { it.render(mouseX, mouseY) }
        AnimationManager.update()
    }

    fun mouseClick(button: Int): Boolean {
        return children.reversed().any { it.handleMouseClick(mouseX, mouseY, button) }
    }

    fun mouseRelease(button: Int): Boolean {
        return children.reversed().any { it.handleMouseRelease(mouseX, mouseY, button) }
    }

    fun mouseMove() {
        children.reversed().forEach { it.handleMouseMove(mouseX, mouseY) }
    }

    fun mouseScroll(horizontalDelta: Double, verticalDelta: Double) {
        children.reversed().forEach { it.handleMouseScroll(mouseX, mouseY, horizontalDelta, verticalDelta) }
    }

    fun charType(keyCode: Int, scanCode: Int, charTyped: Char): Boolean {
        return children.reversed().any { it.handleCharType(keyCode, scanCode, charTyped) }
    }

    fun onWindowResize() {
        children.forEach { it.onWindowResize() }
    }

    fun cleanup() {
        children.toList().forEach { it.destroy() }
        children.clear()
        AnimationManager.clear()
    }
}
