# Lattice

Unified UI framework for Minecraft clients on Fabric (Minecraft 26.1.2).
Lattice supersedes **Knit** (client base library) and **Vexel** (NanoVG UI
library), merging both into one dependency with a single event bus, one input
abstraction, and one screen stack.

## Features

- NanoVG vector renderer (rects, gradients, text, images, SVG, shadows)
- Constraint-based layout with scrollable containers and a full widget set
- Semantic theming (`Theme`) — retheme every widget in one place
- Time-based animations with easing and presets
- Single event bus with client lifecycle, tick, and render events
- `UIScreen` bridging vanilla screens, plus direct-draw HUD hooks
- Commodore command DSL over Brigadier, fluent `Component` text builder
- Tick and wall-clock schedulers, client/input utilities

## Install
```
maven { url = 'https://bllry.com/maven' }
```
```
### Version 1.0.0

### Gradle
```
implementation 'xyz.meowing:lattice-26.1.2-fabric:1.0.0'
```

Maven
```
<dependency>
  <groupId>xyz.meowing</groupId>
  <artifactId>lattice-26.1.2-fabric</artifactId>
  <version>1.0.0</version>
</dependency>
```
## Documentation

Full docs live in [`docs/`](docs/README.md) — start with
[getting started](docs/getting-started.md), or the
[Knit/Vexel migration guide](docs/migration.md). Design rationale is in
[docs/architecture.md](docs/architecture.md).

A runnable demo mod lives in [`example/`](example/README.md): `/lattice`
opens a screen showing every widget; `/lattice hud`, `/lattice chat`, and
`/lattice greet` demo the render, text, and command APIs.

## Building

With Nix: `nix-shell --run './gradlew build'` (requires JDK 25 otherwise).
Publish to your local Maven repository with `./gradlew publish`
(artifact `xyz.meowing:lattice-26.1.2-fabric`).

Runtime NanoVG natives ship for linux, linux-arm64, windows, macos, and
macos-arm64.

## License

GPL-3.0-or-later. Portions of the rendering layer derive from OdinFabric
(BSD-3-Clause); see NOTICE.md.
# lattice
