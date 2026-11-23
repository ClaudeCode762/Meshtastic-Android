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
import android.view.Window

/**
 * Turns a rotary-encoder event (the Pixel Watch crown, a rotating bezel) into a mouse-wheel event at ([x], [y]).
 *
 * Compose scrollables take rotary input only while focused, which shared phone screens never request, but every one of
 * them takes the mouse wheel. Rotary `AXIS_SCROLL` is the opposite sign of the wheel's `AXIS_VSCROLL` (as RecyclerView
 * reads them), so clockwise scrolls content down. Returns null for any other event; the caller recycles the result.
 */
internal fun rotaryAsMouseScroll(event: MotionEvent, x: Float, y: Float): MotionEvent? {
    val isRotaryScroll =
        event.isFromSource(InputDevice.SOURCE_ROTARY_ENCODER) && event.actionMasked == MotionEvent.ACTION_SCROLL
    val scroll = if (isRotaryScroll) event.getAxisValue(MotionEvent.AXIS_SCROLL) else 0f
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

/**
 * Hands a rotary event nothing focused took to the window again as a wheel event at the screen's centre. The wheel
 * event passes through here too, and is not rotary, so it is never converted twice.
 */
internal class RotaryScrollWindowCallback(private val delegate: Window.Callback, private val window: Window) :
    Window.Callback by delegate {
    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (delegate.dispatchGenericMotionEvent(event)) return true
        val decor = window.decorView
        val wheel = rotaryAsMouseScroll(event, decor.width / 2f, decor.height / 2f)
        return wheel != null &&
            try {
                decor.dispatchGenericMotionEvent(wheel)
            } finally {
                wheel.recycle()
            }
    }
}
