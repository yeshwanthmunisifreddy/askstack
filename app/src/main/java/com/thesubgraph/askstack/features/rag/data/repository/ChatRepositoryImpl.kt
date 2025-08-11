package com.thesubgraph.askstack.features.rag.data.repository

import com.thesubgraph.askstack.base.utils.network.RequestWrapper
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.rag.data.local.database.dao.ConversationDao
import com.thesubgraph.askstack.features.rag.data.local.database.dao.MessageDao
import com.thesubgraph.askstack.features.rag.data.local.mappers.toDomain
import com.thesubgraph.askstack.features.rag.data.local.mappers.toEntity
import com.thesubgraph.askstack.features.rag.data.local.mappers.toMessageSource
import com.thesubgraph.askstack.features.rag.data.remote.OpenAIApiService
import com.thesubgraph.askstack.features.rag.data.remote.dto.CreateThreadRequest
import com.thesubgraph.askstack.features.rag.data.remote.dto.MessageRequest
import com.thesubgraph.askstack.features.rag.data.remote.dto.RunRequest
import com.thesubgraph.askstack.features.rag.data.remote.streaming.OpenAIStreamHandler
import com.thesubgraph.askstack.features.rag.data.remote.streaming.StreamEvent
import com.thesubgraph.askstack.features.rag.domain.model.ChatMessage
import com.thesubgraph.askstack.features.rag.domain.model.Conversation
import com.thesubgraph.askstack.features.rag.domain.model.MessageRole
import com.thesubgraph.askstack.features.rag.domain.model.MessageSource
import com.thesubgraph.askstack.features.rag.domain.model.MessageStatus
import com.thesubgraph.askstack.features.rag.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class ChatRepositoryImpl @Inject constructor(
    private val openAIApiService: OpenAIApiService,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val requestWrapper: RequestWrapper,
    private val streamHandler: OpenAIStreamHandler,
    private val securePreferences: com.thesubgraph.askstack.features.rag.data.local.storage.SecurePreferences
) : ChatRepository {
    @OptIn(ExperimentalTime::class)
    override suspend fun createConversation(
        assistantId: String,
        title: String
    ): Flow<ValueResult<Conversation>> {
        return flow {
            val results = requestWrapper.execute(
                mapper = { response ->
                    val now = kotlin.time.Clock.System.now()
                    val conversation = Conversation(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        threadId = response.id,
                        assistantId = assistantId,
                        createdAt = now,
                        updatedAt = now
                    )
                    conversation
                },
                apiCall = {
                    openAIApiService.createThread(
                        authorization = "Bearer ${securePreferences.getApiKey()}",
                        request = CreateThreadRequest()
                    )
                }
            )
            if (results is ValueResult.Success) {
                conversationDao.insertConversation(results.data.toEntity())
            }
            emit(results)
        }
    }


    override suspend fun getConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getConversation(conversationId: String): Conversation? {
        return conversationDao.getConversationById(conversationId)?.toDomain()
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateConversationTitle(conversationId: String, title: String) {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        conversationDao.updateConversationTitle(conversationId, title, now)
    }

    override suspend fun deleteConversation(conversationId: String) {
        conversationDao.deleteConversation(conversationId)
    }

    override suspend fun getMessages(conversationId: String): Flow<List<ChatMessage>> {
        return messageDao.getMessagesByConversationId(conversationId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun sendMessage(
        conversationId: String,
        content: String,
        assistantId: String
    ): Flow<StreamEvent> = flow {
        val conversation = getConversation(conversationId) ?: return@flow
        
        // Save user message first
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            content = content,
            role = MessageRole.USER,
            timestamp = kotlin.time.Clock.System.now(),
            status = MessageStatus.SENT
        )
        saveMessage(userMessage)

        // Create assistant message placeholder
        val assistantMessageId = UUID.randomUUID().toString()
        val assistantMessage = ChatMessage(
            id = assistantMessageId,
            conversationId = conversationId,
            content = "",
            role = MessageRole.ASSISTANT,
            timestamp = kotlin.time.Clock.System.now(),
            isStreaming = true,
            status = MessageStatus.SENDING
        )
        saveMessage(assistantMessage)

        try {
            // Add user message to thread
            val messageResponse = openAIApiService.addMessage(
                authorization = "Bearer ${securePreferences.getApiKey()}",
                threadId = conversation.threadId,
                request = MessageRequest(role = "user", content = content)
            )

            if (!messageResponse.isSuccessful) {
                emit(StreamEvent.Error("Failed to add message to thread"))
                markMessageAsFailed(assistantMessageId)
                return@flow
            }

            // Create run with streaming
            val runResponse = openAIApiService.createRunWithStreaming(
                authorization = "Bearer ${securePreferences.getApiKey()}",
                threadId = conversation.threadId,
                request = RunRequest(assistantId = assistantId, stream = true)
            )

            if (!runResponse.isSuccessful) {
                emit(StreamEvent.Error("Failed to create run"))
                markMessageAsFailed(assistantMessageId)
                return@flow
            }

            // Handle streaming response
            runResponse.body()?.let { responseBody ->
                var accumulatedContent = ""
                var citations: List<com.thesubgraph.askstack.features.rag.data.remote.streaming.Citation> = emptyList()

                streamHandler.handleStream(responseBody).collect { event ->
                    emit(event)
                    
                    when (event) {
                        is StreamEvent.MessageDelta -> {
                            accumulatedContent += event.content
                            updateMessage(
                                assistantMessageId,
                                accumulatedContent,
                                citations.map { it.toMessageSource() }
                            )
                        }
                        is StreamEvent.MessageCompleted -> {
                            citations = event.citations
                            updateMessage(
                                assistantMessageId,
                                event.content,
                                citations.map { it.toMessageSource() }
                            )
                            markMessageAsSent(assistantMessageId)
                            
                            // Update conversation's last message and timestamp
                            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
                            conversationDao.updateLastMessage(conversationId, event.content, now)
                        }
                        is StreamEvent.RunFailed -> {
                            markMessageAsFailed(assistantMessageId)
                        }
                        is StreamEvent.Error -> {
                            markMessageAsFailed(assistantMessageId)
                        }
                        else -> { /* Handle other events if needed */ }
                    }
                }
            }
        } catch (e: Exception) {
            emit(StreamEvent.Error(e.message ?: "Unknown error"))
            markMessageAsFailed(assistantMessageId)
        }
    }

    override suspend fun saveMessage(message: ChatMessage) {
        messageDao.insertMessage(message.toEntity())
    }

    override suspend fun updateMessage(
        messageId: String,
        content: String,
        sources: List<MessageSource>
    ) {
        val sourcesJson = if (sources.isEmpty()) null else com.google.gson.Gson().toJson(sources)
        messageDao.updateMessageContent(messageId, content, sourcesJson)
        messageDao.updateMessageStreamingStatus(messageId, false)
    }

    override suspend fun getQueuedMessages(): List<ChatMessage> {
        return messageDao.getQueuedMessages().map { it.toDomain() }
    }

    override suspend fun markMessageAsSent(messageId: String) {
        messageDao.updateMessageStatus(messageId, MessageStatus.SENT.name.lowercase())
        messageDao.updateMessageStreamingStatus(messageId, false)
    }

    override suspend fun markMessageAsFailed(messageId: String) {
        messageDao.updateMessageStatus(messageId, MessageStatus.FAILED.name.lowercase())
        messageDao.updateMessageStreamingStatus(messageId, false)
    }
}
