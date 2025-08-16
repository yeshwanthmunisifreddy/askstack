package com.thesubgraph.askstack.features.search.domain.usecase

import com.thesubgraph.askstack.features.search.domain.repository.QuestionRepository

class SearchQuestionUseCase (private val repository: QuestionRepository){
    suspend fun search(query: String) = repository.search(query)
}