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

import com.acornui.application
import com.acornui.component.*
import com.acornui.component.input.*
import com.acornui.component.layout.FlowGroup
import com.acornui.component.layout.hFlowGroup
import com.acornui.component.layout.hGroup
import com.acornui.component.layout.vGroup
import com.acornui.component.scroll.scrollArea
import com.acornui.component.style.StyleTag
import com.acornui.component.text.text
import com.acornui.component.text.textArea
import com.acornui.css.cssVar
import com.acornui.css.percent
import com.acornui.demo.initThemes
import com.acornui.demo.themeButton
import com.acornui.di.Context
import com.acornui.dom.addCssToHead
import com.acornui.dom.div
import com.acornui.dom.form
import com.acornui.input.clicked
import com.acornui.runMain
import com.acornui.skins.Theme
import com.acornui.time.Date
import com.acornui.version

/**
 * An example of input controls.
 */
class InputsDemo(owner: Context) : DivComponent(owner) {

	init {
		addClass(styleTag)
		initThemes()

		+scrollArea {

			applyCss(
				"""
					padding: ${cssVar(Theme::padding)};
					width: 100%;
					height: 100%;
			"""
			)

			+form {
				style.display = "contents"
				+vGroup {
					applyCss(
						"""
						min-height: 100%;						
					"""
					)

					+hGroup {
						+themeButton()
					}

					+panel {
						applyCss("""
							width: 100%;
							max-width: 800px;
							margin-left: auto;
							margin-right: auto;							
						""")

						+hFlowGroup {
							addClass(mainFlowTag)

							+vGroup {

								+button("Button") {
									clicked.listen {
										println("Button clicked")
									}
								}

								+checkbox(true) {
									label = "Checkbox"
								}

								+checkbox {
									indeterminate = true

									var count = 0
									changed.listen {
										if (++count % 3 == 0)
											indeterminate = true
									}
									label = "Checkbox with indeterminate state and long name!"
									style.maxWidth = "300px"
								}

								val radioGroup = RadioGroup()
								+radio(radioGroup, "1") {
									label = "Radio 1"
								}
								+radio(radioGroup, "2") {
									label = "Radio 2"
								}
								+radio(radioGroup, "3") {
									label = "Radio 3"
								}

								+switch {
									label = "Switch"
								}

								+textInput {
									placeholder = "Text Input"
								}

								+textArea {
									placeholder = "Text Area"
									applyCss("max-width: 100%;")
								}

								+fileInput {
									changed.listen {
										println("File changed")
									}
									input.listen {
										println("File input")
									}
								}

								+dateInput {
									defaultValueAsDate = Date()

									changed.listen {
										println(valueToString())
									}
								}

								+monthInput {
									defaultValueAsDate = Date()

									changed.listen {
										println(valueToString())
									}
								}

								+numberInput {
									placeholder = "Number"
								}

							}


							+vGroup {

								+emailInput {
									placeholder = "Email"
									autocomplete = "username"
									name = "exampleEmail"
								}

								+passwordInput {
									placeholder = "Password"
									autocomplete = "new-password"
									name = "examplePassword"
								}

								+rangeInput()

								+searchInput {
									placeholder = "Search"
								}

								+timeInput {
									defaultValueAsDate = Date()

									changed.listen {
										println(valueToString())
										println(valueToString(second = null))
									}
								}

								+colorInput {
									dom.defaultValue = "#ff0000"
								}

								// Disabled components:

								+button {
									label = "Button Disabled"
									disabled = true
								}
								+checkbox(true) {
									label = "Checkbox Disabled"
									disabled = true
								}


								+checkbox {
									indeterminate = true
									disabled = true
									label = "Disabled indeterminate check"
								}

								+textInput {
									placeholder = "Text Input Disabled"
									disabled = true
								}

								+textArea {
									placeholder = "Text Input Disabled"
									disabled = true
								}

								+rangeInput {
									disabled = true
								}

								+radio(RadioGroup(), "4") {
									label = "Radio 4 Disabled"
									disabled = true
								}

								+switch {
									label = "Switch Disabled"
									disabled = true
								}

								+tabNavigator {
									applyCss("max-height: 200px;")
									tabs {
										+tab("one", "One")
										+tab("two", "Two")
									}
									+div {
										tab = "one"
										+"One"
									}
									+div {
										tab = "two"
										+"Soko radicchio bunya nuts gram dulse silver beet parsnip napa cabbage lotus root sea lettuce brussels sprout cabbage. Catsear cauliflower garbanzo yarrow salsify chicory garlic bell pepper napa cabbage lettuce tomato kale arugula melon sierra leone bologi rutabaga tigernut. Sea lettuce gumbo grape kale kombu cauliflower salsify kohlrabi okra sea lettuce broccoli celery lotus root carrot winter purslane turnip greens garlic. Jicama garlic courgette coriander radicchio plantain scallion cauliflower fava bean desert raisin spring onion chicory bunya nuts. Sea lettuce water spinach gram fava bean leek dandelion silver beet eggplant bush tomato."
									}
								}

							}
						}
					}
					+hFlowGroup {
						width(100.percent)
						+resetInput("Reset")
						+submitInput("Submit")
					}

					+text("v$version") {
						applyCss("""
							justify-self: flex-end;
							align-self: flex-end;
							margin-top: auto;
							""")
					}
				} // vgroup
			} // form

		} // scroll area

	}

	@Suppress("CssOverwrittenProperties")
	companion object {

		val styleTag = StyleTag("InputsDemo")
		val mainFlowTag = StyleTag("mainFlow")

		init {
			addCssToHead(
				"""
					
$mainFlowTag {
	justify-content: space-evenly;
	width: 100%;
}

$mainFlowTag > ${FlowGroup.contentsTag} > div {
	max-width: 250px;
	align-items: stretch;
}

"""
			)
		}
	}
}

/**
 * `main` is our main entry point.
 *
 * This method is wrapped in a [runMain] block to set up the main context.
 */
fun main() = runMain {

	application("acornUiRoot") {
		// Create and add our main component to the stage:
		+InputsDemo(this)
	}
}
