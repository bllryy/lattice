plugins {
	id("net.fabricmc.fabric-loom")
	id("org.jetbrains.kotlin.jvm")
}

// Stonecutter branch: applied once per Minecraft version (`:example:26.3`, ...),
// building against the lattice project for the same version.
val minecraftVersion = sc.current.version
val fabricApiVersion: String = sc.properties["deps.fabric_api"]
val nanovgVersion: String = sc.properties["deps.nanovg"]
val minecraftCompat: String = sc.properties["mod.mc_compat"]

version = rootProject.property("mod_version") as String
// Stonecutter names both projects after the version (`:26.3`, `:example:26.3`), so a distinct
// group keeps Gradle from treating the lattice dependency as this project depending on itself.
group = "${rootProject.property("maven_group")}.example"

base {
	archivesName = "lattice-example-$minecraftVersion-fabric"
}

repositories {
	mavenCentral()
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	implementation("net.fabricmc:fabric-loader:${rootProject.property("loader_version")}")
	implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("net.fabricmc:fabric-language-kotlin:${rootProject.property("fabric_kotlin_version")}")
	implementation("org.lwjgl:lwjgl-nanovg:$nanovgVersion")

	implementation(project(":${sc.current.project}"))
}

loom {
	runConfigs.all {
		runDirectory = rootProject.file("run")
	}
}

tasks.processResources {
	val props = mapOf("version" to project.version, "minecraft" to minecraftCompat)
	inputs.properties(props)
	filesMatching("fabric.mod.json") {
		expand(props)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
	options.release = 25
}

java {
	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
	jvmToolchain(25)
}
