package xyz.meowing.lattice.render

import com.mojang.blaze3d.systems.RenderSystem
//? if >=26.3 {
import com.mojang.renderpearl.backend.opengl.GlStateManager
//?} else {
/*import com.mojang.blaze3d.opengl.GlStateManager
*///?}
import org.apache.logging.log4j.LogManager
import org.lwjgl.nanovg.NVGColor
import org.lwjgl.nanovg.NVGPaint
import org.lwjgl.nanovg.NanoSVG
import org.lwjgl.nanovg.NanoVG
import org.lwjgl.nanovg.NanoVGGL3
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL33C
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryUtil
import xyz.meowing.lattice.client.Client
import xyz.meowing.lattice.render.Color.Companion.alpha
import xyz.meowing.lattice.render.Color.Companion.blue
import xyz.meowing.lattice.render.Color.Companion.green
import xyz.meowing.lattice.render.Color.Companion.red
import java.lang.reflect.Method
import java.nio.ByteBuffer
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

// Derived from OdinFabric (BSD-3-Clause, (c) odtheking); see NOTICE.md.
object NVGRenderer : Renderer {
    private val nvgPaint = NVGPaint.malloc()
    private val nvgColor = NVGColor.malloc()
    private val nvgColor2 = NVGColor.malloc()

    private val fontMap = HashMap<Font, NVGFont>()
    private val fontBounds = FloatArray(4)

    private val images = HashMap<Image, NVGImage>()

    private var scissor: Scissor? = null

    private var vg = -1L

    // NanoVG draws with OpenGL; 26.2+ can run on Vulkan instead, where it has to stay off.
    override val isAvailable: Boolean by lazy {
        val backend = RenderSystem.getDevice()?.let { fieldValue(it, "backend") }
        val openGL = backend == null || backend.javaClass.simpleName.startsWith("Gl")
        if (!openGL) {
            LogManager.getLogger("Lattice").warn("Lattice rendering is disabled: NanoVG needs the OpenGL backend, but Minecraft is running on ${backend.javaClass.simpleName}. Set Graphics API to OpenGL in Video Settings.")
        }
        openGL
    }

