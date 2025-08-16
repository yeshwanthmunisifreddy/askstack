package com.thesubgraph.askstack.features.search.data.serialization

import com.thesubgraph.askstack.base.utils.DateUtils
import com.thesubgraph.askstack.base.utils.network.ResponseDomainMapper
import com.thesubgraph.askstack.features.search.domain.model.Question
import kotlinx.serialization.Serializable

@Serializable
data class QuestionDto(
    val question_id: Long? = null,
    val title: String? = null,
    val is_answered: Boolean? = null,
    val view_count: Int? = null,
    val answer_count: Int? = null,
    val score: Int? = null,
    val last_activity_date: Long? = null,
    val creation_date: Long? = null,
    val last_edit_date: Long? = null,
    val link: String? = null,
    val tags: List<String>? = null,
    val owner: OwnerDto?= null,
    val body: String? = null
): ResponseDomainMapper<Question>{
    override fun mapToDomain(): Question {
        return Question(
            id = question_id ?: 0L,
            title = title ?: "",
            body = body,
            creationDate = DateUtils.convertTimestampToLocalDateTime(creation_date?:0L),
            lastActivityDate = DateUtils.convertTimestampToLocalDateTime(last_activity_date?:0L),
            lastEditDate = DateUtils.convertTimestampToLocalDateTime(last_edit_date?:0L),
            score = score ?: 0,
            answerCount = answer_count ?: 0,
            viewCount = view_count ?: 0,
            link = link ?: "",
            isAnswered = is_answered ?: false,
            tags = tags ?: emptyList(),
            owner = owner?.mapToDomain()
        )
    }

}
