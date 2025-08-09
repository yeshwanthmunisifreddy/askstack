package com.thesubgraph.askstack.features.stackoverflow.domain.usecase

import com.thesubgraph.askstack.features.stackoverflow.domain.repository.QuestionRepository

class SearchQuestionUseCase (private val repository: QuestionRepository){
    suspend fun search(query: String) = repository.search(query)
}