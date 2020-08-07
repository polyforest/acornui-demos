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


		+vGroup() {
			+a("#") {
				+"Hello World"
			}
			+panel {
				+a("#") {
					+"Hello World"
				}
				+tree<TreeNode> {
					data = TreeNode("a", listOf(
						TreeNode("a.a", listOf(
							TreeNode("a.a.a"),
							TreeNode("a.a.b")
						)),
						TreeNode("a.b", listOf(
							TreeNode("a.b.a"),
							TreeNode("a.b.b")
						)),
						TreeNode("a.c", listOf(
							TreeNode("a.c.a"),
							TreeNode("a.c.b"),
							TreeNode("a.c.c"),
						))
					))
					all {
						toggled = true
						formatter { data -> data?.label ?: "Unset" }
					}
				}
			}

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