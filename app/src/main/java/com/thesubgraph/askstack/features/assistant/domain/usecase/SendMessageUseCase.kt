package com.thesubgraph.askstack.features.assistant.domain.usecase

import com.thesubgraph.askstack.features.assistant.data.remote.streaming.StreamEvent
import com.thesubgraph.askstack.features.assistant.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        assistantId: String
    ): Flow<StreamEvent> {
        return chatRepository.sendMessage(conversationId, content, assistantId)
    }
    
    suspend fun stopStreaming(messageId: String) {
        chatRepository.cancelStreaming(messageId)
    }
}
