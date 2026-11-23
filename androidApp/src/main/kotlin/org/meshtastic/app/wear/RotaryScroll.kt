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

import android.view.InputDevice
import android.view.MotionEvent

/**
 * Turns a rotary-encoder event (the Pixel Watch crown, a rotating bezel) into a mouse-wheel event at ([x], [y]).
 *
 * Compose scrollables take rotary input only while focused, which shared phone screens never request, but every one of
 * them takes the mouse wheel. Rotary `AXIS_SCROLL` is the opposite sign of the wheel's `AXIS_VSCROLL` (as RecyclerView
 * reads them), so clockwise scrolls content down. Returns null for any other event; the caller recycles the result.
 */
internal fun rotaryAsMouseScroll(event: MotionEvent, x: Float, y: Float): MotionEvent? {
    if (!event.isFromSource(InputDevice.SOURCE_ROTARY_ENCODER)) return null
    if (event.actionMasked != MotionEvent.ACTION_SCROLL) return null
    val scroll = event.getAxisValue(MotionEvent.AXIS_SCROLL)
    if (scroll == 0f) return null
    val properties =
        MotionEvent.PointerProperties().apply {
            id = 0
            toolType = MotionEvent.TOOL_TYPE_MOUSE
        }
    val coords =
        MotionEvent.PointerCoords().apply {
            this.x = x
            this.y = y
            setAxisValue(MotionEvent.AXIS_VSCROLL, -scroll)
        }
    return MotionEvent.obtain(
        event.downTime,
        event.eventTime,
        MotionEvent.ACTION_SCROLL,
        1,
        arrayOf(properties),
        arrayOf(coords),
        0,
        0,
        1f,
        1f,
        event.deviceId,
        0,
        InputDevice.SOURCE_MOUSE,
        0,
    )
}
