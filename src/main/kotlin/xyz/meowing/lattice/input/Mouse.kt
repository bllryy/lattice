package xyz.meowing.lattice.input

import com.mojang.blaze3d.platform.InputConstants
//? if >=26.3 {
import org.lwjgl.sdl.SDLMouse
//?} else {
/*import org.lwjgl.glfw.GLFW
import xyz.meowing.lattice.render.Resolution.windowHandle
*///?}
import xyz.meowing.lattice.client.Client.minecraft
import xyz.meowing.lattice.render.Resolution
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

    fun isMouseButton(code: Int): Boolean = code in InputConstants.MOUSE_BUTTON_LEFT..InputConstants.MOUSE_BUTTON_8

    fun isPressed(code: Int): Boolean {
        if (!isMouseButton(code)) return false
        //? if >=26.3 {
        // SDL button state is a bitmask where button N is bit N - 1; 0L (NULL) skips the position out-params.
        return SDLMouse.nSDL_GetMouseState(0L, 0L) and (1 shl (code - 1)) != 0
        //?} else {
        /*return GLFW.glfwGetMouseButton(windowHandle, code) == GLFW.GLFW_PRESS
        *///?}
    }
}
