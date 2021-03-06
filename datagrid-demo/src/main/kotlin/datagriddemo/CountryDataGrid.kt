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

import com.acornui.collection.sumByLong
import com.acornui.component.applyCss
import com.acornui.component.datagrid.DataGrid
import com.acornui.component.datagrid.cell
import com.acornui.component.datagrid.editorCell
import com.acornui.component.datagrid.headerCell
import com.acornui.component.input.NumberInput
import com.acornui.component.input.TextInput
import com.acornui.component.input.numberInput
import com.acornui.component.input.textInput
import com.acornui.component.smallSpinner
import com.acornui.component.style.LoadingStyles
import com.acornui.di.Context
import com.acornui.dom.img
import com.acornui.formatters.numberFormatter
import com.acornui.formatters.percentFormatter
import com.acornui.frame
import com.acornui.observe.bind
import com.acornui.observe.dataBinding
import com.acornui.observe.or
import com.acornui.signal.once

class CountryDataGrid(owner: Context) : DataGrid<CountryData>(owner) {

	init {
		val worldPop = dataBinding(0L)
		dataChanged.listen { e ->
			worldPop.value = e.newData.sumByLong { it.population }
		}

		applyCss(
			"""
grid-template-columns: 32px 1fr 0.8fr 0.5fr;

			"""
		)

		header {
			+headerCell {
				applyCss("pointer-events: none;")
				tabIndex = -1
				+smallSpinner {
					applyCss("margin-top: 0.1em;")
					addClass(LoadingStyles.showOnLoading)
				}
			}
			+headerCell("Country") {
				title = "Country"

				bindSortingBy { it.name }
			}
			+headerCell("Population 2020") {
				title = "Population 2020"

				bindSortingBy { it.population }
			}
			+headerCell("% World") {
				applyCss(
					"""
						white-space: nowrap;
					"""
				)
				title = "Percentage of world population"

				bindSortingBy { it.population }
			}
		}

		val nF = numberFormatter()
		val pF = percentFormatter()
		pF.maxFractionDigits = 2

		val grid = this
		footer {
			applyCss("""font-weight: 400;""")
			+cell("Total:") {
				applyCss(
					"grid-column: 1/3;"
				)
			}
			+cell {
				grid.dataChanged.listen {
					bind(worldPop) {
						label = nF.format(it)
					}
				}
			}
			+cell()
		}

		rows {
			+cell {
				+img {
					data { v ->
						src = ""
						frame.once {
							src = v.flag
						}
					}
				}
			}
			+cell {
				tabIndex = 0
				data {
					label = it.name
				}
			}
			+cell {
				tabIndex = 0
				data {
					label = nF.format(it.population)
				}
			}
			+cell {
				tabIndex = 0
				bind(worldPop or dataChanged) {
					val pop = data?.population?.toDouble() ?: 0.0
					label = pF.format(pop / worldPop.value)
				}
			}
		}

		rowEditor {
			+cell {
				tabIndex = -1
				+img {
					data {
						src = it.flag
					}
				}
			}

			val nameInput: TextInput
			+editorCell {
				nameInput = +textInput {
					data {
						value = it.name
					}
					required = true
					changed.listen {
						if (value == "America") {
							setCustomValidity("Only one america!")
						} else {
							setCustomValidity("")
						}
					}
				}
			}

			val populationInput: NumberInput
			+editorCell {
				populationInput = +numberInput {
					data {
						valueAsNumber = it.population.toDouble()
					}
					step = 1.0
					max = 10e10
					min = 1.0
					required = true
				}
			}

			+cell {
				tabIndex = -1
				bind(worldPop or dataChanged) {
					val pop = data?.population?.toDouble() ?: 0.0
					label = pF.format(pop / worldPop.value)
				}
			}

			dataBuilder = {
				it.copy(
					name = nameInput.value,
					population = populationInput.valueAsNumber.toLong()
				)
			}
		}
	}

}

data class CountryData(
	val name: String,
	val population: Long,
	val flag: String
)