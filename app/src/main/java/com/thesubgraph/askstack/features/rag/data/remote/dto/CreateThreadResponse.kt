package com.thesubgraph.askstack.features.rag.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateThreadResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("metadata")
    val metadata: Map<String, String>?
)
