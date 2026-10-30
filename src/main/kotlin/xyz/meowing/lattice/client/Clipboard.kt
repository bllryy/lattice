package xyz.meowing.lattice.client

import xyz.meowing.lattice.client.Client.minecraft

object Clipboard {
    @JvmStatic
    var string: String
        get() = minecraft.keyboardHandler.clipboard
        set(value) { minecraft.keyboardHandler.clipboard = value }
}
