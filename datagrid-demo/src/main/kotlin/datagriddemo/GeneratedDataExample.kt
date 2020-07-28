///*
// * Copyright 2019 Poly Forest, LLC
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package datagriddemo
//
//import com.acornui.component.UiComponent
//import com.acornui.component.checkbox
//import com.acornui.component.datagrid.IntColumn
//import com.acornui.component.datagrid.dataGrid
//import com.acornui.component.hRadioGroup
//import com.acornui.component.layout.HAlign
//import com.acornui.component.layout.algorithm.FlowVAlign
//import com.acornui.component.layout.algorithm.VerticalLayoutContainer
//import com.acornui.component.layout.algorithm.flow
//import com.acornui.component.scroll.ScrollPolicy
//import com.acornui.component.text.text
//import com.acornui.di.Context
//import com.acornui.i18n.i18n
//import com.acornui.i18n.i18nBundle
//import com.acornui.i18n.string
//import com.acornui.input.interaction.click
//import com.acornui.math.Pad
//import com.acornui.observe.bind
//import com.acornui.replaceTokens
//
//class GeneratedDataExample(owner: Context) : VerticalLayoutContainer<UiComponent>(owner) {
//
//	init {
//		style.padding = Pad(8f)
//
//		val data = ActiveList<Int>()
//		val dG = +dataGrid(data) {
//			hScrollPolicy = ScrollPolicy.AUTO
//		} layout { fill(); horizontalAlign = HAlign.CENTER }
//
//		addElement(0, flow {
//			style.verticalAlign = FlowVAlign.TOP
////			+checkbox {
////				label = "HScrolling"
////				selected = true
////				click().add {
////					dG.hScrollPolicy = if (selected) ScrollPolicy.AUTO else ScrollPolicy.OFF
////				}
////			}
//			+checkbox {
//				label = "Resizable"
//				toggled = true
//				click().add {
//					dG.columnResizingEnabled = toggled
//				}
//			}
//			+checkbox {
//				label = "Sortable"
//				toggled = true
//				click().add {
//					dG.columnSortingEnabled = toggled
//				}
//			}
//			+checkbox {
//				label = "Reorderable"
//				toggled = true
//				click().add {
//					dG.columnReorderingEnabled = toggled
//				}
//			}
//
//			+checkbox {
//				label = "Fixed row height"
//				toggled = false
//				click().add {
//					dG.rowHeight = if (toggled) 30f else null
//				}
//			}
////			+button {
////				label = "Add Rows"
////				click().add {
////					for (i in 0..99) {
////						data.add(data.size)
////					}
////				}
////			}
//
//			+hRadioGroup<String> {
//				+radioButton("small", "Small")
//				+radioButton("medium", "Medium")
//				+radioButton("large", "Large")
//				+radioButton("bananas", "Bananas")
//				value = "small"
//
//				bind(changed) {
//					data.batchUpdate {
//						data.clear()
//						val rows = when (value) {
//							"small" -> 10
//							"medium" -> 1_000
//							"large" -> 100_000
//							"bananas" -> 1_000_000
//							else -> 0
//						}
//						for (i in 0 until rows) {
//							data.add(i)
//						}
//
//						val cols = when (value) {
//							"small" -> 5
//							"medium" -> 10
//							"large" -> 100
//							"bananas" -> 1_000
//							else -> 0
//						}
//						dG.columns.clear()
//						for (i in 0 until cols) {
//							dG.columns.add(
//									object : IntColumn<Int>() {
//										init {
//											widthPercent = 0.18f
//										}
//
//										override fun createHeaderCell(owner: Context): UiComponent {
//											return owner.text {
//												i18n { text = string("column").replaceTokens("$i") ?: "" }
//											}
//										}
//
//										override fun getCellData(row: Int): Int? = row + i
//										override fun setCellData(row: Int, value: Int?) {}
//									}
//							)
//						}
//					}
//				}
//			}
//
//		}) layout { widthPercent = 1f }
//
//
//	}
//}