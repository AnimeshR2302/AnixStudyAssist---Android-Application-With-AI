package com.anix.landing.anixstudyassist.feature.landing.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.android.anixstudyassist.datahandler.domain.model.Topic
import com.anix.android.anixstudyassist.datahandler.domain.repo.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val studyRepository: StudyRepository
) : ViewModel() {

    val topics: StateFlow<List<Topic>> = studyRepository.getTopics()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}