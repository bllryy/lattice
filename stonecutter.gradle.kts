plugins {
	id("dev.kikugie.stonecutter")
	id("net.fabricmc.fabric-loom") apply false
	id("org.jetbrains.kotlin.jvm") apply false
}

// The version the sources in src/ are currently written for. Switch with the
// "Set active project to ..." tasks; commit with this back on the vcsVersion.
stonecutter active "26.3"
