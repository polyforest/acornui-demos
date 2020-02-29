@file:Suppress("RemoveExplicitTypeArguments", "SuspiciousCollectionReassignment")

package com.acornui.performancetest

import com.acornui.AppConfig
import com.acornui.WindowConfig
import com.acornui.async.delay
import com.acornui.ceilInt
import com.acornui.collection.ArrayList
import com.acornui.component.*
import com.acornui.component.layout.HAlign
import com.acornui.component.layout.VAlign
import com.acornui.component.layout.algorithm.*
import com.acornui.component.layout.spacer
import com.acornui.component.scroll.scrollArea
import com.acornui.component.text.*
import com.acornui.di.Context
import com.acornui.gl.core.CachedGl20
import com.acornui.graphic.Color
import com.acornui.input.interaction.click
import com.acornui.logging.Log
import com.acornui.math.Vector2
import com.acornui.math.Vector3
import com.acornui.math.nextFloat
import com.acornui.persistence.Persistence
import com.acornui.persistence.getItem
import com.acornui.popup.PopUpInfo
import com.acornui.popup.addPopUp
import com.acornui.serialization.jsonStringify
import com.acornui.skins.BasicUiSkin
import com.acornui.skins.Theme
import com.acornui.skins.ThemeFontVo
import com.acornui.text.NumberFormatter
import com.acornui.text.dateTimeFormatter
import com.acornui.text.numberFormatter
import com.acornui.text.percentFormatter
import com.acornui.time.Date
import com.acornui.time.DateRo
import com.acornui.time.onTick
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.json.Json
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.nanoseconds
import kotlin.time.seconds

private val TEST_DURATION = 10.seconds

/**
 * If the tests dip below this framerate, don't run the next more taxing test.
 */
private const val MIN_FPS = 5.0

class PerformanceTest(owner: Context) : StackLayoutContainer<UiComponent>(owner) {

	private val name: String = "0.3.25"

	private val persistence by Persistence

	private var history: List<PerformanceResultsSuite> = persistence.getItem("performanceHistory", ArrayListSerializer(PerformanceResultsSuite.serializer())) ?: emptyList()
		set(value) {
			field = value
			persistence.setItem("performanceHistory", jsonStringify(ArrayListSerializer(PerformanceResultsSuite.serializer()), value))
		}

	private val atlasPath = "assets/uiskin/icons.json"

	init {
		window.continuousRendering = true
		val windowConfig = inject(AppConfig).window
		window.setSize(windowConfig.initialWidth, windowConfig.initialHeight)
		style.horizontalAlign = HAlign.CENTER
		style.verticalAlign = VAlign.MIDDLE
		val theme = Theme(
				bodyFont = ThemeFontVo("Roboto_Mono", size = FontSize.SMALL, color = Color(0x333333ff)),
				menuFont = ThemeFontVo("Roboto_Mono", size = FontSize.SMALL, color = Color(0x333366ff)),
				headingFont = ThemeFontVo("Roboto_Mono", size = FontSize.REGULAR, color = Color(0x333355ff)),
				formLabelFont = ThemeFontVo("Roboto_Mono", size = FontSize.SMALL, color = Color(0x27273aff))
		)
		BasicUiSkin(stage, theme).apply()
		showMain()

	}

	private fun showMain() {
//		stage.style.showRedrawRegions = true
		+vGroup {
			style.gap = 20f
			style.horizontalAlign = HAlign.CENTER

			+headingText("Acorn UI Performance Tester")

			val nameInput: TextInput
			val fastModeInput: Button
			+form {
				+formLabel("Fast Mode:")
				fastModeInput = +checkbox() { toggled = true }
				+formLabel("Name:")
				nameInput = +textInput {
					text = name
				}
			}
			+button("Begin Test") {
				click().add {
					runTests(nameInput.text, fastModeInput.toggled)
				}
			} layout { width = 200f; height = 100f }

			+hr() layout { width = 800f }

			+headingText("Compare Results")
			+form {
				+formLabel("Previous:")
				val inputA: TextInput
				+hGroup {
					inputA = +textInput()
					+iconImageButton(atlasPath, "round_history_white_18dp") {
						click().add {
							showHistory(inputA)
						}
					}
				}
				+formLabel("New:")
				val inputB: TextInput
				+hGroup {
					inputB = +textInput()
					+iconImageButton(atlasPath, "round_history_white_18dp") {
						click().add {
							showHistory(inputB)
						}
					}
				}
				+spacer()
				+button("Compare") {
					click().add {
						try {
							val resultsA = Json.nonstrict.parse(PerformanceResultsSuite.serializer(), inputA.text)
							val resultsB = Json.nonstrict.parse(PerformanceResultsSuite.serializer(), inputB.text)
							val comparisonStr = resultsB.getComparisonStr(resultsA)
							addPopUp(popUpInfo = PopUpInfo(this@PerformanceTest.windowPanel { +textArea {
								text = comparisonStr

							} layout { fill() } }, layoutData = canvasLayoutData { left = 10f; top = 10f; bottom = 10f; right = 10f }))
						} catch (e: Throwable) {
							window.alert(e.message ?: "Error")
						}
					}
				}
			}
		}
	}

