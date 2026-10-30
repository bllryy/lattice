plugins {
	id("net.fabricmc.fabric-loom")
	id("org.jetbrains.kotlin.jvm")
	`maven-publish`
}

// Applied once per Minecraft version (`:26.1.2`, `:26.2`, `:26.3`); see settings.gradle.kts.
val minecraftVersion = sc.current.version
val fabricApiVersion: String = sc.properties["deps.fabric_api"]
val nanovgVersion: String = sc.properties["deps.nanovg"]
val minecraftCompat: String = sc.properties["mod.mc_compat"]
val artifactName = "lattice-$minecraftVersion-fabric"

version = property("mod_version") as String
group = property("maven_group") as String

base {
	archivesName = artifactName
}

repositories {
	mavenCentral()
}

// 26.x is non-obfuscated: fabric-loom without remapping, no mappings dependency.
dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
	implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
	implementation("org.jetbrains.kotlin:kotlin-stdlib")
	implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
	implementation("com.mojang:brigadier:${property("brigadier_version")}")

	// NanoVG is not part of Minecraft's LWJGL modules, so bundle it (and its
	// natives) into the mod jar via include so it is present at runtime.
	implementation("org.lwjgl:lwjgl-nanovg:$nanovgVersion")
	include("org.lwjgl:lwjgl-nanovg:$nanovgVersion")
	for (platform in listOf("linux", "linux-arm64", "windows", "macos", "macos-arm64")) {
		runtimeOnly("org.lwjgl:lwjgl-nanovg:$nanovgVersion:natives-$platform")
		include("org.lwjgl:lwjgl-nanovg:$nanovgVersion:natives-$platform")
	}
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
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

kotlin {
	jvmToolchain(25)
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = artifactName
			from(components["java"])
		}
	}
	repositories {
		mavenLocal()

		// Self-hosted Maven. Point it at a server with MAVEN_URL (+ MAVEN_USERNAME /
		// MAVEN_PASSWORD), as env vars or -P properties; without one it writes a
		// plain file repository to build/maven.
		fun setting(name: String) = providers.gradleProperty(name).orElse(providers.environmentVariable(name))
		val mavenUrl = setting("MAVEN_URL")
		maven {
			name = "bllry"
			url = uri(mavenUrl.orElse(rootProject.layout.buildDirectory.dir("maven").map { it.asFile.toURI().toString() }).get())
			if (setting("MAVEN_USERNAME").isPresent) {
				credentials {
					username = setting("MAVEN_USERNAME").get()
					password = setting("MAVEN_PASSWORD").get()
				}
			}
		}
	}
}

// Copies this version's lattice and example mod jars into a Minecraft mods folder.
// Override the target with -PmodsDir=/path/to/mods (defaults to the Prism instance).
val defaultModsDir = "${System.getProperty("user.home")}/.local/share/PrismLauncher/instances/Lattice testing/minecraft/mods"

tasks.register<Copy>("deployMods") {
	group = "lattice"
	description = "Copies the lattice and example mod jars into a Minecraft mods folder."
	val exampleJar = project(":example:${sc.current.project}").tasks.named<Jar>("jar")
	dependsOn(tasks.jar, exampleJar)

	from(tasks.jar.flatMap { it.archiveFile })
	from(exampleJar.flatMap { it.archiveFile })
	into(providers.gradleProperty("modsDir").orElse(defaultModsDir))
}
