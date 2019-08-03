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
	main = "datagriddemo.jvm.DataGridDemoJvmKt"
}