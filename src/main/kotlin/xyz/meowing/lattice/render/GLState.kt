package xyz.meowing.lattice.render

/** Tracks GL state that NanoVG frames must restore for vanilla rendering. */
object GLState {
    @JvmStatic
    var previousBoundTexture = -1

    @JvmStatic
    var previousActiveTexture = -1

    @JvmStatic
    var previousProgram = -1

    @JvmStatic
    var previousSampler = -1

    @JvmStatic
    var previousVao = -1

    @JvmStatic
    var drawing = false
}
