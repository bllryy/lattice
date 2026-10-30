# Getting started

## Dependency

Add the Maven repository and the artifact for your Minecraft version
(`lattice-26.1.2-fabric`, `lattice-26.2-fabric` or `lattice-26.3-fabric`):

```kotlin
repositories {
    maven("https://bllry.com/maven")
}

dependencies {
    implementation("xyz.meowing:lattice-26.3-fabric:1.1.0")
}
```

To try local changes, run `./gradlew publishToMavenLocal` in this repository
and add `mavenLocal()` to your repositories.

Declare `"lattice": "*"` in your `fabric.mod.json` `depends` block so the
loader orders things correctly.

## A first screen

```kotlin
import xyz.meowing.lattice.ui.*
import xyz.meowing.lattice.ui.component.*
import xyz.meowing.lattice.ui.theme.Theme
import xyz.meowing.lattice.ui.widget.Button

class HelloScreen : UIScreen("Hello") {
    override fun afterInitialization() {
        val panel = Rectangle(backgroundColor = Theme.surface, borderRadius = 8f)
            .setSizing(300f, Size.Pixels, 160f, Size.Pixels)
            .setPositioning(Pos.ScreenCenter, Pos.ScreenCenter)
            .childOf(window)

        Text("Hello, Lattice", fontSize = 16f)
            .setPositioning(0f, Pos.ParentCenter, 20f, Pos.ParentPixels)
            .childOf(panel)

        Button(text = "Close")
            .setPositioning(Pos.ParentCenter, Pos.ParentCenter)
            .onClick { onClose(); true }
            .childOf(panel)
    }
}

HelloScreen().display()   // opens on the next tick, thread-safe
```

`afterInitialization` runs once; build your element tree there and attach
roots with `childOf(window)`. Input, resize, and cleanup are handled by
`UIScreen`. Override `onRenderGui()` for per-frame drawing after the window
renders, `onCloseGui()` for teardown.

## Drawing without a screen (HUDs)

```kotlin
import xyz.meowing.lattice.Lattice.eventBus
import xyz.meowing.lattice.Lattice.renderer
import xyz.meowing.lattice.event.RenderEvent

eventBus.register<RenderEvent.Gui> {
    renderer.text("hud text", 10f, 10f, 12f, 0xFFFFFFFF.toInt())
}
```

The event fires inside a prepared NanoVG frame every GUI frame; draw with
`Lattice.renderer` directly. Coordinates are window pixels
(`render.Resolution.windowWidth/Height`).
