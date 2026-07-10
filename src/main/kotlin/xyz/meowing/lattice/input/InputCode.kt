package xyz.meowing.lattice.input

sealed interface InputCode {
    val code: Int
    val isPressed: Boolean
    val displayName: String get() = Inputs.getDisplayName(code)
}

data class Key(override val code: Int) : InputCode {
    override val isPressed: Boolean get() = Keyboard.isPressed(code)
}

data class MouseButton(override val code: Int) : InputCode {
    override val isPressed: Boolean get() = Mouse.isPressed(code)
}
