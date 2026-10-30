package xyz.meowing.lattice.input

import com.mojang.blaze3d.platform.InputConstants

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
                isShift = (mods and InputConstants.MOD_SHIFT) != 0,
                isCtrl = (mods and InputConstants.MOD_CONTROL) != 0,
                isAlt = (mods and InputConstants.MOD_ALT) != 0,
                isSuper = (mods and InputConstants.MOD_SUPER) != 0,
            )
        }
    }

    fun toMods(): Int {
        return listOf(
            isShift to InputConstants.MOD_SHIFT,
            isCtrl to InputConstants.MOD_CONTROL,
            isAlt to InputConstants.MOD_ALT,
            isSuper to InputConstants.MOD_SUPER,
            isCapsLock to InputConstants.MOD_CAPS_LOCK,
            isNumLock to InputConstants.MOD_NUM_LOCK
        ).sumOf { (value, mod) -> if (value) mod else 0 }
    }
}
