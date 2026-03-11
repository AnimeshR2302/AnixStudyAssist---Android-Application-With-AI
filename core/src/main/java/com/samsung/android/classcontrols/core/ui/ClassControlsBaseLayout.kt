package com.anix.android.anixstudyassist.core.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnixStudyAssistBaseLayout(
    topAppBar: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val adaptiveInfo = AnixStudyAssistAdaptive.current
    val isCenteredLayout = shouldUseCenteredContent(adaptiveInfo)
    val contentWidthFraction = resolveContentWidthFraction(adaptiveInfo)
    val horizontalPadding = if (isCenteredLayout) adaptiveDp(24.dp) else adaptiveDp(16.dp)
    val verticalPadding = adaptiveDp(16.dp)

    val dimens = AnixStudyAssistDimens(
        contentWidthFraction = contentWidthFraction,
        isCenteredLayout = isCenteredLayout,
        screenHorizontalPadding = horizontalPadding,
        screenVerticalPadding = verticalPadding,
        xs = adaptiveDp(4.dp),
        sm = adaptiveDp(8.dp),
        md = adaptiveDp(16.dp),
        lg = adaptiveDp(24.dp)
    )

    Scaffold(
        topBar = topAppBar
    ) { innerPadding ->
        CompositionLocalProvider(LocalAnixStudyAssistDimens provides dimens) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                content(
                    Modifier
                        .fillMaxSize()
                        .fillMaxWidth(dimens.contentWidthFraction)
                        .padding(
                            horizontal = dimens.screenHorizontalPadding,
                            vertical = dimens.screenVerticalPadding
                        )
                )
            }
        }
    }
}

@Stable
data class AnixStudyAssistDimens(
    val contentWidthFraction: Float,
    val isCenteredLayout: Boolean,
    val screenHorizontalPadding: Dp,
    val screenVerticalPadding: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp
)

val LocalAnixStudyAssistDimens = compositionLocalOf<AnixStudyAssistDimens> {
    error("AnixStudyAssistDimens was not provided. Wrap content in AnixStudyAssistTheme.")
}

object AnixStudyAssistLayout {
    val dimens: AnixStudyAssistDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalAnixStudyAssistDimens.current
}

internal fun shouldUseCenteredContent(info: AdaptiveUiInfo): Boolean {
    val isTablet = info.smallestScreenWidthDp >= 600
    val isLandscape = info.orientation == Configuration.ORIENTATION_LANDSCAPE
    return isTablet || isLandscape
}

internal fun resolveContentWidthFraction(info: AdaptiveUiInfo): Float {
    return if (shouldUseCenteredContent(info)) 0.86f else 1f
}
