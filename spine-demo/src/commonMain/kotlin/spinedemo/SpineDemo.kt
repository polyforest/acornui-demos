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

package spinedemo

import com.acornui.AppConfig
import com.acornui.WindowConfig
import com.acornui.asset.cacheSet
import com.acornui.component.UiComponent
import com.acornui.component.button
import com.acornui.component.image
import com.acornui.component.layout.HAlign
import com.acornui.component.layout.VAlign
import com.acornui.component.layout.algorithm.CanvasLayoutContainer
import com.acornui.component.layout.algorithm.vGroup
import com.acornui.di.Context
import com.acornui.graphic.Color
import com.acornui.input.interaction.click
import com.acornui.math.Pad
import com.acornui.skins.BasicUiSkin
import com.acornui.skins.Theme
import com.acornui.tween.Tween
import com.esotericsoftware.spine.component.SkeletonComponent
import com.esotericsoftware.spine.component.loadSkeleton
import com.esotericsoftware.spine.component.skeletonComponent
import com.esotericsoftware.spine.component.spineScene
import kotlinx.coroutines.launch

/**
 * @author nbilyk
 */
class SpineDemo(owner: Context) : CanvasLayoutContainer<UiComponent>(owner) {

	private var raptor: SkeletonComponent? = null

	init {
		Tween.prepare()
		BasicUiSkin(stage, Theme(bgColor = Color(0x111111ff))).apply()

		println("SpineDemo#onInitialized")


		+image {
			+spineScene {

				// The 0,0 of the raptor is bottom middle, setting the origin to top left.
				defaultWidth = 1400f
				defaultHeight = 1200f
				setOrigin(-defaultWidth!! * 0.5f, -defaultHeight!!)

				launch {
					val skeleton = loadSkeleton("assets/raptor/raptor.json", "assets/raptor/raptorAssets.json", null, cacheSet()).await()
					raptor = +skeletonComponent(skeleton) {
						animationState.data.defaultMix = 0.25f
						animationState.setAnimation(0, "walk", loop = true)
					}
				}
			} layout {
				verticalAlign = VAlign.BOTTOM
				horizontalAlign = HAlign.CENTER
			}
		} layout { fill() }



		+vGroup {
			style.padding = Pad(4f)
			+button {
				label = "Draw"
				click().add {
					it.handled = true
					raptor?.animationState?.setAnimation(1, "gungrab")
				}
			}
		}

		click().add {
			if (!it.handled) {
				raptor?.animationState?.setAnimation(0, "jump")
				raptor?.animationState?.addAnimation(0, "walk", loop = true)
			}
		}
	}
}

fun config() = AppConfig(window = WindowConfig(title = "SpineDemo", backgroundColor = Color(0.1f, 0.1f, 0.1f, 1f)))