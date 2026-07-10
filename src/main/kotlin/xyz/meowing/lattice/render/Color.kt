package xyz.meowing.lattice.render

// Derived from OdinFabric (BSD-3-Clause, (c) odtheking); see NOTICE.md.
class Color(hue: Float, saturation: Float, brightness: Float, alpha: Float = 1f) {
    constructor(hsb: FloatArray, alpha: Float = 1f) : this(hsb[0], hsb[1], hsb[2], alpha)
    constructor(r: Int, g: Int, b: Int, alpha: Float = 1f) : this(
        java.awt.Color.RGBtoHSB(r, g, b, FloatArray(size = 3)), alpha
    )
    constructor(rgba: Int) : this(rgba.red, rgba.green, rgba.blue, alpha = rgba.alpha / 255f)
    constructor(rgba: Int, alpha: Float) : this(rgba.red, rgba.green, rgba.blue, alpha)
    constructor(hex: String) : this(
        hex.take(2).toInt(16),
        hex.substring(2, 4).toInt(16),
        hex.substring(4, 6).toInt(16),
        hex.substring(6, 8).toInt(16) / 255f
    )

    var hue = hue
        set(value) {
            field = value
            needsUpdate = true
        }

    var saturation = saturation
        set(value) {
            field = value
            needsUpdate = true
        }

    var brightness = brightness
        set(value) {
            field = value
            needsUpdate = true
        }

    var alphaFloat = alpha
        set(value) {
            field = value
            needsUpdate = true
        }

    private var needsUpdate = true

    /** ARGB value, recomputed lazily when the HSBA components change. */
    var rgba: Int = 0
        get() {
            if (needsUpdate) {
                field = (java.awt.Color.HSBtoRGB(hue, saturation, brightness) and 0x00FFFFFF) or
                    ((this.alphaFloat * 255).toInt() shl 24)
                needsUpdate = false
            }
            return field
        }
        private set

    inline val red get() = rgba.red
    inline val green get() = rgba.green
    inline val blue get() = rgba.blue
    inline val alpha get() = rgba.alpha

    inline val redFloat get() = red / 255f
    inline val greenFloat get() = green / 255f
    inline val blueFloat get() = blue / 255f

    @OptIn(ExperimentalStdlibApi::class)
    fun hex(includeAlpha: Boolean = true): String {
        val hexString = rgba.toHexString(HexFormat.UpperCase)
        return if (includeAlpha) hexString.substring(2) + hexString.take(2)
        else hexString.substring(2)
    }

    inline val isTransparent: Boolean get() = this.alphaFloat == 0f

    override fun toString(): String = "Color(red=$red,green=$green,blue=$blue,alpha=$alpha)"

    override fun hashCode(): Int {
        var result = hue.toInt()
        result = 31 * result + saturation.toInt()
        result = 31 * result + brightness.toInt()
        result = 31 * result + this.alphaFloat.toInt()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return other is Color && rgba == other.rgba
    }

    fun copy(): Color = Color(this.rgba)

    companion object {
        inline val Int.red get() = this shr 16 and 0xFF
        inline val Int.green get() = this shr 8 and 0xFF
        inline val Int.blue get() = this and 0xFF
        inline val Int.alpha get() = this shr 24 and 0xFF

        fun Color.brighter(factor: Float = 1.3f): Color {
            return Color(hue, saturation, (brightness * factor.coerceAtLeast(1f)).coerceAtMost(1f), this.alphaFloat)
        }

        fun Color.darker(factor: Float = 0.7f): Color {
            return Color(hue, saturation, brightness * factor, this.alphaFloat)
        }

        fun Color.withAlpha(alpha: Float, newInstance: Boolean = true): Color {
            return if (newInstance) Color(red, green, blue, alpha)
            else {
                this.alphaFloat = alpha
                this
            }
        }

        fun Color.multiplyAlpha(factor: Float): Color {
            return Color(red, green, blue, (alphaFloat * factor).coerceIn(0f, 1f))
        }

        fun Color.hsbMax(): Color = Color(hue, 1f, 1f)
    }
}
