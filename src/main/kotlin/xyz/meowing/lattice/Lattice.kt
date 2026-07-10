package xyz.meowing.lattice

import xyz.meowing.lattice.event.EventBus
import xyz.meowing.lattice.render.Font
import xyz.meowing.lattice.render.NVGRenderer
import xyz.meowing.lattice.render.Renderer

object Lattice {
    const val MOD_ID = "lattice"

    @JvmStatic
    val eventBus = EventBus()

    @JvmStatic
    val defaultFont: Font by lazy { Font("Default", "/assets/$MOD_ID/font.ttf") }

    private var _renderer: Renderer? = null

    @JvmStatic
    var renderer: Renderer
        get() = _renderer ?: NVGRenderer
        set(value) {
            _renderer = value
        }
}
