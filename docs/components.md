# Components

## Element

`Element<T>` is the self-typed base class: children, layout state, visibility,
focus, hover/press state, listeners, and fluent configuration (every setter
returns `T`). Subclasses implement `onRender(mouseX, mouseY)`.

Input listeners (fluent):

```kotlin
element
    .onClick { event -> true }        // return true to consume
    .onRelease { event -> true }
    .onScroll { event -> true }
    .onMouseEnter { }
    .onMouseExit { }
    .onMouseMove { }
    .onCharType { event -> true }     // needs focus, or ignoreFocus()
    .onValueChange { value -> }       // widgets push their new value here
```

Tree management: `childOf(parent)`, `addChild(child)`, `destroy()` (recursive,
detaches from parent, clears listeners). Attach roots to a `Window` (screens
expose one as `window`).

Focus: clicking focuses an element and unfocuses the rest of the tree;
`setRequiresFocus()` makes clicks elsewhere drop focus; `ignoreFocus()` lets
an element receive key events without focus.

Debug: `enableDebugRendering()` draws hitboxes (cyan = idle, yellow =
hovered, orange = focused), recursively.

## Box

`Box<T>` adds padding and optional vertical scrolling (see
[layout](layout.md)). It is the base for anything that contains other
elements.

## Built-in components (`ui.component`)

- **`Container`** — invisible `Box`; pure layout/scroll region.
- **`Rectangle`** — `Box` that paints: background (solid or two-color
  gradient), border (solid or gradient), per-corner radii
  (`borderRadiusVarying`), hover/pressed background swap, `dropShadow()`,
  `rotation`/`rotateTo()`.
- **`Text`** — single-line label; `Auto` size measures the string. Optional
  shadow. Ignores the mouse.
- **`SvgImage`** — rasterized SVG with tint (`setSvgColor(argb)`) and
  rotation; releases its GPU image on `destroy()`.
- **`Tooltip`** — attach with `element.addTooltip("text")`; fades in/out on
  hover. Reposition with `setPosition(TooltipPosition.Top/Bottom/Left/Right)`.

## Window and UIScreen

`Window` is the root container: it drives rendering (`draw()`), routes input,
and cleans up (`cleanup()` destroys the tree and stops animations).

`UIScreen` bridges a vanilla `Screen` to a `Window`: it renders through the
`RenderEvent.Gui` NanoVG frame, translates vanilla input events, closes on
ESC when nothing consumes the key, and calls `afterInitialization()` once.
`display()` opens the screen on the next tick from any thread.
