package xyz.meowing.lattice.ui.component

import xyz.meowing.lattice.Lattice
import xyz.meowing.lattice.Lattice.renderer
import xyz.meowing.lattice.render.Font
import xyz.meowing.lattice.ui.Element
import xyz.meowing.lattice.ui.Pos
import xyz.meowing.lattice.ui.Size

class Text(
    var text: String = "",
    var textColor: Int = 0xFFFFFFFF.toInt(),
    var fontSize: Float = 12f,
    var shadowEnabled: Boolean = false,
    var font: Font = Lattice.defaultFont
) : Element<Text>() {

    init {
        setSizing(Size.Auto, Size.Auto)
        setPositioning(Pos.ParentPixels, Pos.ParentPixels)
        ignoreMouseEvents()
    }

    override fun onRender(mouseX: Float, mouseY: Float) {
        if (text.isEmpty()) return

        if (shadowEnabled) renderer.shadowedText(text, x, y, fontSize, textColor, font)
        else renderer.text(text, x, y, fontSize, textColor, font)
    }

    override fun getAutoWidth(): Float = renderer.textWidth(text, fontSize, font)

    override fun getAutoHeight(): Float = fontSize

    fun text(newText: String): Text = apply {
        text = newText
    }

    fun color(color: Int): Text = apply {
        textColor = color
    }

    fun fontSize(size: Float): Text = apply {
        fontSize = size
    }

    fun font(newFont: Font): Text = apply {
        font = newFont
    }

    fun shadow(enabled: Boolean = true): Text = apply {
        shadowEnabled = enabled
    }
}
