package com.thesubgraph.askstack.features.rag.domain.usecase

import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.rag.domain.model.Assistant
import com.thesubgraph.askstack.features.rag.domain.model.AssistantTool
import com.thesubgraph.askstack.features.rag.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateAssistantUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        name: String,
        instructions: String,
        model: String,
        tools: List<AssistantTool>,
        description: String? = null
    ): Flow<ValueResult<Assistant>> {
        return chatRepository.createAssistant(name, instructions, model, tools, description)
    }
}
