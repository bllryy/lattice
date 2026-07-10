# Theming

`xyz.meowing.lattice.ui.theme.Theme` holds the semantic tokens every widget
default reads:

| Token | Role |
| --- | --- |
| `accent` | primary accent (slider fill, switch on-state, checkmark, dropdown icon) |
| `accentSelection` | text selection highlight |
| `background` / `backgroundHover` / `backgroundPressed` | interactive surfaces |
| `backgroundDark` / `backgroundDisabled` | filled/disabled states |
| `surface` / `surfaceBorder` | panels and tooltips |
| `border` | default widget border |
| `text` / `textMuted` | foreground text |
| `track` / `scrollbar` | slider/switch tracks, scrollbars |
| `animFast` / `animNormal` / `animSlow` | shared animation durations (ms) |

All colors are Int ARGB. Defaults match the original Vexel palette.

Tokens are read **at construction time** — widget constructor defaults
evaluate `Theme.x` when the widget is created. So:

```kotlin
Theme.accent = 0xFF10B981.toInt()   // before building UI: everything green
```

Retheming a live screen means rebuilding it (see the "Cycle accent" row in
the example mod). Explicit constructor arguments always win over tokens.
