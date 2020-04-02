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
		val jsMain by getting {
			dependencies {
				implementation(npm("jszip", "3.2.2"))
			}
		}
	}
}