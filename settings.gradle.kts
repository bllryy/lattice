pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		mavenCentral()
		gradlePluginPortal()
	}

	plugins {
		id("net.fabricmc.fabric-loom") version "1.19.3"
		id("org.jetbrains.kotlin.jvm") version "2.4.21"
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.8"
}

// Each Minecraft version builds as its own subproject (`:26.3`, `:example:26.3`, ...)
// from the shared sources in src/, preprocessed by Stonecutter `//? if` comments.
// Per-version dependency versions live in stonecutter.properties.toml.
stonecutter {
	create(rootProject) {
		versions("26.1.2", "26.2", "26.3")
		vcsVersion = "26.3"
		branch("example")
	}
}

rootProject.name = "lattice"
