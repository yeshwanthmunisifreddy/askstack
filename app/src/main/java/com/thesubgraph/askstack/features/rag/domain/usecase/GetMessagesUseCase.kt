package com.thesubgraph.askstack.features.rag.domain.usecase

import com.thesubgraph.askstack.features.rag.domain.model.ChatMessage
import com.thesubgraph.askstack.features.rag.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(conversationId: String): Flow<List<ChatMessage>> {
        return chatRepository.getMessages(conversationId)
    }
}
