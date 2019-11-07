package com.acornui.newproject

import com.acornui.component.UiComponent
import com.acornui.component.layout.algorithm.VerticalLayoutContainer
import com.acornui.component.text.text
import com.acornui.di.Owned
import com.acornui.skins.BasicUiSkin

class NewProjectMain(owner: Owned) : VerticalLayoutContainer<UiComponent>(owner) {

	init {
		BasicUiSkin(stage).apply()
		+text("Hello")
	}
}