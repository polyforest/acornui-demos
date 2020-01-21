rootProject.name = "acornui-demos"

pluginManagement {
	val acornVersion: String by extra
	repositories {
		mavenLocal()
		maven {
			url = uri("http://artifacts.acornui.com/mvn/")
		}
		maven {
			url = uri("https://dl.bintray.com/kotlin/kotlin-dev/")
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

// Add modules as they are created.  By default, subprojects take on the name of their root directory in gradle.
include("text-demo", "spine-demo", "rect-demo", "datagrid-demo", "fileio-demo", "new-project", "performance-test")