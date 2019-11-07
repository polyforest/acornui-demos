rootProject.name = "acornui-demos"

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
				requested.id.namespace == "com.acornui" ->
					useVersion(acornVersion)
			}
		}
	}
}

enableFeaturePreview("GRADLE_METADATA")

// Add modules as they are created.  By default, subprojects take on the name of their root directory in gradle.
include("text-demo", "spine-demo", "rect-demo", "datagrid-demo", "fileio-demo", "new-project")
//include("new-project")

// Acorn composite project as sub-projects as a workaround to https://youtrack.jetbrains.com/issue/KT-30285
//val acornUiHome: String? by extra
//if (acornUiHome != null && file(acornUiHome!!).exists()) {
//	include(":acornui")
//	project(":acornui").projectDir = file("$acornUiHome")
//
//	listOf("utils", "core", "game", "spine", "test-utils").forEach { acornModule ->
//		val name = ":acornui:acornui-$acornModule"
//		include(name)
//		project(name).projectDir = file("$acornUiHome/acornui-$acornModule")
//	}
//	listOf("lwjgl", "webgl").forEach { backend ->
//		val name = ":acornui:backends:acornui-$backend-backend"
//		include(name)
//		project(name).projectDir = file("$acornUiHome/backends/acornui-$backend-backend")
//	}
//}
