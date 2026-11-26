/*
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package io.element.android.compound.colors

import androidx.compose.ui.graphics.Color
import io.element.android.compound.tokens.generated.SemanticColors

/** TEMP colors from branding/oko/brand.json. */
object OkoPalette {
    private val lightAccent = Color(0xFF008069)
    private val darkAccent = Color(0xFF21C063)

    fun branded(upstream: SemanticColorsLightDark) = SemanticColorsLightDark(
        light = upstream.light.copy(
            bgAccentRest = lightAccent,
            bgAccentHovered = Color(0xFF006E5A),
            bgAccentPressed = Color(0xFF005C4A),
            bgAccentSelected = Color(0x1F008069),
            bgAccentSubtle = Color(0xFFEFFAF5),
            bgBadgeAccent = Color(0xFFCAEFDE),
            textBadgeAccent = Color(0xFF005C4A),
            borderAccentPrimary = Color(0xFF008069),
            borderAccentSubtle = Color(0xFF34937D),
            gradientActionStop1 = Color(0xFFA7E4CA),
            gradientActionStop2 = Color(0xFF5BC59C),
            gradientActionStop3 = Color(0xFF008069),
            gradientActionStop4 = Color(0xFF005C4A),
            iconAccentPrimary = Color(0xFF008069),
            iconAccentTertiary = Color(0xFF298876),
            textActionAccent = Color(0xFF008069),
            bgActionPrimaryRest = Color(0xFF008069),
            bgActionPrimaryHovered = Color(0xFF006E5A),
            bgActionPrimaryPressed = Color(0xFF005C4A),
            bgActionPrimaryDisabled = Color(0xFFA6ADB7),
        ),
        dark = upstream.dark.copy(
            bgAccentRest = darkAccent,
            bgAccentHovered = Color(0xFF44CF7D),
            bgAccentPressed = Color(0xFF61DB91),
            bgAccentSelected = Color(0x3321C063),
            bgAccentSubtle = Color(0xFF09251A),
            bgBadgeAccent = Color(0xFF153B2B),
            textBadgeAccent = Color(0xFF61DB91),
            borderAccentPrimary = Color(0xFF21C063),
            borderAccentSubtle = Color(0xFF34966A),
            gradientActionStop1 = Color(0xFF61DB91),
            gradientActionStop2 = Color(0xFF34CA79),
            gradientActionStop3 = Color(0xFF149859),
            gradientActionStop4 = Color(0xFF125738),
            iconAccentPrimary = Color(0xFF21C063),
            iconAccentTertiary = Color(0xFF3A9C6D),
            textActionAccent = Color(0xFF21C063),
            bgActionPrimaryRest = Color(0xFF21C063),
            bgActionPrimaryHovered = Color(0xFF44CF7D),
            bgActionPrimaryPressed = Color(0xFF61DB91),
            bgActionPrimaryDisabled = Color(0xFF4A4F55),
        ),
    )

    // Preserve upstream previews and screenshot baselines, which use the original theme.
    fun bubbleBackground(colors: SemanticColors, fromMe: Boolean): Color? {
        if (colors.bgAccentRest != (if (colors.isLight) lightAccent else darkAccent)) return null
        return if (colors.isLight) {
            if (fromMe) Color(0xFFECF8F2) else Color(0xFFF2F5F6)
        } else {
            if (fromMe) Color(0xFF0A1A14) else Color(0xFF171B20)
        }
    }
}
