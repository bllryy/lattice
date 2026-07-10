package xyz.meowing.lattice.ui.widget

import xyz.meowing.lattice.ui.theme.Theme
import xyz.meowing.lattice.animation.EasingType
import xyz.meowing.lattice.animation.fadeIn
import xyz.meowing.lattice.animation.fadeOut
import xyz.meowing.lattice.ui.component.Rectangle
import xyz.meowing.lattice.ui.component.SvgImage
import xyz.meowing.lattice.ui.Pos
import xyz.meowing.lattice.ui.Size
import xyz.meowing.lattice.ui.Element

class CheckBox(
    var checkmarkColor: Int = Theme.accent,
    var disabledBackgroundColor: Int = Theme.backgroundDisabled,
    var enabledBackgroundColor: Int = Theme.backgroundDark,
    var backgroundHoverColor: Int = Theme.track,
    var backgroundPressedColor: Int = 0xFF1B1B1B.toInt(),
    backgroundColor: Int = Theme.background,
    borderColor: Int = Theme.border,
    borderRadius: Float = 4f,
    borderThickness: Float = 1f,
    padding: FloatArray = floatArrayOf(4f, 4f, 4f, 4f),
    hoverColor: Int? = Theme.backgroundHover,
    pressedColor: Int? = Theme.backgroundPressed,
    widthType: Size = Size.Pixels,
    heightType: Size = Size.Pixels
) : Element<CheckBox>(widthType, heightType) {
    var checked: Boolean = false

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

    val checkMark = SvgImage(svgPath = "/assets/lattice/checkmark.svg", color = checkmarkColor and 0x00FFFFFF)
        .setSizing(120f, Size.Percent, 120f, Size.Percent)
        .setPositioning(0f, Pos.ParentCenter, 0f, Pos.ParentCenter)
        .childOf(background)

    init {
        setSizing(20f, Size.Pixels, 20f, Size.Pixels)
        setPositioning(Pos.ParentPixels, Pos.ParentPixels)
        onClick { _ ->
            setChecked(!checked, animated = true)
            true
        }
    }

    fun setChecked(value: Boolean, animated: Boolean = true, silent: Boolean = false) {
        checked = value
        if (!checked) {
            if (animated) {
                checkMark.setSvgColor(checkmarkColor)
                checkMark.fadeOut(100, EasingType.EASE_IN)
            }
        } else if (animated) {
            checkMark.fadeIn(100, EasingType.EASE_IN)
        }
        if (!silent) onValueChange.forEach { it.invoke(checked) }
    }

    private fun updateBackgroundColor() {
        val newColor = when {
            !checked -> disabledBackgroundColor
            isPressed -> backgroundPressedColor
            isHovered -> backgroundHoverColor
            else -> enabledBackgroundColor
        }
        background.backgroundColor(newColor)
    }

    override fun onRender(mouseX: Float, mouseY: Float) {
        background.isHovered = isHovered
        background.isPressed = isPressed
        updateBackgroundColor()
    }
}