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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class RotaryScrollTest {

    private fun event(source: Int, action: Int = MotionEvent.ACTION_SCROLL, scroll: Float = 1.5f): MotionEvent {
        val properties = MotionEvent.PointerProperties().apply { id = 0 }
        val coords = MotionEvent.PointerCoords().apply { setAxisValue(MotionEvent.AXIS_SCROLL, scroll) }
        return MotionEvent.obtain(
            0L,
            10L,
            action,
            1,
            arrayOf(properties),
            arrayOf(coords),
            0,
            0,
            1f,
            1f,
            3,
            0,
            source,
            0,
        )
    }

    @Test
    fun `crown rotation becomes a mouse wheel scroll at the given point`() {
        val wheel = assertNotNull(rotaryAsMouseScroll(event(InputDevice.SOURCE_ROTARY_ENCODER), 100f, 120f))
        assertTrue(wheel.isFromSource(InputDevice.SOURCE_MOUSE))
        assertEquals(MotionEvent.ACTION_SCROLL, wheel.actionMasked)
        assertEquals(MotionEvent.TOOL_TYPE_MOUSE, wheel.getToolType(0))
        assertEquals(100f, wheel.x)
        assertEquals(120f, wheel.y)
        // Clockwise (positive AXIS_SCROLL) scrolls content down, which is a negative wheel delta.
        assertEquals(-1.5f, wheel.getAxisValue(MotionEvent.AXIS_VSCROLL))
    }

    @Test
    fun `other sources, other actions and zero deltas are left alone`() {
        assertNull(rotaryAsMouseScroll(event(InputDevice.SOURCE_MOUSE), 0f, 0f))
        assertNull(rotaryAsMouseScroll(event(InputDevice.SOURCE_ROTARY_ENCODER, MotionEvent.ACTION_MOVE), 0f, 0f))
        assertNull(rotaryAsMouseScroll(event(InputDevice.SOURCE_ROTARY_ENCODER, scroll = 0f), 0f, 0f))
    }
}
