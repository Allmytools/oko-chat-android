/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.features.enterprise.impl

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.google.common.truth.Truth.assertThat
import io.element.android.compound.colors.OkoPalette
import io.element.android.compound.colors.SemanticColorsLightDark
import org.junit.Test

class OkoPaletteTest {
    private val defaults = SemanticColorsLightDark.default
    private val brand = OkoPalette.branded(defaults)

    @Test
    fun textAndIconsMeetContrastTargetsInBothThemes() {
        for (colors in listOf(brand.light, brand.dark)) {
            assertThat(contrast(colors.textActionAccent, colors.bgCanvasDefault)).isAtLeast(4.5f)
            assertThat(contrast(colors.textOnSolidPrimary, colors.bgAccentRest)).isAtLeast(4.5f)
            assertThat(contrast(colors.textOnSolidPrimary, colors.bgActionPrimaryRest)).isAtLeast(4.5f)
            assertThat(contrast(colors.iconAccentPrimary, colors.bgCanvasDefault)).isAtLeast(3f)
            assertThat(contrast(colors.borderAccentPrimary, colors.bgCanvasDefault)).isAtLeast(3f)
            for (fromMe in listOf(true, false)) {
                val bubble = checkNotNull(OkoPalette.bubbleBackground(colors, fromMe))
                assertThat(contrast(colors.textSecondary, bubble)).isAtLeast(4.5f)
            }
        }
    }

    @Test
    fun upstreamPreviewPalettesKeepTheirOriginalBubbleAliases() {
        assertThat(OkoPalette.bubbleBackground(defaults.light, fromMe = true)).isNull()
        assertThat(OkoPalette.bubbleBackground(defaults.dark, fromMe = false)).isNull()
    }

    private fun contrast(first: Color, second: Color): Float {
        val a = first.luminance()
        val b = second.luminance()
        return (maxOf(a, b) + 0.05f) / (minOf(a, b) + 0.05f)
    }
}
