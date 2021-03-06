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

package com.acornui.demo

import com.acornui.component.StageStyle
import com.acornui.component.UiComponent
import com.acornui.component.input.button
import com.acornui.component.stage
import com.acornui.component.text.text
import com.acornui.css.css
import com.acornui.di.Context
import com.acornui.dom.add
import com.acornui.dom.addStyleToHead
import com.acornui.dom.head
import com.acornui.dom.linkElement
import com.acornui.google.Icons
import com.acornui.google.icon
import com.acornui.skins.DefaultStyles
import com.acornui.skins.darkTheme
import com.acornui.skins.theme
import kotlinx.browser.localStorage

fun initThemes() {
	head.add(
		linkElement(
			"https://fonts.googleapis.com/css2?family=Montserrat:wght@300;500&display=swap",
			rel = "stylesheet"
		)
	)
	addStyleToHead(
		"""
				${StageStyle.stage} {
					font-family: 'Montserrat', sans-serif;
					font-weight: 300;
				}
				
			"""
	)
	DefaultStyles
}

fun Context.themeButton(): UiComponent {
	return button {
		+icon(Icons.BRIGHTNESS_MEDIUM)
		val label = +text("Dark") {
			style.marginLeft = css("0.5ch")
		}
		toggleOnClick = true
		toggled = (localStorage.getItem("darkMode") ?: "false").toBoolean()
		if (toggled)
			stage.theme = darkTheme

		val refreshLabel = {
			label.label = if (toggled) "Light" else "Dark"
		}
		refreshLabel()
		toggledChanged.listen {
			refreshLabel()
			stage.theme = if (toggled) darkTheme else null
			localStorage.setItem("darkMode", toggled.toString())
		}
	}
}