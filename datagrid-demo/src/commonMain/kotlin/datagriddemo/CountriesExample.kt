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
import com.acornui.collection.ActiveList
import com.acornui.collection.ObservableList
import com.acornui.collection.addAll
import com.acornui.collection.and
import com.acornui.compareTo2
import com.acornui.component.UiComponent
import com.acornui.component.atlas
import com.acornui.component.checkbox
import com.acornui.component.datagrid.*
import com.acornui.component.layout.HAlign
import com.acornui.component.layout.algorithm.*
import com.acornui.component.layout.spacer
import com.acornui.component.scroll.ScrollPolicy
import com.acornui.component.style.addStyleRule
import com.acornui.component.style.filter
import com.acornui.component.text.TextField
import com.acornui.component.text.strong
import com.acornui.component.text.text
import com.acornui.component.vRadioGroup
import com.acornui.di.Context
import com.acornui.i18n.i18n
import com.acornui.i18n.i18nBundle
import com.acornui.i18n.labelI18n
import com.acornui.i18n.string
import com.acornui.input.interaction.click
import com.acornui.math.Pad
import com.acornui.observe.bind
import com.acornui.replaceTokens
import com.acornui.text.NumberFormatType
import kotlinx.coroutines.launch
import kotlin.collections.set

class CountriesExample(owner: Context) : VerticalLayoutContainer<UiComponent>(owner) {

