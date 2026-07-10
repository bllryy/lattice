# Rendering

`Lattice.renderer` implements `render.Renderer`, backed by NanoVG
(`NVGRenderer`). All colors are Int ARGB (`0xAARRGGBB`). Coordinates are raw
window pixels.

## Frame lifecycle

Inside `RenderEvent.Gui` or `UIScreen` a frame is already open — just draw.
Outside those hooks, wrap calls yourself:

```kotlin
renderer.beginFrame(Resolution.windowWidth.toFloat(), Resolution.windowHeight.toFloat())
// ... draw ...
renderer.endFrame()
```

`beginFrame` binds Minecraft's main framebuffer and saves GL state;
`endFrame` restores what vanilla expects.

## Drawing

```kotlin
renderer.rect(x, y, w, h, color, radius)
renderer.rect(x, y, w, h, color, tr, tl, br, bl)           // per-corner radii
renderer.gradientRect(x, y, w, h, c1, c2, Gradient.TopToBottom, radius)
renderer.hollowRect(x, y, w, h, thickness, color, radius)  // border only
renderer.circle(cx, cy, radius, color)
renderer.line(x1, y1, x2, y2, thickness, color)
renderer.dropShadow(x, y, w, h, blur, spread, shadowColor, radius)

renderer.text("hi", x, y, size, color)                     // Lattice.defaultFont
renderer.shadowedText("hi", x, y, size, color)
renderer.wrappedText("long...", x, y, maxWidth, size, color)
renderer.textWidth("hi", size)                             // measure
renderer.textBounds("long...", maxWidth, size)             // [minX, minY, maxX, maxY]

renderer.svg("/assets/mymod/icon.svg", x, y, w, h)         // after createImage
renderer.image(image, x, y, w, h, radius)
```

Transforms and clipping nest with `push()`/`pop()` and
`pushScissor(x, y, w, h)`/`popScissor()`; `translate`, `scale`, `rotate`
(radians), and `globalAlpha` apply to subsequent calls.

## Fonts and images

`Font(name, resourcePath | inputStream)` — TTFs registered lazily with
NanoVG. The bundled default is `Lattice.defaultFont`.

`createImage(path, width, height, tint)` loads a PNG/JPG or rasterizes an
SVG (tint replaces `currentColor`); images are reference-counted — pair with
`deleteImage`. Paths resolve as classpath resources or filesystem paths.

## Custom backends

`Renderer` is an interface; swap the implementation with
`Lattice.renderer = MyRenderer`.
