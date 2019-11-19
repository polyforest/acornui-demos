/*
 * Copyright 2019 Poly Forest, LLC
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

package com.acornui.performancetest.js

import com.acornui.async.runMain
import com.acornui.component.Stage
import com.acornui.component.stage
import com.acornui.di.Injector
import com.acornui.di.InjectorImpl
import com.acornui.performancetest.MeasuredStage
import com.acornui.performancetest.PerformanceTest
import com.acornui.performancetest.appConfig
import com.acornui.popup.PopUpManager
import com.acornui.popup.PopUpManagerImpl
import com.acornui.webgl.WebGlApplication

fun main() = runMain {
	object : WebGlApplication("performanceTestRoot") {
		override suspend fun createInjector(): Injector {
			val p = InjectorImpl(bootstrap.dependenciesList() + listOf(PopUpManager to PopUpManagerImpl()))
			return InjectorImpl(p, listOf(Stage to MeasuredStage(Stage.factory(p)!!)))
		}
	}.start(appConfig) {
		stage.addElement(PerformanceTest(this))
	}
}


