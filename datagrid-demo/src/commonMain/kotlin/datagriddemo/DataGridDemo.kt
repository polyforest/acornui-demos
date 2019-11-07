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
import com.acornui.component.hr
import com.acornui.component.layout.algorithm.flow
import com.acornui.component.layout.algorithm.hGroup
import com.acornui.component.layout.algorithm.vGroup
import com.acornui.component.layout.spacer
import com.acornui.component.radioGroup
import com.acornui.component.scroll.scrollArea
import com.acornui.component.text.text
import com.acornui.di.Owned
import com.acornui.i18n.Locale
import com.acornui.i18n.chooseLocale
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
class DataGridDemo(owner: Owned) : StackLayoutContainer<UiComponent>(owner), NavBindable {

	private val nav = navBinding("countries")

	init {
		Tween.prepare()

		BasicUiSkin(stage, Theme()).apply()

		+scrollArea {
			+vGroup {
				+flow {
					style.padding = Pad(4f)

					+hGroup {
						radioGroup<String> {
							changed.add {
								navigate(selectedData ?: "")
							}
							+radioButton("countries", "Countries")
							+radioButton("generated", "Generated")
							selectedData = "countries"
						}
					}
					+spacer(width = 10f)

					+hGroup {
						radioGroup<Locale> {
							changed.add {
								if (selectedData != null)
									userInfo.currentLocale.change { listOf(selectedData!!)  }
							}
							+radioButton(Locale("en-US"), "English")
							+radioButton(Locale("de-DE"), "German")
							+radioButton(Locale("fr-FR"), "French")
							selectedData = chooseLocale(listOf(Locale("en-US"), Locale("de-DE"), Locale("fr-FR")))
						}
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