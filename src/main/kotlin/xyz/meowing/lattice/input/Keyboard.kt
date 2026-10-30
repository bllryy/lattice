package xyz.meowing.lattice.input

import com.mojang.blaze3d.platform.InputConstants
//? if >=26.3 {
import org.lwjgl.sdl.SDLScancode
//?} else {
/*import org.lwjgl.glfw.GLFW
import xyz.meowing.lattice.render.Resolution.window
*///?}

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
        //? if >=26.3 {
        return code in 0 until SDLScancode.SDL_SCANCODE_COUNT
        //?} else {
        /*return code in 0..GLFW.GLFW_KEY_LAST
        *///?}
    }

    @JvmStatic
    fun isPressed(code: Int): Boolean {
        if (!isKeyboardButton(code)) return false
        //? if >=26.3 {
        return InputConstants.isKeyDown(code)
        //?} else {
        /*return InputConstants.isKeyDown(window, code)
        *///?}
    }
}
