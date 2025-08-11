package com.thesubgraph.askstack.features.rag.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesubgraph.askstack.features.rag.data.local.storage.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val securePreferences: SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCurrentSettings()
    }

    private fun loadCurrentSettings() {
        _uiState.value = _uiState.value.copy(
            apiKey = securePreferences.getApiKey() ?: "",
            assistantId = securePreferences.getDefaultAssistantId() ?: ""
        )
    }

    fun setApiKey(apiKey: String) {
        _uiState.value = _uiState.value.copy(apiKey = apiKey)
    }

    fun setAssistantId(assistantId: String) {
        _uiState.value = _uiState.value.copy(assistantId = assistantId)
    }

    fun saveSettings() {
        val currentState = _uiState.value
        
        if (currentState.apiKey.isBlank()) {
            _uiState.value = currentState.copy(
                message = "API key cannot be empty"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true)
            
            try {
                // Simulate network delay for better UX
                delay(500)
                
                securePreferences.saveApiKey(currentState.apiKey)
                
                if (currentState.assistantId.isNotBlank()) {
                    securePreferences.saveDefaultAssistantId(currentState.assistantId)
                }
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Settings saved successfully!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    message = "Failed to save settings: ${e.message}"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class SettingsUiState(
    val apiKey: String = "",
    val assistantId: String = "",
    val isSaving: Boolean = false,
    val message: String? = null
)
