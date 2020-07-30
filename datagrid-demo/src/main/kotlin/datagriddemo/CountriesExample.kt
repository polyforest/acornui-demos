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

import com.acornui.asset.loadText
import com.acornui.collection.sumByLong
import com.acornui.component.DivComponent
import com.acornui.component.applyCss
import com.acornui.component.datagrid.DataGrid
import com.acornui.component.datagrid.cell
import com.acornui.component.datagrid.dataGrid
import com.acornui.component.datagrid.headerCell
import com.acornui.component.input.button
import com.acornui.component.layout.LayoutStyles
import com.acornui.component.style.StyleTag
import com.acornui.css.css
import com.acornui.di.Context
import com.acornui.dom.addCssToHead
import com.acornui.formatters.numberFormatter
import com.acornui.formatters.percentFormatter
import com.acornui.input.clicked
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

			applyCss(
				"""
width: 500px;
resize: both;
grid-template-columns: [continent] 1fr [country] 1fr [pop-2015] 1fr [pop-2016] 1fr [pop-change] 1fr;
			"""
			)

			header {
				+headerCell("Continent")
				+headerCell("Country")
				+headerCell("Population 2015")
				+headerCell("Population 2016")
				+headerCell("Population Change")
			}

			val nF = numberFormatter()
			val pF = percentFormatter()
			pF.maxFractionDigits = 1

			val grid = this
			footer {
				applyCss("""font-weight: 400;""")
				+cell("Total:") {
				}
				+cell {
					grid.dataChanged.listen {
						label = nF.format(grid.data.sumByLong { it.population2015 })
					}
					applyCss("""
						grid-column-start: pop-2015;
					""")
				}
				+cell {
					grid.dataChanged.listen {
						label = nF.format(grid.data.sumByLong { it.population2016 })
					}
					applyCss("""
						grid-column-start: pop-2016;
					""")
				}

				+cell {
					grid.dataChanged.listen {
						val p1 = grid.data.sumByLong { it.population2015 }
						val p2 = grid.data.sumByLong { it.population2016 }
						label = pF.format(p2.toDouble() / p1.toDouble() - 1.0)
					}
					applyCss("""
						grid-column-start: pop-change;
					""")
				}
			}

			rows {
				+cell(it.continentalRegion)
				+cell(it.countryOrArea)
				+cell(nF.format(it.population2015))
				+cell(nF.format(it.population2016))
				+cell(pF.format(it.populationChange))
			}


		}

		dG.data = parseCountries("""
1	China	Asia	Eastern Asia	1382323332	1376048943	0.01
2	India	Asia	Southern Asia	1326801576	1311050527	0.01
3	United States	Americas	Northern America	324118787	321773631	0.01
4	Indonesia	Asia	South-Eastern Asia	260581100	257563815	0.01
5	Brazil	Americas	South America	209567920	207847528	0.01
6	Pakistan	Asia	Southern Asia	192826502	188924874	0.02
		""")

//		dG.data += CountryData(0, "Fake", "North America", "Junk", 0, 0, 0f)

		+button("Load more data") {
			clicked.listen { e ->
				dG.style.height = css("calc(100vh - 150px)")
				disabled = true
				launch {
					val it = loadText("assets/countries.tsv")
					dG.data = parseCountries(it)
					dispose()
				}
			}
		}

//		launch {
//			val it = loadText("assets/countries.tsv")
//			setData(it)
//		}


