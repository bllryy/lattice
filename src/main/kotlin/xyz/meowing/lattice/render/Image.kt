package xyz.meowing.lattice.render

import org.lwjgl.system.MemoryUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.Files

// Derived from OdinFabric (BSD-3-Clause, (c) odtheking); see NOTICE.md.
class Image(
    val identifier: String,
    var stream: InputStream = getStream(identifier),
    private var buffer: ByteBuffer? = null
) {
    val isSVG: Boolean = identifier.endsWith(".svg", true)

    fun buffer(): ByteBuffer {
        if (buffer == null) {
            val bytes = stream.readBytes()
            buffer = MemoryUtil.memAlloc(bytes.size).put(bytes).flip() as ByteBuffer
            stream.close()
        }
        return buffer ?: throw IllegalStateException("Image has no data")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Image) return false
        return identifier == other.identifier
    }

    override fun hashCode(): Int = identifier.hashCode()

    companion object {
        private fun getStream(path: String): InputStream {
            val trimmedPath = path.trim()
            val file = File(trimmedPath)
            return if (file.exists() && file.isFile) Files.newInputStream(file.toPath())
            else this::class.java.getResourceAsStream(trimmedPath) ?: throw FileNotFoundException(trimmedPath)
        }
    }
}
