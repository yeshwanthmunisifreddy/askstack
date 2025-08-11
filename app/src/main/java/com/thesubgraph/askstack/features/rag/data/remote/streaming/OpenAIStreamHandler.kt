package com.thesubgraph.askstack.features.rag.data.remote.streaming

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class OpenAIStreamHandler @Inject constructor(
    private val gson: Gson
) {
    
    fun handleStream(responseBody: ResponseBody): Flow<StreamEvent> = flow {
        try {
            val reader = BufferedReader(InputStreamReader(responseBody.byteStream()))
            
            reader.useLines { lines ->
                for (line in lines) {
                    if (line.startsWith("data: ")) {
                        val data = line.substring(6).trim()
                        
                        if (data == "[DONE]") {
                            emit(StreamEvent.Done)
                            break
                        }
                        
                        try {
                            val streamData = gson.fromJson(data, StreamData::class.java)
                            val event = parseStreamData(streamData)
                            event?.let { emit(it) }
                        } catch (e: Exception) {
                            Log.w("OpenAIStreamHandler", "Failed to parse stream data: $data", e)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("OpenAIStreamHandler", "Error handling stream", e)
            emit(StreamEvent.Error(e.message ?: "Unknown streaming error"))
        } finally {
            responseBody.close()
        }
    }
    
    private fun parseStreamData(streamData: StreamData): StreamEvent? {
        return when (streamData.objectType) {
            "thread.run" -> {
                when (streamData.data?.status) {
                    "queued" -> StreamEvent.RunQueued
                    "in_progress" -> StreamEvent.RunInProgress
                    "completed" -> StreamEvent.RunCompleted
                    "failed" -> StreamEvent.RunFailed(streamData.data.lastError?.message ?: "Run failed")
                    else -> null
                }
            }
            "thread.message" -> {
                StreamEvent.MessageCreated(streamData.data?.id ?: "")
            }
            "thread.message.delta" -> {
                val content = streamData.data?.delta?.content?.firstOrNull()?.text?.value
                content?.let { StreamEvent.MessageDelta(it) }
            }
            "thread.message.completed" -> {
                val message = streamData.data
                val content = message?.content?.firstOrNull()?.text?.value ?: ""
                val citations = message?.content?.firstOrNull()?.text?.annotations?.mapNotNull { annotation ->
                    annotation.fileCitation?.let { citation ->
                        Citation(
                            fileId = citation.fileId,
                            quote = citation.quote ?: "",
                            startIndex = annotation.startIndex,
                            endIndex = annotation.endIndex
                        )
                    }
                } ?: emptyList()
                StreamEvent.MessageCompleted(content, citations)
            }
            else -> null
        }
    }
}

sealed class StreamEvent {
    object RunQueued : StreamEvent()
    object RunInProgress : StreamEvent()
    object RunCompleted : StreamEvent()
    data class RunFailed(val error: String) : StreamEvent()
    data class MessageCreated(val messageId: String) : StreamEvent()
    data class MessageDelta(val content: String) : StreamEvent()
    data class MessageCompleted(val content: String, val citations: List<Citation>) : StreamEvent()
    data class Error(val message: String) : StreamEvent()
    object Done : StreamEvent()
}

data class Citation(
    val fileId: String,
    val quote: String,
    val startIndex: Int,
    val endIndex: Int
)
