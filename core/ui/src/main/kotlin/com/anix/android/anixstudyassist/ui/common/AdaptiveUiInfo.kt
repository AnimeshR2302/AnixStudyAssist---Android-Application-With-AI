package com.anix.android.anixstudyassist.ui.common

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo

object AnixAdaptiveUiInfo {
    val current: AdaptiveUiInfo
        @Composable
        @ReadOnlyComposable
        get() = LocalAdaptiveUiInfo.current
}

@Stable
data class AdaptiveUiInfo(
    val windowWidthSizeClass: WindowWidthSizeClass,
    val windowHeightSizeClass: WindowHeightSizeClass,
    val screenWidthDp: Int,
    val screenHeightDp: Int,
    val orientation: Int,
    val smallestScreenWidthDp: Int,
    val density: Float,
    val densityDpi: Int,
    val fontScale: Float,
    val effectiveUiScale: Float
)

val LocalAdaptiveUiInfo = compositionLocalOf<AdaptiveUiInfo> {
    error("AdaptiveUiInfo was not provided. Wrap content in AnixStudyAssistTheme.")
}

@Composable
internal fun ProvideAdaptiveUiInfo(
    content: @Composable () -> Unit
) {
    val adaptiveUiInfo = rememberAdaptiveUiInfo()
    CompositionLocalProvider(LocalAdaptiveUiInfo provides adaptiveUiInfo) {
        content()
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAdaptiveUiInfo(): AdaptiveUiInfo {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val context = LocalContext.current
    val localContainerSize = LocalWindowInfo.current.containerSize

    val activity = context.findActivityOrNull()
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }

    val widthDp = with(density) { localContainerSize.width.toDp().value.toInt() }
    val heightDp = with(density) { localContainerSize.height.toDp().value.toInt() }

    val widthSizeClass = windowSizeClass?.widthSizeClass
        ?: widthSizeClassFromDp(widthDp)
    val heightSizeClass = windowSizeClass?.heightSizeClass
        ?: heightSizeClassFromDp(heightDp)

    return remember(
        widthSizeClass,
        heightSizeClass,
        widthDp,
        heightDp,
        configuration.orientation,
        configuration.smallestScreenWidthDp,
        configuration.densityDpi,
        density.density,
        density.fontScale
    ) {
        AdaptiveUiInfo(
            windowWidthSizeClass = widthSizeClass,
            windowHeightSizeClass = heightSizeClass,
            screenWidthDp = widthDp,
            screenHeightDp = heightDp,
            orientation = configuration.orientation,
            smallestScreenWidthDp = configuration.smallestScreenWidthDp,
            density = density.density,
            densityDpi = configuration.densityDpi,
            fontScale = density.fontScale,
            effectiveUiScale = density.density * density.fontScale
        )
    }
}

private fun widthSizeClassFromDp(widthDp: Int): WindowWidthSizeClass = when {
    widthDp < 600 -> WindowWidthSizeClass.Compact
    widthDp < 840 -> WindowWidthSizeClass.Medium
    else -> WindowWidthSizeClass.Expanded
}

private fun heightSizeClassFromDp(heightDp: Int): WindowHeightSizeClass = when {
    heightDp < 480 -> WindowHeightSizeClass.Compact
    heightDp < 900 -> WindowHeightSizeClass.Medium
    else -> WindowHeightSizeClass.Expanded
}

private tailrec fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}
