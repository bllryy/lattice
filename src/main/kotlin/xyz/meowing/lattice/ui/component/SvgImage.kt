package xyz.meowing.lattice.ui.component

import xyz.meowing.lattice.Lattice.renderer
import xyz.meowing.lattice.animation.EasingType
import xyz.meowing.lattice.animation.animateFloat
import xyz.meowing.lattice.ui.Element
import xyz.meowing.lattice.ui.Pos
import xyz.meowing.lattice.ui.Size

class SvgImage(
    var svgPath: String = "",
    startingWidth: Float = 80f,
    startingHeight: Float = 80f,
    color: Int = 0xFFFFFFFF.toInt()
) : Element<SvgImage>() {
    var color = color
        private set

    var image = renderer.createImage(svgPath, startingWidth.toInt(), startingHeight.toInt(), color)
        private set

    var rotation: Float = 0f

    init {
        setPositioning(Pos.ParentPixels, Pos.ParentPixels)
        ignoreMouseEvents()
        setSizing(startingWidth, Size.Pixels, startingHeight, Size.Pixels)
    }

    override fun onRender(mouseX: Float, mouseY: Float) {
        if (svgPath.isEmpty()) return

        val centerX = x + width / 2f
        val centerY = y + height / 2f

        if (rotation != 0f) {
            renderer.push()
            renderer.translate(centerX, centerY)
            renderer.rotate(Math.toRadians(rotation.toDouble()).toFloat())
            renderer.translate(-centerX, -centerY)
        }

        renderer.image(image, x, y, width, height)

        if (rotation != 0f) {
            renderer.pop()
        }
    }

    override fun destroy() {
        renderer.deleteImage(image)
        super.destroy()
    }

    fun rotateTo(angle: Float, duration: Long = 300, type: EasingType = EasingType.EASE_OUT, onComplete: (() -> Unit)? = null): SvgImage {
        animateFloat({ rotation }, { rotation = it }, angle, duration, type, onComplete = onComplete)
        return this
    }

    fun setSvgColor(newColor: Int) {
        if (color != newColor) {
            color = newColor
            renderer.deleteImage(image)
            image = renderer.createImage(svgPath, width.toInt(), height.toInt(), color)
        }
    }
}
