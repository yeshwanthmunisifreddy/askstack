package com.thesubgraph.askstack.features.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("object")
    val objectType: String,
    @SerializedName("created_at")
    val createdAt: Long,
    @SerializedName("thread_id")
    val threadId: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("content")
    val content: List<MessageContentDto>,
    @SerializedName("metadata")
    val metadata: Map<String, String>?
)

data class MessageContentDto(
    @SerializedName("type")
    val type: String,
    @SerializedName("text")
    val text: MessageTextDto?
)

data class MessageTextDto(
    @SerializedName("value")
    val value: String,
    @SerializedName("annotations")
    val annotations: List<AnnotationDto>?
)

data class AnnotationDto(
    @SerializedName("type")
    val type: String,
    @SerializedName("text")
    val text: String,
    @SerializedName("file_citation")
    val fileCitation: FileCitationDto?,
    @SerializedName("start_index")
    val startIndex: Int,
    @SerializedName("end_index")
    val endIndex: Int
)

data class FileCitationDto(
    @SerializedName("file_id")
    val fileId: String,
    @SerializedName("quote")
    val quote: String?
)
