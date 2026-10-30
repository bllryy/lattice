package xyz.meowing.lattice.input

import com.mojang.blaze3d.platform.InputConstants
//? if >=26.3 {
import org.lwjgl.sdl.SDLScancode
//?} else {
/*import org.lwjgl.glfw.GLFW
*///?}

/** Key codes are Minecraft's: GLFW key codes before 26.3, SDL scancodes from 26.3. */
object Keys {
    val KEY_NONE = Key(InputConstants.UNKNOWN.value)

    val KEY_ESCAPE = Key(InputConstants.KEY_ESCAPE)
    val KEY_F1 = Key(InputConstants.KEY_F1)
    val KEY_F2 = Key(InputConstants.KEY_F2)
    val KEY_F3 = Key(InputConstants.KEY_F3)
    val KEY_F4 = Key(InputConstants.KEY_F4)
    val KEY_F5 = Key(InputConstants.KEY_F5)
    val KEY_F6 = Key(InputConstants.KEY_F6)
    val KEY_F7 = Key(InputConstants.KEY_F7)
    val KEY_F8 = Key(InputConstants.KEY_F8)
    val KEY_F9 = Key(InputConstants.KEY_F9)
    val KEY_F10 = Key(InputConstants.KEY_F10)
    val KEY_F11 = Key(InputConstants.KEY_F11)
    val KEY_F12 = Key(InputConstants.KEY_F12)
    val KEY_F13 = Key(InputConstants.KEY_F13)
    val KEY_F14 = Key(InputConstants.KEY_F14)
    val KEY_F15 = Key(InputConstants.KEY_F15)

    val KEY_LEFT_SHIFT = Key(InputConstants.KEY_LSHIFT)
    val KEY_RIGHT_SHIFT = Key(InputConstants.KEY_RSHIFT)
    val KEY_LEFT_CONTROL = Key(InputConstants.KEY_LCONTROL)
    val KEY_RIGHT_CONTROL = Key(InputConstants.KEY_RCONTROL)
    val KEY_LEFT_ALT = Key(InputConstants.KEY_LALT)
    val KEY_RIGHT_ALT = Key(InputConstants.KEY_RALT)
    //? if >=26.3 {
    val KEY_LEFT_SUPER = Key(InputConstants.KEY_LGUI)
    val KEY_RIGHT_SUPER = Key(InputConstants.KEY_RGUI)
    //?} else {
    /*val KEY_LEFT_SUPER = Key(InputConstants.KEY_LSUPER)
    val KEY_RIGHT_SUPER = Key(InputConstants.KEY_RSUPER)
    *///?}
    val KEY_CAPS_LOCK = Key(InputConstants.KEY_CAPSLOCK)
    val KEY_NUM_LOCK = Key(InputConstants.KEY_NUMLOCK)
    val KEY_SCROLL_LOCK = Key(InputConstants.KEY_SCROLLLOCK)

    val KEY_TAB = Key(InputConstants.KEY_TAB)
    val KEY_ENTER = Key(InputConstants.KEY_RETURN)
    val KEY_BACKSPACE = Key(InputConstants.KEY_BACKSPACE)
    val KEY_DELETE = Key(InputConstants.KEY_DELETE)
    val KEY_INSERT = Key(InputConstants.KEY_INSERT)
    val KEY_PAGE_UP = Key(InputConstants.KEY_PAGEUP)
    val KEY_PAGE_DOWN = Key(InputConstants.KEY_PAGEDOWN)
    val KEY_HOME = Key(InputConstants.KEY_HOME)
    val KEY_END = Key(InputConstants.KEY_END)

    val KEY_LEFT = Key(InputConstants.KEY_LEFT)
    val KEY_RIGHT = Key(InputConstants.KEY_RIGHT)
    val KEY_UP = Key(InputConstants.KEY_UP)
    val KEY_DOWN = Key(InputConstants.KEY_DOWN)

    val KEY_0 = Key(InputConstants.KEY_0)
    val KEY_1 = Key(InputConstants.KEY_1)
    val KEY_2 = Key(InputConstants.KEY_2)
    val KEY_3 = Key(InputConstants.KEY_3)
    val KEY_4 = Key(InputConstants.KEY_4)
    val KEY_5 = Key(InputConstants.KEY_5)
    val KEY_6 = Key(InputConstants.KEY_6)
    val KEY_7 = Key(InputConstants.KEY_7)
    val KEY_8 = Key(InputConstants.KEY_8)
    val KEY_9 = Key(InputConstants.KEY_9)

