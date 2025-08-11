package com.thesubgraph.askstack.features.rag.domain.usecase

import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.rag.domain.model.Conversation
import com.thesubgraph.askstack.features.rag.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(assistantId: String, title: String): Flow<ValueResult<Conversation>> {
        return chatRepository.createConversation(assistantId, title)
    }
}
