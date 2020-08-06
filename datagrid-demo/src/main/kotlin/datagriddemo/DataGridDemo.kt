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

package datagriddemo

import com.acornui.app
import com.acornui.component.Div
import com.acornui.component.applyCss
import com.acornui.component.layout.vGroup
import com.acornui.component.scroll.scrollArea
import com.acornui.component.style.cssClass
import com.acornui.component.text.text
import com.acornui.css.percent
import com.acornui.demo.initThemes
import com.acornui.demo.themeButton
import com.acornui.di.Context
import com.acornui.i18n.i18nBundle
import com.acornui.version

/**
 * @author nbilyk
 */
class DataGridDemo(owner: Context) : Div(owner) {



	init {
		i18nBundle("datagrid")
		initThemes()

		addClass(styleTag)
		+scrollArea {
			height(100.percent)
			+vGroup {
//					width(100.percent)
//				+flow {
//					style.padding = Pad(4f)
//
//					+hRadioGroup<String>() {
//						changed.add {
//							navigate(value ?: "")
//						}
//						+radioButton("countries", "Countries")
//						+radioButton("generated", "Generated")
//						value = "countries"
//					}
//					+spacer(width = 10f)
//
//					+hRadioGroup<Locale>() {
//						changed.add {
//							if (value != null)
//								userInfo.currentLocale.change { listOf(value!!) }
//						}
//						+radioButton(Locale("en-US"), "English")
//						+radioButton(Locale("de-DE"), "German")
//						+radioButton(Locale("fr-FR"), "French")
////							value = chooseLocale(listOf(Locale("en-US"), Locale("de-DE"), Locale("fr-FR")))
//					}
//
//					+spacer(width = 20f)
//
//					+text("v$version")
//				} layout { widthPercent = 1f }
//
//				+hr() layout { widthPercent = 1f }



				+themeButton()


				+CountriesExample(this)

				+text("v$version") {
					applyCss("""
							justify-self: flex-end;
							align-self: flex-end;
							margin-top: auto;
							""")
				}

//				navAddElement(nav, "countries", disposeOnRemove = true) { CountriesExample(this) layout { fill() } }
//				navAddElement(nav, "generated", disposeOnRemove = true) { GeneratedDataExample(this) layout { fill() } }
			}
		}


	}

	companion object {

		val styleTag by cssClass()

	}
}

fun main() = app("acornUiRoot") {
	+DataGridDemo(this)
}
