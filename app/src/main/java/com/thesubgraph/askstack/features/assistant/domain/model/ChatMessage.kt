package com.thesubgraph.askstack.features.assistant.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant


data class ChatMessage @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val conversationId: String,
    val content: String,
    val role: MessageRole,
    val timestamp: Instant ,
    val sources: List<MessageSource> = emptyList(),
    val isStreaming: Boolean = false,
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageRole {
    USER, ASSISTANT
}

enum class MessageStatus {
    SENDING, SENT, FAILED
}

data class MessageSource(
    val fileId: String,
    val quote: String,
    val fileName: String? = null,
    val startIndex: Int,
    val endIndex: Int
)
