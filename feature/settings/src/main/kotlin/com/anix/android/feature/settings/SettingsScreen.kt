package com.anix.android.anixstudyassist.feature.settings

import androidx.compose.runtime.Composable
import com.anix.android.anixstudyassist.core.nav.SettingsScreenNavigations
import com.anix.android.anixstudyassist.feature.settings.ui.SettingsScreen as SettingsScreenContent

@Composable
fun SettingsScreen(
    navigations: SettingsScreenNavigations
) {
    SettingsScreenContent(
        onBackClick = navigations.onBack,
        onLogoutClick = navigations.onLogout,
        onSettingClick = navigations::onOpenSetting
    )
}
