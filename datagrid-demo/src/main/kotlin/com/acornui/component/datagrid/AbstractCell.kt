package com.acornui.component.datagrid

import com.acornui.formatters.StringFormatter

/**
 * The base class for datagrid cells.
 */
abstract class AbstractCell<T> {

	abstract val formatter: StringFormatter<T>


}