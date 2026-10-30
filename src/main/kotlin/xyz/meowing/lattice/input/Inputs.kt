package xyz.meowing.lattice.input

import com.mojang.blaze3d.platform.InputConstants
//? if <26.3 {
/*import org.lwjgl.glfw.GLFW
*///?}

object Inputs {
    fun get(code: Int): InputCode {
        return when {
            Keyboard.isKeyboardButton(code) -> Key(code)
            Mouse.isMouseButton(code) -> MouseButton(code)
            else -> throw IllegalArgumentException("Code $code is not a valid key or mouse button")
        }
    }

    @JvmStatic
    @JvmOverloads
    fun getDisplayName(code: Int, scanCode: Int = -1): String {
        //? if >=26.3 {
        // 26.3 key codes are already SDL scancodes, so scanCode adds nothing.
        val name = InputConstants.Type.KEYBOARD.getOrCreate(code).displayName.string
        //?} else {
        /*val keyName = GLFW.glfwGetKeyName(code, scanCode)
        if (keyName != null) {
            return if (keyName.length == 1) keyName.uppercase() else keyName
        }
        val name = (if (code == -1) {
            InputConstants.Type.SCANCODE.getOrCreate(scanCode)
        } else {
            InputConstants.Type.KEYSYM.getOrCreate(code)
        }).displayName.string
        *///?}
        return if (name.length == 1) name.uppercase() else name
    }
}
