package com.thesubgraph.askstack.features.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesubgraph.askstack.features.assistant.domain.model.Conversation
import com.thesubgraph.askstack.features.assistant.domain.usecase.DeleteConversationUseCase
import com.thesubgraph.askstack.features.assistant.domain.usecase.GetConversationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val getConversationsUseCase: GetConversationsUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase
) : ViewModel() {

    data class UiState(
        val conversations: List<Conversation> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val showDeleteDialog: Boolean = false,
        val conversationToDelete: Conversation? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                getConversationsUseCase().flowOn(Dispatchers.IO).collect { conversations ->
                    _uiState.value = _uiState.value.copy(
                        conversations = conversations,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load conversations"
                )
            }
        }
    }

    fun refreshConversations() {
        loadConversations()
    }
    
    fun showDeleteDialog(conversation: Conversation) {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = true,
            conversationToDelete = conversation
        )
    }
    
    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteDialog = false,
            conversationToDelete = null
        )
    }
    
    fun deleteConversation() {
        val conversationToDelete = _uiState.value.conversationToDelete ?: return
        
        viewModelScope.launch {
            try {
                deleteConversationUseCase(conversationToDelete.id)
                hideDeleteDialog()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete conversation",
                    showDeleteDialog = false,
                    conversationToDelete = null
                )
            }
        }
    }
}
