plugins {
	id("com.acornui.app")
}

kotlin {
	sourceSets {
		commonMain {
			dependencies {
				implementation("com.acornui.skins:basic")
			}
		}
	}
}

tasks.runJvm {
	main = "textdemo.jvm.TextDemoJvmKt"
}