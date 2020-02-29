rootProject.name = "acornui-demos"

pluginManagement {
	val acornVersion: String by settings
	buildscript {
		repositories {
			if (acornVersion.endsWith("-SNAPSHOT")) {
				maven("https://oss.sonatype.org/content/repositories/snapshots")
				mavenLocal()
			}
			mavenCentral()
			jcenter()
			maven("https://dl.bintray.com/kotlin/kotlin-eap/")
		}
		dependencies {
			classpath("com.acornui:gradle-app-plugins:$acornVersion")
		}
	}
}
apply(plugin = "com.acornui.settings")

// Add modules as they are created.  By default, subprojects take on the name of their root directory in gradle.
include("text-demo", "spine-demo", "rect-demo", "datagrid-demo", "fileio-demo", "new-project", "performance-test")