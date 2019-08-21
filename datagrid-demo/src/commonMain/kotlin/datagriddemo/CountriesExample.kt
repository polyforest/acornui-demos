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
import com.acornui.async.globalLaunch
import com.acornui.collection.ActiveList
import com.acornui.collection.ObservableList
import com.acornui.collection.addAll
import com.acornui.component.UiComponent
import com.acornui.component.atlas
import com.acornui.component.checkbox
import com.acornui.component.datagrid.*
import com.acornui.component.layout.HAlign
import com.acornui.component.layout.algorithm.*
import com.acornui.component.layout.spacer
import com.acornui.component.radioGroup
import com.acornui.component.scroll.ScrollPolicy
import com.acornui.component.style.addStyleRule
import com.acornui.component.style.and
import com.acornui.component.text.TextField
import com.acornui.component.text.strong
import com.acornui.component.text.text
import com.acornui.compareTo2
import com.acornui.di.Owned
import com.acornui.di.own
import com.acornui.i18n.i18n
import com.acornui.input.interaction.click
import com.acornui.replaceTokens
import com.acornui.text.NumberFormatType
import com.acornui.math.Pad
import com.acornui.signal.bind
import kotlin.collections.HashMap
import kotlin.collections.set

class CountriesExample(owned: Owned) : VerticalLayoutContainer(owned) {

	private val bundle = own(i18n("datagrid"))

	init {
		style.padding = Pad(8f)

		val data = ActiveList<CountryData>()

		globalLaunch {
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
//			rowHeight = 80f
			editable = true
			val headerFlowStyle = FlowLayoutStyle()
			headerFlowStyle.multiline = true
			addStyleRule(headerFlowStyle, TextField and DataGrid.HEADER_CELL)

//			val bodyFlowStyle = bind(FlowStyle())
//			bodyFlowStyle.multiline = false
//			setStyle(bodyFlowStyle, DataGrid.BODY_CELL, TextField)

			hScrollPolicy = ScrollPolicy.AUTO

			columns.addAll(
					object : IntColumn<CountryData>(injector) {
						init {
							flexible = true
							width = 60f
						}

						override fun createHeaderCell(owner: Owned): UiComponent {
							return owner.text {
								bundle.bind { text = it["popRank"] ?: "" }
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

						override fun createCell(owner: Owned): DataGridCell<String?> {
							return object : HorizontalLayoutContainer(owner), DataGridCell<String?> {
								val flag = +atlas {}
								val textField = +text() layout { widthPercent = 1f }
								private var _data: String? = null

								override fun setData(value: String?) {
									if (_data == value) return
									_data = value
									if (value != null) flag.setRegion("assets/flags.json", value.replace(" ", "_"))
									else flag.clear()
									textField.text = value ?: ""
								}
							}
						}

						override fun createEditorCell(owner: Owned): DataGridEditorCell<String> {
							throw UnsupportedOperationException("not implemented")
						}

						override fun createHeaderCell(owner: Owned): UiComponent {
							return owner.text {
								bundle.bind { text = it["countryOrArea"] ?: "" }
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

						override fun createHeaderCell(owner: Owned): UiComponent {
							return owner.text {
								bundle.bind { text = it["continentalRegion"] ?: "" }
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

						override fun createHeaderCell(owner: Owned): UiComponent {
							return owner.text {
								bundle.bind { text = it["statisticalRegion"] ?: "" }
							}
						}

						override fun getCellData(row: CountryData): String = row.statisticalRegion
						override fun setCellData(row: CountryData, value: String) {
							row.statisticalRegion = value
						}
					},

					object : IntColumn<CountryData>(injector) {
						init {
							flexible = true
							width = 117f
						}

						override fun createHeaderCell(owner: Owned): UiComponent {
							return owner.text {
								bundle.bind { text = it["population"]?.replaceTokens("2016") ?: "" }
							}
						}

						override fun getCellData(row: CountryData): Int = row.population2016
						override fun setCellData(row: CountryData, value: Int?) {
							row.population2016 = value ?: 0
						}
					},

					object : IntColumn<CountryData>(injector) {
						init {
							flexible = true
							width = 117f
						}

						override fun createHeaderCell(owner: Owned): UiComponent {
							return owner.text {
								bundle.bind { text = it["population"]?.replaceTokens("2015") ?: "" }
							}
						}

						override fun getCellData(row: CountryData): Int = row.population2015
						override fun setCellData(row: CountryData, value: Int?) {
							row.population2015 = value ?: 0
						}
					},

					object : FloatColumn<CountryData>(injector) {
						init {
							formatter.type = NumberFormatType.PERCENT
							flexible = true
							width = 90f
						}

						override fun createHeaderCell(owner: Owned): UiComponent {
							return owner.text {
								bundle.bind { text = it["change"] ?: "" }
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
			+vGroup {
				+text("Group by:") { strong = true }
				radioGroup<String> {
					changed.add {
						dG.groups.clear()
						val existing = HashMap<String, Boolean>()
						when (selectedData) {
							"continental" -> {
								for (datum in dG.data) {
									val region = datum.continentalRegion
									if (!existing.containsKey(region)) {
										existing[region] = true
										val newGroup = object : DataGridGroup<CountryData>() {
											override fun createHeader(owner: Owned, list: ObservableList<CountryData>): DataGridGroupHeader {
												return owner.dataGridGroupHeader(this, list, region) {
													+spacer() layout { widthPercent = 1f }
													+text {
														own(list.bind {
															text = "Total: " + list.size
														})
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
											override fun createHeader(owner: Owned, list: ObservableList<CountryData>): DataGridGroupHeader {
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
					selectedData = "nothing"
				}
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