    init {
        if (isAvailable) {
            vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS or NanoVGGL3.NVG_STENCIL_STROKES)
            require(vg != -1L) { "Failed to initialize NanoVG" }
        }
    }

    // Resolved reflectively once: the GL backend's framebuffer lookup is not public API.
    private var fboLookup: ((Any, Any?) -> Int)? = null
    private var fboLookupFailed = false
    private var loggedFbo = false

    private fun fieldValue(obj: Any, name: String): Any? {
        var clazz: Class<*>? = obj.javaClass
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name).apply { isAccessible = true }.get(obj)
            } catch (_: NoSuchFieldException) {
                clazz = clazz.superclass
            }
        }
        return null
    }

    private fun findMethod(start: Class<*>, name: String, parameterCount: Int): Method {
        var clazz: Class<*>? = start
        while (clazz != null) {
            clazz.declaredMethods.firstOrNull { it.name == name && it.parameterCount == parameterCount }
                ?.let { return it.apply { isAccessible = true } }
            clazz = clazz.superclass
        }
        throw IllegalStateException("no $name")
    }

    private fun mainFramebuffer(colorTex: Any, depthTex: Any?): Int {
        if (fboLookupFailed) return 0

        try {
            val lookup = fboLookup ?: run {
                val device = RenderSystem.getDevice() ?: return 0
                // GpuDevice wraps the GL backend (GlDevice), which owns directStateAccess.
                val backend = fieldValue(device, "backend") ?: device
                val dsa = fieldValue(backend, "directStateAccess")
                    ?: throw IllegalStateException("no directStateAccess")

                // 26.2+ caches FBOs per attachment set: FrameBufferCache.getFbo(dsa, colors, depth).
                // Before that each color texture cached its own: GlTexture.getFbo(dsa, depth).
                //? if >=26.2 {
                val cache = fieldValue(backend, "frameBufferCache")
                    ?: throw IllegalStateException("no frameBufferCache")
                val getFbo = findMethod(cache.javaClass, "getFbo", 3)
                val newLookup: (Any, Any?) -> Int = { color, depth -> getFbo.invoke(cache, dsa, listOf(color), depth) as Int }
                //?} else {
                /*val getFbo = findMethod(colorTex.javaClass, "getFbo", 2)
                val newLookup: (Any, Any?) -> Int = { color, depth -> getFbo.invoke(color, dsa, depth) as Int }
                *///?}
                newLookup.also { fboLookup = it }
            }

            // Pass the depth texture so getFbo returns the FBO with the depth+stencil
            // attachment; NanoVG's stencil fills render nothing on a color-only FBO.
            return lookup(colorTex, depthTex)
        } catch (e: Throwable) {
            fboLookupFailed = true
            LogManager.getLogger("Lattice").warn("FBO lookup failed: device=${RenderSystem.getDevice()?.javaClass?.name} color=${colorTex.javaClass.name} depth=${depthTex?.javaClass?.name}", e)
            return 0
        }
    }

    override fun beginFrame(width: Float, height: Float) {
        check(!GLState.drawing) { "[NVGRenderer] Already drawing, but called beginFrame" }

        GLState.previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE)
        GLState.previousProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM)

        val renderTarget = Client.mainRenderTarget
        val colorTex = renderTarget.getColorTexture() ?: return
        if (vg == -1L) return

        val fbo = mainFramebuffer(colorTex, renderTarget.getDepthTexture())
        if (!loggedFbo) {
            loggedFbo = true
            LogManager.getLogger("Lattice").info("NVG beginFrame: fbo=$fbo failed=$fboLookupFailed size=${width}x$height target=${renderTarget.width}x${renderTarget.height}")
        }
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo)
        GlStateManager._viewport(0, 0, renderTarget.width, renderTarget.height)
        GlStateManager._activeTexture(GL30.GL_TEXTURE0)

        // NanoVG binds its own VAO without restoring the previous one.
        GLState.previousVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING)

        // Unbind MC's sampler from unit 0 so NanoVG's font atlas texture samples
        // with its own parameters; otherwise glyphs render blank while shapes are fine.
        GLState.previousSampler = GL11.glGetInteger(GL33C.GL_SAMPLER_BINDING)
        GL33C.glBindSampler(0, 0)

        NanoVG.nvgBeginFrame(vg, width, height, 1f)
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT or NanoVG.NVG_ALIGN_TOP)
        GLState.drawing = true
    }

    override fun endFrame() {
        check(GLState.drawing) { "[NVGRenderer] Not drawing, but called endFrame" }
        NanoVG.nvgEndFrame(vg)

        // Restore the default state vanilla expects after NanoVG's frame.
        GlStateManager._disableCull()
        GlStateManager._disableDepthTest()
        //? if >=26.2 {
        GlStateManager._enableBlend(0)
        //?} else {
        /*GlStateManager._enableBlend()
        *///?}
        GlStateManager._blendFuncSeparate(770, 771, 1, 0)

        // NanoVG's stencil strokes can leave the stencil test on and the color mask off.
        GL11.glDisable(GL11.GL_STENCIL_TEST)
        GL11.glColorMask(true, true, true, true)

        if (GLState.previousProgram != -1) GlStateManager._glUseProgram(GLState.previousProgram)

        // Restore the sampler on unit 0 that we unbound for the font atlas.
        GlStateManager._activeTexture(GL30.GL_TEXTURE0)
        if (GLState.previousSampler != -1) GL33C.glBindSampler(0, GLState.previousSampler)

        if (GLState.previousActiveTexture != -1) {
            GlStateManager._activeTexture(GLState.previousActiveTexture)
            if (GLState.previousBoundTexture != -1) GlStateManager._bindTexture(GLState.previousBoundTexture)
        }

        if (GLState.previousVao != -1) GL30.glBindVertexArray(GLState.previousVao)

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0)
        GLState.drawing = false
    }

    override fun push() = NanoVG.nvgSave(vg)

    override fun pop() = NanoVG.nvgRestore(vg)

    override fun scale(x: Float, y: Float) = NanoVG.nvgScale(vg, x, y)

    override fun translate(x: Float, y: Float) = NanoVG.nvgTranslate(vg, x, y)

    override fun rotate(amount: Float) = NanoVG.nvgRotate(vg, amount)

    override fun globalAlpha(amount: Float) = NanoVG.nvgGlobalAlpha(vg, amount.coerceIn(0f, 1f))

    override fun pushScissor(x: Float, y: Float, w: Float, h: Float) {
        scissor = Scissor(scissor, x, y, w + x, h + y)
        scissor?.applyScissor()
    }

    override fun popScissor() {
        NanoVG.nvgResetScissor(vg)
        scissor = scissor?.previous
        scissor?.applyScissor()
    }

    override fun line(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int) {
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgMoveTo(vg, x1, y1)
        NanoVG.nvgLineTo(vg, x2, y2)
        NanoVG.nvgStrokeWidth(vg, thickness)
        color(color)
        NanoVG.nvgStrokeColor(vg, nvgColor)
        NanoVG.nvgStroke(vg)
    }

    override fun rect(x: Float, y: Float, w: Float, h: Float, color: Int, radius: Float, roundTop: Boolean) {
        NanoVG.nvgBeginPath(vg)

        if (roundTop) {
            NanoVG.nvgMoveTo(vg, x, y + h)
            NanoVG.nvgLineTo(vg, x + w, y + h)
            NanoVG.nvgLineTo(vg, x + w, y + radius)
            NanoVG.nvgArcTo(vg, x + w, y, x + w - radius, y, radius)
            NanoVG.nvgLineTo(vg, x + radius, y)
            NanoVG.nvgArcTo(vg, x, y, x, y + radius, radius)
            NanoVG.nvgLineTo(vg, x, y + h)
        } else {
            NanoVG.nvgMoveTo(vg, x, y)
            NanoVG.nvgLineTo(vg, x + w, y)
            NanoVG.nvgLineTo(vg, x + w, y + h - radius)
            NanoVG.nvgArcTo(vg, x + w, y + h, x + w - radius, y + h, radius)
            NanoVG.nvgLineTo(vg, x + radius, y + h)
            NanoVG.nvgArcTo(vg, x, y + h, x, y + h - radius, radius)
            NanoVG.nvgLineTo(vg, x, y)
        }

        NanoVG.nvgClosePath(vg)
        color(color)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgFill(vg)
    }

    override fun rect(x: Float, y: Float, w: Float, h: Float, color: Int, radius: Float) {
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRoundedRect(vg, x, y, w, h + .5f, radius)
        color(color)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgFill(vg)
    }

    override fun rect(x: Float, y: Float, w: Float, h: Float, color: Int) {
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRect(vg, x, y, w, h + .5f)
        color(color)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgFill(vg)
    }

    override fun rect(x: Float, y: Float, w: Float, h: Float, color: Int, topRight: Float, topLeft: Float, bottomRight: Float, bottomLeft: Float) {
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRoundedRectVarying(vg, round(x), round(y), round(w), round(h), topRight, topLeft, bottomRight, bottomLeft)
        color(color)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgFill(vg)
    }

    override fun hollowRect(x: Float, y: Float, w: Float, h: Float, thickness: Float, color: Int, radius: Float) {
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRoundedRect(vg, x, y, w, h, radius)
        NanoVG.nvgStrokeWidth(vg, thickness)
        NanoVG.nvgPathWinding(vg, NanoVG.NVG_HOLE)
        color(color)
        NanoVG.nvgStrokeColor(vg, nvgColor)
        NanoVG.nvgStroke(vg)
    }

    override fun hollowGradientRect(x: Float, y: Float, w: Float, h: Float, thickness: Float, color1: Int, color2: Int, gradient: Gradient, radius: Float) {
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRoundedRect(vg, x, y, w, h, radius)
        NanoVG.nvgStrokeWidth(vg, thickness)
        gradient(color1, color2, x, y, w, h, gradient)
        NanoVG.nvgStrokePaint(vg, nvgPaint)
        NanoVG.nvgStroke(vg)
    }

    override fun gradientRect(x: Float, y: Float, w: Float, h: Float, color1: Int, color2: Int, gradient: Gradient, radius: Float) {
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRoundedRect(vg, x, y, w, h, radius)
        gradient(color1, color2, x, y, w, h, gradient)
        NanoVG.nvgFillPaint(vg, nvgPaint)
        NanoVG.nvgFill(vg)
    }

    override fun dropShadow(x: Float, y: Float, width: Float, height: Float, blur: Float, spread: Float, shadowColor: Int, radius: Float) {
        val r = shadowColor.red.toByte()
        val g = shadowColor.green.toByte()
        val b = shadowColor.blue.toByte()

        NanoVG.nvgRGBA(r, g, b, 125, nvgColor)
        NanoVG.nvgRGBA(r, g, b, 0, nvgColor2)

        NanoVG.nvgBoxGradient(
            vg,
            x - spread,
            y - spread,
            width + 2 * spread,
            height + 2 * spread,
            radius + spread,
            blur,
            nvgColor,
            nvgColor2,
            nvgPaint
        )
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRoundedRect(
            vg,
            x - spread - blur,
            y - spread - blur,
            width + 2 * spread + 2 * blur,
            height + 2 * spread + 2 * blur,
            radius + spread
        )
        NanoVG.nvgRoundedRect(vg, x, y, width, height, radius)
        NanoVG.nvgPathWinding(vg, NanoVG.NVG_HOLE)
        NanoVG.nvgFillPaint(vg, nvgPaint)
        NanoVG.nvgFill(vg)
    }

    override fun circle(x: Float, y: Float, radius: Float, color: Int) {
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgCircle(vg, x, y, radius)
        color(color)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgFill(vg)
    }

    override fun text(text: String, x: Float, y: Float, size: Float, color: Int, font: Font) {
        NanoVG.nvgFontSize(vg, size)
        NanoVG.nvgFontFaceId(vg, getFontID(font))
        color(color)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgText(vg, x, y + .5f, text)
    }

    override fun shadowedText(text: String, x: Float, y: Float, size: Float, color: Int, font: Font, shadowColor: Int, offsetX: Float, offsetY: Float, blur: Float) {
        NanoVG.nvgFontFaceId(vg, getFontID(font))
        NanoVG.nvgFontSize(vg, size)

        NanoVG.nvgFontBlur(vg, blur)
        color(shadowColor)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgText(vg, x + offsetX, y + offsetY, text)

        NanoVG.nvgFontBlur(vg, 0f)
        color(color)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgText(vg, x, y + .5f, text)
    }

    override fun textWidth(text: String, size: Float, font: Font): Float {
        if (vg == -1L) return 0f
        NanoVG.nvgFontSize(vg, size)
        NanoVG.nvgFontFaceId(vg, getFontID(font))
        return NanoVG.nvgTextBounds(vg, 0f, 0f, text, fontBounds)
    }

    override fun wrappedText(text: String, x: Float, y: Float, w: Float, size: Float, color: Int, font: Font, lineHeight: Float) {
        NanoVG.nvgFontSize(vg, size)
        NanoVG.nvgFontFaceId(vg, getFontID(font))
        NanoVG.nvgTextLineHeight(vg, lineHeight)
        color(color)
        NanoVG.nvgFillColor(vg, nvgColor)
        NanoVG.nvgTextBox(vg, x, y, w, text)
    }

    override fun textBounds(text: String, w: Float, size: Float, font: Font, lineHeight: Float): FloatArray {
        val bounds = FloatArray(4)
        if (vg == -1L) return bounds
        NanoVG.nvgFontSize(vg, size)
        NanoVG.nvgFontFaceId(vg, getFontID(font))
        NanoVG.nvgTextLineHeight(vg, lineHeight)
        NanoVG.nvgTextBoxBounds(vg, 0f, 0f, w, text, bounds)
        return bounds // [minX, minY, maxX, maxY]
    }

    override fun image(image: Int, textureWidth: Int, textureHeight: Int, subX: Int, subY: Int, subW: Int, subH: Int, x: Float, y: Float, w: Float, h: Float, radius: Float) {
        if (image == -1) return

        val sx = subX.toFloat() / textureWidth
        val sy = subY.toFloat() / textureHeight
        val sw = subW.toFloat() / textureWidth
        val sh = subH.toFloat() / textureHeight

        val iw = w / sw
        val ih = h / sh
        val ix = x - iw * sx
        val iy = y - ih * sy

        NanoVG.nvgImagePattern(vg, ix, iy, iw, ih, 0f, image, 1f, nvgPaint)
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRoundedRect(vg, x, y, w, h + .5f, radius)
        NanoVG.nvgFillPaint(vg, nvgPaint)
        NanoVG.nvgFill(vg)
    }

    override fun image(image: Image, x: Float, y: Float, w: Float, h: Float, radius: Float) {
        NanoVG.nvgImagePattern(vg, x, y, w, h, 0f, getImage(image), 1f, nvgPaint)
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRoundedRect(vg, x, y, w, h + .5f, radius)
        NanoVG.nvgFillPaint(vg, nvgPaint)
        NanoVG.nvgFill(vg)
    }

    override fun svg(id: String, x: Float, y: Float, w: Float, h: Float, alpha: Float) {
        val nvg = getImage(Image(id))

        NanoVG.nvgImagePattern(vg, x, y, w, h, 0f, nvg, alpha, nvgPaint)
        NanoVG.nvgBeginPath(vg)
        NanoVG.nvgRect(vg, x, y, w, h + .5f)
        NanoVG.nvgFillPaint(vg, nvgPaint)
        NanoVG.nvgFill(vg)
    }

    fun createNVGImage(glId: Int, width: Int, height: Int): Int {
        if (vg == -1L) return -1
        return NanoVGGL3.nvglCreateImageFromHandle(vg, glId, width, height, 0)
    }

    override fun createImage(resourcePath: String, width: Int, height: Int, color: Int): Image {
        val image = Image(resourcePath)

        if (image.isSVG) {
            images.getOrPut(image) { NVGImage(0, loadSVG(image, width, height, color)) }.count++
        } else {
            images.getOrPut(image) { NVGImage(0, loadImage(image)) }.count++
        }
        return image
    }

    override fun cleanCache() {
        val iter = images.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next()
            if (vg != -1L) NanoVG.nvgDeleteImage(vg, entry.value.nvg)
            iter.remove()
        }
    }

    override fun deleteImage(image: Image) {
        val nvgImage = images[image] ?: return
        nvgImage.count--
        if (nvgImage.count == 0) {
            if (vg != -1L) NanoVG.nvgDeleteImage(vg, nvgImage.nvg)
            images.remove(image)
        }
    }

    private fun getImage(image: Image): Int {
        return images[image]?.nvg ?: throw IllegalStateException("Image (${image.identifier}) doesn't exist")
    }

    // Without a NanoVG context (Vulkan backend) images are tracked but never uploaded.
    private fun loadImage(image: Image): Int {
        if (vg == -1L) return -1
        val w = IntArray(1)
        val h = IntArray(1)
        val channels = IntArray(1)
        val buffer = STBImage.stbi_load_from_memory(image.buffer(), w, h, channels, 4)
            ?: throw NullPointerException("Failed to load image: ${image.identifier}")
        return NanoVG.nvgCreateImageRGBA(vg, w[0], h[0], 0, buffer)
    }

    private fun loadSVG(image: Image, svgWidth: Int, svgHeight: Int, color: Int): Int {
        if (vg == -1L) return -1
        var vec = image.stream.use { it.bufferedReader().readText() }

        val hexColor = "#%06X".format(color and 0xFFFFFF)
        vec = vec.replace("currentColor", hexColor)

        val svg = NanoSVG.nsvgParse(vec, "px", 96f)
            ?: throw IllegalStateException("Failed to parse ${image.identifier}")

        val width = if (svgWidth > 0) svgWidth else svg.width().toInt()
        val height = if (svgHeight > 0) svgHeight else svg.height().toInt()
        val buffer = MemoryUtil.memAlloc(width * height * 4)

        val previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D)

        try {
            val rasterizer = NanoSVG.nsvgCreateRasterizer()
            NanoSVG.nsvgRasterize(rasterizer, svg, 0f, 0f, width.toFloat() / svg.width(), buffer, width, height, width * 4)
            val nvgImage = NanoVG.nvgCreateImageRGBA(vg, width, height, 0, buffer)
            NanoSVG.nsvgDeleteRasterizer(rasterizer)

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture)

            return nvgImage
        } finally {
            NanoSVG.nsvgDelete(svg)
            MemoryUtil.memFree(buffer)
        }
    }

    private fun color(color: Int) {
        NanoVG.nvgRGBA(color.red.toByte(), color.green.toByte(), color.blue.toByte(), color.alpha.toByte(), nvgColor)
    }

    private fun color(color1: Int, color2: Int) {
        NanoVG.nvgRGBA(color1.red.toByte(), color1.green.toByte(), color1.blue.toByte(), color1.alpha.toByte(), nvgColor)
        NanoVG.nvgRGBA(color2.red.toByte(), color2.green.toByte(), color2.blue.toByte(), color2.alpha.toByte(), nvgColor2)
    }

    private fun gradient(color1: Int, color2: Int, x: Float, y: Float, w: Float, h: Float, direction: Gradient) {
        color(color1, color2)
        when (direction) {
            Gradient.LeftToRight -> NanoVG.nvgLinearGradient(vg, x, y, x + w, y, nvgColor, nvgColor2, nvgPaint)
            Gradient.TopToBottom -> NanoVG.nvgLinearGradient(vg, x, y, x, y + h, nvgColor, nvgColor2, nvgPaint)
            Gradient.TopLeftToBottomRight -> NanoVG.nvgLinearGradient(vg, x, y, x + w, y + h, nvgColor, nvgColor2, nvgPaint)
        }
    }

    private fun getFontID(font: Font): Int {
        return fontMap.getOrPut(font) {
            val buffer = font.buffer()
            NVGFont(NanoVG.nvgCreateFontMem(vg, font.name, buffer, false), buffer)
        }.id
    }

    private class Scissor(val previous: Scissor?, val x: Float, val y: Float, val maxX: Float, val maxY: Float) {
        fun applyScissor() {
            if (previous == null) NanoVG.nvgScissor(vg, x, y, maxX - x, maxY - y)
            else {
                val x = max(x, previous.x)
                val y = max(y, previous.y)
                val width = max(0f, (min(maxX, previous.maxX) - x))
                val height = max(0f, (min(maxY, previous.maxY) - y))
                NanoVG.nvgScissor(vg, x, y, width, height)
            }
        }
    }

    private data class NVGImage(var count: Int, val nvg: Int)
    private data class NVGFont(val id: Int, val buffer: ByteBuffer)
}
