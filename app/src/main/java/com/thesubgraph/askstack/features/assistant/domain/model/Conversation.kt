package com.thesubgraph.askstack.features.assistant.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class Conversation @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val title: String,
    val threadId: String,
    val assistantId: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val lastMessage: String? = null
)
