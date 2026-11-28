/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.oko.wallpaper

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import io.element.android.compound.colors.OkoPalette
import io.element.android.compound.colors.SemanticColorsLightDark
import io.element.android.compound.theme.ElementTheme
import io.element.android.compound.theme.Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class OkoWallpaperEmulatorTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun wallpaperStaysFixedWhileScrollingOnSmallHeap() {
        val activity = compose.activity
        val memory = checkNotNull(activity.getSystemService(ActivityManager::class.java))
        assertTrue("Expected a constrained app heap, got ${memory.memoryClass} MiB", memory.memoryClass <= 256)
        val palette = OkoPalette.branded(SemanticColorsLightDark.default)
        compose.setContent {
            ElementTheme(theme = Theme.Light, applySystemBarsUpdate = false, compoundLight = palette.light) {
                Box(Modifier.fillMaxSize().testTag("wallpaper-surface")) {
                    OkoTimelineBackground(enabled = true)
                    LazyColumn(Modifier.fillMaxSize().testTag("test-timeline")) {
                        items(120) { index ->
                            Spacer(
                                Modifier
                                    .height(56.dp)
                                    .fillMaxWidth(0.5f)
                                    .background(if (index % 2 == 0) Color(0xFFD9FDD3) else Color.White)
                                    .testTag("row-$index"),
                            )
                        }
                    }
                }
            }
        }
        compose.waitUntil(timeoutMillis = 12_000) { wallpaperPatch().distinct().size > 8 }
        val before = wallpaperPatch()
        saveScreenshot("low-memory-scroll-before.png")
        val timeline = compose.onNodeWithTag("test-timeline")
        timeline.performScrollToIndex(40)
        compose.onNodeWithTag("row-40").assertExists()
        repeat(4) { timeline.performTouchInput { swipeUp() } }
        assertEquals("The wallpaper must stay fixed when the timeline scrolls", before, wallpaperPatch())
        saveScreenshot("low-memory-scroll-after.png")
    }

    @Test
    fun captureNotificationIconFromFinalApk() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = compose.activity
        val packageName = "com.interatletika.oko.test"
        val appResources = context.packageManager.getResourcesForApplication(packageName)
        val iconId = appResources.getIdentifier("ic_notification", "drawable", packageName)
        assertNotEquals("The final APK must contain its OKO notification icon", 0, iconId)
        val manager = checkNotNull(context.getSystemService(NotificationManager::class.java))
        val channel = "oko-asset-proof"
        manager.createNotificationChannel(NotificationChannel(channel, "OKO test", NotificationManager.IMPORTANCE_DEFAULT))
        manager.notify(
            4,
            Notification.Builder(context, channel)
                .setSmallIcon(Icon.createWithResource(packageName, iconId))
                .setContentTitle("OKO test")
                .setContentText("Перевірка значка з готового APK")
                .build(),
        )
        val automation = instrumentation.uiAutomation
        try {
            automation.executeShellCommand("cmd statusbar expand-notifications").close()
            automation.waitForIdle(600, 5_000)
            saveScreenshot("notification-icon.png")
        } finally {
            automation.executeShellCommand("cmd statusbar collapse").close()
            manager.cancel(4)
        }
    }

    private fun wallpaperPatch(): List<Color> {
        val map = compose.onNodeWithTag("wallpaper-surface").captureToImage().toPixelMap()
        return (1..10).flatMap { y ->
            (6..9).map { x -> map[map.width * x / 10, map.height * y / 11] }
        }
    }

    private fun saveScreenshot(name: String) {
        val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
        val directory = checkNotNull(compose.activity.getExternalFilesDir(null))
        val bitmap = checkNotNull(automation.takeScreenshot())
        try {
            File(directory, name).outputStream().use { file ->
                assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, file))
            }
        } finally {
            bitmap.recycle()
        }
    }
}
