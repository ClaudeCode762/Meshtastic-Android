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

/**
 * How the phone UI is fitted onto a watch face. The watch runs the full app, so nothing is dropped; the UI is drawn at
 * a smaller [scale] so a phone-sized compact layout fits, and kept [insetFraction] of the screen away from each edge so
 * a round display does not clip toolbars, buttons and the navigation bar.
 */
internal data class WatchDisplay(val scale: Float, val insetFraction: Float) {
    companion object {
        /** The logical width, in dp, the compact phone layouts are laid out against on a watch. */
        const val TARGET_WIDTH_DP = 280f

        /** Below this, text gets too small to read on a wrist. */
        const val MIN_SCALE = 0.7f

        /** Keeps the content rectangle's corners close to the circle; rounded components hide the remainder. */
        const val ROUND_INSET_FRACTION = 0.12f

        const val SQUARE_INSET_FRACTION = 0.02f

        fun of(shortestSideDp: Float, isRound: Boolean): WatchDisplay = WatchDisplay(
            scale = (shortestSideDp / TARGET_WIDTH_DP).coerceIn(MIN_SCALE, 1f),
            insetFraction = if (isRound) ROUND_INSET_FRACTION else SQUARE_INSET_FRACTION,
        )
    }
}
