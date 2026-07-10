# Layout

Every element carries an x/y position constraint, a width/height sizing mode,
an optional alignment, and an optional offset. Layout resolves top-down each
frame, with per-element caches invalidated on change or resize.

## Position — `Pos`

Set with `setPositioning(xVal, xPos, yVal, yPos)` or `setPositioning(xPos, yPos)`.

| Mode | Meaning |
| --- | --- |
| `ParentPixels` | `parent origin + padding + constraint` (the default) |
| `ParentPercent` | percentage across the parent's padded content area |
| `ParentCenter` | centered in the parent, constraint as extra offset |
| `ScreenPixels` / `ScreenPercent` / `ScreenCenter` | same, relative to the window |
| `AfterSibling` | after the previous visible sibling (per axis), constraint as gap |
| `MatchSibling` | same coordinate as the previous sibling |

## Size — `Size`

Set with `setSizing(w, wType, h, hType)`.

| Mode | Meaning |
| --- | --- |
| `Pixels` | fixed size |
| `Percent` | percentage of the parent's padded content area (value via the same setter) |
| `Auto` | fits children (components override this: `Text` measures its string, `Box` adds padding) |
| `Fill` | stretch to the parent's far edge minus visible later siblings |

Clamp `Auto` with `setMaxAutoSize(maxWidth, maxHeight)`.

## Alignment and offset

`alignLeft()/alignRight()/alignTop()/alignBottom()` (or
`setAlignment(x, y)`) snap to the parent's padded edges after positioning;
the position constraint then acts as an inset. `setOffset(x, y)` adds a
final pixel or percent offset.

## Padding and scrolling

Padding lives on `Box` (so on `Container`, `Rectangle`, and every widget
built from them) as `floatArrayOf(top, right, bottom, left)`. Children
position inside the padded area.

Set `scrollable = true` on a `Box` to get vertical scrolling with an
auto-hiding, draggable scrollbar (`scrollbarWidth/Color/Radius/Padding`).
Content is scissored to the padded area.

## Coordinate spaces

Element `x/y/width/height` are raw window pixels. `element.raw` and
`element.scaled` expose edges/center in raw and GUI-scaled coordinates
(`scaled` divides by the vanilla GUI scale factor).
