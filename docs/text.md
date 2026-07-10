# Text

`xyz.meowing.lattice.text` wraps vanilla `Component` in a fluent builder.

```kotlin
import xyz.meowing.lattice.text.*

val message = buildText {
    text("Lattice ") { color(0x4c87f9); bold() }
    text("docs: ")
    text("click") { green(); underlined(); openUrl("https://example.com") }
    space()
    text("hover") { yellow(); onHover("shown on hover") }
}

Chat.fakeMessage(message)          // client-side chat line
```

Entry points:

- `Texts.literal("...")` / `"...".asText()` — single styled component.
- `Texts.fromFormatted("§ahello")` / `"...".asFormattedText()` — parse legacy
  `§` codes.
- `component.asBuilder()` — wrap an existing vanilla `Component`.
- `buildText { }` — chain of parts (as above).

Builder styling: the 16 named colors (`red()`, `gold()`, …) or
`color(rgb)` / `color("#RRGGBB")`, plus `bold()`, `italic()`, `underlined()`,
`strikethrough()`, `obfuscated()`, `insertion()`.

Interactivity: `runCommand("/cmd")`, `suggestCommand`, `copyToClipboard`,
`openUrl`, `changePage`, `onHover(text | builder | HoverEvent)`.

Convert with `toVanilla()` (alias `build()`); `string()` flattens to plain
text. `ColorCodes` and `FormattingCodes` expose the legacy palette and
`§`-code helpers (`strip`, `translateAlternate`).
