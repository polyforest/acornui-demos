plugins {
	id("com.acornui.app")
}

kotlin {
	sourceSets {
		commonMain {
			dependencies {
				runtimeOnly("com.acornui.skins:basic")
			}
		}
	}
}

tasks.runJvm {
	main = "rectdemo.jvm.RectDemoJvmKt"
}