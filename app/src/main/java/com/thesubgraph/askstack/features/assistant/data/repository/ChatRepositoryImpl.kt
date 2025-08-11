package com.thesubgraph.askstack.features.assistant.data.repository

import com.thesubgraph.askstack.base.utils.network.RequestWrapper
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.assistant.data.local.database.dao.ConversationDao
import com.thesubgraph.askstack.features.assistant.data.local.database.dao.MessageDao
import com.thesubgraph.askstack.features.assistant.data.local.mappers.toDomain
import com.thesubgraph.askstack.features.assistant.data.local.mappers.toEntity
import com.thesubgraph.askstack.features.assistant.data.local.mappers.toMessageSource
import com.thesubgraph.askstack.features.assistant.data.remote.OpenAIApiService
import com.thesubgraph.askstack.features.assistant.data.remote.dto.CreateThreadRequest
import com.thesubgraph.askstack.features.assistant.data.remote.dto.MessageRequest
import com.thesubgraph.askstack.features.assistant.data.remote.dto.RunRequest
import com.thesubgraph.askstack.features.assistant.data.remote.streaming.Citation
import com.thesubgraph.askstack.features.assistant.data.remote.streaming.OpenAIStreamHandler
import com.thesubgraph.askstack.features.assistant.data.remote.streaming.StreamEvent
import com.thesubgraph.askstack.features.assistant.domain.model.ChatMessage
import com.thesubgraph.askstack.features.assistant.domain.model.Conversation
import com.thesubgraph.askstack.features.assistant.domain.model.MessageRole
import com.thesubgraph.askstack.features.assistant.domain.model.MessageSource
import com.thesubgraph.askstack.features.assistant.domain.model.MessageStatus
import com.thesubgraph.askstack.features.assistant.domain.repository.ChatRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.coroutines.coroutineContext
import kotlin.time.ExperimentalTime

