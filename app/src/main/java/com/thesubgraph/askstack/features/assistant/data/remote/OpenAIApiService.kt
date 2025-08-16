package com.thesubgraph.askstack.features.assistant.data.remote

import com.thesubgraph.askstack.features.assistant.data.remote.dto.CreateThreadRequest
import com.thesubgraph.askstack.features.assistant.data.remote.dto.CreateThreadResponse
import com.thesubgraph.askstack.features.assistant.data.remote.dto.MessageRequest
import com.thesubgraph.askstack.features.assistant.data.remote.dto.MessageResponse
import com.thesubgraph.askstack.features.assistant.data.remote.dto.RunRequest
import com.thesubgraph.askstack.features.assistant.data.remote.dto.RunResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Streaming

interface OpenAIApiService {
    @POST("v1/threads")
    suspend fun createThread(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Beta") beta: String = "assistants=v2",
        @Body request: CreateThreadRequest = CreateThreadRequest()
    ): Response<CreateThreadResponse>

    @POST("v1/threads/{thread_id}/messages")
    suspend fun addMessage(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Beta") beta: String = "assistants=v2",
        @Path("thread_id") threadId: String,
        @Body request: MessageRequest
    ): Response<MessageResponse>

    @POST("v1/threads/{thread_id}/runs")
    suspend fun createRun(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Beta") beta: String = "assistants=v2",
        @Path("thread_id") threadId: String,
        @Body request: RunRequest
    ): Response<RunResponse>

    @GET("v1/threads/{thread_id}/runs/{run_id}")
    suspend fun getRun(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Beta") beta: String = "assistants=v2",
        @Path("thread_id") threadId: String,
        @Path("run_id") runId: String
    ): Response<RunResponse>

    @POST("v1/threads/{thread_id}/runs")
    @Streaming
    suspend fun createRunWithStreaming(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Beta") beta: String = "assistants=v2",
        @Header("Accept") accept: String = "text/event-stream",
        @Header("Cache-Control") cacheControl: String = "no-cache",
        @Header("Connection") connection: String = "keep-alive",
        @Path("thread_id") threadId: String,
        @Body request: RunRequest
    ): Response<okhttp3.ResponseBody>
}
