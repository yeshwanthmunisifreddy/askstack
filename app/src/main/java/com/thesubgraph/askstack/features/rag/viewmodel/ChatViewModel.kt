package com.thesubgraph.askstack.features.rag.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.rag.data.local.storage.SecurePreferences
import com.thesubgraph.askstack.features.rag.data.remote.streaming.StreamEvent
import com.thesubgraph.askstack.features.rag.domain.model.ChatMessage
import com.thesubgraph.askstack.features.rag.domain.model.Conversation
import com.thesubgraph.askstack.features.rag.domain.usecase.CreateConversationUseCase
import com.thesubgraph.askstack.features.rag.domain.usecase.GetMessagesUseCase
import com.thesubgraph.askstack.features.rag.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val createConversationUseCase: CreateConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    private val securePreferences: SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private val _messageInput = MutableStateFlow("")
    val messageInput = _messageInput.asStateFlow()

    fun setMessageInput(input: String) {
        _messageInput.value = input
    }

    fun initializeChat(conversationId: String? = null, assistantId: String? = null) {
        if (conversationId != null) {
            loadExistingConversation(conversationId)
        } else if (assistantId != null) {
            createNewConversation(assistantId)
        } else {
            _uiState.value = _uiState.value.copy(
                error = "No conversation ID or assistant ID provided"
            )
        }
    }

    private fun createNewConversation(assistantId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            createConversationUseCase(assistantId,"New Chat").flowOn(Dispatchers.IO).collect { result->
               when(result){
                   is ValueResult.Failure -> {
                       _uiState.value = _uiState.value.copy(
                           isLoading = false,
                           error = result.error.message
                       )
                   }
                   is ValueResult.Success -> {
                       val conversation = result.data
                       _uiState.value = _uiState.value.copy(
                           conversation = conversation,
                           isLoading = false
                       )
                       loadMessages(conversation.id)
                   }
               }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun loadExistingConversation(conversationId: String) {
        _uiState.value = _uiState.value.copy(
            conversation = Conversation(
                id = conversationId,
                title = "Loading...",
                threadId = "",
                assistantId = "",
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
        )
        loadMessages(conversationId)
    }

    private fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            getMessagesUseCase(conversationId)
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Failed to load messages"
                    )
                }
                .collect { messages ->
                    _uiState.value = _uiState.value.copy(
                        messages = messages,
                        isLoading = false
                    )
                }
        }
    }

    fun sendMessage() {
        val currentState = _uiState.value
        val conversation = currentState.conversation ?: return
        val content = _messageInput.value.trim()
        
        if (content.isBlank()) return

        val assistantId = securePreferences.getDefaultAssistantId() 
            ?: conversation.assistantId.takeIf { it.isNotBlank() }
            ?: return

        _messageInput.value = ""
        _uiState.value = _uiState.value.copy(isSending = true, error = null)

        viewModelScope.launch {
            sendMessageUseCase(conversation.id, content, assistantId)
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isSending = false,
                        error = error.message ?: "Failed to send message"
                    )
                }
                .collect { event ->
                    handleStreamEvent(event)
                }
        }
    }

    private fun handleStreamEvent(event: StreamEvent) {
        when (event) {
            is StreamEvent.RunQueued -> {
                // Message is queued for processing
            }
            is StreamEvent.RunInProgress -> {
                // Assistant is processing
            }
            is StreamEvent.RunCompleted -> {
                _uiState.value = _uiState.value.copy(isSending = false)
            }
            is StreamEvent.RunFailed -> {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    error = event.error
                )
            }
            is StreamEvent.MessageCreated -> {
                // Assistant message created
            }
            is StreamEvent.MessageDelta -> {
                // Real-time content update handled by repository
            }
            is StreamEvent.MessageCompleted -> {
                _uiState.value = _uiState.value.copy(isSending = false)
            }
            is StreamEvent.Error -> {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    error = event.message
                )
            }
            is StreamEvent.Done -> {
                _uiState.value = _uiState.value.copy(isSending = false)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun retryLastMessage() {
        sendMessage()
    }
}

data class ChatUiState(
    val conversation: Conversation? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null
)
