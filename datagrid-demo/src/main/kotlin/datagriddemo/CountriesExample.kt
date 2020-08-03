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

import com.acornui.asset.loadText
import com.acornui.collection.sumByLong
import com.acornui.component.DivComponent
import com.acornui.component.applyCss
import com.acornui.component.datagrid.*
import com.acornui.component.input.button
import com.acornui.component.input.numberInput
import com.acornui.component.input.textInput
import com.acornui.component.layout.LayoutStyles
import com.acornui.component.style.StyleTag
import com.acornui.css.css
import com.acornui.di.Context
import com.acornui.dom.addCssToHead
import com.acornui.dom.img
import com.acornui.formatters.numberFormatter
import com.acornui.formatters.percentFormatter
import com.acornui.frame
import com.acornui.input.clicked
import com.acornui.logging.Log
import com.acornui.observe.bind
import com.acornui.observe.dataBinding
import com.acornui.signal.once
import kotlinx.coroutines.launch


class CountriesExample(owner: Context) : DivComponent(owner) {

	private val dG: DataGrid<CountryData>

	init {
		addClass(styleTag)
		addClass(LayoutStyles.vGroup)
		applyCss("""
			align-content: center;
			margin: 50px auto;
		""")

//		+hFlowGroup {
//			for (i in 0..20) {
//				+button("Test $i")
//			}
//		}

		dG = +dataGrid<CountryData> {
			val worldPop = dataBinding(0L)
			dataChanged.listen { e ->
				worldPop.value = e.newData.sumByLong { it.population }
			}

			applyCss(
				"""
width: 600px;
min-height: 100px;
grid-template-columns: 32px repeat(3, auto);

			"""
			)

			header {
				+headerCell()
				+headerCell("Country")
				+headerCell("Population 2020") {
					title = "Population 2020"
				}
				+headerCell("% World") {
					applyCss("""
						white-space: nowrap;
					""")
					title = "Percentage of world population"
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

			rowFactory = {
				row(it) {
					+cell {
						frame.once { _ ->
							+img(it.flag)
						}
					}
					+cell(it.name) {
						tabIndex = 0
					}
					+cell(nF.format(it.population)) {
						tabIndex = 0
					}
					+cell {
						tabIndex = 0
						bind(worldPop) { worldPop ->
							label = pF.format(it.population.toDouble() / worldPop)
						}
					}
				}
			}

			rowEditorFactory = {
				editorRow(it) {
					+cell {
						+img(it.flag)
					}
					+cell {
						+textInput {
							value = it.name
							required = true
						}
					}
					+cell {
						+numberInput {
							valueAsNumber = it.population.toDouble()
							step = 1.0
							max = 10e10
							min = 1.0
							required = true
						}
					}
					+cell {
						tabIndex = 0
						bind(worldPop) { worldPop ->
							label = pF.format(it.population.toDouble() / worldPop)
						}
					}
				}
			}
		}

		dG.data = parseCountries("""
China[b]	1403772560	//upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Flag_of_the_People%27s_Republic_of_China.svg/23px-Flag_of_the_People%27s_Republic_of_China.svg.png
India[c]	1365368088	//upload.wikimedia.org/wikipedia/en/thumb/4/41/Flag_of_India.svg/23px-Flag_of_India.svg.png
United States[d]	330047753	//upload.wikimedia.org/wikipedia/en/thumb/a/a4/Flag_of_the_United_States.svg/23px-Flag_of_the_United_States.svg.png
Indonesia	269603400	//upload.wikimedia.org/wikipedia/commons/thumb/9/9f/Flag_of_Indonesia.svg/23px-Flag_of_Indonesia.svg.png
Pakistan[e]	220892331	//upload.wikimedia.org/wikipedia/commons/thumb/3/32/Flag_of_Pakistan.svg/23px-Flag_of_Pakistan.svg.png
Brazil	211866273	//upload.wikimedia.org/wikipedia/en/thumb/0/05/Flag_of_Brazil.svg/22px-Flag_of_Brazil.svg.png
		""")

//		dG.data += CountryData(0, "Fake", "North America", "Junk", 0, 0, 0f)

		+button("Load more data") {
			clicked.listen { e ->
				dG.style.height = css("calc(100vh - 150px)")
				disabled = true
				launch {
					// Data from: https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population
					val it = loadText("assets/countries.tsv")
					dG.data = parseCountries(it)
					dispose()
				}
			}
		}
	}

	private fun parseCountries(it: String): List<CountryData> {
		val data = ArrayList<CountryData>()
		val countries = it.trim().split('\n')
		for (country in countries) {
			val countrySplit = country.split('\t')
			try {
				val newCountry = CountryData(
					countrySplit[0],
					countrySplit[1].toLong(),
					countrySplit[2]
				)
				data.add(newCountry)
			} catch (e: Throwable) {
				Log.warn("Bad row: $country")
			}
		}
		return data
	}

	companion object {
		val styleTag = StyleTag("CountriesExample")

		init {
			addCssToHead("""
${DataGrid.headerCellStyle} {
		
}
			""")
		}
	}
}

private data class CountryData(
	val name: String,
	val population: Long,
	val flag: String
)