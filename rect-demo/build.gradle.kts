plugins {
	id("com.acornui.app")
}

val acornVersion: String by project
kotlin {
	sourceSets {
		commonMain {
			dependencies {
				runtimeOnly("com.acornui.skins:basic:$acornVersion")
			}
		}
	}
}

tasks.runJvm {
	main = "rectdemo.jvm.RectDemoJvmKt"
}