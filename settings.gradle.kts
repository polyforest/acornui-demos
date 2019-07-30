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
include("text-demo:app", "spine-demo:app", "rect-demo:app")