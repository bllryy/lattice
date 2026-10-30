package xyz.meowing.lattice.client

import com.mojang.blaze3d.pipeline.RenderTarget
import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.ChatComponent
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component
import net.minecraft.world.level.GameType
import net.minecraft.world.scores.DisplaySlot
import java.nio.file.Path

object Client {
    private val tabListComparator: Comparator<PlayerInfo> = compareBy(
        { it.getGameMode() == GameType.SPECTATOR },
        { it.getTeam()?.name ?: "" },
        { it.getProfile().name.lowercase() }
    )

    @JvmStatic
    val minecraft: Minecraft = Minecraft.getInstance()

    @JvmStatic
    val world: ClientLevel? get() = minecraft.level

    @JvmStatic
    val player: LocalPlayer? get() = minecraft.player

    /** The open screen, or null when in game. Screen handling moved from [Minecraft] to `Gui` in 26.2. */
    @JvmStatic
    val screen: Screen?
        //? if >=26.2 {
        get() = minecraft.gui.screen()
        //?} else {
        /*get() = minecraft.screen
        *///?}

    /** Opens [screen], or closes the current one when null. Must run on the render thread. */
    @JvmStatic
    fun openScreen(screen: Screen?) {
        //? if >=26.2 {
        minecraft.gui.setScreen(screen)
        //?} else {
        /*minecraft.setScreen(screen)
        *///?}
    }

    @JvmStatic
    val chat: ChatComponent
        //? if >=26.2 {
        get() = minecraft.gui.hud.getChat()
        //?} else {
        /*get() = minecraft.gui.getChat()
        *///?}

    @JvmStatic
    val mainRenderTarget: RenderTarget
        //? if >=26.2 {
        get() = minecraft.gameRenderer.mainRenderTarget()
        //?} else {
        /*get() = minecraft.mainRenderTarget
        *///?}

    @JvmStatic
    val minecraftVersion: String by lazy {
        SharedConstants.getCurrentVersion().name()
    }

    @JvmStatic
    val gameDirectory: Path get() = minecraft.gameDirectory.toPath()

    @JvmStatic
    val configDirectory: Path get() = gameDirectory.resolve("config")

    @JvmStatic
    val tablist: List<PlayerInfo>
        get() = minecraft.getConnection()
            ?.getListedOnlinePlayers()
            ?.sortedWith(tabListComparator) ?: emptyList()

    @JvmStatic
    val players: List<PlayerInfo>
        get() = tablist.filter { it.getProfile().id.version() == 4 }

    @JvmStatic
    val scoreboard: Collection<Component>
        get() {
            val scoreboard = world?.scoreboard ?: return emptyList()
            val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return emptyList()
            return scoreboard.listPlayerScores(objective)
                .sortedBy { -it.value() }
                .map {
                    val ownerName = Component.literal(it.owner())
                    val team = scoreboard.getPlayerTeam(it.owner())
                    if (team == null) {
                        ownerName.copy()
                    } else {
                        Component.empty().also { main ->
                            main.append(team.getPlayerPrefix())
                            if (ownerName.string.isNotEmpty()) main.append(ownerName)
                            main.append(team.getPlayerSuffix())
                        }
                    }
                }
        }

    val scoreboardTitle get() = world?.scoreboard?.getDisplayObjective(DisplaySlot.SIDEBAR)?.displayName
}
