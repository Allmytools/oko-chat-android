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
        }
    }

    @Test
    fun approvedBubblesKeepReadableTextAndLinks() {
        val lightMine = checkNotNull(OkoPalette.bubbleBackground(brand.light, fromMe = true))
        val darkMine = checkNotNull(OkoPalette.bubbleBackground(brand.dark, fromMe = true))
        val darkOther = checkNotNull(OkoPalette.bubbleBackground(brand.dark, fromMe = false))
        assertThat(lightMine).isEqualTo(Color(0xFFD9FDD3))
        assertThat(OkoPalette.bubbleBackground(brand.light, fromMe = false)).isNull()
        assertThat(darkMine).isEqualTo(Color(0xFF173A2C))
        assertThat(darkOther).isEqualTo(Color(0xFF21282C))
        for ((colors, bubble) in listOf(brand.light to lightMine, brand.dark to darkMine, brand.dark to darkOther)) {
            assertThat(contrast(colors.textPrimary, bubble)).isAtLeast(4.5f)
            assertThat(contrast(colors.textLinkExternal, bubble)).isAtLeast(4.5f)
        }
        assertThat(contrast(brand.light.textSecondary, lightMine)).isAtLeast(4.5f)
        // Keep the approved bubble colors and the existing Element timestamp token.
        assertThat(contrast(brand.dark.textSecondary, darkMine)).isWithin(0.01f).of(3.53f)
        assertThat(contrast(brand.dark.textSecondary, darkOther)).isWithin(0.01f).of(4.22f)
    }

    @Test
    fun unapprovedPaletteTokensKeepTheElementValues() {
        for ((original, branded) in listOf(defaults.light to brand.light, defaults.dark to brand.dark)) {
            assertThat(branded.bgAccentSelected).isEqualTo(original.bgAccentSelected)
            assertThat(branded.bgActionPrimaryDisabled).isEqualTo(original.bgActionPrimaryDisabled)
            assertThat(branded.textLinkExternal).isEqualTo(original.textLinkExternal)
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
