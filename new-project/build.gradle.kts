import com.acornui.build.plugins.util.kotlinExt
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinCompilationToRunnableFiles

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
//		val jsMain by getting {
////			dependencies {
////				implementation(npm("jszip", "3.2.2"))
////			}
//		}
	}
}


tasks.runJvm {
	main = "com.acornui.newproject.jvm.NewProjectJvmKt"
}

