package xyz.meowing.lattice.render

import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Derived from OdinFabric (BSD-3-Clause, (c) odtheking); see NOTICE.md.
class Font {
    val name: String
    private val resourcePath: String?
    private val cachedBytes: ByteArray?

    constructor(name: String, resourcePath: String) {
        this.name = name
        this.resourcePath = resourcePath
        this.cachedBytes = null
    }

    constructor(name: String, inputStream: InputStream) {
        this.name = name
        this.resourcePath = null
        this.cachedBytes = inputStream.use { it.readBytes() }
    }

    fun buffer(): ByteBuffer {
        val bytes = cachedBytes ?: run {
            val stream = this::class.java.getResourceAsStream(resourcePath!!)
                ?: throw FileNotFoundException(resourcePath)
            stream.use { it.readBytes() }
        }

        return ByteBuffer.allocateDirect(bytes.size)
            .order(ByteOrder.nativeOrder())
            .put(bytes)
            .flip() as ByteBuffer
    }

    override fun hashCode(): Int = name.hashCode()

    override fun equals(other: Any?): Boolean = other is Font && name == other.name
}
