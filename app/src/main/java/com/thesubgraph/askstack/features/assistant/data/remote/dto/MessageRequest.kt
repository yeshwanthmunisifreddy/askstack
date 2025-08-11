package com.thesubgraph.askstack.features.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MessageRequest(
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("metadata")
    val metadata: Map<String, String>? = null
)
