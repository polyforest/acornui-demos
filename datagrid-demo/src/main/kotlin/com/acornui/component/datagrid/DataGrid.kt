@file:Suppress("CssReplaceWithShorthandSafely", "CssInvalidPropertyValue", "CssUnresolvedCustomProperty")

package com.acornui.component.datagrid

import com.acornui.component.*
import com.acornui.component.input.Button
import com.acornui.component.input.button
import com.acornui.component.style.StyleTag
import com.acornui.component.text.TextFieldImpl
import com.acornui.component.text.text
import com.acornui.css.cssVar
import com.acornui.di.Context
import com.acornui.dom.addCssToHead
import com.acornui.dom.div
import com.acornui.input.focusin
import com.acornui.recycle.recycle
import com.acornui.signal.signal
import com.acornui.skins.Theme
import com.acornui.time.nextFrameCallback
import org.w3c.dom.Node
import org.w3c.dom.asList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import com.acornui.dom.footer as footerEl

class DataGrid<E>(owner: Context) : DivComponent(owner) {

	class DataChangeEvent<E>(oldData: List<E>, newData: List<E>)

	val dataChanged = signal<DataChangeEvent<E>>()

	private var rowBuilder: RowBuilder<E>? = null

	var data: List<E>  = emptyList()
		set(value) {
			val old = field
			if (old == value) return
			field = value
			dataChanged.dispatch(DataChangeEvent(old, value))
			refreshRows()
		}

	/**
	 * Necessary only for Safari
	 * https://stackoverflow.com/questions/57934803/workaround-for-a-safari-position-sticky-webkit-sticky-bug
	 */
	val mainContainer = addChild(div() {
		addClass(mainContainerStyle)
	})

	val header = mainContainer.addElement(div {
		addClass(headerRowStyle)
	})

	/**
	 * Calls the specified function [block] with [header] as its receiver.
	 * @return Returns [header]
	 */
	fun header(block: UiComponent.() -> Unit) = header.apply(block)

	val contents = mainContainer.addElement(div {
		addClass(contentsContainerStyle)
	})

	val footer = mainContainer.addElement(footerEl {
		addClass(footerRowStyle)
	})

	/**
	 * Calls the specified function [block] with [footer] as its receiver.
	 * @return Returns [footer]
	 */
	fun footer(block: UiComponent.() -> Unit) = footer.apply(block)

	/**
	 * Sets the builder for rows.
	 */
	fun rows(init: RowBuilder<E>) {
		contents.clearElements(dispose = true)
		rowBuilder = init
		refreshRows()
	}

	private val refreshRows = nextFrameCallback {
		val rowsContainer = contents.unsafeCast<ElementParent<DataGridRow<E>>>()
		recycle(data, rowsContainer.elements, factory = {
			item: E, index: Int ->
			row(item) {
				addClass(rowStyle)
				rowBuilder?.invoke(this, item)
			}
		}, configure = {
			element: WithNode, item: E, index: Int ->
		}, disposer = {
			it.dispose()
		}, retriever = {
			element ->
			element.data
		})
	}

	init {
		addClass(Panel.panelColorsStyle)
		addClass(styleTag)

		contents.focusin.listen {
			val row = it.target.unsafeCast<Node>().parentNode!!
			val cellIndex = row.childNodes.asList().indexOf(it.target)
			val rowIndex = row.parentNode!!.childNodes.asList().indexOf(row)

			println("Focused $rowIndex $cellIndex")
		}
	}

	companion object {

		val styleTag = StyleTag("DataGrid")
		val mainContainerStyle = StyleTag("DataGrid_mainContainer")
		val headerRowStyle = StyleTag("DataGrid_headerRow")
		val footerRowStyle = StyleTag("DataGrid_footerRow")
		val headerCellStyle = StyleTag("DataGrid_headerCell")
		val contentsContainerStyle = StyleTag("DataGrid_contentsContainer")
		val rowStyle = StyleTag("DataGrid_row")
		val cellStyle = StyleTag("DataGrid_cell")

		init {

			addCssToHead(
				"""
$styleTag {	
	display: flex;
	flex-direction: column;
	box-sizing: border-box;
	position: relative;
	overflow: auto;
}

$cellStyle {
	padding: ${cssVar(Theme::inputPadding)};
}

$mainContainerStyle {
	grid-template-columns: inherit;
}

$headerRowStyle {
	grid-template-columns: inherit;
	grid-auto-rows: min-content;
	display: grid;
	flex-grow: 0;
	flex-shrink: 0;
	position: -webkit-sticky;
	position: sticky;
	top: 0;
}

$headerRowStyle > div:first-child {
	border-top-left-radius: var(--br);
}

$headerRowStyle > div {
	--br: calc(${cssVar(Theme::borderRadius)} - ${cssVar(Theme::borderThickness)});
	border-radius: 0;
	grid-row-start: 1;
	align-self: stretch;
	top: 0;
	border: none;
}

$footerRowStyle {
	grid-template-columns: inherit;
	grid-auto-rows: min-content;
	display: grid;
	flex-grow: 0;
	flex-shrink: 0;
	position: -webkit-sticky;
	position: sticky;
	bottom: 0;
	box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.35);
}

$styleTag *:focus {
    box-shadow: inset 0 0 0 ${cssVar(Theme::focusThickness)} ${cssVar(Theme::focus)};
	border-color: ${cssVar(Theme::focus)};
	/* The default focus transition isn't snappy enough for the datagrid */
	transition: box-shadow 0.0s ease-in-out;
}

$contentsContainerStyle {
	display: grid;
	grid-template-columns: inherit;
	grid-auto-rows: min-content;
	border-bottom-left-radius: inherit;
}

$rowStyle {
	display: contents;
}

$contentsContainerStyle > div:nth-child(2n+0) > $cellStyle {
	background: ${cssVar(Theme::dataRowEvenBackground)};
}

$contentsContainerStyle > div:nth-child(2n+1) > $cellStyle {
	background: ${cssVar(Theme::dataRowOddBackground)};
}



			"""
			)
		}
	}
}

class DataGridRow<E>(owner: Context, val data: E) : DivComponent(owner) {

	init {
		addClass(DataGrid.rowStyle)
	}
}

inline fun <E> Context.row(data: E, init: ComponentInit<DataGridRow<E>> = {}): DataGridRow<E> {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return DataGridRow(this, data).apply(init)
}

typealias RowBuilder<E> = DataGridRow<E>.(E) -> Unit

inline fun <E> Context.dataGrid(init: ComponentInit<DataGrid<E>> = {}): DataGrid<E> {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return DataGrid<E>(this).apply(init)
}

inline fun <E> Context.dataGrid(data: List<E>, init: ComponentInit<DataGrid<E>> = {}): DataGrid<E> {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return DataGrid<E>(this).apply {
		this.data = data
		init()
	}
}

inline fun DataGrid<*>.headerCell(label: String = "", init: ComponentInit<Button> = {}): Button {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return button {
		addClass(DataGrid.headerCellStyle)
		this.label = label
		init()
	}
}

inline fun DataGrid<*>.cell(text: String = "", init: ComponentInit<TextFieldImpl> = {}): TextFieldImpl {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return text {
		tabIndex = 0
		addClass(DataGrid.cellStyle)
		this.text = text
		init()
	}
}