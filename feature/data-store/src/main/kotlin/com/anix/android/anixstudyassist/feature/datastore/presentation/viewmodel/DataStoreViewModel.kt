package com.anix.android.anixstudyassist.feature.datastore.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class DataStoreUiState(
    val files: List<String> = emptyList(),
    val totalSize: Long = 0L,
    val showClearConfirmDialog: Boolean = false
)

@HiltViewModel
class DataStoreViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DataStoreUiState())
    val uiState: StateFlow<DataStoreUiState> = _uiState.asStateFlow()

    private val storageDir = File(application.filesDir, "data_store")

    init {
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        refreshState()
    }

    private fun refreshState() {
        val files = storageDir.listFiles()?.map { it.name } ?: emptyList()
        val totalSize = storageDir.listFiles()?.sumOf { it.length() } ?: 0L
        _uiState.value = DataStoreUiState(files = files, totalSize = totalSize)
    }

    fun addData(uri: Uri) {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val fileName = getFileName(context, uri) ?: "data_${System.currentTimeMillis()}"
            val destFile = File(storageDir, fileName)

            try {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { input.copyTo(it) }
                }
                refreshState()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = it.getString(index)
                    }
                }
            }
        }

        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    fun showClearAllConfirmDialog() = _uiState.update { it.copy(showClearConfirmDialog = true) }

    fun removeClearAllDialog() = _uiState.update { it.copy(showClearConfirmDialog = false) }

    fun onConfirmDeleteAll() {
        clearAllData()
        removeClearAllDialog()
    }

    fun clearAllData() {
        storageDir.listFiles()?.forEach { it.delete() }
        refreshState()

    }
}
