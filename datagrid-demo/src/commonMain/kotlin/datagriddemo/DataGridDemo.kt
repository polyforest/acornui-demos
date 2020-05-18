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

import com.acornui.component.StackLayoutContainer
import com.acornui.component.UiComponent
import com.acornui.component.hRadioGroup
import com.acornui.component.hr
import com.acornui.component.layout.algorithm.flow
import com.acornui.component.layout.algorithm.vGroup
import com.acornui.component.layout.spacer
import com.acornui.component.scroll.scrollArea
import com.acornui.component.text.text
import com.acornui.di.Context
import com.acornui.i18n.Locale
import com.acornui.i18n.i18nBundle
import com.acornui.math.Pad
import com.acornui.nav.NavBindable
import com.acornui.nav.navAddElement
import com.acornui.nav.navBinding
import com.acornui.nav.navigate
import com.acornui.skins.BasicUiSkin
import com.acornui.skins.Theme
import com.acornui.system.userInfo
import com.acornui.tween.Tween
import com.acornui.version

/**
 * @author nbilyk
 */
class DataGridDemo(owner: Context) : StackLayoutContainer<UiComponent>(owner), NavBindable {

	private val nav = navBinding("countries")

	init {
		Tween.prepare()
		i18nBundle("datagrid")

		BasicUiSkin(stage, Theme()).apply()

		+scrollArea {
			+vGroup {
				+flow {
					style.padding = Pad(4f)

					+hRadioGroup<String>() {
						changed.add {
							navigate(value ?: "")
						}
						+radioButton("countries", "Countries")
						+radioButton("generated", "Generated")
						value = "countries"
					}
					+spacer(width = 10f)

					+hRadioGroup<Locale>() {
						changed.add {
							if (value != null)
								userInfo.currentLocale.change { listOf(value!!) }
						}
						+radioButton(Locale("en-US"), "English")
						+radioButton(Locale("de-DE"), "German")
						+radioButton(Locale("fr-FR"), "French")
//							value = chooseLocale(listOf(Locale("en-US"), Locale("de-DE"), Locale("fr-FR")))
					}

					+spacer(width = 20f)

					+text {
						text = "v ${version.toVersionString()}"
					}
				} layout { widthPercent = 1f }

				+hr() layout { widthPercent = 1f }

				navAddElement(nav, "countries", disposeOnRemove = true) { CountriesExample(this) layout { fill() } }
				navAddElement(nav, "generated", disposeOnRemove = true) { GeneratedDataExample(this) layout { fill() } }
			} layout { fill() }
		} layout { fill() }
	}
}