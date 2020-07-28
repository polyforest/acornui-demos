@file:Suppress("CssReplaceWithShorthandSafely", "CssInvalidPropertyValue", "CssUnresolvedCustomProperty")

package com.acornui.component

import com.acornui.component.input.Button
import com.acornui.component.input.button
import com.acornui.component.style.StyleTag
import com.acornui.component.text.TextFieldImpl
import com.acornui.component.text.text
import com.acornui.css.cssVar
import com.acornui.di.Context
import com.acornui.dom.addCssToHead
import com.acornui.dom.div
import com.acornui.skins.Theme
import com.acornui.time.nextFrameCallback
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class DataGrid<E>(owner: Context) : DivComponent(owner) {

	private var rowFactory: RowFactory<E>? = null
	private var data: List<E> = emptyList()

	val header = addChild(div {
		addClass(headerRowStyle)
	})

	val contents = addChild(div {
		addClass(contentsContainer)
	})

	fun header(init: UiComponent.() -> Unit) {
		header.apply(init)
	}

	fun row(init: RowFactory<E>) {
		rowFactory = init
		refreshRows()
	}

	fun data(value: List<E>) {
		data = value
		refreshRows()
	}

	private val refreshRows = nextFrameCallback {
		contents.clearElements(dispose = true)
		for (datum in data) {
			contents.addElement(div {
				addClass(rowStyle)
				rowFactory?.invoke(this, datum)
			})
		}
	}

	init {
		addClass(styleTag)
	}

	companion object {
		val styleTag = StyleTag("DataGrid")
		val headerRowStyle = StyleTag("DataGrid_headerRow")
		val headerCellStyle = StyleTag("DataGrid_headerCell")
		val contentsContainer = StyleTag("DataGrid_contentsContainer")
		val rowStyle = StyleTag("DataGrid_row")
		val cellStyle = StyleTag("DataGrid_cell")

		init {

			addCssToHead(
				"""
$styleTag {	
	display: inline-grid;
	
	background: ${cssVar(Theme::panelBackground)};
	border: ${cssVar(Theme::borderThickness)} solid ${cssVar(Theme::border)};
	border-radius: ${cssVar(Theme::borderRadius)};
	box-sizing: border-box;
	overflow: auto;
	box-shadow: ${cssVar(Theme::componentShadow)};
	grid-auto-rows: min-content;
}

$cellStyle {
	padding: ${cssVar(Theme::inputPadding)};
}

$headerRowStyle {
	display: contents;
}


$headerRowStyle > div:first-child {
	border-top-left-radius: var(--br);
}

$headerRowStyle > div {
	--br: calc(${cssVar(Theme::borderRadius)} - ${cssVar(Theme::borderThickness)});
	border-radius: 0;
	grid-row-start: 1;
	align-self: stretch;
	position: sticky;
	top: 0;
	border: none;
	clip-path: polygon(-10% -10%, 110% -10%, 110% 100%, -10% 100%);
}

$headerCellStyle:focus {
    box-shadow: inset 0 0 0 ${cssVar(Theme::focusThickness)} ${cssVar(Theme::focus)};
	border-color: ${cssVar(Theme::focus)};
}

$contentsContainer {
	display: contents;
}

$rowStyle {
	display: contents;
}

$contentsContainer > div:nth-child(2n+0) > $cellStyle {
	background: ${cssVar(Theme::dataRowEvenBackground)};
}

$contentsContainer > div:nth-child(2n+1) > $cellStyle {
	background: ${cssVar(Theme::dataRowOddBackground)};
}



			"""
			)
		}
	}
}

typealias RowFactory<E> = UiComponent.(E) -> Unit

inline fun <E> Context.dataGrid(init: ComponentInit<DataGrid<E>> = {}): DataGrid<E> {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return DataGrid<E>(this).apply(init)
}

inline fun <E> Context.dataGrid(data: List<E>, init: ComponentInit<DataGrid<E>> = {}): DataGrid<E> {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return DataGrid<E>(this).apply {
		data(data)
		init()
	}
}

inline fun DataGrid<*>.headerCell(init: ComponentInit<Button> = {}): Button {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return button {
		addClass(DataGrid.headerCellStyle)
		init()
	}
}

inline fun DataGrid<*>.cell(text: String = "", init: ComponentInit<TextFieldImpl> = {}): TextFieldImpl {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return text {
		addClass(DataGrid.cellStyle)
		this.text = text
		init()
	}
}