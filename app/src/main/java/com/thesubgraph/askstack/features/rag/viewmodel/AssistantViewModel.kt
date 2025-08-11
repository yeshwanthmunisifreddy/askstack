package com.thesubgraph.askstack.features.rag.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.rag.domain.model.Assistant
import com.thesubgraph.askstack.features.rag.domain.model.AssistantTool
import com.thesubgraph.askstack.features.rag.domain.model.AssistantToolType
import com.thesubgraph.askstack.features.rag.domain.usecase.CreateAssistantUseCase
import com.thesubgraph.askstack.features.rag.domain.usecase.ListAssistantsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val createAssistantUseCase: CreateAssistantUseCase,
    private val listAssistantsUseCase: ListAssistantsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAssistants()
    }

    fun loadAssistants() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            listAssistantsUseCase()
                .flowOn(Dispatchers.IO)
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load assistants"
                    )
                }
                .collect { result ->
                    when (result) {
                        is ValueResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                assistants = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                        is ValueResult.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.error.message
                            )
                        }
                    }
                }
        }
    }

    fun createAssistant(
        name: String,
        instructions: String,
        model: String = "gpt-4o",
        includeFileSearch: Boolean = true,
        includeCodeInterpreter: Boolean = false,
        description: String? = null
    ) {
        if (name.isBlank() || instructions.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Name and instructions are required"
            )
            return
        }

        val tools = mutableListOf<AssistantTool>()
        if (includeFileSearch) {
            tools.add(AssistantTool(AssistantToolType.FILE_SEARCH))
        }
        if (includeCodeInterpreter) {
            tools.add(AssistantTool(AssistantToolType.CODE_INTERPRETER))
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCreating = true, error = null)
            
            createAssistantUseCase(name, instructions, model, tools, description)
                .flowOn(Dispatchers.IO)
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isCreating = false,
                        error = error.message ?: "Failed to create assistant"
                    )
                }
                .collect { result ->
                    when (result) {
                        is ValueResult.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isCreating = false,
                                error = null,
                                message = "Assistant '${result.data.name}' created successfully!"
                            )
                            // Refresh the list
                            loadAssistants()
                        }
                        is ValueResult.Failure -> {
                            _uiState.value = _uiState.value.copy(
                                isCreating = false,
                                error = result.error.message
                            )
                        }
                    }
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}

data class AssistantUiState(
    val assistants: List<Assistant> = emptyList(),
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val error: String? = null,
    val message: String? = null
)