	private fun runTests(name: String, fastMode: Boolean) {
		clearElements(dispose = true)
		val container = +container {
			interactivityMode = InteractivityMode.NONE
		} layout { fill() }
		launch {
			Log.info("Beginning tests")
			val totalTime = measureTime {
				val results = if (fastMode) {
					container.runAllTests(
							name,
							listOf(10, 100, 500),
							listOf(0f, 0.25f, 1f),
							listOf(Constructor("Button", 50f) { buttonComponent() }),
							listOf<Updater<*>>(Updater<Vector2>("Transform", 1f, { Vector2().random(rng) }, ::translation))
					)
				} else {
					container.runAllTests(
							name,
							listOf(10, 100, 500),
							listOf(0f, 0.25f, 1f),
							listOf(Constructor("Sprite", 1f) { spriteComponent() }, Constructor("Button", 50f) { buttonComponent() }, Constructor("Text Area", 100f) { textAreaComponent() }, Constructor("Data Grid", 1000f) { dataGridComponent() }),
							//					listOf(Constructor("Data Grid", 100f) { dataGridComponent() }),
							listOf<Updater<*>>(
									Updater<Vector2>("Transform", 1f, { Vector2().random(rng) }, ::translation),
									Updater<Vector3>("Rotation", 2f, { Vector3().random(rng).scl(0.1f, 0.1f, 0.1f) }, ::rotation),
									Updater<Vector2>("Resize", 10f, { Vector2().random(rng) }, ::resize)
							)
					)
				}
				showSuite(results)
				history += results
			}
			Log.info("Completed in $totalTime")
			showMain()
		}

	}

	private fun showSuite(results: PerformanceResultsSuite) {
		val output = "${results.name}\n\n${results.toGridString()}\n\n${jsonStringify(PerformanceResultsSuite.serializer(), results)}"
		Log.info(output)
		addPopUp(popUpInfo = PopUpInfo(windowPanel { +textArea { text = output } layout { fill() } }, layoutData = canvasLayoutData { left = 10f; top = 10f; bottom = 10f; right = 10f }))
	}

	private fun showHistory(inputA: TextInput) {
		addPopUp(PopUpInfo(windowPanel {
			+scrollArea {
				+vGroup {
					if (history.isEmpty()) {
						+text("No test history")
					}
					for (suite in history) {
						+hGroup {
							+button(suite.name) {
								click().add {
									inputA.text = jsonStringify(PerformanceResultsSuite.serializer(), suite)
									this@windowPanel.close()
								}
							}

							+iconImageButton(atlasPath, "round_open_in_new_white_18dp") {
								click().add {
									showSuite(suite)
								}
							}

							+iconImageButton(atlasPath, "round_delete_forever_white_18dp") {
								click().add {
									history -= suite
								}
							}
						}
					}
				}
			} layout { width = 400f; height = 400f }
		}))
	}
}

fun Context.spriteComponent(): UiComponent {
	return drawableC(Sprite(inject(CachedGl20)).apply {
		setUv(0f, 0f, 0f, 0f, false)
	}) {
		colorTint = Color(0.29f, 0f, 0.51f, 0.5f)
		defaultWidth = 20f
		defaultHeight = 20f
	}
}

fun Context.buttonComponent(): UiComponent {
	return button("Beetroot water spinach")
}

fun Context.textAreaComponent(): UiComponent {
	return textArea {
		defaultWidth = 100f
		defaultHeight = 50f
		text = """Celery quandong swiss chard chicory earthnut pea potato. Salsify taro catsear garlic gram celery bitterleaf wattle seed collard greens nori. Grape wattle seed kombu beetroot horseradish carrot squash brussels sprout chard."""
	}
}

private var rng = Random(0)

