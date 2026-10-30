# Lattice Example

A small Fabric client mod that exercises the framework end to end. Built for
every Minecraft version as part of the root Gradle build (`./gradlew build`),
into `example/versions/<minecraft>/build/libs/`.

In game:

- `/lattice` (alias `/latticedemo`) — opens the demo screen: every built-in
  widget in a scrollable panel, tooltips, `Size.Fill` layout, bounce/fade
  animations, and a button that retheme-and-rebuilds via `Theme`.
- `/lattice hud` — toggles a HUD overlay drawn directly with the renderer
  from `RenderEvent.Gui` (no screen involved).
- `/lattice chat` — chat message built with the `buildText { }` DSL
  (colors, hover, click-to-run).
- `/lattice greet <name> <times>` — Commodore parsing typed lambda
  parameters as command arguments, spaced out with `TickScheduler`.

Source map:

- `LatticeExample.kt` — entrypoint, command tree, text DSL.
- `DemoScreen.kt` — `UIScreen`, layout constraints, all widgets, theming.
- `DemoHud.kt` — event-bus render hook with direct renderer calls.
