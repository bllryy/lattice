package xyz.meowing.lattice.example

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import xyz.meowing.lattice.client.Chat
import xyz.meowing.lattice.command.Commodore
import xyz.meowing.lattice.scheduler.TickScheduler
import xyz.meowing.lattice.text.buildText

object LatticeExample : ClientModInitializer {
    private val command = Commodore("lattice", "latticedemo") {
        runs { DemoScreen().display() }

        literal("hud") {
            runs {
                DemoHud.enabled = !DemoHud.enabled
                Chat.fakeMessage("Lattice HUD ${if (DemoHud.enabled) "enabled" else "disabled"}")
            }
        }

        literal("chat") {
            runs {
                Chat.fakeMessage(buildText {
                    text("Lattice ") { color(0x4c87f9); bold() }
                    text("text DSL: ")
                    text("hover me") {
                        yellow()
                        underlined()
                        onHover("Built with buildText { }")
                    }
                    text(" or ")
                    text("click me") { green(); runCommand("/lattice") }
                })
            }
        }

        // Typed lambda parameters become command arguments, parsed by Commodore.
        literal("greet") {
            runs { name: String, times: Int ->
                repeat(times.coerceIn(1, 5)) { i ->
                    TickScheduler.schedule((i * 10).toLong()) {
                        Chat.fakeMessage("Hello, $name!")
                    }
                }
            }
        }
    }

    override fun onInitializeClient() {
        DemoHud.init()
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            command.register(dispatcher)
        }
    }
}
