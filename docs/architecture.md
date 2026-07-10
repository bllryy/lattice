# Architecture

Lattice replaces Knit and Vexel with one library. This document records the
major decisions and what changed relative to the two parents.

## Module layout

Single Gradle module; boundaries are packages with a strict dependency
direction (lower layers never import higher ones):

```
event/       EventBus, EventCall, lifecycle/tick/render events
client/      Minecraft wrappers: Client, Player, Chat, Clipboard, Desktop, Loader
input/       GLFW-backed Keys/Mouse/Keyboard, InputCode abstraction
render/      Renderer interface, NVGRenderer, Font/Image/Color/Gradient, Resolution, GLState
scheduler/   TickScheduler (game ticks), TimeScheduler (wall clock)
animation/   Animation core, manager, element extensions, presets
ui/          Element, Box, Window, UIScreen, layout enums, element events
ui/component Container, Rectangle, Text, SvgImage, Tooltip
ui/widget    Button, CheckBox, ColorPicker, Dropdown, Keybind, NumberInput, Slider, Switch, TextInput
ui/theme     Theme tokens
text/        Texts + TextBuilder (Component DSL)
command/     Commodore command DSL over Brigadier
util/        Number/String helpers
mixins/      (Java) Minecraft, GameRenderer, GlStateManager
```

A multi-module split was considered and rejected: the whole library is ~10k
lines and every layer targets the same Minecraft version; module plumbing
would outweigh the benefit.

## Key decisions

**One event bus.** Knit and Vexel each created their own `EventBus` instance,
so subscribers had to know which library fired an event. `Lattice.eventBus`
is the only bus. The unused class-hierarchy dispatch path (`checkHierarchy`)
was removed — subscribers register on concrete event classes.

**One screen class.** Knit's `KnitScreen` adapted vanilla `Screen` input and
Vexel's `VexelScreen` layered a `Window` plus render-event wiring on top.
`UIScreen` does both jobs in one class half the size.

**`Box` deduplicates scrolling.** Vexel's `Container` and `Rectangle` each
carried a full copy of the scroll/scrollbar/hover/padding logic (~300 lines
duplicated). `Box` owns padding, scrolling, scrollbar drawing and the
scroll-adjusted input handling; `Container` is `Box` with no drawing and
`Rectangle` is `Box` plus painting. The layout engine reads padding uniformly
through `Box` instead of type-switching on two unrelated classes.

**Int ARGB everywhere.** The renderer API mixed Int ARGB, `java.awt.Color`,
and Vexel's HSB `Color` class. The `Renderer` interface now takes Int ARGB
only; the HSB `Color` remains in `render/` as a utility (used by ColorPicker
internals and available to clients).

**Theme tokens.** Widgets previously hardcoded their palette. Widget
constructor defaults now read `Theme` (accent, surfaces, borders, text,
track/scrollbar, animation durations), initialized to the exact Vexel palette
so the rendered result is unchanged. Aether's theme system inspired the shape.

**Fabric-only, current-version-only.** Knit carried multi-loader preprocessor
blocks (Forge/NeoForge) and multi-version conditionals that this port could
never exercise. All of it is gone; `client.Loader` is a thin Fabric wrapper.

## Deleted dead code

- World rendering layer: `RenderContext` was all no-ops and
  `MixinWorldRenderer` threw unconditionally in the 26.1.2 port. Gone, along
  with `WorldRenderEvent` (14 event types that could never fire).
- `MixinClientCommonNetworkHandler`: empty mixin.
- `TickEvent.Server` and the server scheduler: nothing ever posted or ticked
  them.
- Duplicate mixin sources: both parents shipped identical `.java` files in
  `src/main/java` *and* `src/main/kotlin`, which broke `sourcesJar` on
  Gradle 9.
- `Renderer.createImage(id)` parameter: ignored by the implementation.
- `scrollbarIgnorePadding`/`scrollbarCustomPadding`: duplicated
  `scrollbarPadding`.

## Fixes

- **Linux natives**: Vexel shipped only `natives-macos-arm64` for NanoVG.
  Lattice ships linux, linux-arm64, windows, macos, macos-arm64 (as Aether
  does).
- **Framebuffer reflection cached**: `NVGRenderer.beginFrame` walked class
  hierarchies reflectively every frame to find `directStateAccess`/`getFbo`;
  the lookup now resolves once.
- **Render hook is lazy**: the GameRenderer mixin skips the whole NanoVG
  frame when nothing subscribes to `RenderEvent.Gui`.
- **`Mouse.Scaled.y`** used the width-based scale factor (copy-paste bug in
  Knit).
- **`TickScheduler` actually runs**: Knit's was never wired to a tick source;
  Lattice registers it on `TickEvent.Start` on first use.
- **`TimeScheduler.repeat` honored `initialDelayMillis`** only in one
  overload; the rewrite has one code path.
- **Animation preset leak**: `slideIn`/`bounceScale` stored original geometry
  in global maps keyed by `hashCode()`, never cleaned; the bookkeeping now
  lives on the element and dies with it.
- **`SvgImage` releases its NVG image** on destroy and no longer generates a
  random UUID cache key per recolor.

## Threading model

The event bus uses `CopyOnWriteArrayList`/`ConcurrentHashMap`; posting is
safe from any thread, but UI elements must only be touched on the render
thread. `TimeScheduler` runs on a background pool (use it + `display()` /
`TickScheduler.post` to hop back).