//		val dG = +dataGrid(data) {
//			editable = true
//			val headerFlowStyle = FlowLayoutStyle()
//			headerFlowStyle.multiline = true
//			addStyleRule(headerFlowStyle, TextField.filter and DataGrid.HEADER_CELL.filter)
//
//			hScrollPolicy = ScrollPolicy.AUTO
//
//			columns.addAll(
//					object : IntColumn<CountryData>() {
//						init {
//							flexible = true
//							width = 60f
//						}
//
//						override fun createHeaderCell(owner: Context): UiComponent {
//							return owner.text {
//								i18n { text = string("popRank") }
//							}
//						}
//
//						override fun getCellData(row: CountryData): Int = row.popRank
//
//						override fun setCellData(row: CountryData, value: Int?) {
//							row.popRank = value ?: 0
//						}
//					},
//					object : DataGridColumn<CountryData, String>() {
//						init {
//							flexible = true
//							sortable = true
//							width = 300f
//							minWidth = 100f
//							editable = false
//						}
//
//						override fun createCell(owner: Context): DataGridCell<String> {
//							return object : HorizontalLayoutContainer<UiComponent>(owner), DataGridCell<String> {
//								val flag = +atlas {}
//								val textField = +text() layout { widthPercent = 1f }
//
//								override var value: String? = null
//									set(value) {
//										if (field == value) return
//										field = value
//										if (value != null) flag.region("assets/flags.json", value.replace(" ", "_"))
//										else flag.clear()
//										textField.text = value ?: ""
//									}
//							}
//						}
//
//						override fun createEditorCell(owner: Context): DataGridEditorCell<String> {
//							throw UnsupportedOperationException("not implemented")
//						}
//
//						override fun createHeaderCell(owner: Context): UiComponent {
//							return owner.text {
//								i18n { text = string("countryOrArea") }
//							}
//						}
//
//						override fun getCellData(row: CountryData): String = row.countryOrArea
//						override fun setCellData(row: CountryData, value: String) {}
//
//						override fun compareRows(row1: CountryData, row2: CountryData): Int {
//							return getCellData(row1).compareTo2(getCellData(row2), ignoreCase = true)
//						}
//					},
//
//					object : StringColumn<CountryData>() {
//						init {
//							flexible = true
//							width = 117f
//						}
//
//						override fun createHeaderCell(owner: Context): UiComponent {
//							return owner.text {
//								labelI18n("continentalRegion")
//							}
//						}
//
//						override fun getCellData(row: CountryData): String = row.continentalRegion
//						override fun setCellData(row: CountryData, value: String) {
//							row.continentalRegion = value
//						}
//					},
//
//					object : StringColumn<CountryData>() {
//						init {
//							flexible = true
//							width = 117f
//						}
//
//						override fun createHeaderCell(owner: Context): UiComponent {
//							return owner.text {
//								labelI18n("statisticalRegion")
//							}
//						}
//
//						override fun getCellData(row: CountryData): String = row.statisticalRegion
//						override fun setCellData(row: CountryData, value: String) {
//							row.statisticalRegion = value
//						}
//					},
//
//					object : IntColumn<CountryData>() {
//						init {
//							flexible = true
//							width = 117f
//						}
//
//						override fun createHeaderCell(owner: Context): UiComponent {
//							return owner.text {
//								i18n { text = string("population").replaceTokens("2016") }
//							}
//						}
//
//						override fun getCellData(row: CountryData): Int = row.population2016
//						override fun setCellData(row: CountryData, value: Int?) {
//							row.population2016 = value ?: 0
//						}
//					},
//
//					object : IntColumn<CountryData>() {
//						init {
//							flexible = true
//							width = 117f
//						}
//
//						override fun createHeaderCell(owner: Context): UiComponent {
//							return owner.text {
//								i18n { text = string("population").replaceTokens("2015") }
//							}
//						}
//
//						override fun getCellData(row: CountryData): Int = row.population2015
//						override fun setCellData(row: CountryData, value: Int?) {
//							row.population2015 = value ?: 0
//						}
//					},
//
//					object : FloatColumn<CountryData>() {
//						init {
//							formatter.type = NumberFormatType.PERCENT
//							flexible = true
//							width = 90f
//						}
//
//						override fun createHeaderCell(owner: Context): UiComponent {
//							return owner.text {
//								labelI18n("change")
//							}
//						}
//
//						override fun getCellData(row: CountryData): Float = row.populationChange
//						override fun setCellData(row: CountryData, value: Float?) {
//							row.populationChange = value ?: 0f
//						}
//					}
//
//			)
//		} layout { fill(); horizontalAlign = HAlign.CENTER }
//
//		addElement(0, flow {
//			style.verticalAlign = FlowVAlign.TOP
//			+checkbox {
//				label = "HScrolling"
//				toggled = true
//				click().add {
//					dG.hScrollPolicy = if (toggled) ScrollPolicy.AUTO else ScrollPolicy.OFF
//				}
//			}
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
//			+vRadioGroup<String> {
//
//				+text("Group by:") { strong = true }
//				changed.add {
//					dG.groups.clear()
//					val existing = HashMap<String, Boolean>()
//					when (value) {
//						"continental" -> {
//							for (datum in dG.data) {
//								val region = datum.continentalRegion
//								if (!existing.containsKey(region)) {
//									existing[region] = true
//									val newGroup = object : DataGridGroup<CountryData>() {
//										override fun createHeader(owner: Context, list: ObservableList<CountryData>): DataGridGroupHeader {
//											return owner.dataGridGroupHeader(this, list, region) {
//												+spacer() layout { widthPercent = 1f }
//												+text {
//													bind(list) {
//														text = "Total: " + list.size
//													}
//												}
//											}
//										}
//									}
//									newGroup.filter = {
//										it.continentalRegion == region
//									}
//									dG.groups.add(newGroup)
//								}
//							}
//						}
//						"statistical" -> {
//							for (datum in dG.data) {
//								val region = datum.statisticalRegion
//								if (!existing.containsKey(region)) {
//									existing[region] = true
//									val newGroup = object : DataGridGroup<CountryData>() {
//										override fun createHeader(owner: Context, list: ObservableList<CountryData>): DataGridGroupHeader {
//											return owner.dataGridGroupHeader(this, list, region)
//										}
//									}
//									newGroup.filter = {
//										it.statisticalRegion == region
//									}
//									dG.groups.add(newGroup)
//								}
//							}
//						}
//
//					}
//
//				}
//				+radioButton("nothing", "Nothing")
//				+radioButton("continental", "Continental Regions")
//				+radioButton("statistical", "Statistical Regions")
//				value = "nothing"
//			}
//		}) layout { widthPercent = 1f }
	}

	private fun parseCountries(it: String): List<CountryData> {
		val data = ArrayList<CountryData>()
		val countries = it.trim().split('\n')
		for (country in countries) {
			val countrySplit = country.split('\t')
			val newCountry = CountryData(
				countrySplit[0].toInt(),
				countrySplit[1],
				countrySplit[2],
				countrySplit[3],
				countrySplit[4].toLong(),
				countrySplit[5].toLong(),
				countrySplit[6].toFloat()
			)
			data.add(newCountry)
		}
		return data
	}

	companion object {
		val styleTag = StyleTag("CountriesExample")

		init {
			addCssToHead("""
${DataGrid.headerCellStyle} {
	min-width: 50px;	
}
			""")
		}
	}
}

private data class CountryData(
	var popRank: Int,
	var countryOrArea: String,
	var continentalRegion: String,
	var statisticalRegion: String,
	var population2016: Long,
	var population2015: Long,
	var populationChange: Float
)