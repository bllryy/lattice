package xyz.meowing.lattice.text

import xyz.meowing.lattice.text.ChainBuilder
import xyz.meowing.lattice.text.TextBuilder
import xyz.meowing.lattice.text.FormattingCodes

import net.minecraft.network.chat.Component as VanillaText

object Texts {
    @JvmStatic
    fun literal(text: String): TextBuilder = TextBuilder(text)

    @JvmStatic
    fun empty(): TextBuilder = TextBuilder("")

    @JvmStatic
    fun fromVanilla(
        text: VanillaText
    ): TextBuilder {
        val builder = TextBuilder("")
        builder.vanilla = text
        return builder
    }

    @JvmStatic
    fun fromFormatted(text: String): TextBuilder {
        val parts = text.split(FormattingCodes.SECTION)
        if (parts.size <= 1) return literal(text)

        val root = empty()
        var current = literal("")

        for (i in parts.indices) {
            if (i == 0 && parts[i].isNotEmpty()) {
                root.append(literal(parts[i]))
                continue
            }

            val part = parts[i]
            if (part.isEmpty()) continue

            val code = part[0].lowercaseChar()
            val content = if (part.length > 1) part.substring(1) else ""

            when (code) {
                in '0'..'9', in 'a'..'f' -> {
                    if (current.text.isNotEmpty()) root.append(current)
                    current = literal(content)
                    FormattingCodes.codeToColor(code)?.let { current.color(it) }
                }
                'r' -> {
                    if (current.text.isNotEmpty()) root.append(current)
                    current = literal(content)
                }
                'l' -> {
                    current.bold()
                    current.text += content
                }
                'o' -> {
                    current.italic()
                    current.text += content
                }
                'n' -> {
                    current.underlined()
                    current.text += content
                }
                'm' -> {
                    current.strikethrough()
                    current.text += content
                }
                'k' -> {
                    current.obfuscated()
                    current.text += content
                }
                else -> current.text += FormattingCodes.SECTION + part
            }
        }

        if (current.text.isNotEmpty()) root.append(current)
        return root
    }

    @JvmStatic
    fun builder(): ChainBuilder = ChainBuilder()
}

fun String.asText(): TextBuilder = Texts.literal(this)

fun String.asFormattedText(): TextBuilder = Texts.fromFormatted(this)

fun VanillaText.asBuilder(): TextBuilder = Texts.fromVanilla(this)
fun VanillaText.toBuilder(): TextBuilder = Texts.fromVanilla(this)

fun text(content: String, builder: TextBuilder.() -> Unit = {}): TextBuilder {
    return Texts.literal(content).apply(builder)
}

fun buildText(builder: ChainBuilder.() -> Unit): TextBuilder {
    return Texts.builder().apply(builder).build()
}