private suspend fun ElementContainer<UiComponent>.runAllTests(suiteName: String, counts: List<Int>, percentsUpdated: List<Float>, constructors: List<Constructor>, updaters: List<Updater<*>>): PerformanceResultsSuite {
	val results = ArrayList<PerformanceResults>()
	var heaviestAllowed = Float.MAX_VALUE

	for (count in counts) {
		for (constructor in constructors) {
			for (percentUpdated in percentsUpdated) {
				for (updater in updaters) {
					val difficulty = count * constructor.difficulty * updater.difficulty
					if (difficulty > heaviestAllowed) {
						results.add(PerformanceResults(0, percentUpdated, constructor.name, updater.name, 0.0, PerformanceMetrics(), PerformanceMetrics(), 0, 0, 0.0))
					} else {
						if (count >= 500 && constructor.difficulty >= 1000f) continue // Don't even try making 500 datagrids...
						val r = runTest(count, percentUpdated, constructor, updater)
						if (r.fpsAvg < MIN_FPS)
							heaviestAllowed = difficulty
						results.add(r)
					}
				}
			}
		}
	}
	return PerformanceResultsSuite(suiteName, results = results)
}

private suspend fun <T> ElementContainer<UiComponent>.runTest(
		elementCount: Int,
		percentUpdated: Float,
		constructor: Constructor,
		updater: Updater<T>
): PerformanceResults {
	val stage = stage as MeasuredStage
	rng = Random(0)
	gc()
	delay(2.seconds)
	stage.clearPerformanceMetrics()
	val data = ArrayList<T>(elementCount) { updater.dataFactory() }
	val elements = ArrayList<UiComponent>(elementCount)
	val constructionTime = measureTime {
		repeat(elementCount) {
			val component = constructor.componentFactory(this)
			component.setPosition(rng.nextFloat(0f, WINDOW_WIDTH - 400f), rng.nextFloat(0f, WINDOW_HEIGHT - 200f))
			elements.add(addElement(component))
		}
		update() // Include the time for the very first update.
	}
	val updateCount = getUpdateCount(elementCount, percentUpdated)
	var disposed = false
	val tickHandle = onTick {
		check(!disposed)
		for (i in 0 until updateCount) {
			updater.updateMethod(elements[i], data[i])
		}
	}
	delay(TEST_DURATION)
	val totalDrawCalls = stage.totalDrawCalls
	val totalFrames = stage.totalFrames
	val updatePerformance = stage.updatePerformance.copy()
	val renderPerformance = stage.renderPerformance.copy()
	tickHandle.dispose()
	disposed = true
	clearElements(dispose = true)

	return PerformanceResults(
			elementCount,
			percentUpdated,
			constructor.name,
			updater.name,
			constructionTime.inNanoseconds,
			updatePerformance,
			renderPerformance,
			totalFrames,
			totalDrawCalls,
			TEST_DURATION.inNanoseconds
	)
}

fun getUpdateCount(elementCount: Int, percentUpdated: Float): Int {
	return maxOf(1, ceilInt(percentUpdated * elementCount))
}

data class Constructor(val name: String, val difficulty: Float, val componentFactory: Context.() -> UiComponent)
data class Updater<T>(val name: String, val difficulty: Float, val dataFactory: () -> T, val updateMethod: (c: UiComponent, data: T) -> Unit)

fun translation(component: UiComponent, data: Vector2) {
	component.x += data.x
	component.y += data.y
	if (data.x > 0f && component.right >= WINDOW_WIDTH ||
			data.x < 0f && component.x <= 0f) {
		data.x = -data.x
	}
	if (data.y > 0f && component.bottom >= WINDOW_HEIGHT ||
			data.y < 0f && component.y <= 0f) {
		data.y = -data.y
	}
}

fun rotation(component: UiComponent, data: Vector3) {
	component.rotation += data.x
	component.rotationX += data.y
	component.rotationY += data.z
}

fun resize(component: UiComponent, data: Vector2) {
	val w = (component.explicitWidth ?: component.width) + data.x * 10f
	val h = (component.explicitHeight ?: component.height) + data.y * 10f
	component.setSize(w, h)

	if (data.x > 0f && w >= MAX_SIZE ||
			data.x < 0f && w <= MIN_SIZE) {
		data.x = -data.x
	}
	if (data.y > 0f && h >= MAX_SIZE ||
			data.y < 0f && h <= MIN_SIZE) {
		data.y = -data.y
	}
}

const val WINDOW_WIDTH = 1200f
const val WINDOW_HEIGHT = 800f

const val MIN_SIZE = 100f
const val MAX_SIZE = 500f

val appConfig = AppConfig(window = WindowConfig(title = "Performance Runner", initialWidth = WINDOW_WIDTH, initialHeight = WINDOW_HEIGHT))

