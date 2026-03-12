package com.anix.android.anixstudyassist.feature.landing

import androidx.compose.runtime.Composable
import com.anix.android.anixstudyassist.core.nav.LandingScreenNavigations
import com.anix.android.anixstudyassist.feature.landing.ui.LandingScreen as LandingScreenContent

@Composable
fun LandingScreen(
    navigations: LandingScreenNavigations,
) {
    LandingScreenContent(
        onSubjectClick = navigations::onOpenClass,
        onAiModeClick = navigations.onOpenAiChat,
        onSettingsClick = navigations.onOpenSettings,
        onAddClick = { }
    )
}
