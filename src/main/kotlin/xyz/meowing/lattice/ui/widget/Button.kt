package xyz.meowing.lattice.ui.widget

import xyz.meowing.lattice.ui.theme.Theme
import xyz.meowing.lattice.Lattice
import xyz.meowing.lattice.ui.component.Rectangle
import xyz.meowing.lattice.ui.component.Text
import xyz.meowing.lattice.ui.Pos
import xyz.meowing.lattice.ui.Size
import xyz.meowing.lattice.ui.Element
import xyz.meowing.lattice.render.Font

class Button(
    var text: String = "",
    var textColor: Int = 0xFFFFFFFF.toInt(),
    var hoverTextColor: Int? = null,
    var pressedTextColor: Int? = null,
    fontSize: Float = 12f,
    font: Font = Lattice.defaultFont,
    shadowEnabled: Boolean = false,
    backgroundColor: Int = Theme.background,
    borderColor: Int = Theme.border,
    borderRadius: Float = 4f,
    borderThickness: Float = 1f,
    padding: FloatArray = floatArrayOf(8f, 16f, 8f, 16f),
    hoverColor: Int? = Theme.backgroundHover,
    pressedColor: Int? = Theme.backgroundPressed,
    widthType: Size = Size.Auto,
    heightType: Size = Size.Auto
) : Element<Button>(widthType, heightType) {
    val background = Rectangle(
        backgroundColor,
        borderColor,
        borderRadius,
        borderThickness,
        padding,
        hoverColor,
        pressedColor,
        Size.Percent,
        Size.Percent
    )
        .setSizing(100f, Size.Percent, 100f, Size.Percent)
        .ignoreMouseEvents()
        .childOf(this)

    val innerText = Text(text, textColor, fontSize, shadowEnabled, font)
        .childOf(background)
        .setPositioning(Pos.ParentCenter, Pos.ParentCenter)

    init {
        setPositioning(Pos.ParentPixels, Pos.ParentPixels)
    }

    override fun onRender(mouseX: Float, mouseY: Float) {
        background.isHovered = isHovered
        background.isPressed = isPressed

        if (text.isNotEmpty()) {
            val currentTextColor = when {
                isHovered && pressedTextColor != null -> pressedTextColor!!
                isPressed && hoverTextColor != null -> hoverTextColor!!
                else -> textColor
            }
            innerText.textColor = currentTextColor
        }
    }

    fun text(text: String): Button = apply {
        innerText.text = text
    }

    fun textColor(color: Int): Button = apply {
        this.textColor = color
        innerText.textColor = color
    }

    fun fontSize(size: Float): Button = apply {
        innerText.fontSize = size
    }

    fun font(font: Font): Button = apply {
        innerText.font = font
    }

    fun hoverColors(bg: Int? = null, text: Int? = null): Button = apply {
        background.hoverColor = bg
        this.hoverTextColor = text
    }

    fun pressedColors(bg: Int? = null, text: Int? = null): Button = apply {
        background.pressedColor = bg
        this.pressedTextColor = text
    }

    fun shadow(enabled: Boolean = true): Button = apply {
        innerText.shadowEnabled = enabled
    }

    fun padding(top: Float, right: Float, bottom: Float, left: Float): Button = apply {
        background.padding(top, right, bottom, left)
    }

    fun padding(all: Float): Button = apply {
        background.padding(all)
    }

    fun backgroundColor(color: Int): Button = apply {
        background.backgroundColor(color)
    }

    fun borderColor(color: Int): Button = apply {
        background.borderColor(color)
    }

    fun borderRadius(radius: Float): Button = apply {
        background.borderRadius(radius)
    }

    fun borderThickness(thickness: Float): Button = apply {
        background.borderThickness(thickness)
    }

    fun hoverColor(color: Int): Button = apply {
        background.hoverColor(color)
    }

    fun pressedColor(color: Int): Button = apply {
        background.pressedColor(color)
    }

    override fun getAutoWidth(): Float = background.getAutoWidth()
    override fun getAutoHeight(): Float = background.getAutoHeight()
}