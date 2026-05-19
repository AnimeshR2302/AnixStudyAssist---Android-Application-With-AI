package com.anix.android.anixstudyassist.datahandler.domain.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Topic(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Int, // Hex Int
    val subTopics: List<SubTopic> = emptyList()
)

@Serializable
data class SubTopic(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val progress: Float = 0.0f,
    val isCompleted: Boolean = false
)