class ChatRepositoryImpl @Inject constructor(
    private val openAIApiService: OpenAIApiService,
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val requestWrapper: RequestWrapper,
    private val streamHandler: OpenAIStreamHandler,
    private val securePreferences: com.thesubgraph.askstack.features.assistant.data.local.storage.SecurePreferences
) : ChatRepository {

    private val ENABLE_MOCK_STREAMING = false
    private val ENABLE_SMOOTH_TYPING = true
    private val TYPING_SPEED_MULTIPLIER = 4.0

    private val activeStreamJobs = ConcurrentHashMap<String, Job>()
    private val activeResponseBodies = ConcurrentHashMap<String, okhttp3.ResponseBody>()

    @OptIn(ExperimentalTime::class)
    override suspend fun createConversation(
        assistantId: String,
        title: String
    ): Flow<ValueResult<Conversation>> {
        return flow {
            if (ENABLE_MOCK_STREAMING) {
                val now = kotlin.time.Clock.System.now()
                val conversation = Conversation(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    threadId = "mock_thread_${UUID.randomUUID()}",
                    assistantId = assistantId,
                    createdAt = now,
                    updatedAt = now
                )
                conversationDao.insertConversation(conversation.toEntity())
                emit(ValueResult.Success(conversation))
                return@flow
            }

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

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            content = content,
            role = MessageRole.USER,
            timestamp = kotlin.time.Clock.System.now(),
            status = MessageStatus.SENT
        )
        saveMessage(userMessage)

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

        if (ENABLE_MOCK_STREAMING) {
            val streamingJob = coroutineContext[Job]
            streamingJob?.let { activeStreamJobs[assistantMessageId] = it }

            try {
                mockStreamingResponse(
                    conversationId,
                    content,
                    assistantMessageId
                ).collect { event ->
                    emit(event)
                }
            } finally {
                activeStreamJobs.remove(assistantMessageId)
            }
            return@flow
        }

        try {
            val streamingJob = coroutineContext[Job]
            streamingJob?.let { activeStreamJobs[assistantMessageId] = it }

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

            val runResponse = openAIApiService.createRunWithStreaming(
                authorization = "Bearer ${securePreferences.getApiKey()}",
                threadId = conversation.threadId,
                request = RunRequest(assistantId = assistantId, stream = true)
            )

            if (!runResponse.isSuccessful) {
                val errorBody = runResponse.errorBody()?.string()
                emit(StreamEvent.Error("Failed to create run: ${runResponse.code()} - $errorBody"))
                markMessageAsFailed(assistantMessageId)
                return@flow
            }

            runResponse.body()?.let { responseBody ->
                activeResponseBodies[assistantMessageId] = responseBody

                var accumulatedContent = ""
                var citations: List<Citation> = emptyList()

                streamHandler.handleStream(responseBody).collect { event ->
                    emit(event)

                    when (event) {
                        is StreamEvent.MessageDelta -> {
                            val chunk = event.content

                            if (ENABLE_SMOOTH_TYPING) {
                                handleStreamingChunk(
                                    chunk = chunk,
                                    assistantMessageId = assistantMessageId,
                                    currentContent = accumulatedContent,
                                    citations = citations.map { it.toMessageSource() }
                                ) { newContent ->
                                    accumulatedContent = newContent
                                }
                            } else {
                                accumulatedContent += chunk
                                updateMessage(
                                    assistantMessageId,
                                    accumulatedContent,
                                    citations.map { it.toMessageSource() }
                                )
                            }
                        }

                        is StreamEvent.MessageCompleted -> {
                            citations = event.citations

                            updateMessage(
                                assistantMessageId,
                                event.content,
                                citations.map { it.toMessageSource() }
                            )

                            messageDao.updateMessageStreamingStatus(assistantMessageId, false)

                            markMessageAsSent(assistantMessageId)

                            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
                            conversationDao.updateLastMessage(conversationId, event.content, now)
                        }

                        is StreamEvent.RunCompleted -> {
                            messageDao.updateMessageStreamingStatus(assistantMessageId, false)
                        }

                        is StreamEvent.Done -> {
                            messageDao.updateMessageStreamingStatus(assistantMessageId, false)
                        }

                        is StreamEvent.RunFailed -> {
                            markMessageAsFailed(assistantMessageId)
                        }

                        is StreamEvent.Error -> {
                            markMessageAsFailed(assistantMessageId)
                        }

                        else -> { /* Handle other events if needed */
                        }
                    }
                }
            } ?: run {
                emit(StreamEvent.Error("Response body is null"))
                markMessageAsFailed(assistantMessageId)
            }
        } catch (e: Exception) {
            emit(StreamEvent.Error(e.message ?: "Unknown error"))
            markMessageAsFailed(assistantMessageId)
        } finally {
            messageDao.updateMessageStreamingStatus(assistantMessageId, false)

            activeStreamJobs.remove(assistantMessageId)
            activeResponseBodies.remove(assistantMessageId)
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

        try {
            messageDao.updateMessageContent(messageId, content, sourcesJson)
            kotlinx.coroutines.delay(1)
        } catch (e: Exception) {
        }
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

    override suspend fun cancelStreaming(messageId: String) {
        activeStreamJobs[messageId]?.cancel()
        activeStreamJobs.remove(messageId)

        activeResponseBodies[messageId]?.close()
        activeResponseBodies.remove(messageId)

        messageDao.updateMessageStreamingStatus(messageId, false)
        markMessageAsSent(messageId)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun mockStreamingResponse(
        conversationId: String,
        userContent: String,
        assistantMessageId: String
    ): Flow<StreamEvent> = flow {

        try {
            emit(StreamEvent.RunQueued)
            kotlinx.coroutines.currentCoroutineContext().ensureActive()
            delay(500)

            emit(StreamEvent.RunInProgress)
            kotlinx.coroutines.currentCoroutineContext().ensureActive()
            delay(800)

            val mockResponse = generateMockResponse(userContent)

            var accumulatedContent = ""
            val words = mockResponse.split(" ").filter { it.isNotEmpty() }

            for ((index, word) in words.withIndex()) {
                kotlinx.coroutines.currentCoroutineContext().ensureActive()

                val wordToAdd = if (index == 0) word else " $word"
                accumulatedContent += wordToAdd

                emit(StreamEvent.MessageDelta(wordToAdd))

                updateMessage(assistantMessageId, accumulatedContent, emptyList())

                if (index < words.size - 1) {
                    val baseDelay = when {
                        word.endsWith(".") || word.endsWith("!") || word.endsWith("?") -> (200..400)
                        word.endsWith(",") || word.endsWith(";") || word.endsWith(":") -> (150..250)
                        word.length > 8 -> (120..200)
                        else -> (80..150)
                    }
                    val adjustedDelay = (baseDelay.random() * TYPING_SPEED_MULTIPLIER).toLong()
                    delay(adjustedDelay)
                }
            }

            kotlinx.coroutines.currentCoroutineContext().ensureActive()
            emit(StreamEvent.MessageCompleted(accumulatedContent, emptyList()))
            markMessageAsSent(assistantMessageId)

            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
            conversationDao.updateLastMessage(conversationId, accumulatedContent, now)

            emit(StreamEvent.RunCompleted)
            emit(StreamEvent.Done)

        } catch (e: kotlinx.coroutines.CancellationException) {
            messageDao.updateMessageStreamingStatus(assistantMessageId, false)
            markMessageAsSent(assistantMessageId)

            try {
                emit(StreamEvent.RunCompleted)
                emit(StreamEvent.Done)
            } catch (_: Exception) {
            }
        }
    }

    private fun generateMockResponse(userInput: String): String {
        return when {
            userInput.contains(
                "hello",
                ignoreCase = true
            ) -> "Hello there! How can I assist you today?"

            userInput.contains(
                "kotlin",
                ignoreCase = true
            ) -> "Kotlin is a modern, statically typed programming language developed by JetBrains. It's widely used for Android app development and is fully interoperable with Java."

            userInput.contains(
                "android",
                ignoreCase = true
            ) -> "Android is a mobile operating system developed by Google. It is based on a modified version of the Linux kernel and other open-source software, and is designed primarily for touchscreen mobile devices such as smartphones and tablets."

            userInput.contains(
                "compose",
                ignoreCase = true
            ) -> "Jetpack Compose is Android's modern toolkit for building native UI. It simplifies and accelerates UI development on Android with less code, powerful tools, and intuitive Kotlin APIs."

            userInput.contains(
                "assistants api",
                ignoreCase = true
            ) -> "The OpenAI Assistants API allows you to build AI assistants within your applications. It handles thread management, tool usage, and streaming responses, making it easier to create conversational AI experiences."

            else -> "I understand you're asking about '$userInput'. This is a mock response to test the streaming functionality. The typing effect should work smoothly, showing each word as it appears in real-time."
        }
    }

    private suspend fun handleStreamingChunk(
        chunk: String,
        assistantMessageId: String,
        currentContent: String,
        citations: List<MessageSource>,
        onContentUpdate: (String) -> Unit
    ) {
        if (chunk.isEmpty()) return

        if (chunk.length <= 5) {
            val newContent = currentContent + chunk
            onContentUpdate(newContent)
            updateMessage(assistantMessageId, newContent, citations)
            return
        }

        val words = chunk.split(" ").filter { it.isNotEmpty() }
        var partialContent = currentContent

        for ((index, word) in words.withIndex()) {
            val wordToAdd = when {
                index == 0 && partialContent.isNotEmpty() && !partialContent.endsWith(" ") -> " $word"
                index == 0 -> word
                else -> " $word"
            }

            partialContent += wordToAdd
            onContentUpdate(partialContent)
            updateMessage(assistantMessageId, partialContent, citations)

            if (index < words.size - 1) {
                val baseDelay = when {
                    word.endsWith(".") || word.endsWith("!") || word.endsWith("?") -> (200..400)
                    word.endsWith(",") || word.endsWith(";") || word.endsWith(":") -> (150..250)
                    word.length > 8 -> (120..200)
                    else -> (80..150)
                }
                val adjustedDelay = (baseDelay.random() * TYPING_SPEED_MULTIPLIER).toLong()
                delay(adjustedDelay)
            }

        }
    }
}
