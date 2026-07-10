# Widgets

All widgets live in `xyz.meowing.lattice.ui.widget`, extend `Element`, take
their colors from [`Theme`](theming.md) by default, and report changes
through `onValueChange { value -> }`.

| Widget | Value pushed | Notes |
| --- | --- | --- |
| `Button` | — | `onClick`; text/hover/pressed colors, fluent styling |
| `Switch` | `Boolean` | animated thumb; `setEnabled(value, animated, silent)` |
| `CheckBox` | `Boolean` | SVG checkmark; `setChecked(value, animated, silent)` |
| `Slider` | `Float` | `minValue`/`maxValue`/`step`, draggable + click-to-set |
| `Dropdown` | `Int` (index) | floating popup list; `options: List<String>` |
| `TextInput` | `String` | caret, selection, clipboard (ctrl-C/X/V), placeholder |
| `NumberInput` | number as `String` | `allowDecimals`, `allowNegative` |
| `Keybind` | `Int` (key code) | click, then press a key; ESC clears |
| `ColorPicker` | `java.awt.Color` | floating HSB panel with alpha + hex field |

Typical usage:

```kotlin
Slider(value = 50f, minValue = 0f, maxValue = 100f, step = 1f)
    .onValueChange { v -> config.volume = v as Float }
    .childOf(row)

Dropdown(options = listOf("A", "B", "C"))
    .setSizing(150f, Size.Pixels, 26f, Size.Pixels)
    .onValueChange { i -> config.mode = i as Int }
    .childOf(row)
```

`onValueChange` payloads are typed `Any`; cast to the widget's value type
from the table above.

Widgets set sensible default sizes in their constructors (e.g. `Slider`
200×24, `CheckBox` 20×20); override with `setSizing`. `TextInput` and
`NumberInput` need explicit sizing.

Composite widgets expose their parts (`Button.background`,
`Button.innerText`, `Slider.thumb`, …) for fine-grained styling.
