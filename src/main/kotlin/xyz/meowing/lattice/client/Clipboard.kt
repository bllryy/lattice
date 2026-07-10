package xyz.meowing.lattice.client

import org.lwjgl.glfw.GLFW
import xyz.meowing.lattice.client.Client.minecraft

object Clipboard {
    @JvmStatic
    var string: String
        get() = GLFW.glfwGetClipboardString(minecraft.window.handle()) ?: ""
        set(value) { GLFW.glfwSetClipboardString(minecraft.window.handle(), value) }
}
