package xyz.meowing.lattice.input

import org.lwjgl.glfw.GLFW
import xyz.meowing.lattice.client.Client.minecraft
import xyz.meowing.lattice.render.Resolution
import xyz.meowing.lattice.render.Resolution.windowHandle
import kotlin.math.max

object Mouse {
    object Raw {
        val x: Double get() = minecraft.mouseHandler.xpos()
        val y: Double get() = minecraft.mouseHandler.ypos()
    }

    object Scaled {
        val x: Double get() = Raw.x * Resolution.scaledWidth / max(1, Resolution.windowWidth)
        val y: Double get() = Raw.y * Resolution.scaledHeight / max(1, Resolution.windowHeight)
    }

    var isCursorGrabbed: Boolean
        get() = minecraft.mouseHandler.isMouseGrabbed
        set(value) {
            if (value) minecraft.mouseHandler.grabMouse() else minecraft.mouseHandler.releaseMouse()
        }

    fun isMouseButton(code: Int): Boolean = code in GLFW.GLFW_MOUSE_BUTTON_1..GLFW.GLFW_MOUSE_BUTTON_8

    fun isPressed(code: Int): Boolean {
        if (!isMouseButton(code)) return false
        val state = GLFW.glfwGetMouseButton(windowHandle, code)
        return state == GLFW.GLFW_PRESS || state == GLFW.GLFW_REPEAT
    }
}
