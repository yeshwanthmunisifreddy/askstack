package com.thesubgraph.askstack.features.rag.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class Assistant(
    val id: String,
    val name: String?,
    val description: String?,
    val model: String,
    val instructions: String?,
    val tools: List<AssistantTool>,
    val createdAt: Instant,
    val metadata: Map<String, String> = emptyMap()
)

data class AssistantTool(
    val type: AssistantToolType
)

enum class AssistantToolType {
    CODE_INTERPRETER,
    FILE_SEARCH,
    FUNCTION;
    
    companion object {
        fun fromString(value: String): AssistantToolType {
            return when (value.lowercase()) {
                "code_interpreter" -> CODE_INTERPRETER
                "file_search" -> FILE_SEARCH
                "function" -> FUNCTION
                else -> FILE_SEARCH // default
            }
        }
        
        fun toString(type: AssistantToolType): String {
            return when (type) {
                CODE_INTERPRETER -> "code_interpreter"
                FILE_SEARCH -> "file_search"
                FUNCTION -> "function"
            }
        }
    }
}
