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

package com.acornui.performancetest

import com.acornui.collection.addAll
import com.acornui.component.UiComponent
import com.acornui.component.datagrid.*
import com.acornui.component.layout.algorithm.FlowLayoutStyle
import com.acornui.component.scroll.ScrollPolicy
import com.acornui.component.style.addStyleRule
import com.acornui.component.style.and
import com.acornui.component.text.TextField
import com.acornui.component.text.text
import com.acornui.di.Owned
import com.acornui.text.NumberFormatType

fun Owned.dataGridComponent(): DataGrid<*> {
	return dataGrid(countryData) {
		defaultWidth = 400f
		defaultHeight = 300f
		val headerFlowStyle = FlowLayoutStyle()
		headerFlowStyle.multiline = true
		addStyleRule(headerFlowStyle, TextField and DataGrid.HEADER_CELL)
		hScrollPolicy = ScrollPolicy.AUTO
		columns.addAll(
				object : IntColumn<CountryData>(injector) {
					init {
						flexible = true
						width = 60f
					}

					override fun createHeaderCell(owner: Owned): UiComponent {
						return owner.text {
							text = "Pea horseradish"
						}
					}

					override fun getCellData(row: CountryData): Int = row.popRank

					override fun setCellData(row: CountryData, value: Int?) {
						row.popRank = value ?: 0
					}
				},

				object : StringColumn<CountryData>() {
					init {
						flexible = true
						width = 117f
					}

					override fun createHeaderCell(owner: Owned): UiComponent {
						return owner.text {
							text = "Turnip greens"
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
							text = "Gumbo beet"
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
							text = "Soko radicchio"
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
							text = "Dandelion zucchini"
						}
					}

					override fun getCellData(row: CountryData): Float = row.populationChange
					override fun setCellData(row: CountryData, value: Float?) {
						row.populationChange = value ?: 0f
					}
				}

		)
	}
}

