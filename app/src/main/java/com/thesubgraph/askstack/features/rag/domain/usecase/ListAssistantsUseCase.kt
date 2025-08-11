package com.thesubgraph.askstack.features.rag.domain.usecase

import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.rag.domain.model.Assistant
import com.thesubgraph.askstack.features.rag.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListAssistantsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(): Flow<ValueResult<List<Assistant>>> {
        return chatRepository.listAssistants()
    }
}
