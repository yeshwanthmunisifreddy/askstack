package com.thesubgraph.askstack.features.search.domain.repository

import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.search.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun search(query: String): Flow<ValueResult<List<Question>>>
}