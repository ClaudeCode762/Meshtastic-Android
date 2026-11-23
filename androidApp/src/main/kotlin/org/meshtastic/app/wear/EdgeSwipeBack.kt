/*
 * Copyright (c) 2026 Meshtastic LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.meshtastic.app.wear

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

/** Share of the width, from the left edge, a back swipe has to start in. */
private const val EDGE_FRACTION = 0.12f

/** Share of the width a back swipe has to travel. */
private const val TRIGGER_FRACTION = 0.25f

/** A back swipe has to be this many times more horizontal than vertical. */
private const val HORIZONTAL_DOMINANCE = 2f

/**
 * Wear OS's swipe from the left edge as back. The window's own swipe-to-dismiss is off on watches (values-watch), since
 * it finishes the activity instead of going back one screen; this watches the gesture in the initial pass, before any
 * child, and consumes it only once it is clearly a back swipe, so maps and horizontal lists starting further in keep
 * their drags.
 */
internal fun Modifier.edgeSwipeToBack(onBack: () -> Unit): Modifier = pointerInput(onBack) {
    val edge = size.width * EDGE_FRACTION
    val threshold = size.width * TRIGGER_FRACTION
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        if (down.position.x > edge) return@awaitEachGesture
        var triggered = false
        while (true) {
            val change =
                awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull { it.id == down.id } ?: break
            if (!change.pressed) {
                if (triggered) change.consume()
                break
            }
            val dx = change.position.x - down.position.x
            val dy = change.position.y - down.position.y
            if (!triggered && dx > threshold && dx > abs(dy) * HORIZONTAL_DOMINANCE) {
                triggered = true
                onBack()
            }
            if (triggered) change.consume()
        }
    }
}
