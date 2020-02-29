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

package fileiodemo

import com.acornui.component.UiComponent
import com.acornui.component.button
import com.acornui.component.layout.algorithm.VerticalLayoutContainer
import com.acornui.component.layout.algorithm.hGroup
import com.acornui.component.layout.algorithm.vGroup
import com.acornui.component.scroll.scrollArea
import com.acornui.component.text.TextArea
import com.acornui.component.text.text
import com.acornui.component.text.textArea
import com.acornui.di.Context
import com.acornui.file.FileFilterGroup
import com.acornui.file.FileIoManager
import com.acornui.file.FileReader
import com.acornui.input.interaction.click
import com.acornui.math.Pad
import com.acornui.observe.bind
import com.acornui.observe.dataBinding
import com.acornui.popup.alert
import com.acornui.skins.BasicUiSkin
import com.acornui.skins.Theme
import kotlinx.coroutines.launch

class FileIoDemo(owner: Context) : VerticalLayoutContainer<UiComponent>(owner) {

	private val editor: TextArea
	private val dataBinding = dataBinding(emptyList<FileReader>())
	private var fileManager: FileIoManager
	private val saveSupported: Boolean
	// Set extensions to null for all.
	private val filterGroups = listOf(listOf(".txt", ".kt"), listOf("zip"), listOf("image/*")).map { FileFilterGroup(it) }
	private var multipleFilesOpen = false

	init {
		BasicUiSkin(stage, Theme()).apply()
		fileManager = inject(FileIoManager)
		saveSupported = fileManager.saveSupported
		style.padding = Pad(5f)

		+hGroup {
			+text("File Io Demo")
			+hGroup {
				style.gap = 0f
				dataBinding.bind {
					clearElements()
					if (it.isNotEmpty()) apply {
						for (i in 0..it.lastIndex) {
							if (i < 1)
								+text(" - ${it[0].name}")
							else
								+text(" | ${it[i].name}")
						}
					}
				}
			}
		}

		+hGroup {
			+button("Open File") {
				click().add {
					openFile()
				}
			}

			+button("Open Files") {
				click().add {
					openFiles()
				}
			}

			+hGroup {
				dataBinding.bind {
					visible = saveSupported && !multipleFilesOpen && it.isNotEmpty()
				}
				+button("Save File") {
					click().add {
						saveFile()
					}
				}
			}

			+button("Clear Files") {
				click().add {
					clearFiles()
				}
			}
		}

		+vGroup {
			+scrollArea {
				editor = +textArea() layout { fill() }
			} layout { fill() }

			dataBinding.bind { fileReaders ->
				clearElements(dispose = false)
				editor.text = ""

				launch {
					fileReaders.forEach { fileReader ->
						editor.text += "${fileReader.name}\n${fileReader.readAsString()}\n\n\n"
					}
				}
			}
		} layout { fill() }
	}

	private fun openFile() {
		tryCatch {
			fileManager.pickFileForOpen(filterGroups) {
				multipleFilesOpen = false
				dataBinding.value = listOf(it)
			}
		}
	}

	private fun openFiles() {
		tryCatch {
			fileManager.pickFilesForOpen(filterGroups) {
				multipleFilesOpen = it.size > 1
				dataBinding.value = it
			}
		}
	}

	private fun saveFile() {
		tryCatch {
			fileManager.saveText(editor.text.substringAfter("\n", ""), filterGroups, "example.txt", "txt")
		}
	}

	private fun clearFiles() {
		multipleFilesOpen = false
		dataBinding.value = emptyList()
	}

	private fun tryCatch(block: () -> Unit) {
		try {
			block.invoke()
		} catch (e: Exception) {
			alert("Error", e.message ?: "Some unknown error has occurred.")
		}
	}
}

