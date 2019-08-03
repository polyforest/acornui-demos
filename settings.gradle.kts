rootProject.name = "acornui-demos"

val kotlinVersion: String by extra
val acornVersion: String by extra

pluginManagement {
	repositories {
		mavenLocal()
		maven {
			url = uri("http://artifacts.acornui.com/mvn/")
		}
		gradlePluginPortal()
	}
	resolutionStrategy {
		eachPlugin {
			when {
				requested.id.id == "kotlinx-serialization" ->
					useModule("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
				requested.id.namespace == "org.jetbrains.kotlin" ->
					useVersion(kotlinVersion)
				requested.id.namespace == "com.acornui" ->
					useVersion(acornVersion)
			}
		}
	}
}

enableFeaturePreview("GRADLE_METADATA")

// Add modules as they are created.  By default, subprojects take on the name of their root directory in gradle.
include("text-demo", "spine-demo", "rect-demo")


// Acorn composite project as sub-projects as a workaround to https://youtrack.jetbrains.com/issue/KT-30285
//val acornUiHome: String? by extra
//
//if (acornUiHome != null && file(acornUiHome!!).exists()) {
//	listOf("utils", "core", "game", "spine", "test-utils").forEach { acornModule ->
//		include(":acornui:acornui-$acornModule")
//		val proj = project(":acornui:acornui-$acornModule")
//		proj.projectDir = file("$acornUiHome/acornui-$acornModule")
//	}
//	listOf("lwjgl", "webgl").forEach { backend ->
//		include(":acornui:backends:acornui-$backend-backend")
//		val proj = project(":acornui:backends:acornui-$backend-backend")
//		proj.projectDir = file("$acornUiHome/backends/acornui-$backend-backend")
//	}
//}
