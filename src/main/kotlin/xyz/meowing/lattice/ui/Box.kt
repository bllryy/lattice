@file:Suppress("UNCHECKED_CAST")

package xyz.meowing.lattice.ui

import xyz.meowing.lattice.ui.theme.Theme
import xyz.meowing.lattice.Lattice.renderer
import xyz.meowing.lattice.animation.EasingType
import xyz.meowing.lattice.animation.fadeIn
import xyz.meowing.lattice.animation.fadeOut

/** Base for elements with padding and optional vertical scrolling. */
abstract class Box<T : Box<T>>(
    var padding: FloatArray = floatArrayOf(0f, 0f, 0f, 0f),
    var scrollable: Boolean = false,
    widthType: Size = Size.Auto,
    heightType: Size = Size.Auto
) : Element<T>(widthType, heightType) {
    var scrollOffset: Float = 0f
    var showScrollbar: Boolean = true
    var scrollbarWidth: Float = 6f
    var scrollbarColor: Int = Theme.scrollbar
    var scrollbarRadius: Float = 3f
    var scrollbarPadding: Float = 0f
    var scissorBufferVertical: Float = 0f
    var scissorBufferHorizontal: Float = 2f

    private var isDraggingScrollbar = false
    private var scrollbarDragOffset = 0f

    private val viewWidth: Float get() = width - padding[1] - padding[3]
    private val viewHeight: Float get() = height - padding[0] - padding[2]

    private fun drawScrollbar() {
        if (!scrollable || !showScrollbar) return
        val contentHeight = getContentHeight()
        if (contentHeight <= viewHeight) return

        val scrollbarX = x + width - padding[1] - scrollbarWidth - scrollbarPadding
        val scrollbarHeight = (viewHeight / contentHeight) * viewHeight
        val scrollbarY = y + padding[0] + (scrollOffset / contentHeight) * viewHeight

        renderer.rect(scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeight, scrollbarColor, scrollbarRadius)
    }

    private fun isPointInScrollbar(mouseX: Float, mouseY: Float): Boolean {
        if (!scrollable || !showScrollbar) return false
        val contentHeight = getContentHeight()
        if (contentHeight <= viewHeight) return false

        val scrollbarX = x + width - padding[1] - scrollbarWidth - scrollbarPadding
        val scrollbarHeight = (viewHeight / contentHeight) * viewHeight
        val scrollbarY = y + padding[0] + (scrollOffset / contentHeight) * viewHeight

        return mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
            mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight
    }

    protected fun updateHoverStates(mouseX: Float, mouseY: Float) {
        val adjustedMouseY = if (scrollable) mouseY + scrollOffset else mouseY
        val wasHovered = isHovered
        isHovered = isPointInside(mouseX, mouseY)

        when {
            isHovered && !wasHovered -> {
                listeners.mouseEnter.forEach { it(MouseEvent.Move.Enter(mouseX, mouseY, this)) }
                onHoverChanged(true)
            }
            !isHovered && wasHovered -> {
                listeners.mouseExit.forEach { it(MouseEvent.Move.Exit(mouseX, mouseY, this)) }
                onHoverChanged(false)
            }
        }

        if (isHovered) listeners.mouseMove.forEach { it(MouseEvent.Move(mouseX, mouseY, this)) }

        children.reversed().forEach { child ->
            if (scrollable && !isMouseOnVisible(mouseX, mouseY)) {
                unhoverRecursive(child, mouseX, adjustedMouseY)
            } else {
                child.handleMouseMove(mouseX, adjustedMouseY)
            }
        }
    }

    protected open fun onHoverChanged(hovered: Boolean) {
        tooltipElement?.let { tooltip ->
            if (hovered) {
                tooltip.fadeIn(Theme.animNormal, EasingType.EASE_OUT)
                tooltip.innerText.fadeIn(Theme.animNormal, EasingType.EASE_OUT)
            } else {
                tooltip.fadeOut(Theme.animNormal, EasingType.EASE_OUT)
                tooltip.innerText.fadeOut(Theme.animNormal, EasingType.EASE_OUT)
            }
        }
    }

    private fun unhoverRecursive(element: Element<*>, mouseX: Float, mouseY: Float) {
        if (element.isHovered) {
            element.isHovered = false
            element.listeners.mouseExit.forEach { it(MouseEvent.Move.Exit(mouseX, mouseY, this)) }
        }
        element.children.forEach { unhoverRecursive(it, mouseX, mouseY) }
    }

    override fun handleMouseScroll(mouseX: Float, mouseY: Float, horizontal: Double, vertical: Double): Boolean {
        if (!visible) return false
        if (!isMouseOnVisible(mouseX, mouseY)) return false

        val adjustedMouseY = if (scrollable) mouseY + scrollOffset else mouseY
        val childHandled = children.reversed().any { it.handleMouseScroll(mouseX, adjustedMouseY, horizontal, vertical) }

        if (!childHandled && scrollable && isPointInside(mouseX, mouseY)) {
            val contentHeight = getContentHeight()

            if (contentHeight > viewHeight) {
                val scrollAmount = vertical.toFloat() * -30f
                val maxScroll = contentHeight - viewHeight
                scrollOffset = (scrollOffset + scrollAmount).coerceIn(0f, maxScroll)

                updateHoverStates(mouseX, mouseY)
                return true
            }
        }

        return childHandled
    }

    override fun handleMouseMove(mouseX: Float, mouseY: Float): Boolean {
        if (!visible) return false

        if (isDraggingScrollbar) {
            val contentHeight = getContentHeight()
            val relativeY = mouseY - scrollbarDragOffset - (y + padding[0])
            val scrollRatio = (relativeY / viewHeight).coerceIn(0f, 1f)
            val maxScroll = contentHeight - viewHeight
            scrollOffset = (scrollRatio * maxScroll).coerceIn(0f, maxScroll)
            updateHoverStates(mouseX, mouseY)
            return true
        }

        updateHoverStates(mouseX, mouseY)
        return isHovered
    }

    override fun handleMouseClick(mouseX: Float, mouseY: Float, button: Int): Boolean {
        if (!visible) return false

        if (isPointInScrollbar(mouseX, mouseY)) {
            isDraggingScrollbar = true
            val contentHeight = getContentHeight()
            val scrollbarY = y + padding[0] + (scrollOffset / contentHeight) * viewHeight
            scrollbarDragOffset = mouseY - scrollbarY
            return true
        }

        if (scrollable && !isMouseOnVisible(mouseX, mouseY)) return false

        val adjustedMouseY = if (scrollable) mouseY + scrollOffset else mouseY
        val childHandled = children.reversed().any { it.handleMouseClick(mouseX, adjustedMouseY, button) }

        return when {
            childHandled -> true
            isPointInside(mouseX, mouseY) -> {
                isPressed = true
                focus()
                listeners.mouseClick.any { it(MouseEvent.Click(mouseX, mouseY, button, this)) } || listeners.mouseClick.isEmpty()
            }
            else -> false
        }
    }

    override fun handleMouseRelease(mouseX: Float, mouseY: Float, button: Int): Boolean {
        if (!visible) return false

        if (isDraggingScrollbar) {
            isDraggingScrollbar = false
            return true
        }

        val adjustedMouseY = if (scrollable) mouseY + scrollOffset else mouseY
        val wasPressed = isPressed
        isPressed = false

        val childHandled = if (scrollable && !isMouseOnVisible(mouseX, mouseY)) {
            false
        } else {
            children.reversed().any { it.handleMouseRelease(mouseX, adjustedMouseY, button) }
        }

        return childHandled ||
            (
                wasPressed &&
                    isPointInside(mouseX, mouseY) &&
                    (
                        listeners.mouseRelease.any { it(MouseEvent.Release(mouseX, mouseY, button, this)) } ||
                            listeners.mouseRelease.isEmpty()
                        )
                )
    }

    fun getContentHeight(): Float {
        val visibleChildren = children.filter { !it.isFloating }
        if (visibleChildren.isEmpty()) return 0f

        val bottomChild = visibleChildren.maxByOrNull { it.y + it.height } ?: return 0f
        return bottomChild.y + bottomChild.height - (y + padding[0])
    }

    fun isMouseOnVisible(mouseX: Float, mouseY: Float): Boolean {
        if (!scrollable) return true

        val contentX = x + padding[3]
        val contentY = y + padding[0]

        return mouseX >= contentX && mouseX <= contentX + viewWidth && mouseY >= contentY && mouseY <= contentY + viewHeight
    }

    public override fun getAutoWidth(): Float {
        val visibleChildren = children.filter { it.visible && !it.isFloating }
        if (visibleChildren.isEmpty()) return padding[1] + padding[3]

        val minX = visibleChildren.minOf { it.x }
        val maxX = visibleChildren.maxOf { it.x + it.width }

        val calculated = (maxX - minX) + padding[3] + padding[1]
        return maxAutoWidth?.let { calculated.coerceAtMost(it) } ?: calculated
    }

    public override fun getAutoHeight(): Float {
        val visibleChildren = children.filter { it.visible && !it.isFloating }
        if (visibleChildren.isEmpty()) return padding[0] + padding[2]

        val minY = visibleChildren.minOf { it.y }
        val maxY = visibleChildren.maxOf { it.y + it.height }

        val calculated = (maxY - minY) + padding[0] + padding[2]
        return maxAutoHeight?.let { calculated.coerceAtMost(it) } ?: calculated
    }

    override fun renderChildren(mouseX: Float, mouseY: Float) {
        if (scrollable) {
            renderer.push()
            renderer.pushScissor(
                x + padding[3] - scissorBufferHorizontal,
                y + padding[0] - scissorBufferVertical,
                viewWidth + scissorBufferHorizontal * 2,
                viewHeight + scissorBufferVertical * 2
            )
            renderer.translate(0f, -scrollOffset)
        }

        children.forEach { it.render(mouseX, mouseY) }

        if (scrollable) {
            renderer.popScissor()
            renderer.pop()
        }

        if (showScrollbar && (isHovered || isDraggingScrollbar)) drawScrollbar()
    }

    fun isVisibleInScrollableParents(): Boolean {
        var current: Any? = this
        while (current != null) {
            when (current) {
                is Element<*> -> {
                    if (!current.visible) return false
                    if (current is Box<*> && current.scrollable) {
                        val centerX = getScreenX() + width / 2
                        val centerY = getScreenY() + height / 2
                        if (!current.isMouseOnVisible(centerX, centerY)) return false
                    }
                    current = current.parent
                }
                else -> break
            }
        }
        return true
    }

    fun getScreenX(): Float = x

    fun getScreenY(): Float {
        var totalScrollOffset = 0f
        var current = parent
        while (current != null) {
            when (current) {
                is Box<*> -> totalScrollOffset += current.scrollOffset
                is Window -> break
            }
            current = if (current is Element<*>) current.parent else null
        }
        return y - totalScrollOffset
    }

    fun scrollable(enabled: Boolean): T {
        scrollable = enabled
        return this as T
    }

    fun setScissorBuffer(vertical: Float, horizontal: Float): T {
        scissorBufferVertical = vertical
        scissorBufferHorizontal = horizontal
        return this as T
    }

    fun showScrollbar(show: Boolean): T {
        showScrollbar = show
        return this as T
    }

    fun scrollbarWidth(width: Float): T {
        scrollbarWidth = width
        return this as T
    }

    fun scrollbarColor(color: Int): T {
        scrollbarColor = color
        return this as T
    }

    fun scrollbarRadius(radius: Float): T {
        scrollbarRadius = radius
        return this as T
    }

    fun scrollbarPadding(padding: Float): T {
        scrollbarPadding = padding
        return this as T
    }

    fun padding(top: Float = 0f, right: Float = 0f, bottom: Float = 0f, left: Float = 0f): T {
        padding[0] = top
        padding[1] = right
        padding[2] = bottom
        padding[3] = left
        return this as T
    }

    fun padding(all: Float): T = padding(all, all, all, all)

    fun width(newWidth: Float): T {
        width = newWidth
        return this as T
    }

    fun height(newHeight: Float): T {
        height = newHeight
        return this as T
    }
}
