package xyz.meowing.lattice.text

import xyz.meowing.lattice.text.Texts

import net.minecraft.network.chat.Component as VanillaText

class ChainBuilder {
    private val parts = mutableListOf<TextBuilder>()

    fun text(content: String, builder: TextBuilder.() -> Unit = {}): ChainBuilder {
        parts.add(Texts.literal(content).apply(builder))
        return this
    }

    fun append(builder: TextBuilder): ChainBuilder {
        parts.add(builder)
        return this
    }

    fun newLine(): ChainBuilder {
        parts.add(Texts.literal("\n"))
        return this
    }

    fun space(): ChainBuilder {
        parts.add(Texts.literal(" "))
        return this
    }

    fun build(): TextBuilder {
        if (parts.isEmpty()) return Texts.empty()
        val result = parts.first()
        for (i in 1 until parts.size) {
            result.append(parts[i])
        }
        return result
    }

    fun toVanilla(): VanillaText {
        return build().toVanilla()
    }
}