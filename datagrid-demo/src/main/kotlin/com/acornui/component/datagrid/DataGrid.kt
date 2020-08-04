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

import com.acornui.Disposable
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
import com.acornui.input.Ascii
import com.acornui.input.Event
import com.acornui.input.focusin
import com.acornui.input.keyPressed
import com.acornui.recycle.Clearable
import com.acornui.recycle.recycle
import com.acornui.signal.SignalSubscription
import com.acornui.signal.signal
import com.acornui.skins.Theme
import com.acornui.time.nextFrameCallback
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
import org.w3c.dom.HTMLInputElement
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.Delegates
import com.acornui.dom.footer as footerEl

open class DataGrid<E>(owner: Context) : DivComponent(owner) {

	class DataChangeEvent<E>(val oldData: List<E>, val newData: List<E>)

	/**
	 * This grid's [data] has changed.
	 *
	 * This may not be cancelled.
	 */
	val dataChanged = signal<DataChangeEvent<E>>()

	class RowSubmittedEvent<E>(
		val index: Int,
		val oldData: E,
		val newData: E
	) {
		constructor(index: Int, rowEditor: DataGridRowEditor<E>) : this(index, rowEditor.data!!, rowEditor.dataBuilder.invoke(rowEditor.data!!))
	}

	/**
	 * Dispatched when the row editor has been validated and the editor is about to be closed.
	 * This is typically when data requests should be made.
	 * @see autoMutate
	 */
	val rowSubmitted = signal<RowSubmittedEvent<E>>()

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

	var rowBuilder: (DataGridRow<E>.() -> Unit)? = null
		private set

	fun rows(builder: DataGridRow<E>.() -> Unit) {
		displayRows.forEach(Disposable::dispose)
		displayRows.clear()
		rowBuilder = builder
		refreshRows()
	}

	var data: List<E> = emptyList()
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
	 */
	fun header(block: UiComponent.() -> Unit) {
		header.apply(block)
	}

	val contents = mainContainer.addElement(div {
		addClass(contentsContainerStyle)
	})

	private val displayRows = ArrayList<DataGridRow<E>>()

	/**
	 * The row editor. This may be configured via [rowEditor]
	 */
	val rowEditor = DataGridRowEditor<E>(this).apply {
		submitted.listen {
			editNextRow()
		}
	}

	/**
	 * Returns the currently edited row.
	 */
	val editedRow: E?
		get() = rowEditor.data

	/**
	 * Returns the index of the row currently being edited.
	 */
	val editedRowIndex: Int
		get() = if (editedRow == null) -1 else displayRows.indexOfFirst { it.data === editedRow }

	/**
	 * Calls the specified function [block] with [rowEditor] as its receiver.
	 */
	fun rowEditor(block: DataGridRowEditor<E>.() -> Unit) {
		rowEditor.apply(block)
	}

	val footer = mainContainer.addElement(footerEl {
		addClass(footerRowStyle)
	})

	/**
	 * Calls the specified function [block] with [footer] as its receiver.
	 */
	fun footer(block: UiComponent.() -> Unit) {
		footer.apply(block)
	}

	private val refreshRows = nextFrameCallback {
		val previouslyEdited = editedRow
		editRow(null)
		recycle(data, displayRows, factory = { item: E, index: Int ->
			contents.addElement(index, createRow())
		}, configure = { element: DataGridRow<E>, item: E, index: Int ->
			element.data = item
		}, disposer = {
			it.dispose()
		}, retriever = { element ->
			element.data
		})
		editRow(previouslyEdited)
	}

	private fun createRow() = DataGridRow<E>(this).apply {
		(rowBuilder ?: error("rows not set")).invoke(this)
		focusin.listen {
			userEditRow(data)
		}
	}

	/**
	 * If true, the [data] list will automatically mutate to the new value produced by the row editor.
	 */
	var autoMutate = true

	init {
		addClass(Panel.panelColorsStyle)
		addClass(styleTag)

		keyPressed.listen {
			when (it.keyCode) {
				Ascii.ESCAPE -> editRow(null)
			}
		}

		rowSubmitted.listen { e ->
			if (autoMutate) {
				data = data.toPersistentList().mutate {
					println("e.index ${e.index}")
					it[e.index] = e.newData!!
				}
				println("data $data")
			}
		}
	}

	/**
	 * From a user interaction, sets the currently edited row.
	 * First this will dispatch a [rowEditing] signal, which may be cancelled. If not cancelled, [editRow] will be
	 * called.
	 */
	fun userEditRow(item: E?) {
		val editedRow = editedRow
		if (editedRow == item) return
		val rowEditor = rowEditor
		if (editedRow != null) {
			if (rowEditor.form.checkValidity()) {
				rowSubmitted.dispatch(RowSubmittedEvent(editedRowIndex, rowEditor))
			} else {
				// Edited row has user form errors.
				rowEditor.form.reportValidity()
				return
			}
		}
		val e = RowEditEvent(item)
		rowEditing.dispatch(e)
		if (e.defaultPrevented) return
		editRow(item)
	}

