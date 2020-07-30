/*
 * Copyright 2020 Poly Forest, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

rootProject.name = "acornui-demos"

pluginManagement {
	val acornVersion: String by settings
	repositories {
		gradlePluginPortal()
		mavenCentral()
		jcenter()
		maven("https://oss.sonatype.org/content/repositories/snapshots")
		mavenLocal()
	}
	resolutionStrategy {
		eachPlugin {
			when(requested.id.namespace) {
				"com.acornui" -> useVersion(acornVersion)
			}
		}
	}
}

// Add modules as they are created.  By default, subprojects take on the name of their root directory in gradle.
include("common", "components-demo", "datagrid-demo")