package com.thesubgraph.askstack.features.assistant.domain.usecase

import com.thesubgraph.askstack.features.assistant.domain.repository.ChatRepository
import javax.inject.Inject

class DeleteConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(conversationId: String) {
        chatRepository.deleteConversation(conversationId)
    }
}
