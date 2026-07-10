package xyz.meowing.lattice.ui.component

import xyz.meowing.lattice.Lattice.renderer
import xyz.meowing.lattice.animation.EasingType
import xyz.meowing.lattice.animation.animateFloat
import xyz.meowing.lattice.render.Gradient
import xyz.meowing.lattice.ui.Box
import xyz.meowing.lattice.ui.Size

open class Rectangle(
    var backgroundColor: Int = 0x80000000.toInt(),
    var borderColor: Int = 0xFFFFFFFF.toInt(),
    var borderRadius: Float = 0f,
    var borderThickness: Float = 0f,
    padding: FloatArray = floatArrayOf(0f, 0f, 0f, 0f),
    var hoverColor: Int? = null,
    var pressedColor: Int? = null,
    widthType: Size = Size.Auto,
    heightType: Size = Size.Auto,
    scrollable: Boolean = false
) : Box<Rectangle>(padding, scrollable, widthType, heightType) {
    var secondBorderColor: Int = -1
    var secondBackgroundColor: Int = -1
    var gradientType: Gradient = Gradient.TopLeftToBottomRight
    var dropShadow: Boolean = false
    var rotation: Float = 0f

    var shadowBlur = 30f
    var shadowSpread = 1f
    var shadowColor = 0x80000000.toInt()

    var borderRadiusTopLeft: Float? = null
    var borderRadiusTopRight: Float? = null
    var borderRadiusBottomLeft: Float? = null
    var borderRadiusBottomRight: Float? = null

    public override fun onRender(mouseX: Float, mouseY: Float) {
        if (!visible || (height - (padding[0] + padding[2])) == 0f || (width - (padding[1] + padding[3])) == 0f) return

        val currentBgColor = when {
            isPressed && pressedColor != null -> pressedColor!!
            isHovered && hoverColor != null -> hoverColor!!
            else -> backgroundColor
        }

        val centerX = x + width / 2f
        val centerY = y + height / 2f

        if (rotation != 0f) {
            renderer.push()
            renderer.translate(centerX, centerY)
            renderer.rotate(Math.toRadians(rotation.toDouble()).toFloat())
            renderer.translate(-centerX, -centerY)
        }

        if (dropShadow) {
            renderer.dropShadow(x, y, width, height, shadowBlur, shadowSpread, shadowColor, borderRadius)
        }

        if (currentBgColor != 0) {
            if (hasVaryingRadius()) {
                renderer.rect(
                    x, y, width, height, currentBgColor,
                    borderRadiusTopRight ?: borderRadius,
                    borderRadiusTopLeft ?: borderRadius,
                    borderRadiusBottomRight ?: borderRadius,
                    borderRadiusBottomLeft ?: borderRadius
                )
            } else if (currentBgColor == backgroundColor && secondBackgroundColor != -1) {
                renderer.gradientRect(x, y, width, height, backgroundColor, secondBackgroundColor, gradientType, borderRadius)
            } else {
                renderer.rect(x, y, width, height, currentBgColor, borderRadius)
            }
        }

        if (borderThickness > 0f) {
            if (secondBorderColor != -1) {
                renderer.hollowGradientRect(x, y, width, height, borderThickness, borderColor, secondBorderColor, gradientType, borderRadius)
            } else {
                renderer.hollowRect(x, y, width, height, borderThickness, borderColor, borderRadius)
            }
        }

        if (rotation != 0f) {
            renderer.pop()
        }
    }

    private fun hasVaryingRadius(): Boolean {
        return borderRadiusTopLeft != null || borderRadiusTopRight != null ||
            borderRadiusBottomLeft != null || borderRadiusBottomRight != null
    }

    fun rotateTo(angle: Float, duration: Long = 300, type: EasingType = EasingType.EASE_OUT, onComplete: (() -> Unit)? = null): Rectangle {
        animateFloat({ rotation }, { rotation = it }, angle, duration, type, onComplete = onComplete)
        return this
    }

    fun dropShadow(shadowBlur: Float = 30f, shadowSpread: Float = 1f, shadowColor: Int = 0x000000): Rectangle = apply {
        dropShadow = true
        this.shadowBlur = shadowBlur
        this.shadowSpread = shadowSpread
        this.shadowColor = shadowColor
    }

    open fun backgroundColor(color: Int): Rectangle = apply {
        backgroundColor = color
        secondBackgroundColor = -1
    }

    open fun setBackgroundGradientColor(color1: Int, color2: Int): Rectangle = apply {
        backgroundColor = color1
        secondBackgroundColor = color2
    }

    open fun setBorderGradientColor(color1: Int, color2: Int): Rectangle = apply {
        borderColor = color1
        secondBorderColor = color2
    }

    open fun borderColor(color: Int): Rectangle = apply {
        borderColor = color
        secondBorderColor = -1
    }

    open fun borderGradient(type: Gradient): Rectangle = apply {
        gradientType = type
    }

    open fun borderRadius(radius: Float): Rectangle = apply {
        borderRadius = radius
        borderRadiusTopLeft = null
        borderRadiusTopRight = null
        borderRadiusBottomLeft = null
        borderRadiusBottomRight = null
    }

    open fun borderRadiusVarying(
        topLeft: Float = borderRadius,
        topRight: Float = borderRadius,
        bottomLeft: Float = borderRadius,
        bottomRight: Float = borderRadius
    ): Rectangle = apply {
        borderRadiusTopLeft = topLeft
        borderRadiusTopRight = topRight
        borderRadiusBottomLeft = bottomLeft
        borderRadiusBottomRight = bottomRight
    }

    open fun borderThickness(thickness: Float): Rectangle = apply {
        borderThickness = thickness
    }

    open fun hoverColor(color: Int): Rectangle = apply {
        hoverColor = color
    }

    open fun pressedColor(color: Int): Rectangle = apply {
        pressedColor = color
    }
}
