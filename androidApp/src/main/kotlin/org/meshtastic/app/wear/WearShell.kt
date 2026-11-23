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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import org.meshtastic.core.ui.util.LocalWatchMode

/**
 * Fits the whole app onto a watch. On any other device it is just [content].
 *
 * On a watch the content is drawn at the [WatchDisplay] scale inside a margin that keeps it off a round screen's
 * clipped edges, [LocalWatchMode] switches shared UI to its watch layouts, and a swipe from the left edge goes back.
 * Every screen, dialog and sheet still composes; nothing is hidden.
 */
@Composable
internal fun WearShell(isWatch: Boolean, onBack: () -> Unit, content: @Composable () -> Unit) {
    if (!isWatch) {
        content()
        return
    }
    val isRound = LocalConfiguration.current.isScreenRound
    val density = LocalDensity.current
    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black).edgeSwipeToBack(onBack)) {
        val shortestSide = minOf(maxWidth, maxHeight)
        val display = remember(shortestSide, isRound) { WatchDisplay.of(shortestSide.value, isRound) }
        Box(Modifier.fillMaxSize().padding(shortestSide * display.insetFraction)) {
            CompositionLocalProvider(
                LocalDensity provides Density(density.density * display.scale, density.fontScale),
                LocalWatchMode provides true,
                content = content,
            )
        }
    }
}
