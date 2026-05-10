package com.anix.android.anixstudyassist.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anix.android.anixstudyassist.ui.theme.AdaptiveDimens
import com.anix.android.anixstudyassist.ui.theme.AppColors
import com.anix.android.anixstudyassist.ui.theme.LocalAdaptiveDimens
import com.anix.android.anixstudyassist.ui.theme.LocalAppColors
import com.anix.android.anixstudyassist.ui.theme.rememberAdaptiveDimens
import com.anix.android.anixstudyassist.ui.theme.rememberAppColors

@Composable
fun AnixStudyAssistBaseLayout(
    adaptiveUiInfo: AdaptiveUiInfo = rememberAdaptiveUiInfo(),
    dimens: AdaptiveDimens = rememberAdaptiveDimens(adaptiveUiInfo),
    colors: AppColors = rememberAppColors(),
    topAppBar: @Composable () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    CompositionLocalProvider(
        LocalAdaptiveUiInfo provides adaptiveUiInfo,
        LocalAdaptiveDimens provides dimens,
        LocalAppColors provides colors
    ) {
        Scaffold(topBar = topAppBar) { innerPadding ->
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

