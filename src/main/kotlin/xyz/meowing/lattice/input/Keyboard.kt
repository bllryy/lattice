package xyz.meowing.lattice.input

import org.lwjgl.glfw.GLFW
import xyz.meowing.lattice.render.Resolution.windowHandle

object Keyboard {
    @JvmStatic
    val isShiftKeyPressed: Boolean
        get() = Keys.KEY_LEFT_SHIFT.isPressed || Keys.KEY_RIGHT_SHIFT.isPressed

    @JvmStatic
    val isCtrlKeyPressed: Boolean
        get() = Keys.KEY_LEFT_CONTROL.isPressed || Keys.KEY_RIGHT_CONTROL.isPressed

    @JvmStatic
    val isAltKeyPressed: Boolean
        get() = Keys.KEY_LEFT_ALT.isPressed || Keys.KEY_RIGHT_ALT.isPressed

    @JvmStatic
    val isSuperKeyPressed: Boolean
        get() = Keys.KEY_LEFT_SUPER.isPressed || Keys.KEY_RIGHT_SUPER.isPressed

    @JvmStatic
    fun isKeyboardButton(code: Int): Boolean {
        return code in 0 until GLFW.GLFW_KEY_LAST
    }

    @JvmStatic
    fun isPressed(code: Int): Boolean {
        if (!isKeyboardButton(code)) return false
        val state = GLFW.glfwGetKey(windowHandle, code)
        return state == GLFW.GLFW_PRESS || state == GLFW.GLFW_REPEAT
    }
}
