package com.thesubgraph.askstack.features.rag.data.local.mappers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.thesubgraph.askstack.features.rag.data.local.database.entities.ConversationEntity
import com.thesubgraph.askstack.features.rag.data.local.database.entities.MessageEntity
import com.thesubgraph.askstack.features.rag.data.remote.streaming.Citation
import com.thesubgraph.askstack.features.rag.domain.model.ChatMessage
import com.thesubgraph.askstack.features.rag.domain.model.Conversation
import com.thesubgraph.askstack.features.rag.domain.model.MessageRole
import com.thesubgraph.askstack.features.rag.domain.model.MessageSource
import com.thesubgraph.askstack.features.rag.domain.model.MessageStatus
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

private val gson = Gson()

@OptIn(ExperimentalTime::class)
fun ConversationEntity.toDomain(): Conversation {
    return Conversation(
        id = id,
        title = title,
        threadId = threadId,
        assistantId = assistantId,
        createdAt = kotlin.time.Instant.fromEpochMilliseconds(createdAt),
        updatedAt =  kotlin.time.Instant.fromEpochMilliseconds(updatedAt),
        lastMessage = lastMessage
    )
}

@OptIn(ExperimentalTime::class)
fun Conversation.toEntity(): ConversationEntity {
    return ConversationEntity(
        id = id,
        title = title,
        threadId = threadId,
        assistantId = assistantId,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds(),
        lastMessage = lastMessage
    )
}

@OptIn(ExperimentalTime::class)
fun MessageEntity.toDomain(): ChatMessage {
    val sources = if (sources.isNullOrBlank()) {
        emptyList()
    } else {
        try {
            val type = object : TypeToken<List<MessageSource>>() {}.type
            gson.fromJson<List<MessageSource>>(sources, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    return ChatMessage(
        id = id,
        conversationId = conversationId,
        content = content,
        role = MessageRole.valueOf(role.uppercase()),
        timestamp = kotlin.time.Instant.fromEpochMilliseconds(timestamp),
        sources = sources,
        isStreaming = isStreaming,
        status = MessageStatus.valueOf(status.uppercase())
    )
}

@OptIn(ExperimentalTime::class)
fun ChatMessage.toEntity(): MessageEntity {
    val sourcesJson = if (sources.isEmpty()) null else gson.toJson(sources)
    
    return MessageEntity(
        id = id,
        conversationId = conversationId,
        content = content,
        role = role.name.lowercase(),
        timestamp = timestamp.toEpochMilliseconds(),
        sources = sourcesJson,
        isStreaming = isStreaming,
        status = status.name.lowercase()
    )
}

fun Citation.toMessageSource(fileName: String? = null): MessageSource {
    return MessageSource(
        fileId = fileId,
        quote = quote,
        fileName = fileName,
        startIndex = startIndex,
        endIndex = endIndex
    )
}
