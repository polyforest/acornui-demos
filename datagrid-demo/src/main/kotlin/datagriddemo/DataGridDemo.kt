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

package datagriddemo

import com.acornui.application
import com.acornui.component.DataGrid
import com.acornui.component.DivComponent
import com.acornui.component.layout.vGroup
import com.acornui.component.panel
import com.acornui.component.scroll.scrollArea
import com.acornui.component.style.StyleTag
import com.acornui.css.cssVar
import com.acornui.css.percent
import com.acornui.di.Context
import com.acornui.dom.add
import com.acornui.dom.addCssToHead
import com.acornui.dom.head
import com.acornui.dom.linkElement
import com.acornui.i18n.i18nBundle
import com.acornui.runMain
import com.acornui.skins.Theme
import com.acornui.skins.addCssToHead

/**
 * @author nbilyk
 */
class DataGridDemo(owner: Context) : DivComponent(owner) {



	init {

		i18nBundle("datagrid")
		Theme().addCssToHead()

		head.add(
			linkElement(
				"https://fonts.googleapis.com/css2?family=Montserrat:wght@300;500&display=swap",
				rel = "stylesheet"
			)
		)

		addClass(styleTag)
		+scrollArea {
			height(100.percent)
			+panel {


				+vGroup {
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

					+CountriesExample(this)

//				navAddElement(nav, "countries", disposeOnRemove = true) { CountriesExample(this) layout { fill() } }
//				navAddElement(nav, "generated", disposeOnRemove = true) { GeneratedDataExample(this) layout { fill() } }
				}
			}
		}


	}

	companion object {

		val styleTag = StyleTag("DataGridDemo")

		init {
			addCssToHead(
				"""
				$styleTag {
					padding: ${cssVar(Theme::padding)};
					font-family: 'Montserrat', sans-serif;
					font-weight: 300;
				}
				
				${DataGrid.headerRowStyle} {
					font-weight: 500;
				}
			"""
			)
		}
	}
}

fun main() = runMain {
	application("acornUiRoot", config) {
		addElement(DataGridDemo(this))
	}
}