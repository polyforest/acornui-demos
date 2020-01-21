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

package rectdemo

import com.acornui.component.*
import com.acornui.component.layout.HAlign
import com.acornui.component.layout.VAlign
import com.acornui.component.layout.algorithm.*
import com.acornui.component.scroll.hSlider
import com.acornui.component.scroll.scrollArea
import com.acornui.component.text.text
import com.acornui.di.Owned
import com.acornui.tween.Tween
import com.acornui.graphic.Color
import com.acornui.math.Corners
import com.acornui.math.Pad
import com.acornui.signal.bind
import com.acornui.skins.BasicUiSkin
import com.acornui.skins.Theme

/**
 * @author nbilyk
 */
class RectDemo(owner: Owned) : StackLayoutContainer<UiComponent>(owner) {

	init {
		Tween.prepare()

		BasicUiSkin(stage, Theme()).apply()

		val linearGradient = LinearGradient(GradientDirection.RIGHT,
				ColorStop(Color(1f, 0f, 0f, 1f), 0f),
				ColorStop(Color(1f, 0f, 1f, 1f), 1f)
		)

		+scrollArea {
			style.tossScrolling = true
			+hGroup {
				minWidth = 760f

				style.padding = Pad(10f)
				style.verticalAlign = VAlign.TOP

				// Create the rect now so we can reference it, but don't add it as a child until later.
				val demoRect = rect {
					style.borderRadii = Corners(7.5f)
					style.margin = Pad(10f)
					style.borderThicknesses = Pad(1f)

					style.borderColors = BorderColors(Color(0x272727FF))
					style.backgroundColor = Color(0x0066CCFF)
					layoutData = StackLayoutData().apply {
						width = 200f; height = 140f
					}
				}

				val s = demoRect.style
				+vGroup {
					+headingGroup {
						label = "Border Thickness"

						+form {
							+formLabel("Top")
							+slider({ s.borderThicknesses.top }, { s.borderThicknesses = s.borderThicknesses.copy(top = it) })

							+formLabel("Right")
							+slider({ s.borderThicknesses.right }, { s.borderThicknesses = s.borderThicknesses.copy(right = it) })

							+formLabel("Bottom")
							+slider({ s.borderThicknesses.bottom }, { s.borderThicknesses = s.borderThicknesses.copy(bottom = it) })

							+formLabel("Left")
							+slider({ s.borderThicknesses.left }, { s.borderThicknesses = s.borderThicknesses.copy(left = it) })
						} layout { fill() }
					} layout { widthPercent = 1f }

					+headingGroup {
						label = "Corner Radius"
						+form {
							+formLabel("Top Left X")
							+slider({ s.borderRadii.topLeft.x }, { s.borderRadii = s.borderRadii.copy(topLeft = s.borderRadii.topLeft.copy(x = it)) })
							+formLabel("Top Left Y")
							+slider({ s.borderRadii.topLeft.y }, { s.borderRadii = s.borderRadii.copy(topLeft = s.borderRadii.topLeft.copy(y = it)) })

							+formLabel("Top Right X")
							+slider({ s.borderRadii.topRight.x }, { s.borderRadii = s.borderRadii.copy(topRight = s.borderRadii.topRight.copy(x = it)) })
							+formLabel("Top Right Y")
							+slider({ s.borderRadii.topRight.y }, { s.borderRadii = s.borderRadii.copy(topRight = s.borderRadii.topRight.copy(y = it)) })

							+formLabel("Bottom Right X")
							+slider({ s.borderRadii.bottomRight.x }, { s.borderRadii = s.borderRadii.copy(bottomRight = s.borderRadii.bottomRight.copy(x = it)) })
							+formLabel("Bottom Right Y")
							+slider({ s.borderRadii.bottomRight.y }, { s.borderRadii = s.borderRadii.copy(bottomRight = s.borderRadii.bottomRight.copy(y = it)) })

							+formLabel("Bottom Left X")
							+slider({ s.borderRadii.bottomLeft.x }, { s.borderRadii = s.borderRadii.copy(bottomLeft = s.borderRadii.bottomLeft.copy(x = it)) })
							+formLabel("Bottom Left Y")
							+slider({ s.borderRadii.bottomLeft.y }, { s.borderRadii = s.borderRadii.copy(bottomLeft = s.borderRadii.bottomLeft.copy(y = it)) })
						} layout { fill() }
					} layout { widthPercent = 1f }

					+headingGroup {
						label = "Margin"
						+form {
							+formLabel("Top")
							+slider({ s.margin.top }, { s.margin = s.margin.copy(top = it) })

							+formLabel("Right")
							+slider({ s.margin.right }, { s.margin = s.margin.copy(right = it) })

							+formLabel("Bottom")
							+slider({ s.margin.bottom }, { s.margin = s.margin.copy(bottom = it) })

							+formLabel("Left")
							+slider({ s.margin.left }, { s.margin = s.margin.copy(left = it) })
						} layout { fill() }
					} layout { widthPercent = 1f }

					+headingGroup {
						label = "Size"

						+form {
							val size = demoRect.layoutData as StackLayoutData
							+formLabel("Width")
							+slider({ size.width!! }, { size.width = it }, min = 20f, max = 360f)

							+formLabel("Height")
							+slider({ size.height!! }, { size.height = it }, min = 20f, max = 600f)
						} layout { fill() }
					} layout { widthPercent = 1f }

					+headingGroup {
						label = "Border Color"
						+form {
							+formLabel("Top")
							+colorPicker {
								color = s.borderColors.top
								changed.add {
									s.borderColors = s.borderColors.copy(top = value.toRgb())
								}
							}
							+formLabel("Right")
							+colorPicker {
								color = s.borderColors.right
								changed.add {
									s.borderColors = s.borderColors.copy(right = value.toRgb())
								}
							}
							+formLabel("Bottom")
							+colorPicker {
								color = s.borderColors.bottom
								changed.add {
									s.borderColors = s.borderColors.copy(bottom = value.toRgb())
								}
							}
							+formLabel("Left")
							+colorPicker {
								color = s.borderColors.left
								changed.add {
									s.borderColors = s.borderColors.copy(left = value.toRgb())
								}
							}
						} layout { fill() }
					} layout { widthPercent = 1f }

					+headingGroup {
						label = "Background Gradient"

						val solidForm = form {
							+formLabel("Background Color")
							+colorPicker {
								color = s.backgroundColor
								changed.add {
									s.backgroundColor = value.toRgb()
								}
							}
						}

						val linearForm = vGroup {
							radioGroup<GradientDirection> {
								+radioButton(GradientDirection.ANGLE) { label  = "Angle" }
								+radioButton(GradientDirection.TOP) { label  = "Top" }
								+radioButton(GradientDirection.TOP_RIGHT) { label  = "Top Right" }
								+radioButton(GradientDirection.RIGHT) { label  = "Right" }
								+radioButton(GradientDirection.BOTTOM_RIGHT) { label  = "Bottom Right" }
								+radioButton(GradientDirection.BOTTOM) { label  = "Bottom" }
								+radioButton(GradientDirection.BOTTOM_LEFT) { label  = "Bottom Left" }
								+radioButton(GradientDirection.LEFT) { label  = "Left" }
								+radioButton(GradientDirection.TOP_LEFT) { label  = "Top Left" }

								changed.add {
									s.linearGradient = linearGradient.copy(direction = selectedData ?: GradientDirection.TOP)
								}
								selectedData = linearGradient.direction
							}

						}

						+vGroup {
							radioGroup<GradientType> {
								+radioButton(GradientType.SOLID) { label  = "Solid" }
								+radioButton(GradientType.LINEAR) { label  = "Linear" }
								+hr() layout { widthPercent = 1f }

								changed.bind {
									-linearForm
									-solidForm
									when (selectedData) {
										GradientType.LINEAR -> {
											demoRect.style.linearGradient = linearGradient
											+linearForm layout { fill() }
										}
										else -> {
											demoRect.style.linearGradient = null
											+solidForm layout { fill() }
										}
									}
								}
								selectedData = GradientType.SOLID
							}
						} layout { fill() }
					} layout { widthPercent = 1f }

				} layout { width = 430f }

				+stack {
					style.horizontalAlign = HAlign.CENTER
					+panel {
						style.background = { repeatingTexture("assets/uiskin/AlphaCheckerboard.png") { alpha = 0.4f} }
						style.padding = Pad()
						+demoRect
					}
				} layout { widthPercent = 1f }

			} layout { fill() }
		} layout { fill() }

	}
}

private fun Owned.slider(getter: () -> Float, setter: (value: Float) -> Unit, min: Float = 0f, max: Float = 100f): UiComponent {
	return hGroup {
		val hSlider = +hSlider {
			scrollModel.min = min
			scrollModel.max = max
			scrollModel.value = getter()
			scrollModel.snap = 1f
			scrollModel.changed.add {
				setter(scrollModel.value)
			}
		}
		+text {
			flowStyle.horizontalAlign = FlowHAlign.RIGHT
			text = getter().toInt().toString()
			hSlider.scrollModel.changed.add {
				text = it.value.toInt().toString()
			}
		} layout { width = 30f }
	}
}

private enum class GradientType {
	SOLID,
	LINEAR,
	RADIAL
}