# Lattice 

# MIRROR REPO 

Unified UI framework for Minecraft clients on Fabric (Minecraft 26.1.2, 26.2 and 26.3).
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

Lattice is published per Minecraft version as `xyz.meowing:lattice-<minecraft>-fabric`:

| Minecraft | Artifact                | Latest |
|-----------|-------------------------|--------|
| 26.3      | `lattice-26.3-fabric`   | 1.1.0  |
| 26.2      | `lattice-26.2-fabric`   | 1.1.0  |
| 26.1.2    | `lattice-26.1.2-fabric` | 1.1.0  |

### Gradle

```kotlin
repositories {
    maven("https://bllry.com/maven")
}

dependencies {
    implementation("xyz.meowing:lattice-26.3-fabric:1.1.0")
}
```

### Maven

```xml
<repository>
  <id>bllry</id>
  <url>https://bllry.com/maven</url>
</repository>

<dependency>
  <groupId>xyz.meowing</groupId>
  <artifactId>lattice-26.3-fabric</artifactId>
  <version>1.1.0</version>
</dependency>
```

On 26.2 and newer, Lattice renders only with Minecraft's OpenGL backend (the
default). If a player switches the Graphics API to Vulkan, Lattice logs a warning
and skips drawing.

The public API is version-agnostic: `client.Client`, `input.*`, and `render.*`
absorb the per-version differences, so a mod written against one version
usually compiles unchanged against the others. Use `Client.screen` /
`Client.openScreen(...)` rather than `minecraft.screen` /
`minecraft.setScreen(...)` (screen handling moved off `Minecraft` in 26.2), and
compare against `Keys.*` instead of hardcoding key codes (values are GLFW key
codes before 26.3 and SDL scancodes from 26.3).

## Documentation

Full docs live in [`docs/`](docs/README.md) — start with
[getting started](docs/getting-started.md), or the
[Knit/Vexel migration guide](docs/migration.md). Design rationale is in
[docs/architecture.md](docs/architecture.md).

A runnable demo mod lives in [`example/`](example/README.md): `/lattice`
opens a screen showing every widget; `/lattice hud`, `/lattice chat`, and
`/lattice greet` demo the render, text, and command APIs.

## Building

Requires JDK 25 (with Nix: `nix-shell --run './gradlew build'`).

The build uses [Stonecutter](https://stonecutter.kikugie.dev/) to compile the
shared `src/` for every Minecraft version listed in `settings.gradle.kts`.
Version-specific code is marked with `//? if >=26.3 { ... }` comments, and
per-version dependency versions live in `stonecutter.properties.toml`.

- `./gradlew build` builds every version into `versions/<minecraft>/build/libs/`
  (the example mod lands in `example/versions/<minecraft>/build/libs/`).
- `./gradlew :26.3:build` builds a single version.
- `./gradlew "Set active project to 26.2"` switches `src/` to show 26.2's code
  in the IDE. Run `./gradlew "Reset active project"` (back to 26.3) before committing.
- `./gradlew :26.3:deployMods` copies the lattice and example jars into a mods
  folder (override with `-PmodsDir=...`).
- `./gradlew publishToMavenLocal` installs all versions to `~/.m2`.

### Releasing

CI (`.forgejo/workflows/ci.yml`) builds every version on each push. To release,
bump `mod_version` in `gradle.properties`, commit, and push a matching tag
(`v1.1.0`). The tag build publishes all versions to `https://bllry.com/maven`
using the `MAVEN_USERNAME` / `MAVEN_PASSWORD` repository secrets.

To publish by hand, run `./gradlew publishAllPublicationsToBllryRepository` with
`MAVEN_URL`, `MAVEN_USERNAME` and `MAVEN_PASSWORD` set. Without `MAVEN_URL` it
writes a plain Maven repository to `build/maven/` instead.

Runtime NanoVG natives ship for linux, linux-arm64, windows, macos, and
macos-arm64.

## License

GPL-3.0-or-later. Portions of the rendering layer derive from OdinFabric
(BSD-3-Clause); see NOTICE.md.
# lattice
