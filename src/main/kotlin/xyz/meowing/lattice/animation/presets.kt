package xyz.meowing.lattice.animation

import xyz.meowing.lattice.ui.Element
import xyz.meowing.lattice.ui.component.Rectangle
import xyz.meowing.lattice.ui.component.SvgImage
import xyz.meowing.lattice.ui.component.Text

fun <T : Element<T>> T.fadeIn(
    duration: Long = 300,
    type: EasingType = EasingType.EASE_OUT,
    includeChildren: Boolean = true,
    onComplete: (() -> Unit)? = null
): T {
    visible = true

    if (includeChildren) {
        children.forEach { child ->
            when (child) {
                is Rectangle -> child.fadeIn(duration, type, includeChildren)
                is Text -> child.fadeIn(duration, type, includeChildren)
                is SvgImage -> child.fadeIn(duration, type, includeChildren)
            }
        }
    }

    when (this) {
        is Rectangle -> {
            val targetBg = (backgroundColor and 0x00FFFFFF) or (255 shl 24)
            val targetBorder = (borderColor and 0x00FFFFFF) or (255 shl 24)
            backgroundColor = backgroundColor and 0x00FFFFFF
            borderColor = borderColor and 0x00FFFFFF
            animateColor({ backgroundColor }, { backgroundColor = it }, targetBg, duration, type, onComplete)
            animateColor({ borderColor }, { borderColor = it }, targetBorder, duration, type)
        }
        is Text -> {
            val target = (textColor and 0x00FFFFFF) or (255 shl 24)
            textColor = textColor and 0x00FFFFFF
            animateColor({ textColor }, { textColor = it }, target, duration, type, onComplete)
        }
        is SvgImage -> {
            val target = (color and 0x00FFFFFF) or (255 shl 24)
            animateColor({ color }, { setSvgColor(it) }, target, duration, type, onComplete)
        }
        else -> animateFloat({ 0f }, {}, 1f, duration, type, AnimationType.ALPHA, onComplete)
    }

    return this
}

fun <T : Element<T>> T.fadeOut(
    duration: Long = 300,
    type: EasingType = EasingType.EASE_IN,
    includeChildren: Boolean = true,
    onComplete: (() -> Unit)? = null
): T {
    if (includeChildren) {
        children.forEach { child ->
            when (child) {
                is Rectangle -> child.fadeOut(duration, type, includeChildren)
                is Text -> child.fadeOut(duration, type, includeChildren)
                is SvgImage -> child.fadeOut(duration, type, includeChildren)
            }
        }
    }

    when (this) {
        is Rectangle -> {
            animateColor({ backgroundColor }, { backgroundColor = it }, backgroundColor and 0x00FFFFFF, duration, type) {
                visible = false
                onComplete?.invoke()
            }
            animateColor({ borderColor }, { borderColor = it }, borderColor and 0x00FFFFFF, duration, type)
        }
        is Text -> {
            animateColor({ textColor }, { textColor = it }, textColor and 0x00FFFFFF, duration, type) {
                visible = false
                onComplete?.invoke()
            }
        }
        is SvgImage -> {
            animateColor({ color }, { setSvgColor(it) }, color and 0x00FFFFFF, duration, type) {
                visible = false
                onComplete?.invoke()
            }
        }
        else -> animateFloat({ 1f }, {}, 0f, duration, type, AnimationType.ALPHA) {
            visible = false
            onComplete?.invoke()
        }
    }

    return this
}

fun <T : Element<T>> T.moveTo(
    x: Float,
    y: Float,
    duration: Long = 500,
    type: EasingType = EasingType.EASE_OUT,
    onComplete: (() -> Unit)? = null
): T {
    animatePosition(x, y, duration, type, onComplete)
    return this
}

fun <T : Element<T>> T.scaleTo(
    width: Float,
    height: Float,
    duration: Long = 300,
    type: EasingType = EasingType.EASE_OUT,
    onComplete: (() -> Unit)? = null
): T {
    animateSize(width, height, duration, type, onComplete)
    return this
}

fun <T : Element<T>> T.colorTo(
    color: Int,
    duration: Long = 300,
    type: EasingType = EasingType.LINEAR,
    onComplete: (() -> Unit)? = null
): T {
    when (this) {
        is Rectangle -> animateColor({ backgroundColor }, { backgroundColor = it }, color, duration, type, onComplete)
        is Text -> animateColor({ textColor }, { textColor = it }, color, duration, type, onComplete)
        is SvgImage -> animateColor({ this.color }, { setSvgColor(it) }, color, duration, type, onComplete)
    }
    return this
}

fun <T : Element<T>> T.slideIn(
    fromX: Float = -width,
    fromY: Float = 0f,
    duration: Long = 500,
    type: EasingType = EasingType.EASE_OUT,
    onComplete: (() -> Unit)? = null
): T {
    if (presetOriginalPosition == null) presetOriginalPosition = xConstraint to yConstraint

    val (targetX, targetY) = presetOriginalPosition!!
    xConstraint = fromX
    yConstraint = fromY
    visible = true

    animatePosition(targetX, targetY, duration, type, onComplete)
    return this
}

fun <T : Element<T>> T.bounceScale(
    scale: Float = 1.2f,
    duration: Long = 200,
    onComplete: (() -> Unit)? = null
): T {
    AnimationManager.stopAnimations("${hashCode()}:size", AnimationType.SIZE)
    if (presetOriginalSize == null) presetOriginalSize = width to height

    val (originalWidth, originalHeight) = presetOriginalSize!!

    animateSize(originalWidth * scale, originalHeight * scale, duration / 2, EasingType.EASE_OUT) {
        animateSize(originalWidth, originalHeight, duration / 2, EasingType.EASE_IN, onComplete)
    }

    return this
}
