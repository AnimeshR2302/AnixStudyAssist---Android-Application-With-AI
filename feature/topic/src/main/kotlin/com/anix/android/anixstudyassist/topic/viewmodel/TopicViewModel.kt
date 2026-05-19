package com.anix.android.anixstudyassist.topic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.android.anixstudyassist.datahandler.domain.model.Topic
import com.anix.android.anixstudyassist.datahandler.domain.repo.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    private val studyRepository: StudyRepository
) : ViewModel() {

    fun getTopic(topicId: String): StateFlow<Topic?> = studyRepository.getTopics()
        .map { topics -> topics.find { it.id == topicId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateProgress(topicId: String, subTopicId: String, progress: Float) {
        viewModelScope.launch {
            studyRepository.updateSubTopicProgress(topicId, subTopicId, progress)
        }
    }
}
