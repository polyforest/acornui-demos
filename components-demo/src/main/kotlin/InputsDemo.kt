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
import com.acornui.css.css
import com.acornui.css.percent
import com.acornui.di.Context
import com.acornui.dom.*
import com.acornui.google.Icons
import com.acornui.google.icon
import com.acornui.input.clicked
import com.acornui.runMain
import com.acornui.skins.Theme
import com.acornui.skins.addCssToHead
import com.acornui.skins.darkTheme
import com.acornui.time.Date
import com.acornui.version
import kotlin.browser.localStorage

/**
 * An example of input controls.
 */
class InputsDemo(owner: Context) : DivComponent(owner) {

	private var currentTheme: String = ""
		set(value) {
			if (field == value) return
			if (field.isNotEmpty())
				stage.dom.classList.remove(field)
			stage.dom.classList.add(value)
			field = value
			localStorage.setItem("theme", value)
		}

	init {
		// Adds the basic skin to the stage. This sets up styling for all Acorn UI components.
		println(version)

		val themes = mapOf(
			"default" to Theme(),
			"dark" to darkTheme
		)
		for (theme in themes) {
			theme.value.addCssToHead("." + theme.key)
		}

		currentTheme = localStorage.getItem("theme") ?: "default"

		head.add(
			linkElement(
				"https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@200;400&display=swap",
				rel = "stylesheet"
			)
		)

		+form {
			size(100.percent, 100.percent)
			+scrollArea {
				size(100.percent, 100.percent)

				applyCss(
					"""
                   padding: 15px;
                   max-width: 800px;
                   margin: auto;
                """
				)

				+hGroup {
					+button {
						+icon(Icons.BRIGHTNESS_MEDIUM)
						val label = +text("Dark") {
							style.marginLeft = css("0.5ch")
						}
						toggleOnClick = true
						toggled = currentTheme == "dark"
						val refreshLabel = {
							label.label = if (toggled) "Light" else "Dark"
						}
						refreshLabel()
						toggledChanged.listen {
							refreshLabel()
							currentTheme = (if (toggled) "dark" else "default")
						}
					}
				}

				+panel {

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

					}

				}
				+hFlowGroup {
					width(100.percent)
					+resetInput("Reset")
					+submitInput("Submit")
				}

			} // scroll area
		} // form
	}

	@Suppress("CssOverwrittenProperties")
	companion object {

		val mainFlowTag = StyleTag("mainFlow")

		init {
			addCssToHead(
				"""
${StageImpl.styleTag} * {
  font-family: 'Roboto Mono', monospace;
}

$mainFlowTag {
	width: 100%;
}

$mainFlowTag > ${FlowGroup.contentsTag} {
	justify-content: space-evenly;
}

$mainFlowTag > ${FlowGroup.contentsTag} > div {
	width: 250px;
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