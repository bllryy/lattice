package xyz.meowing.lattice.client

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer

object Loader {
    @JvmStatic
    val isDevelopment: Boolean
        get() = FabricLoader.getInstance().isDevelopmentEnvironment

    @JvmStatic
    val mods: List<ModContainer>
        get() = FabricLoader.getInstance().allMods.toList()

    @JvmStatic
    fun isLoaded(id: String): Boolean = FabricLoader.getInstance().isModLoaded(id)

    @JvmStatic
    fun findMod(id: String): ModContainer? =
        FabricLoader.getInstance().getModContainer(id).orElse(null)
}
