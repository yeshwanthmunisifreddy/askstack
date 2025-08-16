package com.thesubgraph.askstack.features.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateThreadRequest(
    @SerializedName("messages")
    val messages: List<ThreadMessageDto>? = null,
    @SerializedName("metadata")
    val metadata: Map<String, String>? = null
)

data class ThreadMessageDto(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String
)
