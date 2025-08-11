package com.thesubgraph.askstack.features.rag.domain.usecase

import com.thesubgraph.askstack.features.rag.domain.model.Conversation
import com.thesubgraph.askstack.features.rag.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConversationsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(): Flow<List<Conversation>> {
        return chatRepository.getConversations()
    }
}
