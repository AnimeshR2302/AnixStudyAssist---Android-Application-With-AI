package com.anix.android.anixstudyassist.core.nav

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RootGraph : NavKey {
    @Serializable
    data object Auth : RootGraph

    @Serializable
    data class Main(val user: String) : RootGraph
}

@Serializable
sealed interface MainGraph : NavKey {
    @Serializable
    data class Landing(val user: String) : MainGraph

    @Serializable
    data class ClassDetails(val classId: String) : MainGraph

    @Serializable
    data object AiChat : MainGraph

    @Serializable
    data object AiSettings : MainGraph

    @Serializable
    data object Settings : MainGraph
}

@Serializable
sealed interface SettingsGraph : NavKey {
    @Serializable
    data object Main : SettingsGraph

    @Serializable
    data object Setting1 : SettingsGraph
}
