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

import com.acornui.Node
import com.acornui.app
import com.acornui.component.Div
import com.acornui.component.applyCss
import com.acornui.component.layout.LayoutStyles
import com.acornui.component.layout.hGroup
import com.acornui.component.layout.spacer
import com.acornui.component.layout.vGroup
import com.acornui.component.panel
import com.acornui.component.style.cssClass
import com.acornui.component.text.text
import com.acornui.component.tree
import com.acornui.demo.initThemes
import com.acornui.demo.themeButton
import com.acornui.di.Context
import com.acornui.dom.a
import com.acornui.dom.div
import com.acornui.version

/**
 * @author nbilyk
 */
class TreeDemo(owner: Context) : Div(owner) {



	init {
		addClass(LayoutStyles.vGroup)
		applyCss("""
			overflow: auto;
			width: 100%;
			height: 100%;
			padding: 15px;			
		""")
		initThemes()

		+themeButton()


		+hGroup() {
			applyCss("""
				align-items: flex-start;
			""")
			val logArea = vGroup {
				applyCss("""
					max-height: 500px; 
					overflow: auto; 
					padding: 50px;
			""")
			}
			+panel {
				+tree<TreeNode> {
					data = TreeNode("Animalia", listOf(
						TreeNode("Chordate", listOf(
							TreeNode("Mammal", listOf(
								TreeNode("Primate", listOf(
									TreeNode("Hominidae", listOf(
										TreeNode("Homo", listOf(
											TreeNode("Sapiens", listOf(
												TreeNode("Human")
											)),
										)),
									)),
									TreeNode("Pongidae", listOf(
										TreeNode("Pan", listOf(
											TreeNode("Troglodytes", listOf(
												TreeNode("Chimpanzee"),
											)),
										)),
									)),
								)),
								TreeNode("Carnivora", listOf(
									TreeNode("Felidae", listOf(
										TreeNode("Felis", listOf(
											TreeNode("Domestica", listOf(
												TreeNode("House Cat"),
											)),
											TreeNode("Leo", listOf(
												TreeNode("Lion"),
											)),
										)),
									)),
								)),
							)),
						)),
						TreeNode("Arthropoda", listOf(
							TreeNode("Insect", listOf(
								TreeNode("Diptera", listOf(
									TreeNode("Muscidae", listOf(
										TreeNode("Musca", listOf(
											TreeNode("Domestica", listOf(
												TreeNode("Housefly", listOf(

												)),
											)),
										)),
									)),
								)),
							)),
						)),
					))
					all {
						toggled = true
						formatter { data -> data?.label ?: "Unset" }

						leafClicked.listen {
							logArea.addElement(text("${data?.label} Clicked!"))
						}
					}
				}
			}
			+logArea

		}
		+spacer { applyCss("flex-grow: 1;") }
		+text("v$version") {
			applyCss("""
				align-self: flex-end;
				justify-self: flex-end;
						""")
		}

	}

}

fun main() = app("acornUiRoot") {
	+TreeDemo(this)
}



data class TreeNode(val label: String, override val children: List<TreeNode> = emptyList()) : Node