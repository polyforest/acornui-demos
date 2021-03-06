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
import com.acornui.component.Div
import com.acornui.component.applyCss
import com.acornui.component.datagrid.DataGrid
import com.acornui.component.input.button
import com.acornui.component.layout.LayoutStyles
import com.acornui.component.style.cssClass
import com.acornui.component.style.launchWithIndicator
import com.acornui.css.css
import com.acornui.di.Context
import com.acornui.input.clicked
import com.acornui.logging.Log
import kotlinx.coroutines.delay
import kotlin.time.seconds


class CountriesExample(owner: Context) : Div(owner) {

	private val dataGrid: DataGrid<CountryData>

	init {
		addClass(styleTag)
		addClass(LayoutStyles.vGroup)
		applyCss(
			"""
			align-content: center;
			margin: 50px auto;
		"""
		)

		dataGrid = +CountryDataGrid(this).apply {
			applyCss("""
				width: 520px;
				min-height: 200px;				
			""")

			data = parseCountries(
				"""
China[b]	1403772560	//upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Flag_of_the_People%27s_Republic_of_China.svg/23px-Flag_of_the_People%27s_Republic_of_China.svg.png
India[c]	1365368088	//upload.wikimedia.org/wikipedia/en/thumb/4/41/Flag_of_India.svg/23px-Flag_of_India.svg.png
United States[d]	330047753	//upload.wikimedia.org/wikipedia/en/thumb/a/a4/Flag_of_the_United_States.svg/23px-Flag_of_the_United_States.svg.png
Indonesia	269603400	//upload.wikimedia.org/wikipedia/commons/thumb/9/9f/Flag_of_Indonesia.svg/23px-Flag_of_Indonesia.svg.png
Pakistan[e]	220892331	//upload.wikimedia.org/wikipedia/commons/thumb/3/32/Flag_of_Pakistan.svg/23px-Flag_of_Pakistan.svg.png
Brazil	211866273	//upload.wikimedia.org/wikipedia/en/thumb/0/05/Flag_of_Brazil.svg/22px-Flag_of_Brazil.svg.png
		"""
			)

//		dG.data += CountryData(0, "Fake", "North America", "Junk", 0, 0, 0f)

			rowSubmitted.listen {
				println("Updating row ${it.index} ${it.newData}")
				launchWithIndicator {
					delay(2.seconds)
					println("Row update complete ${it.index}")
				}
			}
		}

		+button("Load more data") {
			clicked.listen { _ ->
				dataGrid.style.height = css("calc(100vh - 150px)")
				disabled = true
				launchWithIndicator {
					// Data from: https://en.wikipedia.org/wiki/List_of_countries_and_dependencies_by_population
					val it = loadText("assets/countries.tsv")
					dataGrid.data = parseCountries(it)
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
		val styleTag by cssClass()

	}
}