private val countryData: List<CountryData> = run {
	val countryTxt = """
1	China	Asia	Eastern Asia	1382323332	1376048943	0.01
2	India	Asia	Southern Asia	1326801576	1311050527	0.01
3	United States	Americas	Northern America	324118787	321773631	0.01
4	Indonesia	Asia	South-Eastern Asia	260581100	257563815	0.01
5	Brazil	Americas	South America	209567920	207847528	0.01
6	Pakistan	Asia	Southern Asia	192826502	188924874	0.02
7	Nigeria	Africa	Western Africa	186987563	182201962	0.03
8	Bangladesh	Asia	Southern Asia	162910864	160995642	0.01
9	Russia	Europe	Eastern Europe	143439832	143456918	0.00
10	Mexico	Americas	Central America	128632004	127017224	0.01
11	Japan	Asia	Eastern Asia	126323715	126573481	0.00
12	Philippines	Asia	South-Eastern Asia	102250133	100699395	0.02
13	Ethiopia	Africa	Eastern Africa	101853268	99390750	0.03
14	Vietnam	Asia	South-Eastern Asia	94444200	93447601	0.01
15	Egypt	Africa	Northern Africa	93383574	91508084	0.02
16	Germany	Europe	Western Europe	80682351	80688545	0.00
17	Iran	Asia	Southern Asia	80043146	79109272	0.01
18	Democratic Republic of the Congo	Africa	Middle Africa	79722624	77266814	0.03
19	Turkey	Asia	Western Asia	79622062	78665830	0.01
20	Thailand	Asia	South-Eastern Asia	68146609	67959359	0.00
21	United Kingdom	Europe	Northern Europe	65111143	64715810	0.01
22	France	Europe	Western Europe	64668129	64395345	0.00
23	Italy	Europe	Southern Europe	59801004	59797685	0.00
24	Tanzania	Africa	Eastern Africa	55155473	53470420	0.03
25	South Africa	Africa	Southern Africa	54978907	54490406	0.01
26	Myanmar	Asia	South-Eastern Asia	54363426	53897154	0.01
27	South Korea	Asia	Eastern Asia	50503933	50293439	0.00
28	Colombia	Americas	South America	48654392	48228704	0.01
29	Kenya	Africa	Eastern Africa	47251449	46050302	0.03
30	Spain	Europe	Southern Europe	46064604	46121699	0.00
31	Ukraine	Europe	Eastern Europe	44624373	44823765	0.00
32	Argentina	Americas	South America	43847277	43416755	0.01
33	Sudan	Africa	Northern Africa	41175541	40234882	0.02
34	Algeria	Africa	Northern Africa	40375954	39666519	0.02
35	Uganda	Africa	Eastern Africa	40322768	39032383	0.03
36	Poland	Europe	Eastern Europe	38593161	38611794	0.00
37	Iraq	Asia	Western Asia	37547686	36423395	0.03
38	Canada	Americas	Northern America	36286378	35939927	0.01
39	Morocco	Africa	Northern Africa	34817065	34377511	0.01
40	Afghanistan	Asia	Southern Asia	33369945	32526562	0.03
41	Saudi Arabia	Asia	Western Asia	32157974	31540372	0.02
42	Peru	Americas	South America	31774225	31376670	0.01
43	Venezuela	Americas	South America	31518855	31108083	0.01
44	Malaysia	Asia	South-Eastern Asia	30751602	30331007	0.01
45	Uzbekistan	Asia	Central Asia	30300446	29893488	0.01
46	Nepal	Asia	Southern Asia	28850717	28513700	0.01
47	Mozambique	Africa	Eastern Africa	28751362	27977863	0.03
48	Ghana	Africa	Western Africa	28033375	27409893	0.02
49	Yemen	Asia	Western Asia	27477600	26832215	0.02
50	Angola	Africa	Middle Africa	25830958	25021974	0.03
51	North Korea	Asia	Eastern Asia	25281327	25155317	0.01
52	Madagascar	Africa	Eastern Africa	24915822	24235390	0.03
53	Australia	Oceania	Australia and New Zealand	24309330	23968973	0.01
54	Cameroon	Africa	Middle Africa	23924407	23344179	0.03
55	Taiwan	Asia	Eastern Asia	23395600	23381038	0.00
56	Côte d'Ivoire	Africa	Western Africa	23254184	22701556	0.02
57	Sri Lanka	Asia	Southern Asia	20810816	20715010	0.01
58	Niger	Africa	Western Africa	20715285	19899120	0.04
59	Romania	Europe	Eastern Europe	19372734	19511324	-0.01
60	Burkina Faso	Africa	Western Africa	18633725	18105570	0.03
61	Syria	Asia	Western Asia	18563595	18502413	0.00
62	Mali	Africa	Western Africa	18134835	17599694	0.03
63	Chile	Americas	South America	18131850	17948141	0.01
64	Kazakhstan	Asia	Central Asia	17855384	17625226	0.01
65	Malawi	Africa	Eastern Africa	17749826	17215232	0.03
66	Netherlands	Europe	Western Europe	16979729	16924929	0.00
67	Zambia	Africa	Eastern Africa	16717332	16211767	0.03
68	Guatemala	Americas	Central America	16672956	16342897	0.02
69	Ecuador	Americas	South America	16385450	16144363	0.02
70	Zimbabwe	Africa	Eastern Africa	15966810	15602751	0.02
71	Cambodia	Asia	South-Eastern Asia	15827241	15577899	0.02
72	Senegal	Africa	Western Africa	15589485	15129273	0.03
73	Chad	Africa	Middle Africa	14496739	14037472	0.03
74	Guinea	Africa	Western Africa	12947122	12608590	0.03
75	South Sudan	Africa	Eastern Africa	12733427	12339812	0.03
76	Rwanda	Africa	Eastern Africa	11882766	11609666	0.02
77	Burundi	Africa	Eastern Africa	11552561	11178921	0.03
78	Cuba	Americas	Caribbean	11392889	11389562	0.00
79	Tunisia	Africa	Northern Africa	11375220	11253554	0.01
80	Belgium	Europe	Western Europe	11371928	11299192	0.01
81	Benin	Africa	Western Africa	11166658	10879829	0.03
82	Somalia	Africa	Eastern Africa	11079013	10787104	0.03
83	Greece	Europe	Southern Europe	10919459	10954617	0.00
84	Bolivia	Americas	South America	10888402	10724705	0.02
85	Haiti	Americas	Caribbean	10848175	10711067	0.01
86	Dominican Republic	Americas	Caribbean	10648613	10528391	0.01
87	Czech Republic	Europe	Eastern Europe	10548058	10543186	0.00
88	Portugal	Europe	Southern Europe	10304434	10349803	0.00
89	Azerbaijan	Asia	Western Asia	9868447	9753968	0.01
90	Sweden	Europe	Northern Europe	9851852	9779426	0.01
91	Hungary	Europe	Eastern Europe	9821318	9855023	0.00
92	Belarus	Europe	Eastern Europe	9481521	9495826	0.00
93	United Arab Emirates	Asia	Western Asia	9266971	9156963	0.01
94	Serbia	Europe	Southern Europe	8812705	8850975	0.00
95	Tajikistan	Asia	Central Asia	8669464	8481855	0.02
96	Austria	Europe	Western Europe	8569633	8544586	0.00
97	Switzerland	Europe	Western Europe	8379477	8298663	0.01
98	Israel	Asia	Western Asia	8192463	8064036	0.02
99	Honduras	Americas	Central America	8189501	8075060	0.01
100	Papua New Guinea	Oceania	Melanesia	7776115	7619321	0.02
101	Jordan	Asia	Western Asia	7747800	7594547	0.02
102	Togo	Africa	Western Africa	7496833	7304578	0.03
103	Hong Kong	Asia	Eastern Asia	7346248	7287983	0.01
104	Bulgaria	Europe	Eastern Europe	7097796	7149787	-0.01
105	Laos	Asia	South-Eastern Asia	6918367	6802023	0.02
106	Paraguay	Americas	South America	6725430	6639123	0.01
107	Sierra Leone	Africa	Western Africa	6592102	6453184	0.02
108	Libya	Africa	Northern Africa	6330159	6278438	0.01
109	Nicaragua	Americas	Central America	6150035	6082032	0.01
110	El Salvador	Americas	Central America	6146419	6126583	0.00
111	Kyrgyzstan	Asia	Central Asia	6033769	5939962	0.02
112	Lebanon	Asia	Western Asia	5988153	5850743	0.02
113	Singapore	Asia	South-Eastern Asia	5696506	5603740	0.02
114	Denmark	Europe	Northern Europe	5690750	5669081	0.00
115	Finland	Europe	Northern Europe	5523904	5503457	0.00
116	Turkmenistan	Asia	Central Asia	5438670	5373502	0.01
117	Slovakia	Europe	Eastern Europe	5429418	5426258	0.00
118	Eritrea	Africa	Eastern Africa	5351680	5227791	0.02
119	Norway	Europe	Northern Europe	5271958	5210967	0.01
120	Central African Republic	Africa	Middle Africa	4998493	4900274	0.02
121	Costa Rica	Americas	Central America	4857218	4807850	0.01
122	Palestine	Asia	Western Asia	4797239	4668466	0.03
123	Congo	Africa	Middle Africa	4740992	4620330	0.03
124	Ireland	Europe	Northern Europe	4713993	4688465	0.01
125	Oman	Asia	Western Asia	4654471	4490541	0.04
126	Liberia	Africa	Western Africa	4615222	4503438	0.03
127	New Zealand	Oceania	Australia and New Zealand	4565185	4528526	0.01
128	Croatia	Europe	Southern Europe	4225001	4240317	0.00
129	Mauritania	Africa	Western Africa	4166463	4067564	0.02
130	Moldova	Europe	Eastern Europe	4062862	4068897	0.00
131	Kuwait	Asia	Western Asia	4007146	3892115	0.03
132	Panama	Americas	Central America	3990406	3929141	0.02
133	Georgia	Asia	Western Asia	3979781	3999812	-0.01
134	Bosnia and Herzegovina	Europe	Southern Europe	3802134	3810416	0.00
135	Puerto Rico	Americas	Caribbean	3680772	3683238	0.00
136	Uruguay	Americas	South America	3444071	3431555	0.00
137	Armenia	Asia	Western Asia	3026048	3017712	0.00
138	Mongolia	Asia	Eastern Asia	3006444	2959134	0.02
139	Albania	Europe	Southern Europe	2903700	2896679	0.00
140	Lithuania	Europe	Northern Europe	2850030	2878405	-0.01
141	Jamaica	Americas	Caribbean	2803362	2793335	0.00
142	Namibia	Africa	Southern Africa	2513981	2458830	0.02
143	Botswana	Africa	Southern Africa	2303820	2262485	0.02
144	Qatar	Asia	Western Asia	2291368	2235355	0.03
145	Lesotho	Africa	Southern Africa	2160309	2135022	0.01
146	Republic of Macedonia	Europe	Southern Europe	2081012	2078453	0.00
147	Slovenia	Europe	Southern Europe	2069362	2067526	0.00
148	Gambia	Africa	Western Africa	2054986	1990924	0.03
149	Latvia	Europe	Northern Europe	1955742	1970503	-0.01
150	Guinea-Bissau	Africa	Western Africa	1888429	1844325	0.02
151	Gabon	Africa	Middle Africa	1763142	1725292	0.02
152	Bahrain	Asia	Western Asia	1396829	1377237	0.01
153	Trinidad and Tobago	Americas	Caribbean	1364973	1360088	0.00
154	Estonia	Europe	Northern Europe	1309104	1312558	0.00
155	Swaziland	Africa	Southern Africa	1304063	1286970	0.01
156	Mauritius	Africa	Eastern Africa	1277459	1273212	0.00
157	Timor-Leste	Asia	South-Eastern Asia	1211245	1184765	0.02
158	Cyprus	Europe	Southern Europe	1176598	1165300	0.01
159	Djibouti	Africa	Eastern Africa	899598	887861	0.01
160	Fiji	Oceania	Melanesia	897537	892145	0.01
161	Equatorial Guinea	Africa	Middle Africa	869587	845060	0.03
162	Réunion	Africa	Eastern Africa	867214	861154	0.01
163	Comoros	Africa	Eastern Africa	807118	788474	0.02
164	Bhutan	Asia	Southern Asia	784103	774830	0.01
165	Guyana	Americas	South America	770610	767085	0.01
166	Montenegro	Europe	Southern Europe	626101	625781	0.00
167	Macau	Asia	Eastern Asia	597126	587606	0.02
168	Solomon Islands	Oceania	Melanesia	594934	583591	0.02
169	Western Sahara	Africa	Northern Africa	584206	572540	0.02
170	Luxembourg	Europe	Western Europe	576243	567110	0.02
171	Suriname	Americas	South America	547610	542975	0.01
172	Cabo Verde	Africa	Western Africa	526993	520502	0.01
173	Guadeloupe	Americas	Caribbean	470547	468450	0.00
174	Brunei	Asia	South-Eastern Asia	428874	423188	0.01
175	Malta	Europe	Southern Europe	419615	418670	0.00
176	Martinique	Americas	Caribbean	396364	396425	0.00
177	Bahamas	Americas	Caribbean	392718	388019	0.01
178	Maldives	Asia	Southern Asia	369812	363657	0.02
179	Belize	Americas	Central America	366942	359287	0.02
180	Iceland	Europe	Northern Europe	331778	329425	0.01
181	French Polynesia	Oceania	Polynesia	285735	282764	0.01
182	Barbados	Americas	Caribbean	285006	284215	0.00
183	French Guiana	Americas	South America	275688	268606	0.03
184	Vanuatu	Oceania	Melanesia	270470	264652	0.02
185	New Caledonia	Oceania	Melanesia	266431	263118	0.01
186	Mayotte	Africa	Eastern Africa	246496	240015	0.03
187	Samoa	Oceania	Polynesia	194523	193228	0.01
188	Sao Tome and Principe	Africa	Middle Africa	194390	190344	0.02
189	Saint Lucia	Americas	Caribbean	186383	184999	0.01
190	Guam	Oceania	Micronesia	172094	169885	0.01
191	Guernsey and  Jersey	Europe	Northern Europe	164466	163692	0.01
192	Curaçao	Americas	Caribbean	158635	157203	0.01
193	Kiribati	Oceania	Micronesia	114405	112423	0.02
194	Saint Vincent and the Grenadines	Americas	Caribbean	109644	109462	0.00
195	Grenada	Americas	Caribbean	107327	106825	0.01
196	Tonga	Oceania	Polynesia	106915	106170	0.01
197	United States Virgin Islands	Americas	Caribbean	106415	106291	0.00
198	Federated States of Micronesia	Oceania	Micronesia	104966	104460	0.01
199	Aruba	Americas	Caribbean	104263	103889	0.00
200	Seychelles	Africa	Eastern Africa	97026	96471	0.01
201	Antigua and Barbuda	Americas	Caribbean	92738	91818	0.01
202	Isle of Man	Europe	Northern Europe	88421	87780	0.01
203	Dominica	Americas	Caribbean	73016	72680	0.01
204	Andorra	Europe	Southern Europe	69165	70473	-0.02
205	Bermuda	Americas	Northern America	61662	62004	-0.01
206	Cayman Islands	Americas	Caribbean	60764	59967	0.01
207	Greenland	Americas	Northern America	56196	56186	0.00
208	Saint Kitts and Nevis	Americas	Caribbean	56183	55572	0.01
209	American Samoa	Oceania	Polynesia	55602	55538	0.00
210	Northern Mariana Islands	Oceania	Micronesia	55389	55070	0.01
211	Marshall Islands	Oceania	Micronesia	53069	52993	0.00
212	Faroe Islands	Europe	Northern Europe	48239	48199	0.00
213	Sint Maarten	Americas	Caribbean	39538	38745	0.02
214	Monaco	Europe	Western Europe	37863	37731	0.00
215	Liechtenstein	Europe	Western Europe	37776	37531	0.01
216	Turks and Caicos Islands	Americas	Caribbean	34904	34339	0.02
217	Gibraltar	Europe	Southern Europe	32373	32217	0.01
218	San Marino	Europe	Southern Europe	31950	31781	0.01
219	British Virgin Islands	Americas	Caribbean	30659	30117	0.02
220	Caribbean Netherlands	Americas	Caribbean	25328	24861	0.02
221	Palau	Oceania	Micronesia	21501	21291	0.01
222	Cook Islands	Oceania	Polynesia	20948	20833	0.01
223	Anguilla	Americas	Caribbean	14763	14614	0.01
224	Wallis and Futuna	Oceania	Polynesia	13112	13151	0.00
225	Nauru	Oceania	Micronesia	10263	10222	0.00
226	Tuvalu	Oceania	Polynesia	9943	9916	0.00
227	Saint Pierre and Miquelon	Americas	Northern America	6301	6288	0.00
228	Montserrat	Americas	Caribbean	5154	5125	0.01
229	Saint Helena, Ascension and Tristan da Cunha	Africa	Western Africa	3956	3961	0.00
230	Falkland Islands	Americas	South America	2912	2903	0.00
231	Niue	Oceania	Polynesia	1612	1610	0.00
232	Tokelau	Oceania	Polynesia	1276	1250	0.02
233	Vatican City	Europe	Southern Europe	801	800	0.00
			"""
	val data = ArrayList<CountryData>()
	val countries = countryTxt.split('\n')
	for (country in countries) {
		if (country.isBlank()) continue
		val countrySplit = country.trim().split('\t')
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
	data
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