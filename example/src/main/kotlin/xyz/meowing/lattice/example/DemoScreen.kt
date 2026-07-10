package xyz.meowing.lattice.example

import xyz.meowing.lattice.animation.bounceScale
import xyz.meowing.lattice.client.Chat
import xyz.meowing.lattice.ui.Element
import xyz.meowing.lattice.ui.Pos
import xyz.meowing.lattice.ui.Size
import xyz.meowing.lattice.ui.UIScreen
import xyz.meowing.lattice.ui.component.Rectangle
import xyz.meowing.lattice.ui.component.Text
import xyz.meowing.lattice.ui.theme.Theme
import xyz.meowing.lattice.ui.widget.Button
import xyz.meowing.lattice.ui.widget.CheckBox
import xyz.meowing.lattice.ui.widget.ColorPicker
import xyz.meowing.lattice.ui.widget.Dropdown
import xyz.meowing.lattice.ui.widget.Keybind
import xyz.meowing.lattice.ui.widget.NumberInput
import xyz.meowing.lattice.ui.widget.Slider
import xyz.meowing.lattice.ui.widget.Switch
import xyz.meowing.lattice.ui.widget.TextInput

/** Showcases every built-in widget, the layout system, theming, and animations. */
class DemoScreen : UIScreen("Lattice Demo") {
    override fun afterInitialization() {
        val panel = Rectangle(
            backgroundColor = 0xF0141414.toInt(),
            borderColor = Theme.border,
            borderRadius = 10f,
            borderThickness = 1f,
            padding = floatArrayOf(14f, 14f, 14f, 14f),
            widthType = Size.Pixels,
            heightType = Size.Pixels
        )
            .setSizing(440f, Size.Pixels, 440f, Size.Pixels)
            .setPositioning(Pos.ScreenCenter, Pos.ScreenCenter)
            .dropShadow(shadowBlur = 40f)
            .childOf(window)

        Text("Lattice Demo", fontSize = 18f, shadowEnabled = true)
            .setPositioning(0f, Pos.ParentCenter, 0f, Pos.ParentPixels)
            .childOf(panel)

        // Size.Fill stretches to the remaining panel height below the title.
        val content = Rectangle(
            backgroundColor = 0x00000000,
            padding = floatArrayOf(4f, 10f, 4f, 4f),
            widthType = Size.Percent,
            heightType = Size.Fill,
            scrollable = true
        )
            .setSizing(100f, Size.Percent, 0f, Size.Fill)
            .setPositioning(0f, Pos.ParentPixels, 10f, Pos.AfterSibling)
            .childOf(panel)

        row(content, "Button") {
            val button = Button(text = "Click me")
                .addTooltip("Tooltips fade in on hover")
                .alignRight()
                .childOf(it)

            button.onClick {
                button.bounceScale()
                Chat.fakeMessage("Button clicked!")
                true
            }
        }

        row(content, "Switch (toggles the HUD)") {
            Switch()
                .onValueChange { enabled -> DemoHud.enabled = enabled as Boolean }
                .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
                .alignRight()
                .childOf(it)
                .setEnabled(DemoHud.enabled, animated = false, silent = true)
        }

        row(content, "CheckBox") {
            CheckBox()
                .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
                .alignRight()
                .childOf(it)
        }

        row(content, "Slider") { r ->
            val label = Text("50", textColor = Theme.textMuted, fontSize = 12f)
                .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
                .childOf(r)

            Slider(value = 50f, minValue = 0f, maxValue = 100f, step = 1f)
                .onValueChange { value -> label.text = "${(value as Float).toInt()}" }
                .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
                .alignRight()
                .childOf(r)

            label.setPositioning(-215f, Pos.ParentPixels, 0f, Pos.ParentCenter).alignRight()
        }

        row(content, "Dropdown") {
            Dropdown(options = listOf("Linear", "Ease in", "Ease out", "Ease in/out"))
                .setSizing(150f, Size.Pixels, 26f, Size.Pixels)
                .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
                .alignRight()
                .childOf(it)
        }

        row(content, "TextInput") {
            TextInput(placeholder = "Type here…")
                .setSizing(150f, Size.Pixels, 26f, Size.Pixels)
                .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
                .alignRight()
                .childOf(it)
        }

        row(content, "NumberInput") {
            NumberInput(initialValue = 42, allowNegative = true)
                .setSizing(80f, Size.Pixels, 26f, Size.Pixels)
                .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
                .alignRight()
                .childOf(it)
        }

        row(content, "Keybind") {
            Keybind()
                .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
                .alignRight()
                .childOf(it)
        }

        row(content, "ColorPicker") {
            ColorPicker(initialColor = java.awt.Color(Theme.accent, true))
                .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
                .alignRight()
                .childOf(it)
        }

        row(content, "Theme") {
            Button(text = "Cycle accent")
                .onClick {
                    Theme.accent = when (Theme.accent) {
                        0xFF4c87f9.toInt() -> 0xFF10B981.toInt()
                        0xFF10B981.toInt() -> 0xFFD32F2F.toInt()
                        else -> 0xFF4c87f9.toInt()
                    }
                    // Theme tokens apply at construction, so rebuild the screen.
                    onClose()
                    DemoScreen().display()
                    true
                }
                .alignRight()
                .childOf(it)
        }
    }

    private fun row(parent: Element<*>, label: String, build: (Rectangle) -> Unit) {
        val row = Rectangle(
            backgroundColor = Theme.surface,
            borderRadius = 6f,
            padding = floatArrayOf(8f, 10f, 8f, 10f),
            widthType = Size.Percent,
            heightType = Size.Pixels
        )
            .setSizing(96f, Size.Percent, 42f, Size.Pixels)
            .setPositioning(0f, Pos.ParentPixels, 6f, Pos.AfterSibling)
            .childOf(parent)

        Text(label, fontSize = 13f)
            .setPositioning(0f, Pos.ParentPixels, 0f, Pos.ParentCenter)
            .childOf(row)

        build(row)
    }
}
