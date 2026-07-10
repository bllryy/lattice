package xyz.meowing.lattice.ui

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import xyz.meowing.lattice.Lattice.eventBus
import xyz.meowing.lattice.client.Client.minecraft
import xyz.meowing.lattice.event.EventCall
import xyz.meowing.lattice.event.RenderEvent
import xyz.meowing.lattice.input.Keys
import xyz.meowing.lattice.input.Mouse
import xyz.meowing.lattice.scheduler.TimeScheduler

/** Base screen backed by a Lattice [Window], rendered through the NanoVG overlay hook. */
abstract class UIScreen(screenName: String = "Lattice-Screen") : Screen(Component.literal(screenName)) {
    val window = Window()

    private var renderEvent: EventCall? = null
    private var hasInitialized = false
    private var lastX = -1.0
    private var lastY = -1.0

    /** Called once, the first time the screen initializes. Build the UI here. */
    open fun afterInitialization() {}

    /** Called every frame after the window and animations render. */
    open fun onRenderGui() {}

    open fun onResizeGui() {}

    open fun onCloseGui() {}

    final override fun init() {
        if (!hasInitialized) {
            hasInitialized = true
            afterInitialization()

            renderEvent = eventBus.register<RenderEvent.Gui> {
                if (minecraft.screen == this) {
                    window.draw()
                    onRenderGui()
                }
            }
        } else {
            onResizeGui()
            window.onWindowResize()
        }
        super.init()
    }

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        val newX = Mouse.Raw.x
        val newY = Mouse.Raw.y
        if (newX != lastX || newY != lastY) {
            window.mouseMove()
            lastX = newX
            lastY = newY
        }
    }

    override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        return window.mouseClick(click.button()) || super.mouseClicked(click, doubled)
    }

    override fun mouseReleased(click: MouseButtonEvent): Boolean {
        return window.mouseRelease(click.button()) || super.mouseReleased(click)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        window.mouseScroll(horizontalAmount, verticalAmount)
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)
    }

    override fun keyPressed(input: net.minecraft.client.input.KeyEvent): Boolean {
        val handled = window.charType(input.key(), input.scancode(), ' ')
        if (!handled && input.key() == Keys.KEY_ESCAPE.code) {
            onClose()
            return true
        }
        return handled || super.keyPressed(input)
    }

    override fun charTyped(input: CharacterEvent): Boolean {
        return window.charType(0, 0, input.codepoint().toChar()) || super.charTyped(input)
    }

    override fun onClose() {
        window.cleanup()
        renderEvent?.unregister()
        renderEvent = null
        hasInitialized = false
        onCloseGui()
        super.onClose()
    }

    /** Opens this screen on the next client tick, safe to call from any thread. */
    fun display() {
        TimeScheduler.schedule(50) {
            minecraft.setScreen(this@UIScreen)
        }
    }
}