	/**
	 * Sets the currently edited row.
	 * Dispatches a [rowEdited] signal, which may not be cancelled.
	 */
	fun editRow(item: E?) {
		if (editedRow == item) return
		val e = RowEditEvent(item)
		rowEdited.dispatch(e)

		val editedRowOld = displayRows.firstOrNull { it.data === editedRow }
		editedRowOld?.style?.display = "contents"
		contents.removeElement(rowEditor)
		rowEditor.data = item
		if (item == null) return

		// Swap the display row with the row editor.
		val editedRowIndex = displayRows.indexOfFirst { it.data === item }
		if (editedRowIndex == -1) return
		val editedDisplayRow = displayRows[editedRowIndex]
		editedDisplayRow.style.display = "none"
		contents.addElement(editedRowIndex, rowEditor)

		// Set focus
		val firstInput = rowEditor.dom.getTabbableElements().firstOrNull()
		firstInput?.focus()
		if (firstInput?.tagName.equals("input", ignoreCase = true))
			firstInput.unsafeCast<HTMLInputElement>().select()
	}

	/**
	 * Commits the current row then opens the next row for editing.
	 */
	fun editNextRow() {
		userEditRow(data.getOrNull(editedRowIndex + 1))
	}

	/**
	 * Commits the current row then opens the previous row for editing.
	 */
	fun editPreviousRow() {
		userEditRow(data.getOrNull(editedRowIndex - 1))
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
	scroll-padding-top: 4em; /* To prevent the sticky header from covering up the first row when tabbing */
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
	/* Take the default focus transition, make it snappier and inset to be better for data grid cells. */
    box-shadow: inset 0 0 0 ${cssVar(Theme::focusThickness)} ${cssVar(Theme::focus)};
	border-color: ${cssVar(Theme::focus)};
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

$rowEditorStyle * {

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

open class DataGridRow<E>(owner: Context) : DivComponent(owner) {

	class DataChangeEvent<E>(val old: E?, val new: E?)

	val dataChanged = signal<DataChangeEvent<E>>()

	init {
		addClass(DataGrid.rowStyle)
	}

	/**
	 * Sets the row's data.
	 */
	var data: E? = null
		set(value) {
			val old = field
			if (old == value) return
			field = value
			dataChanged.dispatch(DataChangeEvent(old, value))
		}

	/**
	 * Invokes the callback with the new data when this row's data has changed.
	 */
	fun data(callback: (E) -> Unit): SignalSubscription = dataChanged.listen {
		if (it.new != null)
			callback(it.new)
	}
}

/**
 * Constructs a new data grid row.
 *
 * Example:
 *
 * ```
 * rowFactory = {
 *   row {
 *     +cell {
 *       data {
 *         label = it.name
 *       }
 *       tabIndex = 0
 *     }
 *   }
 * }
 * ```
 *
 * This does not use a dsl marker in order to allow the [DataGridRow.data] method to be invoked without an explicit
 * receiver. (Ideally only this method would be annotated to not need an explicit receiver.)
 */
inline fun <E> Context.row(init: (DataGridRow<E>).() -> Unit = {}): DataGridRow<E> {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return DataGridRow<E>(this).apply(init)
}

open class DataGridRowEditor<E>(owner: Context) : DataGridRow<E>(owner), Clearable {

	val form = addChild(form {
		preventAction()
		+submitInput { style.display = "none" }
	})

	/**
	 * Dispatched when the row editor form has been submitted.
	 */
	val submitted = form.submitted

	init {
		addClass(DataGrid.rowEditorStyle)
	}

	override fun onElementAdded(oldIndex: Int, newIndex: Int, element: WithNode) {
		form.addElement(newIndex, element)
	}

	override fun onElementRemoved(index: Int, element: WithNode) {
		form.removeElement(element)
	}

	/**
	 * When the row is submitted, this will construct the new data element.
	 */
	var dataBuilder: ((E) -> E) by Delegates.notNull()

	override fun clear() {
		data = null
		clearElements(dispose = true)
	}
}

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

inline fun Context.headerCell(label: String = "", init: ComponentInit<Button> = {}): Button {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return button {
		addClass(DataGrid.headerCellStyle)
		this.label = label
		init()
	}
}

inline fun Context.cell(label: String = "", init: ComponentInit<TextFieldImpl> = {}): TextFieldImpl {
	contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
	return text {
		addClass(DataGrid.cellStyle)
		this.label = label
		init()
	}
}