package com.thesubgraph.askstack.features.assistant.data.local.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "conversation_id")
    val conversationId: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "role")
    val role: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,
    @ColumnInfo(name = "sources")
    val sources: String? = null,
    @ColumnInfo(name = "is_streaming")
    val isStreaming: Boolean = false,
    @ColumnInfo(name = "status")
    val status: String = "sent"
)
