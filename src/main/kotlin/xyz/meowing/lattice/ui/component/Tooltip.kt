package xyz.meowing.lattice.ui.component

import xyz.meowing.lattice.ui.theme.Theme
import xyz.meowing.lattice.ui.Box
import xyz.meowing.lattice.ui.Element
import xyz.meowing.lattice.ui.Pos
import xyz.meowing.lattice.ui.Size
import xyz.meowing.lattice.ui.TooltipPosition

class Tooltip(
    backgroundColor: Int = Theme.surface,
    borderColor: Int = Theme.surfaceBorder,
    borderRadius: Float = 4f,
    borderThickness: Float = 1f,
    var padding: FloatArray = floatArrayOf(4f, 4f, 4f, 4f),
    hoverColor: Int? = Theme.surface,
    pressedColor: Int? = Theme.surface,
    widthType: Size = Size.Auto,
    heightType: Size = Size.Auto,
    var position: TooltipPosition = TooltipPosition.Top
) : Element<Tooltip>(widthType, heightType) {
    val backgroundRect = Rectangle(
        backgroundColor,
        borderColor,
        borderRadius,
        borderThickness,
        padding,
        hoverColor,
        pressedColor,
        widthType,
        heightType
    )
        .childOf(this)

    val innerText = Text("Tooltip", 0xFFFFFFFF.toInt(), 12f)
        .setPositioning(Pos.ParentCenter, Pos.ParentCenter)
        .childOf(backgroundRect)

    override var visible: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                cache.invalidate()
                invalidateChildrenCache()
            }
            if (value) {
                backgroundRect.visible = true
                backgroundRect.width = width
                backgroundRect.height = height
            } else {
                backgroundRect.visible = false
            }
        }

    init {
        setSizing(Size.Auto, Size.Auto)
        updatePosition()
        ignoreMouseEvents()
        setFloating()
        backgroundRect.visible = false
        innerText.visible = false
    }

    fun setPosition(newPosition: TooltipPosition): Tooltip {
        position = newPosition
        updatePosition()
        return this
    }

    private fun updatePosition() {
        val parentPadding = (parent as? Box<*>)?.padding ?: floatArrayOf(0f, 0f, 0f, 0f)
        val offset = 24f

        when (position) {
            TooltipPosition.Top -> {
                setPositioning(0f, Pos.ParentCenter, -offset - parentPadding[0], Pos.ParentPixels)
            }
            TooltipPosition.Bottom -> {
                setPositioning(0f, Pos.ParentCenter, offset + parentPadding[2], Pos.ParentPixels)
                alignBottom()
            }
            TooltipPosition.Left -> {
                setPositioning(-offset - parentPadding[3], Pos.ParentPixels, 0f, Pos.ParentCenter)
            }
            TooltipPosition.Right -> {
                setPositioning(offset + parentPadding[1], Pos.ParentPixels, 0f, Pos.ParentCenter)
                alignRight()
            }
        }
    }

    override fun getAutoWidth(): Float = backgroundRect.getAutoWidth()

    override fun getAutoHeight(): Float = backgroundRect.getAutoHeight()

    override fun onRender(mouseX: Float, mouseY: Float) {}
}
