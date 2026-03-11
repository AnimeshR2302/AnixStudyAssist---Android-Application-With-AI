package com.anix.android.anixstudyassist.core.ui

import android.content.res.Configuration
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveUiCoreTest {

    @Test
    fun width_size_class_fallback_mapping_is_correct() {
        assertEquals(WindowWidthSizeClass.Compact, widthSizeClassFromDp(599))
        assertEquals(WindowWidthSizeClass.Medium, widthSizeClassFromDp(600))
        assertEquals(WindowWidthSizeClass.Medium, widthSizeClassFromDp(839))
        assertEquals(WindowWidthSizeClass.Expanded, widthSizeClassFromDp(840))
    }

    @Test
    fun height_size_class_fallback_mapping_is_correct() {
        assertEquals(WindowHeightSizeClass.Compact, heightSizeClassFromDp(479))
        assertEquals(WindowHeightSizeClass.Medium, heightSizeClassFromDp(480))
        assertEquals(WindowHeightSizeClass.Medium, heightSizeClassFromDp(899))
        assertEquals(WindowHeightSizeClass.Expanded, heightSizeClassFromDp(900))
    }

    @Test
    fun scale_factors_are_clamped_for_large_zoom_and_font_case() {
        val info = testInfo(
            windowWidth = WindowWidthSizeClass.Compact,
            windowHeight = WindowHeightSizeClass.Compact,
            orientation = Configuration.ORIENTATION_PORTRAIT,
            screenWidthDp = 360,
            screenHeightDp = 700,
            smallestWidthDp = 360,
            density = 3.2f,
            densityDpi = 560,
            fontScale = 1.4f
        )

        val factors = computeAdaptiveScaleFactors(info)

        assertTrue(factors.dpScale in 0.82f..1.18f)
        assertTrue(factors.spScale in 0.88f..1.12f)
    }

    @Test
    fun popup_like_compact_case_is_detected() {
        val info = testInfo(
            windowWidth = WindowWidthSizeClass.Compact,
            windowHeight = WindowHeightSizeClass.Medium,
            orientation = Configuration.ORIENTATION_LANDSCAPE,
            screenWidthDp = 500,
            screenHeightDp = 420,
            smallestWidthDp = 700,
            density = 2.4f,
            densityDpi = 420,
            fontScale = 1.0f
        )

        assertEquals(ExtremeScaleCase.POPUP_LIKE_COMPACT, classifyScaleCase(info))
    }

    @Test
    fun large_tablet_min_scale_case_is_detected() {
        val info = testInfo(
            windowWidth = WindowWidthSizeClass.Expanded,
            windowHeight = WindowHeightSizeClass.Medium,
            orientation = Configuration.ORIENTATION_LANDSCAPE,
            screenWidthDp = 1280,
            screenHeightDp = 800,
            smallestWidthDp = 960,
            density = 2.0f,
            densityDpi = 320,
            fontScale = 0.85f
        )

        assertEquals(ExtremeScaleCase.LARGE_TABLET_MIN_SCALE, classifyScaleCase(info))
    }

    @Test
    fun line_height_matrix_returns_all_expected_values() {
        assertEquals(17, lineHeightFor(TextSizeToken.S11, TextWeightToken.Normal))
        assertEquals(21, lineHeightFor(TextSizeToken.S13, TextWeightToken.Bold))
        assertEquals(27, lineHeightFor(TextSizeToken.S17, TextWeightToken.SemiBold))
        assertEquals(34, lineHeightFor(TextSizeToken.S21, TextWeightToken.Bold))
    }

    @Test
    fun centered_content_rule_matches_expected_devices() {
        val compactPortraitPhone = testInfo(
            windowWidth = WindowWidthSizeClass.Compact,
            windowHeight = WindowHeightSizeClass.Medium,
            orientation = Configuration.ORIENTATION_PORTRAIT,
            screenWidthDp = 360,
            screenHeightDp = 760,
            smallestWidthDp = 360,
            density = 3.0f,
            densityDpi = 480,
            fontScale = 1f
        )
        val compactLandscapePhone = compactPortraitPhone.copy(
            orientation = Configuration.ORIENTATION_LANDSCAPE,
            screenWidthDp = 760,
            screenHeightDp = 360
        )
        val tabletPortrait = compactPortraitPhone.copy(
            screenWidthDp = 800,
            screenHeightDp = 1280,
            smallestScreenWidthDp = 800
        )

        assertEquals(false, shouldUseCenteredContent(compactPortraitPhone))
        assertEquals(true, shouldUseCenteredContent(compactLandscapePhone))
        assertEquals(true, shouldUseCenteredContent(tabletPortrait))
    }

    @Test
    fun content_width_fraction_is_86_percent_only_for_centered_layout() {
        val phonePortrait = testInfo(
            windowWidth = WindowWidthSizeClass.Compact,
            windowHeight = WindowHeightSizeClass.Medium,
            orientation = Configuration.ORIENTATION_PORTRAIT,
            screenWidthDp = 412,
            screenHeightDp = 915,
            smallestWidthDp = 412,
            density = 2.75f,
            densityDpi = 440,
            fontScale = 1f
        )
        val phoneLandscape = phonePortrait.copy(
            orientation = Configuration.ORIENTATION_LANDSCAPE,
            screenWidthDp = 915,
            screenHeightDp = 412
        )

        assertEquals(1f, resolveContentWidthFraction(phonePortrait))
        assertEquals(0.86f, resolveContentWidthFraction(phoneLandscape))
    }

    private fun testInfo(
        windowWidth: WindowWidthSizeClass,
        windowHeight: WindowHeightSizeClass,
        orientation: Int,
        screenWidthDp: Int,
        screenHeightDp: Int,
        smallestWidthDp: Int,
        density: Float,
        densityDpi: Int,
        fontScale: Float
    ): AdaptiveUiInfo {
        return AdaptiveUiInfo(
            windowWidthSizeClass = windowWidth,
            windowHeightSizeClass = windowHeight,
            orientation = orientation,
            screenWidthDp = screenWidthDp,
            screenHeightDp = screenHeightDp,
            smallestScreenWidthDp = smallestWidthDp,
            density = density,
            densityDpi = densityDpi,
            fontScale = fontScale,
            effectiveUiScale = density * fontScale
        )
    }
}
