package xyz.meowing.lattice.client

import net.minecraft.network.chat.Component
import xyz.meowing.lattice.client.Client.minecraft
import xyz.meowing.lattice.client.Client.player
import xyz.meowing.lattice.text.TextBuilder
import kotlin.math.roundToInt

object Chat {
    private const val CHAT_WIDTH = 320

    @JvmStatic
    fun sendMessage(message: String) {
        player?.connection?.sendChat(message)
    }

    @JvmStatic
    fun sendCommand(command: String) {
        player?.connection?.sendCommand(command.removePrefix("/"))
    }

    @JvmStatic
    fun fakeMessage(message: Component) {
        minecraft.gui.getChat().addClientSystemMessage(message)
    }

    @JvmStatic
    fun fakeMessage(message: String) {
        fakeMessage(Component.literal(message))
    }

    @JvmStatic
    fun fakeMessage(message: TextBuilder) {
        fakeMessage(message.toVanilla())
    }

    @JvmStatic
    fun getChatBreak(): String {
        val dashWidth = minecraft.font.width("-")
        return "-".repeat(CHAT_WIDTH / dashWidth)
    }

    @JvmStatic
    fun getCenteredText(text: String): String {
        val textWidth = minecraft.font.width(text)
        if (textWidth >= CHAT_WIDTH) return text
        val spaceWidth = minecraft.font.width(" ")
        val padding = ((CHAT_WIDTH - textWidth) / 2f / spaceWidth).roundToInt()
        return " ".repeat(padding) + text
    }
}
