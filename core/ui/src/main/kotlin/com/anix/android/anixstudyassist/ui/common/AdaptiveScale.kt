package com.anix.android.anixstudyassist.ui.common

import android.content.res.Configuration
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp

private const val DP_MIN_SCALE = 0.82f
private const val DP_MAX_SCALE = 1.18f
private const val SP_MIN_SCALE = 0.88f
private const val SP_MAX_SCALE = 1.12f

internal data class AdaptiveScaleFactors(
    val dpScale: Float,
    val spScale: Float
)

internal enum class ExtremeScaleCase {
    LARGE_ZOOM_AND_FONT,
    LARGE_TABLET_MIN_SCALE,
    POPUP_LIKE_COMPACT,
    NONE
}

@Composable
@ReadOnlyComposable
fun adaptiveDp(base: Dp): Dp {
    val scale = computeAdaptiveScaleFactors(AnixStudyAssistAdaptive.current).dpScale
    return base * scale
}

@Composable
@ReadOnlyComposable
fun adaptiveSp(base: TextUnit): TextUnit {
    if (base.type != TextUnitType.Sp) return base
    val scale = computeAdaptiveScaleFactors(AnixStudyAssistAdaptive.current).spScale
    return (base.value * scale).sp
}

internal fun computeAdaptiveScaleFactors(info: AdaptiveUiInfo): AdaptiveScaleFactors {
    val widthClassFactor = when (info.windowWidthSizeClass) {
        WindowWidthSizeClass.Compact -> 0.95f
        WindowWidthSizeClass.Medium -> 1.0f
        WindowWidthSizeClass.Expanded -> 1.05f
        else -> 1.0f
    }

    val smallestWidthFactor = when {
        info.smallestScreenWidthDp < 360 -> -0.05f
        info.smallestScreenWidthDp > 1000 -> 0.05f
        else -> 0f
    }

    val zoomCompensation = when {
        info.effectiveUiScale >= 4.0f -> -0.08f
        info.effectiveUiScale >= 3.5f -> -0.04f
        info.effectiveUiScale <= 2.2f -> 0.03f
        else -> 0f
    }

    val orientationFactor = if (
        info.orientation == Configuration.ORIENTATION_LANDSCAPE &&
        info.windowHeightSizeClass == WindowHeightSizeClass.Compact
    ) -0.03f else 0f

    val extremeCaseAdjustment = when (classifyScaleCase(info)) {
        ExtremeScaleCase.LARGE_ZOOM_AND_FONT -> -0.03f
        ExtremeScaleCase.LARGE_TABLET_MIN_SCALE -> 0.03f
        ExtremeScaleCase.POPUP_LIKE_COMPACT -> -0.02f
        ExtremeScaleCase.NONE -> 0f
    }

    val baseScale =
        widthClassFactor + smallestWidthFactor + zoomCompensation + orientationFactor + extremeCaseAdjustment
    val fontPreferenceAdjustment = if (info.fontScale > 1f) {
        (info.fontScale - 1f) * 0.08f
    } else {
        (info.fontScale - 1f) * 0.04f
    }

    val dpScale = baseScale.coerceIn(DP_MIN_SCALE, DP_MAX_SCALE)
    val spScale = (baseScale + fontPreferenceAdjustment).coerceIn(SP_MIN_SCALE, SP_MAX_SCALE)

    return AdaptiveScaleFactors(dpScale = dpScale, spScale = spScale)
}

internal fun classifyScaleCase(info: AdaptiveUiInfo): ExtremeScaleCase {
    val isLargeZoomAndFont = info.fontScale >= 1.25f && info.effectiveUiScale >= 3.5f
    if (isLargeZoomAndFont) return ExtremeScaleCase.LARGE_ZOOM_AND_FONT

    val isLargeTabletMinScale = info.smallestScreenWidthDp >= 840 &&
            info.fontScale <= 0.9f &&
            info.effectiveUiScale <= 2.4f
    if (isLargeTabletMinScale) return ExtremeScaleCase.LARGE_TABLET_MIN_SCALE

    val isPopupLikeCompact = info.windowWidthSizeClass == WindowWidthSizeClass.Compact &&
            info.smallestScreenWidthDp >= 600 &&
            info.screenWidthDp <= 520
    if (isPopupLikeCompact) return ExtremeScaleCase.POPUP_LIKE_COMPACT

    return ExtremeScaleCase.NONE
}
