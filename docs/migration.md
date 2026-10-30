# Migrating from Knit / Vexel

Lattice replaces both libraries with one dependency. Remove `knit` and
`vexel` from your build and `fabric.mod.json`, add `lattice`, then apply the
mappings below. Behavior is preserved unless noted in
[architecture.md](architecture.md#deleted-dead-code).

## API mapping

| Before | After |
| --- | --- |
| `Knit.EventBus` / `Vexel.eventBus` | `Lattice.eventBus` |
| `VexelScreen` + `KnitScreen` | `ui.UIScreen` |
| `VexelElement` | `ui.Element` |
| `VexelWindow` | `ui.Window` |
| `components.core.*` (Container, Rectangle, Text, SvgImage, Tooltip) | `ui.component.*` |
| `elements.*` (Button, Slider, …) | `ui.widget.*` |
| `components.base.enums.*` (Pos, Size, Alignment, Offset) | `ui.*` |
| `animations.*` (extensions, presets, EasingType) | `animation.*` |
| `api.RenderAPI` / `api.nvg.NVGRenderer` | `render.Renderer` / `render.NVGRenderer` |
| `api.style.{Color, Font, Gradient, Image}` | `render.*` |
| `GuiEvent.Render` | `event.RenderEvent.Gui` |
| `KnitClient` | `client.Client` (`client` property is now `minecraft`) |
| `KnitPlayer` / `KnitChat` / `KnitClipboard` / `KnitDesktop` / `KnitLoader` | `client.Player` / `Chat` / `Clipboard` / `Desktop` / `Loader` |
| `KnitKeys` / `KnitMouse` / `KnitKeyboard` / `KnitInputs` | `input.Keys` / `Mouse` / `Keyboard` / `Inputs` |
| `KnitKey` / `KnitMouseButton` / `KnitInputCode` | `input.Key` / `MouseButton` / `InputCode` |
| `KnitResolution` | `render.Resolution` |
| `KnitText` / `text.asKnit()` | `text.Texts` / `asBuilder()` |
| `api.command.*` (Commodore) | `command.*` (unchanged API) |
| `api.utils.{NumberUtils, StringUtils}` | `util.*` |
| `TickScheduler.Client.x` | `TickScheduler.x` (now actually ticks) |

## Behavioral changes

- **One event bus.** Anything registered on Vexel's bus moves to
  `Lattice.eventBus`. `EventBus.post` no longer takes `checkHierarchy` —
  subscribe to concrete event classes.
- **Renderer takes Int ARGB only**: `dropShadow` and `createImage` no longer
  accept `java.awt.Color`; `createImage` lost its unused `id` parameter.
- **Element listener aliases removed**: `mouseClickListeners` →
  `listeners.mouseClick` (the fluent `onClick`-style methods are unchanged).
- **Removed as dead code**: `WorldRenderEvent`/`RenderContext` (were no-op
  stubs), `TickEvent.Server`, the server tick scheduler, timer API on
  `TickScheduler`, `scrollbarIgnorePadding`/`scrollbarCustomPadding`
  (use `scrollbarPadding`).
- **Assets**: `assets/vexel/*` (default font, checkmark, dropdown icons) are
  now `assets/lattice/*`.
- **Widget palette** now flows through `ui.theme.Theme`; values are
  unchanged, but you can retheme globally.
- **Screen access is version-agnostic through `Client`.** Vanilla moved the
  open screen off `Minecraft` onto `Gui` in 26.2. Use `Client.screen` and
  `Client.openScreen(...)` (and `Client.chat`, `Client.mainRenderTarget`)
  instead of `minecraft.screen` / `minecraft.setScreen(...)` so your code
  compiles on every supported version.
- **Key codes follow Minecraft, not GLFW.** `input.Keys` values are
  `InputConstants` key codes — GLFW key codes before 26.3, SDL scancodes from
  26.3. Compare against `Keys.*` rather than hardcoding numeric codes.
