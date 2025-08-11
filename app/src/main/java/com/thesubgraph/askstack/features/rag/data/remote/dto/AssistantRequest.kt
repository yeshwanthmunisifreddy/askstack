package com.thesubgraph.askstack.features.rag.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AssistantRequest(
    @SerializedName("instructions")
    val instructions: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("tools")
    val tools: List<AssistantToolDto>,
    @SerializedName("model")
    val model: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("metadata")
    val metadata: Map<String, String>? = null
)

data class AssistantToolDto(
    @SerializedName("type")
    val type: String
)
