package xyz.meowing.lattice.client

object Desktop {
    @JvmStatic
    val isWindows: Boolean

    @JvmStatic
    val isMac: Boolean

    @JvmStatic
    val isLinux: Boolean

    @JvmStatic
    val isWayland: Boolean

    init {
        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        isWindows = osName.startsWith("windows")
        isMac = osName.startsWith("mac")
        isLinux = osName.startsWith("linux")
        isWayland = isLinux && !System.getenv("WAYLAND_DISPLAY").isNullOrEmpty()
    }
}
