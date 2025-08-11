package com.thesubgraph.askstack.features.rag.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesubgraph.askstack.BuildConfig
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
            apiKey = if (BuildConfig.OPENAI_API_KEY.isNotBlank()) "Loaded from BuildConfig" else "",
            assistantId = if (BuildConfig.OPENAI_ASSISTANT_ID.isNotBlank()) {
                "Loaded from BuildConfig"
            } else {
                securePreferences.getDefaultAssistantId() ?: ""
            },
            isApiKeyFromBuildConfig = BuildConfig.OPENAI_API_KEY.isNotBlank(),
            isAssistantIdFromBuildConfig = BuildConfig.OPENAI_ASSISTANT_ID.isNotBlank()
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
        
        // If both API key and Assistant ID are from BuildConfig, nothing to save
        if (currentState.isApiKeyFromBuildConfig && currentState.isAssistantIdFromBuildConfig) {
            _uiState.value = currentState.copy(
                message = "All settings are loaded from BuildConfig"
            )
            return
        }
        
        // If API key is from BuildConfig, we only need to save assistant ID (if not from BuildConfig)
        if (currentState.isApiKeyFromBuildConfig) {
            viewModelScope.launch {
                _uiState.value = currentState.copy(isSaving = true)
                
                try {
                    delay(300)
                    
                    if (!currentState.isAssistantIdFromBuildConfig && currentState.assistantId.isNotBlank()) {
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
        } else {
            // Legacy behavior for manual API key entry
            if (currentState.apiKey.isBlank()) {
                _uiState.value = currentState.copy(
                    message = "API key cannot be empty"
                )
                return
            }

            viewModelScope.launch {
                _uiState.value = currentState.copy(isSaving = true)
                
                try {
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
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class SettingsUiState(
    val apiKey: String = "",
    val assistantId: String = "",
    val isSaving: Boolean = false,
    val message: String? = null,
    val isApiKeyFromBuildConfig: Boolean = false,
    val isAssistantIdFromBuildConfig: Boolean = false
)
