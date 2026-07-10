package xyz.meowing.lattice.client

import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.item.ItemStack
import xyz.meowing.lattice.client.Client.minecraft

object Player {
    @JvmStatic
    val player: LocalPlayer? get() = minecraft.player

    @JvmStatic
    val name: String? get() = player?.name?.string

    @JvmStatic
    val armor: Array<ItemStack?>
        get() {
            val inv = player?.inventory ?: return arrayOf(null, null, null, null)
            return arrayOf(inv.getItem(36), inv.getItem(37), inv.getItem(38), inv.getItem(39))
        }

    @JvmStatic
    val heldItem: ItemStack? get() = player?.mainHandItem
}