	init {
		style.padding = Pad(8f)

		val data = ActiveList<CountryData>()

		launch {
			val it = loadText("assets/countries.tsv")
			val countries = it.split('\n')
			for (country in countries) {
				val countrySplit = country.split('\t')
				val newCountry = CountryData(
						countrySplit[0].toInt(),
						countrySplit[1],
						countrySplit[2],
						countrySplit[3],
						countrySplit[4].toInt(),
						countrySplit[5].toInt(),
						countrySplit[6].toFloat()
				)
				data.add(newCountry)
			}
		}

		val dG = +dataGrid(data) {
			editable = true
			val headerFlowStyle = FlowLayoutStyle()
			headerFlowStyle.multiline = true
			addStyleRule(headerFlowStyle, TextField.filter and DataGrid.HEADER_CELL.filter)

			hScrollPolicy = ScrollPolicy.AUTO

			columns.addAll(
					object : IntColumn<CountryData>() {
						init {
							flexible = true
							width = 60f
						}

						override fun createHeaderCell(owner: Context): UiComponent {
							return owner.text {
								i18n { text = string("popRank") }
							}
						}

						override fun getCellData(row: CountryData): Int = row.popRank

						override fun setCellData(row: CountryData, value: Int?) {
							row.popRank = value ?: 0
						}
					},
					object : DataGridColumn<CountryData, String>() {
						init {
							flexible = true
							sortable = true
							width = 300f
							minWidth = 100f
							editable = false
						}

						override fun createCell(owner: Context): DataGridCell<String> {
							return object : HorizontalLayoutContainer<UiComponent>(owner), DataGridCell<String> {
								val flag = +atlas {}
								val textField = +text() layout { widthPercent = 1f }

								override var value: String? = null
									set(value) {
										if (field == value) return
										field = value
										if (value != null) flag.region("assets/flags.json", value.replace(" ", "_"))
										else flag.clear()
										textField.text = value ?: ""
									}
							}
						}

						override fun createEditorCell(owner: Context): DataGridEditorCell<String> {
							throw UnsupportedOperationException("not implemented")
						}

						override fun createHeaderCell(owner: Context): UiComponent {
							return owner.text {
								i18n { text = string("countryOrArea") }
							}
						}

						override fun getCellData(row: CountryData): String = row.countryOrArea
						override fun setCellData(row: CountryData, value: String) {}

						override fun compareRows(row1: CountryData, row2: CountryData): Int {
							return getCellData(row1).compareTo2(getCellData(row2), ignoreCase = true)
						}
					},

					object : StringColumn<CountryData>() {
						init {
							flexible = true
							width = 117f
						}

						override fun createHeaderCell(owner: Context): UiComponent {
							return owner.text {
								labelI18n("continentalRegion")
							}
						}

						override fun getCellData(row: CountryData): String = row.continentalRegion
						override fun setCellData(row: CountryData, value: String) {
							row.continentalRegion = value
						}
					},

					object : StringColumn<CountryData>() {
						init {
							flexible = true
							width = 117f
						}

						override fun createHeaderCell(owner: Context): UiComponent {
							return owner.text {
								labelI18n("statisticalRegion")
							}
						}

						override fun getCellData(row: CountryData): String = row.statisticalRegion
						override fun setCellData(row: CountryData, value: String) {
							row.statisticalRegion = value
						}
					},

					object : IntColumn<CountryData>() {
						init {
							flexible = true
							width = 117f
						}

						override fun createHeaderCell(owner: Context): UiComponent {
							return owner.text {
								i18n { text = string("population").replaceTokens("2016") }
							}
						}

						override fun getCellData(row: CountryData): Int = row.population2016
						override fun setCellData(row: CountryData, value: Int?) {
							row.population2016 = value ?: 0
						}
					},

					object : IntColumn<CountryData>() {
						init {
							flexible = true
							width = 117f
						}

						override fun createHeaderCell(owner: Context): UiComponent {
							return owner.text {
								i18n { text = string("population").replaceTokens("2015") }
							}
						}

						override fun getCellData(row: CountryData): Int = row.population2015
						override fun setCellData(row: CountryData, value: Int?) {
							row.population2015 = value ?: 0
						}
					},

					object : FloatColumn<CountryData>() {
						init {
							formatter.type = NumberFormatType.PERCENT
							flexible = true
							width = 90f
						}

						override fun createHeaderCell(owner: Context): UiComponent {
							return owner.text {
								labelI18n("change")
							}
						}

						override fun getCellData(row: CountryData): Float = row.populationChange
						override fun setCellData(row: CountryData, value: Float?) {
							row.populationChange = value ?: 0f
						}
					}

			)
		} layout { fill(); horizontalAlign = HAlign.CENTER }

		addElement(0, flow {
			style.verticalAlign = FlowVAlign.TOP
			+checkbox {
				label = "HScrolling"
				toggled = true
				click().add {
					dG.hScrollPolicy = if (toggled) ScrollPolicy.AUTO else ScrollPolicy.OFF
				}
			}
			+checkbox {
				label = "Resizable"
				toggled = true
				click().add {
					dG.columnResizingEnabled = toggled
				}
			}
			+checkbox {
				label = "Sortable"
				toggled = true
				click().add {
					dG.columnSortingEnabled = toggled
				}
			}
			+checkbox {
				label = "Reorderable"
				toggled = true
				click().add {
					dG.columnReorderingEnabled = toggled
				}
			}
			+vRadioGroup<String> {

				+text("Group by:") { strong = true }
				changed.add {
					dG.groups.clear()
					val existing = HashMap<String, Boolean>()
					when (value) {
						"continental" -> {
							for (datum in dG.data) {
								val region = datum.continentalRegion
								if (!existing.containsKey(region)) {
									existing[region] = true
									val newGroup = object : DataGridGroup<CountryData>() {
										override fun createHeader(owner: Context, list: ObservableList<CountryData>): DataGridGroupHeader {
											return owner.dataGridGroupHeader(this, list, region) {
												+spacer() layout { widthPercent = 1f }
												+text {
													bind(list) {
														text = "Total: " + list.size
													}
												}
											}
										}
									}
									newGroup.filter = {
										it.continentalRegion == region
									}
									dG.groups.add(newGroup)
								}
							}
						}
						"statistical" -> {
							for (datum in dG.data) {
								val region = datum.statisticalRegion
								if (!existing.containsKey(region)) {
									existing[region] = true
									val newGroup = object : DataGridGroup<CountryData>() {
										override fun createHeader(owner: Context, list: ObservableList<CountryData>): DataGridGroupHeader {
											return owner.dataGridGroupHeader(this, list, region)
										}
									}
									newGroup.filter = {
										it.statisticalRegion == region
									}
									dG.groups.add(newGroup)
								}
							}
						}

					}

				}
				+radioButton("nothing", "Nothing")
				+radioButton("continental", "Continental Regions")
				+radioButton("statistical", "Statistical Regions")
				value = "nothing"
			}
		}) layout { widthPercent = 1f }
	}
}

private data class CountryData(
		var popRank: Int,
		var countryOrArea: String,
		var continentalRegion: String,
		var statisticalRegion: String,
		var population2016: Int,
		var population2015: Int,
		var populationChange: Float
)