    val KEY_A = Key(InputConstants.KEY_A)
    val KEY_B = Key(InputConstants.KEY_B)
    val KEY_C = Key(InputConstants.KEY_C)
    val KEY_D = Key(InputConstants.KEY_D)
    val KEY_E = Key(InputConstants.KEY_E)
    val KEY_F = Key(InputConstants.KEY_F)
    val KEY_G = Key(InputConstants.KEY_G)
    val KEY_H = Key(InputConstants.KEY_H)
    val KEY_I = Key(InputConstants.KEY_I)
    val KEY_J = Key(InputConstants.KEY_J)
    val KEY_K = Key(InputConstants.KEY_K)
    val KEY_L = Key(InputConstants.KEY_L)
    val KEY_M = Key(InputConstants.KEY_M)
    val KEY_N = Key(InputConstants.KEY_N)
    val KEY_O = Key(InputConstants.KEY_O)
    val KEY_P = Key(InputConstants.KEY_P)
    val KEY_Q = Key(InputConstants.KEY_Q)
    val KEY_R = Key(InputConstants.KEY_R)
    val KEY_S = Key(InputConstants.KEY_S)
    val KEY_T = Key(InputConstants.KEY_T)
    val KEY_U = Key(InputConstants.KEY_U)
    val KEY_V = Key(InputConstants.KEY_V)
    val KEY_W = Key(InputConstants.KEY_W)
    val KEY_X = Key(InputConstants.KEY_X)
    val KEY_Y = Key(InputConstants.KEY_Y)
    val KEY_Z = Key(InputConstants.KEY_Z)

    val KEY_SPACE = Key(InputConstants.KEY_SPACE)
    val KEY_APOSTROPHE = Key(InputConstants.KEY_APOSTROPHE)
    val KEY_COMMA = Key(InputConstants.KEY_COMMA)
    val KEY_MINUS = Key(InputConstants.KEY_MINUS)
    val KEY_PERIOD = Key(InputConstants.KEY_PERIOD)
    val KEY_SLASH = Key(InputConstants.KEY_SLASH)
    val KEY_SEMICOLON = Key(InputConstants.KEY_SEMICOLON)
    val KEY_EQUAL = Key(InputConstants.KEY_EQUALS)
    val KEY_LEFT_BRACKET = Key(InputConstants.KEY_LBRACKET)
    val KEY_BACKSLASH = Key(InputConstants.KEY_BACKSLASH)
    val KEY_RIGHT_BRACKET = Key(InputConstants.KEY_RBRACKET)
    val KEY_GRAVE_ACCENT = Key(InputConstants.KEY_GRAVE)

    val KEY_NUMPAD_0 = Key(InputConstants.KEY_NUMPAD0)
    val KEY_NUMPAD_1 = Key(InputConstants.KEY_NUMPAD1)
    val KEY_NUMPAD_2 = Key(InputConstants.KEY_NUMPAD2)
    val KEY_NUMPAD_3 = Key(InputConstants.KEY_NUMPAD3)
    val KEY_NUMPAD_4 = Key(InputConstants.KEY_NUMPAD4)
    val KEY_NUMPAD_5 = Key(InputConstants.KEY_NUMPAD5)
    val KEY_NUMPAD_6 = Key(InputConstants.KEY_NUMPAD6)
    val KEY_NUMPAD_7 = Key(InputConstants.KEY_NUMPAD7)
    val KEY_NUMPAD_8 = Key(InputConstants.KEY_NUMPAD8)
    val KEY_NUMPAD_9 = Key(InputConstants.KEY_NUMPAD9)

    //? if >=26.3 {
    val KEY_NUMPAD_DECIMAL = Key(SDLScancode.SDL_SCANCODE_KP_DECIMAL)
    val KEY_NUMPAD_DIVIDE = Key(SDLScancode.SDL_SCANCODE_KP_DIVIDE)
    //?} else {
    /*val KEY_NUMPAD_DECIMAL = Key(GLFW.GLFW_KEY_KP_DECIMAL)
    val KEY_NUMPAD_DIVIDE = Key(GLFW.GLFW_KEY_KP_DIVIDE)
    *///?}
    val KEY_NUMPAD_MULTIPLY = Key(InputConstants.KEY_MULTIPLY)
    //? if >=26.3 {
    val KEY_NUMPAD_SUBTRACT = Key(SDLScancode.SDL_SCANCODE_KP_MINUS)
    //?} else {
    /*val KEY_NUMPAD_SUBTRACT = Key(GLFW.GLFW_KEY_KP_SUBTRACT)
    *///?}
    val KEY_NUMPAD_ADD = Key(InputConstants.KEY_ADD)
    val KEY_NUMPAD_ENTER = Key(InputConstants.KEY_NUMPADENTER)
    val KEY_NUMPAD_EQUAL = Key(InputConstants.KEY_NUMPADEQUALS)

    val KEY_PRINT_SCREEN = Key(InputConstants.KEY_PRINTSCREEN)
    val KEY_PAUSE = Key(InputConstants.KEY_PAUSE)
    //? if >=26.3 {
    val KEY_MENU = Key(SDLScancode.SDL_SCANCODE_APPLICATION)
    //?} else {
    /*val KEY_MENU = Key(GLFW.GLFW_KEY_MENU)
    *///?}
}
