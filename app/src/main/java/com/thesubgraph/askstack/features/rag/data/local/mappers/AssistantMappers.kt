package com.thesubgraph.askstack.features.rag.data.local.mappers

import com.thesubgraph.askstack.features.rag.data.remote.dto.AssistantRequest
import com.thesubgraph.askstack.features.rag.data.remote.dto.AssistantResponse
import com.thesubgraph.askstack.features.rag.data.remote.dto.AssistantToolDto
import com.thesubgraph.askstack.features.rag.domain.model.Assistant
import com.thesubgraph.askstack.features.rag.domain.model.AssistantTool
import com.thesubgraph.askstack.features.rag.domain.model.AssistantToolType
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun AssistantResponse.toDomain(): Assistant {
    return Assistant(
        id = id,
        name = name,
        description = description,
        model = model,
        instructions = instructions,
        tools = tools.map { it.toDomain() },
        createdAt = kotlin.time.Instant.fromEpochSeconds(createdAt),
        metadata = metadata ?: emptyMap()
    )
}

fun AssistantToolDto.toDomain(): AssistantTool {
    return AssistantTool(
        type = AssistantToolType.fromString(type)
    )
}

fun AssistantTool.toDto(): AssistantToolDto {
    return AssistantToolDto(
        type = AssistantToolType.toString(type)
    )
}

fun createAssistantRequest(
    name: String,
    instructions: String,
    model: String,
    tools: List<AssistantTool>,
    description: String? = null
): AssistantRequest {
    return AssistantRequest(
        name = name,
        instructions = instructions,
        model = model,
        tools = tools.map { it.toDto() },
        description = description
    )
}
