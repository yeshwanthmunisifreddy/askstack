package com.thesubgraph.askstack.features.assistant.data.remote.streaming

import com.google.gson.annotations.SerializedName
import com.thesubgraph.askstack.features.assistant.data.remote.dto.AnnotationDto
import com.thesubgraph.askstack.features.assistant.data.remote.dto.ErrorDto
import com.thesubgraph.askstack.features.assistant.data.remote.dto.MessageContentDto

data class StreamData(
    @SerializedName("object")
    val objectType: String,
    @SerializedName("data")
    val data: StreamDataContent?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("event")
    val event: String?,
    @SerializedName("delta")
    val delta: StreamDelta?
)

data class StreamDataContent(
    @SerializedName("id")
    val id: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("last_error")
    val lastError: ErrorDto?,
    @SerializedName("content")
    val content: List<MessageContentDto>?,
    @SerializedName("delta")
    val delta: StreamDelta?
)

data class StreamDelta(
    @SerializedName("content")
    val content: List<DeltaContent>?
)

data class DeltaContent(
    @SerializedName("index")
    val index: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("text")
    val text: DeltaText?
)

data class DeltaText(
    @SerializedName("value")
    val value: String?,
    @SerializedName("annotations")
    val annotations: List<AnnotationDto>?
)
