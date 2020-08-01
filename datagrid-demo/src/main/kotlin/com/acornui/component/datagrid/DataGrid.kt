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

@file:Suppress("CssReplaceWithShorthandSafely", "CssInvalidPropertyValue", "CssUnresolvedCustomProperty")

package com.acornui.component.datagrid

import com.acornui.component.*
import com.acornui.component.input.Button
import com.acornui.component.input.button
import com.acornui.component.input.submitInput
import com.acornui.component.style.StyleTag
import com.acornui.component.text.TextFieldImpl
import com.acornui.component.text.text
import com.acornui.css.css
import com.acornui.css.cssProp
import com.acornui.css.cssVar
import com.acornui.di.Context
import com.acornui.dom.addCssToHead
import com.acornui.dom.div
import com.acornui.dom.form
import com.acornui.dom.getTabbableElements
import com.acornui.input.*
import com.acornui.recycle.recycle
import com.acornui.signal.signal
import com.acornui.skins.Theme
import com.acornui.time.nextFrameCallback
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import com.acornui.dom.footer as footerEl

class DataGrid<E>(owner: Context) : DivComponent(owner) {

	class DataChangeEvent<E>(oldData: List<E>, newData: List<E>)

	/**
	 * This grid's [data] has changed.
	 *
	 * This may not be cancelled.
	 */
	val dataChanged = signal<DataChangeEvent<E>>()

	class RowEditEvent<E>(val item: E?) : Event()

	/**
	 * A row is about to be edited.
	 * This will only dispatch via a user event, such as focusing a cell.
	 * This may be cancelled.
	 */
	val rowEditing = signal<RowEditEvent<E>>()

	/**
	 * A row is being edited.
	 * This may not be cancelled.
	 */
	val rowEdited = signal<RowEditEvent<E>>()

