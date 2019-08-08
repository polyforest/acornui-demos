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
include("text-demo", "spine-demo", "rect-demo", "datagrid-demo", "fileio-demo")