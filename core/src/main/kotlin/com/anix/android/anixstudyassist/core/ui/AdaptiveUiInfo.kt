package com.anix.android.anixstudyassist.core.ui

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

@Stable
data class AdaptiveUiInfo(
    val windowWidthSizeClass: WindowWidthSizeClass,
    val windowHeightSizeClass: WindowHeightSizeClass,
    val orientation: Int,
    val screenWidthDp: Int,
    val screenHeightDp: Int,
    val smallestScreenWidthDp: Int,
    val density: Float,
    val densityDpi: Int,
    val fontScale: Float,
    val effectiveUiScale: Float
)

val LocalAdaptiveUiInfo = compositionLocalOf<AdaptiveUiInfo> {
    error("AdaptiveUiInfo was not provided. Wrap content in AnixStudyAssistTheme.")
}

object AnixStudyAssistAdaptive {
    val current: AdaptiveUiInfo
        @Composable
        @ReadOnlyComposable
        get() = LocalAdaptiveUiInfo.current
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
internal fun rememberAdaptiveUiInfo(): AdaptiveUiInfo {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val context = LocalContext.current

    val activity = context.findActivityOrNull()
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }

    val widthSizeClass = windowSizeClass?.widthSizeClass
        ?: widthSizeClassFromDp(configuration.screenWidthDp)
    val heightSizeClass = windowSizeClass?.heightSizeClass
        ?: heightSizeClassFromDp(configuration.screenHeightDp)

    return remember(
        widthSizeClass,
        heightSizeClass,
        configuration.orientation,
        configuration.screenWidthDp,
        configuration.screenHeightDp,
        configuration.smallestScreenWidthDp,
        configuration.densityDpi,
        density.density,
        density.fontScale
    ) {
        AdaptiveUiInfo(
            windowWidthSizeClass = widthSizeClass,
            windowHeightSizeClass = heightSizeClass,
            orientation = configuration.orientation,
            screenWidthDp = configuration.screenWidthDp,
            screenHeightDp = configuration.screenHeightDp,
            smallestScreenWidthDp = configuration.smallestScreenWidthDp,
            density = density.density,
            densityDpi = configuration.densityDpi,
            fontScale = density.fontScale,
            effectiveUiScale = density.density * density.fontScale
        )
    }
}

internal fun widthSizeClassFromDp(widthDp: Int): WindowWidthSizeClass = when {
    widthDp < 600 -> WindowWidthSizeClass.Compact
    widthDp < 840 -> WindowWidthSizeClass.Medium
    else -> WindowWidthSizeClass.Expanded
}

internal fun heightSizeClassFromDp(heightDp: Int): WindowHeightSizeClass = when {
    heightDp < 480 -> WindowHeightSizeClass.Compact
    heightDp < 900 -> WindowHeightSizeClass.Medium
    else -> WindowHeightSizeClass.Expanded
}

private tailrec fun Context.findActivityOrNull(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivityOrNull()
    else -> null
}
