package com.anix.android.anixstudyassist.ui.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anix.android.anixstudyassist.ui.common.AdaptiveUiInfo
import com.anix.android.anixstudyassist.ui.common.computeAdaptiveScaleFactors

object AnixDimens {
    val dimens: AdaptiveDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalAdaptiveDimens.current
}

@Stable
data class AdaptiveDimens(
    val contentWidthFraction: Float,
    val isCenteredLayout: Boolean,
    val screenHorizontalPadding: Dp,
    val screenVerticalPadding: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp
) {
    companion object {
        /**
         * Creates an [AdaptiveDimens] instance based on the provided [AdaptiveUiInfo].
         * Centralizing this logic ensures that adaptive calculations (scaling, padding, layout strategy)
         * are performed consistently and efficiently in a single pass.
         */
        fun fromAdaptiveInfo(info: AdaptiveUiInfo): AdaptiveDimens {
            val isCentered = info.smallestScreenWidthDp >= 600 ||
                    info.orientation == Configuration.ORIENTATION_LANDSCAPE

            val scale = computeAdaptiveScaleFactors(info).dpScale

            return AdaptiveDimens(
                contentWidthFraction = if (isCentered) 0.86f else 1f,
                isCenteredLayout = isCentered,
                screenHorizontalPadding = (if (isCentered) 24 else 16).dp * scale,
                screenVerticalPadding = 16.dp * scale,
                xs = 4.dp * scale,
                sm = 8.dp * scale,
                md = 16.dp * scale,
                lg = 24.dp * scale
            )
        }
    }
}

val LocalAdaptiveDimens = compositionLocalOf<AdaptiveDimens> {
    error("AnixStudyAssistDimens was not provided. Wrap content in AnixStudyAssistTheme.")
}

@Composable
fun rememberAdaptiveDimens(adaptiveInfo: AdaptiveUiInfo): AdaptiveDimens {
    return remember(adaptiveInfo) {
        AdaptiveDimens.fromAdaptiveInfo(adaptiveInfo)
    }
}
