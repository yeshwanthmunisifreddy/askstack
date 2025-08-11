package com.thesubgraph.askstack.features.search.di

import com.thesubgraph.askstack.features.search.domain.repository.QuestionRepository
import com.thesubgraph.askstack.features.search.domain.usecase.SearchQuestionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @ViewModelScoped
    @Provides
    fun provideSearchQuestionUseCase(repository: QuestionRepository): SearchQuestionUseCase {
        return SearchQuestionUseCase(repository)
    }
}