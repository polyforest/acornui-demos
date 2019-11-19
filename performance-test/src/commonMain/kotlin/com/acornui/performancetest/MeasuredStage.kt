package com.acornui.performancetest

import com.acornui.component.Stage
import com.acornui.gl.core.GlState

/**
 * A wrapper to Stage that measures performance.
 */
class MeasuredStage(private val stage: Stage) : Stage by stage {

	private val glState by GlState

	var totalFrames = 0
	var totalDrawCalls = 0
	val updatePerformance = PerformanceMetrics()
	val renderPerformance = PerformanceMetrics()

	override fun update() {
		updatePerformance.measure {
			stage.update()
		}
	}

	override fun render() {
		renderPerformance.measure {
			stage.render()
		}
		totalFrames++
		totalDrawCalls += glState.batch.renderCount
	}

	fun clearPerformanceMetrics() {
		totalFrames = 0
		totalDrawCalls = 0
		updatePerformance.clear()
		renderPerformance.clear()
	}
}