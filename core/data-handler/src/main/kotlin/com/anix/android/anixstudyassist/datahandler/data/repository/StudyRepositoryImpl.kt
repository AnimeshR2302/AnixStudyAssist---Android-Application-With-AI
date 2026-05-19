package com.anix.android.anixstudyassist.datahandler.data.repository

import android.content.Context
import com.anix.android.anixstudyassist.datahandler.domain.model.SubTopic
import com.anix.android.anixstudyassist.datahandler.domain.model.Topic
import com.anix.android.anixstudyassist.datahandler.domain.repo.StudyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StudyRepository {

    private val file = File(context.filesDir, "study_topics.json")
    private val _topics = MutableStateFlow<List<Topic>>(emptyList())

    init {
        loadTopics()
    }

    private fun loadTopics() {
        if (file.exists()) {
            try {
                val json = file.readText()
                _topics.value = Json.decodeFromString(json)
            } catch (e: Exception) {
                e.printStackTrace()
                _topics.value = emptyList()
            }
        }
    }

    private suspend fun saveTopics() = withContext(Dispatchers.IO) {
        try {
            val json = Json.encodeToString(_topics.value)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getTopics(): Flow<List<Topic>> = _topics.asStateFlow()

    override suspend fun addTopic(name: String, color: Int) {
        val newTopic = Topic(name = name, color = color)
        _topics.value = _topics.value + newTopic
        saveTopics()
    }

    override suspend fun addSubTopic(topicId: String, title: String) {
        val currentTopics = _topics.value
        val updatedTopics = currentTopics.map { topic ->
            if (topic.id == topicId) {
                topic.copy(subTopics = topic.subTopics + SubTopic(title = title))
            } else {
                topic
            }
        }
        _topics.value = updatedTopics
        saveTopics()
    }

    override suspend fun updateSubTopicProgress(
        topicId: String,
        subTopicId: String,
        progress: Float
    ) {
        val currentTopics = _topics.value
        val updatedTopics = currentTopics.map { topic ->
            if (topic.id == topicId) {
                val updatedSubTopics = topic.subTopics.map { subTopic ->
                    if (subTopic.id == subTopicId) {
                        subTopic.copy(progress = progress, isCompleted = progress >= 1.0f)
                    } else {
                        subTopic
                    }
                }
                topic.copy(subTopics = updatedSubTopics)
            } else {
                topic
            }
        }
        _topics.value = updatedTopics
        saveTopics()
    }
}
