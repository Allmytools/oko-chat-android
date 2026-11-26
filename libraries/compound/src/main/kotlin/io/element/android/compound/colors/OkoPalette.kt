/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.compound.colors

import androidx.compose.ui.graphics.Color
import io.element.android.compound.tokens.generated.SemanticColors

/** Approved colors from branding/oko/paket_android/brand.json. */
object OkoPalette {
    private val lightAccent = Color(0xFF008069)
    private val darkAccent = Color(0xFF21C063)

    fun branded(upstream: SemanticColorsLightDark) = SemanticColorsLightDark(
        light = upstream.light.copy(
            bgAccentRest = lightAccent,
            bgAccentHovered = Color(0xFF00715D),
            bgAccentPressed = Color(0xFF006352),
            bgAccentSubtle = Color(0xFFEEF8F5),
            bgBadgeAccent = Color(0xFFC9EDE4),
            textBadgeAccent = Color(0xFF005C4B),
            borderAccentPrimary = Color(0xFF008069),
            borderAccentSubtle = Color(0xFF2FB394),
            gradientActionStop1 = Color(0xFF8FD6C4),
            gradientActionStop2 = Color(0xFF2E9E86),
            gradientActionStop3 = Color(0xFF008069),
            gradientActionStop4 = Color(0xFF005C4B),
            iconAccentPrimary = Color(0xFF008069),
            iconAccentTertiary = Color(0xFF009B7E),
            textActionAccent = Color(0xFF008069),
            bgActionPrimaryRest = Color(0xFF008069),
            bgActionPrimaryHovered = Color(0xFF00715D),
            bgActionPrimaryPressed = Color(0xFF006352),
        ),
        dark = upstream.dark.copy(
            bgAccentRest = darkAccent,
            bgAccentHovered = Color(0xFF3CCB78),
            bgAccentPressed = Color(0xFF5BD48D),
            bgAccentSubtle = Color(0xFF04200F),
            bgBadgeAccent = Color(0xFF0F3A22),
            textBadgeAccent = Color(0xFF5BD48D),
            borderAccentPrimary = Color(0xFF21C063),
            borderAccentSubtle = Color(0xFF146B3A),
            gradientActionStop1 = Color(0xFF5BD48D),
            gradientActionStop2 = Color(0xFF21C063),
            gradientActionStop3 = Color(0xFF168A46),
            gradientActionStop4 = Color(0xFF0B4A26),
            iconAccentPrimary = Color(0xFF21C063),
            iconAccentTertiary = Color(0xFF1A9A4F),
            textActionAccent = Color(0xFF21C063),
            bgActionPrimaryRest = Color(0xFF21C063),
            bgActionPrimaryHovered = Color(0xFF3CCB78),
            bgActionPrimaryPressed = Color(0xFF5BD48D),
        ),
    )

    // Preserve upstream previews and screenshot baselines, which use the original theme.
    fun bubbleBackground(colors: SemanticColors, fromMe: Boolean): Color? {
        if (colors.bgAccentRest != (if (colors.isLight) lightAccent else darkAccent)) return null
        return if (colors.isLight) {
            if (fromMe) Color(0xFFD9FDD3) else null
        } else {
            if (fromMe) Color(0xFF173A2C) else Color(0xFF21282C)
        }
    }
}
