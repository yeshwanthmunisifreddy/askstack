package com.thesubgraph.askstack.features.rag.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AssistantResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("name")
    val name: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("model")
    val model: String,
    @SerializedName("instructions")
    val instructions: String?,
    @SerializedName("tools")
    val tools: List<AssistantToolDto>,
    @SerializedName("metadata")
    val metadata: Map<String, String>?
)

data class AssistantListResponse(
    @SerializedName("object")
    val objectType: String,
    @SerializedName("data")
    val data: List<AssistantResponse>,
    @SerializedName("first_id")
    val firstId: String?,
    @SerializedName("last_id")
    val lastId: String?,
    @SerializedName("has_more")
    val hasMore: Boolean
)
