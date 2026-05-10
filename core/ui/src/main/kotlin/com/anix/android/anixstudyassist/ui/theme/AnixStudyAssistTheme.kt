package com.anix.android.anixstudyassist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.anix.android.anixstudyassist.ui.common.AnixStudyAssistBaseLayout
import com.anix.android.anixstudyassist.ui.common.LocalAdaptiveUiInfo
import com.anix.android.anixstudyassist.ui.common.rememberAdaptiveUiInfo

@Composable
fun AnixStudyAssistTheme(
    topAppBar: @Composable () -> Unit,
    content: @Composable (baseModifier: Modifier) -> Unit
) {
    val adaptiveUiInfo = rememberAdaptiveUiInfo()

    CompositionLocalProvider(
        LocalAdaptiveUiInfo provides adaptiveUiInfo,
        LocalAdaptiveDimens provides rememberAdaptiveDimens(adaptiveUiInfo),
        LocalAppColors provides rememberAppColors()
    ) {
        MaterialTheme(
            colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
            typography = anixStudyAssistTypography()
        ) {
            AnixStudyAssistBaseLayout(
                topAppBar = topAppBar,
                content = content
            )
        }
    }
}
