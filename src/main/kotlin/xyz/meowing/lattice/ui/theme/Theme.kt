package xyz.meowing.lattice.ui.theme

/**
 * Semantic color tokens and animation timings shared by the built-in widgets.
 * Mutable so a client can retheme every widget default in one place.
 */
object Theme {
    var accent = 0xFF4c87f9.toInt()
    var accentSelection = 0x80aac7ff.toInt()

    var background = 0x80404040.toInt()
    var backgroundHover = 0x80505050.toInt()
    var backgroundPressed = 0x80303030.toInt()
    var backgroundDark = 0xFF212121.toInt()
    var backgroundDisabled = 0xFF303030.toInt()

    var surface = 0xFF1e1e1e.toInt()
    var surfaceBorder = 0xFF555759.toInt()

    var border = 0xFF606060.toInt()

    var text = 0xFFFFFFFF.toInt()
    var textMuted = 0xFF787878.toInt()

    var track = 0xFF424242.toInt()
    var scrollbar = 0xFF7c7c7d.toInt()

    var animFast = 100L
    var animNormal = 200L
    var animSlow = 300L
}
