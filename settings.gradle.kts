rootProject.name = "acornui-demos"

buildscript {
	val acornVersion: String by extra
	repositories {
		mavenLocal()
		maven { url = uri("http://artifacts.acornui.com/mvn/") }
	}
	dependencies {
		classpath("com.acornui:gradle-settings-plugins:$acornVersion")
	}
}

plugins.apply("com.acornui.root-settings")

// Add modules as they are created.  By default, subprojects take on the name of their root directory in gradle.
include("text-demo", "spine-demo", "rect-demo", "datagrid-demo", "fileio-demo", "new-project", "performance-test")