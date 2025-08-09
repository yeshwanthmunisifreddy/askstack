package com.thesubgraph.askstack.features.stackoverflow.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: Long,
    val title: String,
    val body: String?,
    val creationDate: LocalDateTime,
    val lastActivityDate: LocalDateTime,
    val lastEditDate: LocalDateTime,
    val score: Int,
    val answerCount: Int,
    val viewCount: Int,
    val link: String,
    val isAnswered: Boolean,
    val tags: List<String>,
    val owner: Owner?= null
)
