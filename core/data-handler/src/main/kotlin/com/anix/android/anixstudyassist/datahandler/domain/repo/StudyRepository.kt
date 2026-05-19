package com.anix.android.anixstudyassist.datahandler.domain.repo

import com.anix.android.anixstudyassist.datahandler.domain.model.Topic
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    fun getTopics(): Flow<List<Topic>>
    suspend fun addTopic(name: String, color: Int)
    suspend fun addSubTopic(topicId: String, title: String)
    suspend fun updateSubTopicProgress(topicId: String, subTopicId: String, progress: Float)
}
