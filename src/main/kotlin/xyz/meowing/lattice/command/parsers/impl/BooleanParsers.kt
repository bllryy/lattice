package xyz.meowing.lattice.command.parsers.impl

import xyz.meowing.lattice.command.parsers.CommandParser
import com.mojang.brigadier.StringReader

/**
 * @author Stivais
 */
object BooleanParser : CommandParser<Boolean> {
    override fun parse(reader: StringReader): Boolean {
        return reader.readBoolean()
    }
}
