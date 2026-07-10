package xyz.meowing.lattice.input

import org.lwjgl.glfw.GLFW

object MouseButtons {
    val LEFT = MouseButton(GLFW.GLFW_MOUSE_BUTTON_LEFT)
    val RIGHT = MouseButton(GLFW.GLFW_MOUSE_BUTTON_RIGHT)
    val MIDDLE = MouseButton(GLFW.GLFW_MOUSE_BUTTON_MIDDLE)
    val BACK = MouseButton(GLFW.GLFW_MOUSE_BUTTON_4)
    val FORWARD = MouseButton(GLFW.GLFW_MOUSE_BUTTON_5)
    val BUTTON6 = MouseButton(GLFW.GLFW_MOUSE_BUTTON_6)
    val BUTTON7 = MouseButton(GLFW.GLFW_MOUSE_BUTTON_7)
    val BUTTON8 = MouseButton(GLFW.GLFW_MOUSE_BUTTON_8)
}
