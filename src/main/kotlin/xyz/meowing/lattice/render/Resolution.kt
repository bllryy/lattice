package xyz.meowing.lattice.render

import com.mojang.blaze3d.platform.Window
import xyz.meowing.lattice.client.Client.minecraft

object Resolution {
    @JvmStatic
    val window: Window get() = minecraft.window

    @JvmStatic
    val windowHandle: Long get() = window.handle()

    @JvmStatic
    val windowWidth: Int get() = window.getWidth()

    @JvmStatic
    val windowHeight: Int get() = window.getHeight()

    @JvmStatic
    val scaledWidth: Int get() = window.getGuiScaledWidth()

    @JvmStatic
    val scaledHeight: Int get() = window.getGuiScaledHeight()

    @JvmStatic
    val scaleFactor: Double get() = window.getGuiScale().toDouble()
}
