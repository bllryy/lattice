package xyz.meowing.lattice.ui.component

import xyz.meowing.lattice.ui.Box
import xyz.meowing.lattice.ui.Size

/** Invisible layout box: padding and optional scrolling with no drawing of its own. */
open class Container(
    padding: FloatArray = floatArrayOf(0f, 0f, 0f, 0f),
    scrollable: Boolean = false,
    widthType: Size = Size.Auto,
    heightType: Size = Size.Auto
) : Box<Container>(padding, scrollable, widthType, heightType) {
    override fun onRender(mouseX: Float, mouseY: Float) {}
}