@Serializable
data class PerformanceResultsSuite(
		val name: String,
		val date: DateRo = Date(),
		val results: List<PerformanceResults>
) {

	fun toGridString(): String {
		val grid = ArrayList<List<Any>>()
		grid.add(listOf("NAME", "AVG CREATE", "AVG UPDATE", "AVG RENDER", "AVG FPS", "AVG DRAW COUNT"))
		for (result in results) {
			if (result.skipped)
				grid.add(listOf(result.name, "-", "-", "-", "-", "-"))
			else
				grid.add(listOf(result.name, result.constructionTimeAvg, result.updatePerformance.average, result.renderPerformance.average, result.fpsAvg, result.drawCallsAvg.roundToInt()))
		}
		return grid.toTabularString()
	}

	fun getComparisonStr(previous: PerformanceResultsSuite): String {
		val dF = dateTimeFormatter()
		var headingStr = "1) ${previous.name} ${dF.format(previous.date)} vs\n"
		headingStr += "2) $name ${dF.format(date)}\nLower percents are better for #2\n\n"
		val grid = ArrayList<List<Any>>()
		grid.add(listOf("NAME", "AVG CREATE", "AVG UPDATE", "AVG RENDER", "AVG FRAME TIME", "AVG DRAW COUNT"))
		for (result in results) {
			val otherResult = previous.results.firstOrNull { it.isSameTest(result) }
			if (otherResult == null)
				grid.add(listOf(result.name, "-", "-", "-", "-", "-"))
			else
				grid.add(listOf(
						result.name,
						compareStr(result.constructionTimeAvg, otherResult.constructionTimeAvg),
						compareStr(result.updatePerformance.average, otherResult.updatePerformance.average),
						compareStr(result.renderPerformance.average, otherResult.renderPerformance.average),
						pF.format(result.frameTimeAvg / otherResult.frameTimeAvg),
						compareStr(pF, result.drawCallsAvg, otherResult.drawCallsAvg)
				))
		}
		return headingStr + grid.toTabularString()
	}
}

private val pF = percentFormatter()
private val nF = numberFormatter() {
	maxFractionDigits = 2
}

private fun compareStr(resultA: Duration, resultB: Duration): String {
	val sign = if (resultA > resultB) "+" else ""
	return "${pF.format(resultA / resultB)} : $sign${resultA - resultB}"
}

private fun compareStr(pF: NumberFormatter, resultA: Double, resultB: Double): String {
	val sign = if (resultA > resultB) "+" else ""
	return "${pF.format(resultA / resultB)} : $sign${nF.format(resultA - resultB)}"
}

private fun List<List<Any>>.toTabularString(): String {
	if (isEmpty()) return ""
	val firstRow = first()
	val numTabs = IntArray(firstRow.size)
	for (col in 0..firstRow.lastIndex) {
		var max = 0
		for (row in 0..lastIndex) {
			max = maxOf(this[row][col].toString().length / 4, max)
		}
		numTabs[col] = max + 1
	}

	var str = ""
	for (row in 0..lastIndex) {
		for (col in 0..firstRow.lastIndex) {
			val cell = this[row][col].toString()
			str += "|\t$cell"
			str += "\t".repeat(numTabs[col] - cell.length / 4)
		}
		str += "|\n"
	}
	return str
}

@Serializable
data class PerformanceResults(
		val count: Int,
		val percentUpdated: Float,
		val constructorName: String,
		val updaterName: String,
		val constructionTime: Double,
		val updatePerformance: PerformanceMetrics,
		val renderPerformance: PerformanceMetrics,
		val totalFrames: Int = 0,
		val totalDrawCalls: Int = 0,
		val testDuration: Double
) {

	fun isSameTest(other: PerformanceResults): Boolean {
		return count == other.count &&
				percentUpdated == other.percentUpdated &&
				constructorName == other.constructorName &&
				updaterName == other.updaterName
	}

	val skipped: Boolean = testDuration == 0.0

	val constructionTimeAvg: Duration
		get() = constructionTime.nanoseconds / count

	val drawCallsAvg: Double
		get() = totalDrawCalls.toDouble() / totalFrames

	val fpsAvg: Double
		get() = totalFrames.toDouble() / testDuration.nanoseconds.inSeconds

	val frameTimeAvg: Double
		get() = testDuration.nanoseconds.inSeconds / totalFrames.toDouble()

	val name: String
		get() {
			return "${count}x${if (count < 100) " " else ""} $constructorName $updaterName ${getUpdateCount(count, percentUpdated)}"
		}
}
