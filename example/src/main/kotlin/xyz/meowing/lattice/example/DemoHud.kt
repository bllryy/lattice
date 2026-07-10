package xyz.meowing.lattice.example

import xyz.meowing.lattice.Lattice.eventBus
import xyz.meowing.lattice.Lattice.renderer
import xyz.meowing.lattice.client.Client.minecraft
import xyz.meowing.lattice.event.RenderEvent
import xyz.meowing.lattice.ui.theme.Theme

/** Draws directly with the renderer every frame — no screen needed. */
object DemoHud {
    var enabled = false

    fun init() {
        eventBus.register<RenderEvent.Gui> {
            if (!enabled || minecraft.screen is DemoScreen) return@register

            renderer.rect(10f, 10f, 4f, 34f, Theme.accent, 2f)
            renderer.shadowedText("Lattice", 22f, 12f, 16f, 0xFFFFFFFF.toInt())
            renderer.text("/lattice opens the demo", 22f, 30f, 11f, Theme.textMuted)
        }
    }
}
