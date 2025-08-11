package com.thesubgraph.askstack.features.rag.data.remote

import com.thesubgraph.askstack.features.rag.data.remote.dto.AssistantListResponse
import com.thesubgraph.askstack.features.rag.data.remote.dto.AssistantRequest
import com.thesubgraph.askstack.features.rag.data.remote.dto.AssistantResponse
import com.thesubgraph.askstack.features.rag.data.remote.dto.CreateThreadRequest
import com.thesubgraph.askstack.features.rag.data.remote.dto.CreateThreadResponse
import com.thesubgraph.askstack.features.rag.data.remote.dto.MessageRequest
import com.thesubgraph.askstack.features.rag.data.remote.dto.MessageResponse
import com.thesubgraph.askstack.features.rag.data.remote.dto.RunRequest
import com.thesubgraph.askstack.features.rag.data.remote.dto.RunResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface OpenAIApiService {

    // Assistant endpoints
    @POST("v1/assistants")
    suspend fun createAssistant(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Beta") beta: String = "assistants=v2",
        @Body request: AssistantRequest
    ): Response<AssistantResponse>

    @GET("v1/assistants")
    suspend fun listAssistants(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Beta") beta: String = "assistants=v2",
        @Query("limit") limit: Int = 20,
        @Query("order") order: String = "desc"
    ): Response<AssistantListResponse>

    @GET("v1/assistants/{assistant_id}")
    suspend fun getAssistant(
        @Header("Authorization") authorization: String,
        @Header("OpenAI-Beta") beta: String = "assistants=v2",
        @Path("assistant_id") assistantId: String
    ): Response<AssistantResponse>

    // Thread endpoints
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
