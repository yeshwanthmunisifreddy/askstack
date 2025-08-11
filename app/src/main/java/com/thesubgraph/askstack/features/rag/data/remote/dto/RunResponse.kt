package com.thesubgraph.askstack.features.rag.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RunResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("thread_id")
    val threadId: String,
    @SerializedName("assistant_id")
    val assistantId: String,
    @SerializedName("status")
    val status: String, // queued, in_progress, requires_action, cancelling, cancelled, failed, completed, expired
    @SerializedName("required_action")
    val requiredAction: RequiredActionDto?,
    @SerializedName("last_error")
    val lastError: ErrorDto?,
    @SerializedName("expires_at")
    val expiresAt: Long?,
    @SerializedName("started_at")
    val startedAt: Long?,
    @SerializedName("cancelled_at")
    val cancelledAt: Long?,
    @SerializedName("failed_at")
    val failedAt: Long?,
    @SerializedName("completed_at")
    val completedAt: Long?,
    @SerializedName("metadata")
    val metadata: Map<String, String>?
)

data class RequiredActionDto(
    @SerializedName("type")
    val type: String,
    @SerializedName("submit_tool_outputs")
    val submitToolOutputs: SubmitToolOutputsDto?
)

data class SubmitToolOutputsDto(
    @SerializedName("tool_calls")
    val toolCalls: List<ToolCallDto>
)

data class ToolCallDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("function")
    val function: FunctionDto?
)

data class FunctionDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("arguments")
    val arguments: String
)

data class ErrorDto(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String
)
