package com.thesubgraph.askstack.features.search.data.repository

import com.thesubgraph.askstack.base.utils.network.RequestWrapper
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.search.data.remote.ApiService
import com.thesubgraph.askstack.features.search.domain.model.Question
import com.thesubgraph.askstack.features.search.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class QuestionRepositoryImpl(
    private val apiService: ApiService,
    private val requestWrapper: RequestWrapper
) : QuestionRepository {
    override suspend fun search(query: String): Flow<ValueResult<List<Question>>> {
        return flow {
            val response =
                requestWrapper.execute(mapper = { it.items.map { questionDto -> questionDto.mapToDomain() } }) {
                    apiService.search(query)
                }
            emit(response)
        }
    }
}