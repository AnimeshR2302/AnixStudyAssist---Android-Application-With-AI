package com.anix.android.anixstudyassist.feature.auth

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavGraph {
    @Serializable
    data object Auth : AppNavGraph

    @Serializable
    data object Landing : AppNavGraph

    @Serializable
    data class ClassDetails(val classId: String) : AppNavGraph

    @Serializable
    data object Settings : AppNavGraph
}
