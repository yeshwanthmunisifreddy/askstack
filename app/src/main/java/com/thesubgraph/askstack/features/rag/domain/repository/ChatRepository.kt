package com.thesubgraph.askstack.features.rag.domain.repository

import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.rag.data.remote.streaming.StreamEvent
import com.thesubgraph.askstack.features.rag.domain.model.ChatMessage
import com.thesubgraph.askstack.features.rag.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    
    // Conversation management
    suspend fun createConversation(assistantId: String, title: String): Flow<ValueResult<Conversation>>
    suspend fun getConversations(): Flow<List<Conversation>>
    suspend fun getConversation(conversationId: String): Conversation?
    suspend fun updateConversationTitle(conversationId: String, title: String)
    suspend fun deleteConversation(conversationId: String)
    
    // Message management
    suspend fun getMessages(conversationId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(
        conversationId: String, 
        content: String,
        assistantId: String
    ): Flow<StreamEvent>
    suspend fun saveMessage(message: ChatMessage)
    suspend fun updateMessage(messageId: String, content: String, sources: List<com.thesubgraph.askstack.features.rag.domain.model.MessageSource>)
    
    // Offline support
    suspend fun getQueuedMessages(): List<ChatMessage>
    suspend fun markMessageAsSent(messageId: String)
    suspend fun markMessageAsFailed(messageId: String)
}