	private var rowBuilder: RowBuilder<E>? = null
	private var rowEditorBuilder: RowBuilder<E>? = null

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
	val mainContainer = addChild(div {
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
	fun rows(builder: RowBuilder<E>) {
		contents.clearElements(dispose = true)
		rowBuilder = builder
		refreshRows()
	}

	fun rowEditor(builder: RowBuilder<E>) {
		rowEditorBuilder = builder
	}

	private val refreshRows = nextFrameCallback {
		val previouslyEdited = editedRow
		editRow(null)
		val rowsContainer = contents.unsafeCast<ElementParent<DataGridRow<E>>>()
		recycle(data, rowsContainer.elements, factory = {
			item: E, index: Int ->
			createRow(item)
		}, configure = {
			element: WithNode, item: E, index: Int ->
		}, disposer = {
			it.dispose()
		}, retriever = {
			element ->
			element.data
		})
		editRow(previouslyEdited)
	}

	private fun createRow(item: E) = row(item) {
		rowBuilder?.invoke(this, item)

		focusin.listen {
			userEditRow(item)
		}
	}

	init {
		addClass(Panel.panelColorsStyle)
		addClass(styleTag)

		keyPressed.listen {
			when (it.keyCode) {
				Ascii.ESCAPE -> editRow(null)
			}
		}

//		contents.focusin.listen {
//			val row = it.target.unsafeCast<Node>().parentNode!!
//			val cellIndex = row.childNodes.asList().indexOf(it.target)
//			val rowIndex = row.parentNode!!.childNodes.asList().indexOf(row)
//
//			println("Focused $rowIndex $cellIndex")
//		}
	}

	/**
	 * From a user interaction, sets the currently edited row.
	 * First this will dispatch a [rowEditing] signal, which may be cancelled. If not cancelled, [editRow] will be
	 * called.
	 */
	fun userEditRow(item: E?) {
		if (editedRow == item) return
		val e = RowEditEvent(item)
		rowEditing.dispatch(e)
		if (e.defaultPrevented) return
		editRow(item)
	}

	var rowEditor: DataGridRow<E>? = null
		private set

	/**
	 * Returns the currently edited row.
	 */
	val editedRow: E?
		get() = rowEditor?.data

	/**
	 * Sets the currently edited row.
	 * Dispatches a [rowEdited] signal, which may not be cancelled.
	 */
	fun editRow(item: E?) {
		if (editedRow == item) return
		val e = RowEditEvent(item)
		rowEdited.dispatch(e)

		val previousRowData = rowEditor?.data
		if (previousRowData != null) {
			val previousRowIndex = contents.elements.indexOf(rowEditor!!)
			if (previousRowIndex != -1) {
				val row = createRow(previousRowData)
				contents.addElement(previousRowIndex, row)
			}
		}

		rowEditor?.dispose()
		rowEditor = null
		val rowEditorBuilder = rowEditorBuilder
		if (rowEditorBuilder == null || item == null) return

		// Dispose the row we'll replace with an editor.
		val rowIndex = contents.elements.indexOfFirst { it.unsafeCast<DataGridRow<E>>().data === item }
		if (rowIndex != -1)
			contents.elements[rowIndex].unsafeCast<DataGridRow<E>>().dispose()
		else return

		rowEditor = row(item) {
			addClass(rowEditorStyle)
			+form {
				preventAction()
				submit.listen {
					println("Submit!")


				}
				rowEditorBuilder.invoke(this, item)
				+submitInput { style.display = "none" }
			}

//			focusout.listen {
//				commitRow()
//			}
		}
		contents.addElement(rowIndex, rowEditor)
		val firstInput = rowEditor!!.dom.getTabbableElements().firstOrNull()
		firstInput?.focus()
		if (firstInput?.tagName.equals("input", ignoreCase = true))
			firstInput.unsafeCast<HTMLInputElement>().select()

	}

	fun commitRow() {
		userEditRow(null)
	}

	companion object {

		val styleTag = StyleTag("DataGrid")
		val mainContainerStyle = StyleTag("DataGrid_mainContainer")
		val headerRowStyle = StyleTag("DataGrid_headerRow")
		val footerRowStyle = StyleTag("DataGrid_footerRow")
		val headerCellStyle = StyleTag("DataGrid_headerCell")
		val contentsContainerStyle = StyleTag("DataGrid_contentsContainer")
		val rowStyle = StyleTag("DataGrid_row")
		val rowEditorStyle = StyleTag("DataGrid_rowEditor")
		val cellStyle = StyleTag("DataGrid_cell")

		val borderThickness: String = css("1px")
		val borderColor: String = css("#ccc")

		init {

			addCssToHead(
				"""
$styleTag {	
	display: flex;
	flex-direction: column;
	box-sizing: border-box;
	position: relative;
	overflow: auto;
	${cssProp(::borderThickness)};
	${cssProp(::borderColor)};
}

$cellStyle {
	padding: ${cssVar(Theme::inputPadding)};
	display: flex;
	align-items: center;
}

$mainContainerStyle {
	min-width: fit-content;
	min-height: fit-content;
	grid-template-columns: inherit;
	align-items: inherit;
	align-content: inherit;
	justify-items: inherit;	
	justify-content: inherit;
	
	display: grid;
	grid-auto-rows: min-content;
	row-gap: ${cssVar(::borderThickness)};
	column-gap: ${cssVar(::borderThickness)};
	background-color: ${cssVar(::borderColor)};
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
	/*align-self: stretch;*/
	border: none;
	position: -webkit-sticky;
	position: sticky;
	top: 0;
	
	font-weight: bolder;
}

$footerRowStyle {
	display: contents;
}

$footerRowStyle > div {
	position: -webkit-sticky;
	position: sticky;
	bottom: 0;
	
	color: ${cssVar(Theme::footerTextColor)};
	background: ${cssVar(Theme::footerBackgroundColor)};
}

$styleTag *:focus {
    box-shadow: inset 0 0 0 ${cssVar(Theme::focusThickness)} ${cssVar(Theme::focus)};
	border-color: ${cssVar(Theme::focus)};
	/* The default focus transition isn't snappy enough for the datagrid */
	transition: box-shadow 0.0s ease-in-out;
}

$contentsContainerStyle {
	display: contents;
	border-bottom-left-radius: inherit;
}

$rowStyle {
	display: contents;
}

$rowEditorStyle > form {
	display: contents;
}

$rowEditorStyle {
	
}

$contentsContainerStyle > $rowStyle:nth-child(2n+0) $cellStyle {
	background: ${cssVar(Theme::dataRowEvenBackground)};
}

$contentsContainerStyle > $rowStyle:nth-child(2n+1) $cellStyle {
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

typealias RowBuilder<E> = UiComponent.(E) -> Unit

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
		addClass(DataGrid.cellStyle)
		this.text = text
		init()
	}
}