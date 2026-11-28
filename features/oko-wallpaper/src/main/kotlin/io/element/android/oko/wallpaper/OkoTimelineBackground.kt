/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.oko.wallpaper

import android.app.ActivityManager
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import io.element.android.compound.theme.ElementTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val WALLPAPER = "oko/interatletika_khaos_chystyi_phone_1290x2796.png"
private val lightAccent = Color(0xFF008069)
private val wallpaperBase = Color(0xFFEFEAE2)

@Composable
fun BoxScope.OkoTimelineBackground(enabled: Boolean) {
    val colors = ElementTheme.colors
    if (!enabled || !colors.isLight || colors.bgAccentRest != lightAccent) return

    val context = LocalContext.current
    val assets = context.assets
    val lowRam = remember(context) { context.getSystemService(ActivityManager::class.java)?.isLowRamDevice == true }

    BoxWithConstraints(Modifier.matchParentSize().background(wallpaperBase)) {
        val smallViewport = with(LocalDensity.current) { maxWidth.roundToPx() <= 322 && maxHeight.roundToPx() <= 699 }
        // BitmapFactory downsamples during PNG decode. Even on a large viewport, no full-size bitmap is allocated.
        val sampleSize = if (lowRam || smallViewport) 4 else 2
        val bitmap = if (LocalInspectionMode.current) {
            remember(assets, sampleSize) { decodeWallpaper(assets, sampleSize) }
        } else {
            val loaded by produceState<ImageBitmap?>(null, assets, sampleSize) {
                value = withContext(Dispatchers.IO) { decodeWallpaper(assets, sampleSize) }
            }
            loaded
        }
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
            )
        }
    }
}

private fun decodeWallpaper(assets: AssetManager, sampleSize: Int): ImageBitmap? =
    assets.open(WALLPAPER).use { input ->
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        BitmapFactory.decodeStream(input, null, options)?.asImageBitmap()
    }
