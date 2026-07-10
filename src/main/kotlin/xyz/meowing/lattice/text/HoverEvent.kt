package xyz.meowing.lattice.text

import xyz.meowing.lattice.text.TextBuilder
import net.minecraft.world.item.ItemStackTemplate

sealed interface HoverEvent {
    data class ShowText(val text: TextBuilder) : HoverEvent
    data class ShowItem(val stack: ItemStackTemplate) : HoverEvent
}