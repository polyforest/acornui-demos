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
@file:ContextualSerialization(Duration::class)

package com.acornui.performancetest


import com.acornui.recycle.Clearable
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.nanoseconds

@Serializable
data class PerformanceMetrics(
		var total: Double = 0.0,
		var min: Double = Double.MAX_VALUE,
		var max: Double = 0.0,
		var count: Int = 0
) : Clearable {

	val average: Duration
		get() = total.nanoseconds / count

	override fun clear() {
		total = 0.0
		min = Double.MAX_VALUE
		max = 0.0
		count = 0
	}
}

inline fun PerformanceMetrics.measure(enabled: Boolean = true, block: () -> Unit) {
	if (enabled) {
		val d = measureTime(block).inNanoseconds
		count++
		total += d
		if (d < min) min = d
		if (d > max) max = d
	} else {
		block()
	}
}