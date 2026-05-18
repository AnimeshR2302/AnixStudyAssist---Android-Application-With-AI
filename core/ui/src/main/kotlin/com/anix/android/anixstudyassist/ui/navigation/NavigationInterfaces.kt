package com.anix.android.anixstudyassist.ui.navigation

interface AuthScreenNavigations {
    fun onLoginSuccess(userId: String)
}

interface LandingScreenNavigations {
    fun onOpenClass(classId: String)
    val onOpenAiChat: () -> Unit
    val onOpenSettings: () -> Unit
    val onOpenDataStore: () -> Unit
    val onOpenAddTopic: () -> Unit
    val onLogout: () -> Unit
}

interface ClassDetailsScreenNavigations {
    val onBack: () -> Unit
    fun onOpenSettings(classId: String)
    val onLogout: () -> Unit
}

interface SettingsScreenNavigations {
    val onBack: () -> Unit
    fun onOpenSetting(settingId: String)
    val onLogout: () -> Unit
}
