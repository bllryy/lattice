package xyz.meowing.lattice.input

import org.lwjgl.glfw.GLFW

data class KeyboardModifiers(
    val isShift: Boolean,
    val isCtrl: Boolean,
    val isAlt: Boolean,
    val isSuper: Boolean,
    val isCapsLock: Boolean = false,
    val isNumLock: Boolean = false
) {
    companion object {
        val current: KeyboardModifiers
            get() = KeyboardModifiers(
                isShift = Keyboard.isShiftKeyPressed,
                isCtrl = Keyboard.isCtrlKeyPressed,
                isAlt = Keyboard.isAltKeyPressed,
                isSuper = Keyboard.isSuperKeyPressed,
            )

        fun wrap(mods: Int): KeyboardModifiers {
            return KeyboardModifiers(
                isShift = (mods and GLFW.GLFW_MOD_SHIFT) != 0,
                isCtrl = (mods and GLFW.GLFW_MOD_CONTROL) != 0,
                isAlt = (mods and GLFW.GLFW_MOD_ALT) != 0,
                isSuper = (mods and GLFW.GLFW_MOD_SUPER) != 0,
            )
        }
    }

    fun toMods(): Int {
        return listOf(
            isShift to GLFW.GLFW_MOD_SHIFT,
            isCtrl to GLFW.GLFW_MOD_CONTROL,
            isAlt to GLFW.GLFW_MOD_ALT,
            isSuper to GLFW.GLFW_MOD_SUPER,
            isCapsLock to GLFW.GLFW_MOD_CAPS_LOCK,
            isNumLock to GLFW.GLFW_MOD_NUM_LOCK
        ).sumOf { (value, mod) -> if (value) mod else 0 }
    }
}
