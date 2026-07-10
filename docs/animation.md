# Animation

Animations are wall-clock based, updated once per frame by
`AnimationManager.update()` (the `Window` calls it). Registering a new
animation with the same element id and type replaces the running one.

## Element extensions (`xyz.meowing.lattice.animation`)

Low-level, animate anything:

```kotlin
element.animateFloat({ element.rotation }, { element.rotation = it }, 90f, 300, EasingType.EASE_OUT)
element.animateColor({ rect.backgroundColor }, { rect.backgroundColor = it }, target, 200)
element.animatePosition(endX, endY, 500, EasingType.EASE_IN_OUT)   // animates constraints
element.animateSize(w, h, 300)
```

All return the running animation and accept an optional `onComplete`.

## Presets

```kotlin
element.fadeIn(300)               // alpha via colors; recurses into children
element.fadeOut(300) { done() }   // hides when finished
element.moveTo(x, y)              // constraint move
element.scaleTo(w, h)
element.colorTo(argb)             // background/text/svg tint by component type
element.slideIn(fromX = -width)   // slides to its original position
element.bounceScale(1.2f)         // press feedback
```

`slideIn`/`bounceScale` remember the element's original geometry on first
use and restore it.

## Easing

`EasingType.LINEAR`, `EASE_IN`, `EASE_OUT`, `EASE_IN_OUT` (quadratic).

Use `Theme.animFast/animNormal/animSlow` for consistent durations.
