# Events & scheduling

## Event bus

One global bus: `Lattice.eventBus`. Subscribers register on concrete event
classes; higher `priority` runs first; exceptions in handlers are caught and
logged.

```kotlin
val call = Lattice.eventBus.register<TickEvent.Start> { doSomething() }
call.unregister()          // and call.register() to resubscribe
```

`post { EventObject() }` (supplier form) skips construction when nothing
listens. Cancellable events stop dispatch when `cancel()` is called and make
`post` return `true`.

## Built-in events

| Event | Fired |
| --- | --- |
| `ClientEvent.Start` / `Stop` | client run/stop |
| `TickEvent.Start` / `End` | each client tick |
| `RenderEvent.Gui` | every frame after vanilla GUI rendering, inside a prepared NanoVG frame (cancellable) |

Custom events: extend `Event` or `CancellableEvent` and `post` them on the
same bus.

## Schedulers

**`TickScheduler`** — game-tick based, runs on the client thread:

```kotlin
TickScheduler.post { }                    // next tick
TickScheduler.schedule(20) { }            // in 20 ticks
val h = TickScheduler.repeat(100) { }     // every 100 ticks
h.cancel()
TickScheduler.repeatDynamic({ nextInterval() }) { }
```

**`TimeScheduler`** — wall-clock, runs on a background thread pool (shut
down automatically on client stop):

```kotlin
TimeScheduler.schedule(500) { }                          // ms
TimeScheduler.repeat(1000, stopCondition = { done }) { }
```

TimeScheduler callbacks are **not** on the render thread — hop back with
`TickScheduler.post` before touching UI or game state.
