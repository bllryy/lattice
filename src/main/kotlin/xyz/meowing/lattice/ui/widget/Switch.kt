package xyz.meowing.lattice.ui.widget

import xyz.meowing.lattice.ui.theme.Theme
import xyz.meowing.lattice.animation.EasingType
import xyz.meowing.lattice.animation.animatePosition
import xyz.meowing.lattice.animation.colorTo
import xyz.meowing.lattice.ui.component.Rectangle
import xyz.meowing.lattice.ui.Pos
import xyz.meowing.lattice.ui.Size
import xyz.meowing.lattice.ui.Element

class Switch(
    var thumbColor: Int = 0xFFFFFFFF.toInt(),
    var thumbDisabledColor: Int = 0xFF9E9E9E.toInt(),
    var trackEnabledColor: Int = Theme.accent,
    var trackDisabledColor: Int = Theme.track,
    var trackHoverColor: Int? = null,
    var trackPressedColor: Int? = null,
    thumbWidth: Float? = null,
    thumbHeight: Float? = null,
    thumbRadius: Float = 13f,
    backgroundColor: Int = Theme.background,
    borderColor: Int = Theme.border,
    borderRadius: Float = 15f,
    borderThickness: Float = 1f,
    padding: FloatArray = floatArrayOf(2f, 2f, 2f, 2f),
    hoverColor: Int? = null,
    pressedColor: Int? = null,
    widthType: Size = Size.Pixels,
    heightType: Size = Size.Pixels
) : Element<Switch>(widthType, heightType) {
    var enabled: Boolean = false
    var enabledPadding: Float = -2f
    var disabledPadding: Float = 0f
    
    val track = Rectangle(
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
    
    val thumb = Rectangle(thumbColor, 0x00000000, thumbRadius, 0f, floatArrayOf(0f, 0f, 0f, 0f), null, null, Size.Pixels, Size.Pixels)
        .ignoreMouseEvents()
        .childOf(track)
    
    init {
        setSizing(50f, Size.Pixels, 26f, Size.Pixels)
        setPositioning(Pos.ParentPixels, Pos.ParentPixels)

        val thumbWidth = thumbWidth ?: (height - 8f)
        val thumbHeight = thumbHeight ?: (height - 8f)
        thumb.setSizing(thumbWidth, Size.Pixels, thumbHeight, Size.Pixels)
        thumb.setPositioning(2f, Pos.ParentPixels, 0f, Pos.ParentCenter)
        
        onClick { _ ->
            setEnabled(!enabled, animated = true)
            true
        }
    }

    fun setEnabled(value: Boolean, animated: Boolean = true, silent: Boolean = false): Switch {
        enabled = value
        
        val thumbTargetX = if (enabled) width - thumb.width + enabledPadding else 2f + disabledPadding
        val trackTargetColor = if (enabled) trackEnabledColor else trackDisabledColor
        val thumbTargetColor = if (enabled) thumbColor else thumbDisabledColor
        
        if (animated) {
            thumb.animatePosition(thumbTargetX, 0f, 200, EasingType.EASE_OUT)
            track.colorTo(trackTargetColor, 200, EasingType.EASE_OUT)
            thumb.colorTo(thumbTargetColor, 200, EasingType.EASE_OUT)
        } else {
            thumb.xConstraint = thumbTargetX
            track.backgroundColor = trackTargetColor
            thumb.backgroundColor = thumbTargetColor
        }
        
        if (!silent) onValueChange.forEach { it.invoke(enabled) }

        return this
    }
    
    private fun updateTrackColor() {
        val baseColor = if (enabled) trackEnabledColor else trackDisabledColor
        val currentColor = when {
            isPressed && trackPressedColor != null -> trackPressedColor!!
            isHovered && trackHoverColor != null -> trackHoverColor!!
            else -> baseColor
        }
        track.backgroundColor = currentColor
    }
    
    override fun onRender(mouseX: Float, mouseY: Float) {
        track.isHovered = isHovered
        track.isPressed = isPressed
        updateTrackColor()
    }

    fun borderThickness(thickness: Float): Switch = apply {
        track.borderThickness = thickness
    }

    fun borderColor(color: Int): Switch = apply {
        track.borderColor = color
    }

    fun borderRadius(radius: Float): Switch = apply {
        track.borderRadius = radius
    }
    
    fun thumbColor(color: Int): Switch = apply {
        thumbColor = color
        if (enabled) thumb.backgroundColor = color
    }
    
    fun thumbDisabledColor(color: Int): Switch = apply {
        thumbDisabledColor = color
        if (!enabled) thumb.backgroundColor = color
    }
    
    fun trackEnabledColor(color: Int): Switch = apply {
        trackEnabledColor = color
        if (enabled) track.backgroundColor = color
    }
    
    fun trackDisabledColor(color: Int): Switch = apply {
        trackDisabledColor = color
        if (!enabled) track.backgroundColor = color
    }
    
    fun trackHoverColor(color: Int): Switch = apply {
        trackHoverColor = color
    }
    
    fun trackPressedColor(color: Int): Switch = apply {
        trackPressedColor = color
    }
}