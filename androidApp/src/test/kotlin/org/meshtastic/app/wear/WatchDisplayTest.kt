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

import kotlin.test.Test
import kotlin.test.assertEquals

class WatchDisplayTest {

    @Test
    fun `a pixel watch sized screen is scaled toward the phone compact width`() {
        val display = WatchDisplay.of(shortestSideDp = 224f, isRound = true)
        assertEquals(0.8f, display.scale, 0.001f)
        assertEquals(WatchDisplay.ROUND_INSET_FRACTION, display.insetFraction)
    }

    @Test
    fun `a small watch is not scaled below the readable minimum`() {
        assertEquals(WatchDisplay.MIN_SCALE, WatchDisplay.of(shortestSideDp = 150f, isRound = true).scale)
    }

    @Test
    fun `a large screen is never scaled up`() {
        assertEquals(1f, WatchDisplay.of(shortestSideDp = 400f, isRound = false).scale)
    }

    @Test
    fun `a square watch keeps only a thin margin`() {
        assertEquals(WatchDisplay.SQUARE_INSET_FRACTION, WatchDisplay.of(220f, isRound = false).insetFraction)
    }
}
