package com.anix.android.anixstudyassist.topic.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.android.anixstudyassist.datahandler.domain.repo.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TopicCreationUiState(
    val name: String = "",
    val selectedColor: Color = Color(0xFF6C63FF),
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class TopicCreationViewModel @Inject constructor(
    private val studyRepository: StudyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopicCreationUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onColorChange(newColor: Color) {
        _uiState.update { it.copy(selectedColor = newColor) }
    }

    fun save(parentTopicId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            if (parentTopicId == null) {
                studyRepository.addTopic(_uiState.value.name, _uiState.value.selectedColor.toArgb())
            } else {
                studyRepository.addSubTopic(parentTopicId, _uiState.value.name)
            }
            _uiState.update { it.copy(isSaving = false, isSuccess = true) }
        }
    }
}
