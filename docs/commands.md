# Commands (Commodore)

Commodore is a Kotlin DSL over Brigadier. Function parameters become command
arguments via reflection.

```kotlin
val command = Commodore("mymod", "mm") {          // name + aliases
    runs { Chat.fakeMessage("base command") }      // /mymod

    literal("teleport", "tp") {                    // /mymod teleport <x> <y> <z>
        runs { x: Float, y: Float, z: Float ->
            // typed params are parsed & suggested automatically
        }
    }

    literal("say") {
        runs { message: GreedyString ->            // consumes the rest of the line
            Chat.sendMessage(message.toString())
        }
    }

    literal("mode") {
        executable {
            param("name") { suggests { listOf("fast", "slow") } }
            runs { name: String -> setMode(name) }
        }
    }
}

// Fabric client commands:
ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
    command.register(dispatcher)
}
```

Built-in parsers: `String`, `GreedyString`, `Int`, `Long`, `Float`,
`Double`, `Boolean`. Optional parameters: give the lambda parameter a
nullable type (`Int?`) — trailing optionals may be omitted by the user.

Custom types: annotate a class with `@CommandParsable` (its primary
constructor parameters must themselves be parsable), or supply a
per-parameter parser with `param("p") { parser { input: String -> ... } }`.
