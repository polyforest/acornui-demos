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

package com.acornui.newproject.js

import com.acornui.component.button
import com.acornui.component.stack
import com.acornui.component.stage
import com.acornui.file.FileIoManager
import com.acornui.input.interaction.click
import com.acornui.js.file.JsFileIoManager
import com.acornui.runMain
import com.acornui.skins.BasicUiSkin
import com.acornui.webgl.webGlApplication
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.w3c.files.Blob
import kotlin.js.Date
import kotlin.js.Promise

fun main() = runMain {
	webGlApplication("newProjectRoot") {
		BasicUiSkin(stage).apply()
		val fileManager = inject(FileIoManager) as JsFileIoManager

		+stack {
			+button("Zip me") {
				click().add {
					println("Zipping")
					js("""var JSZip = require("jszip");""")
					val zip = JSZip()
					zip.file("Hello.txt", "Hello World\n")
					zip.generateAsync(object {
						val type = "blob"
					}).then { content ->
						// see FileSaver.js
						println("Done: content $content")
						//var blobUrl = URL.createObjectURL(content);
						//					saveAs(content, "example.zip");
						fileManager.saveBinary(content, "File.zip")
					}
				}
			}
		}
	}
}

external class ZipObject {
	fun async(type: String): Promise<Any?>
}

external class JSZip {
	fun file(name: String): Promise<ZipObject>
	fun file(name: String, data: String)
	fun file(name: String, data: String, options: dynamic)
	fun file(name: String, data: ArrayBuffer)
	fun file(name: String, data: ArrayBuffer, options: dynamic)
	fun file(name: String, data: Uint8Array)
	fun file(name: String, data: Uint8Array, options: dynamic)
	fun file(name: String, data: Blob)
	fun file(name: String, data: Blob, options: dynamic)
	fun loadAsync(data: ArrayBuffer): Promise<JSZip>

	fun generateAsync(options: dynamic): Promise<Blob>
	fun generateAsync(options: dynamic, onUpdate: (UpdateMetadata) -> Unit): Promise<ZipObject>
}

external class UpdateMetadata {

	/**
	 * The percent of completion (a number between 0 and 100)
	 */
	val percent: Float

	/**
	 * The name of the current file being processed, if any.
	 */
	val currentFile: String
}

fun jsZipAttributes(

		/**
		 * Set to true if the data is base64 encoded. For example image data from a <canvas> element. Plain text and
		 * HTML do not need this option. More.
		 */
		base64: Boolean = false,

		/**
		 * Set to true if the data should be treated as raw content, false if this is a text. If base64 is used, this
		 * defaults to true, if the data is not a string, this will be set to true. More.
		 */
		binary: Boolean = false,

		/**
		 * The current date	the last modification date.
		 */
		date: Date,

		/**
		 * If set, specifies compression method to use for this specific file. If not, the default file compression
		 * will be used, see generateAsync(options).
		 */
		compression: String? = null,

		/**
		 * The options to use when compressing the file, see generateAsync(options).
		 */
		compressionOptions: dynamic = null,

		/**
		 * The comment for this file.
		 */
		comment: String? = null,

		/**
		 * Set to true if (and only if) the input is a “binary string” and has already been prepared with a 0xFF mask.
		 */
		optimizedBinaryString: Boolean = false,

		/**
		 * Set to true if folders in the file path should be automatically created, otherwise there will only be virtual
		 * folders that represent the path to the file.
		 */
		createFolders: Boolean = true,

		/**
		 * The UNIX permissions of the file, if any.
		 * 16 bits
		 */
		unixPermissions: Int? = null,

		/**
		 * The DOS permissions of the file, if any. More.
		 * 6 bits
		 */
		dosPermissions: Int? = null,

		/**
		 * Set to true if this is a directory and content should be ignored.
		 */
		dir: Boolean = false
): dynamic {
	val o = js("({})")
	o["base64"] = base64
	o["binary"] = binary
	o["date"] = date
	o["compression"] = compression
	o["compressionOptions"] = compressionOptions
	o["comment"] = comment
	o["optimizedBinaryString"] = optimizedBinaryString
	o["createFolders"] = createFolders
	o["unixPermissions"] = unixPermissions
	o["dosPermissions"] = dosPermissions
	o["dir"] = dir
	return o
}
