package com.anix.android.anixstudyassist.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AnixStudyAssistTheme(
    topAppBar: @Composable () -> Unit,
    content: @Composable (baseModifier: Modifier) -> Unit
) {
    ProvideAdaptiveUiInfo {
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
