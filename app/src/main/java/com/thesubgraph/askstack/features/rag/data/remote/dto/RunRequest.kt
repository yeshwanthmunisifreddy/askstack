package com.thesubgraph.askstack.features.rag.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RunRequest(
    @SerializedName("assistant_id")
    val assistantId: String,
    @SerializedName("stream")
    val stream: Boolean = false,
    @SerializedName("instructions")
    val instructions: String? = null,
    @SerializedName("additional_instructions")
    val additionalInstructions: String? = null,
    @SerializedName("metadata")
    val metadata: Map<String, String>? = null
)
