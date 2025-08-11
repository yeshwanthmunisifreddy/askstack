package com.thesubgraph.askstack.features.search.di

import com.thesubgraph.askstack.base.utils.network.RequestWrapper
import com.thesubgraph.askstack.features.search.data.remote.ApiService
import com.thesubgraph.askstack.features.search.data.repository.QuestionRepositoryImpl
import com.thesubgraph.askstack.features.search.domain.repository.QuestionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideQuestionRepository(
        requestWrapper: RequestWrapper,
        apiService: ApiService
    ): QuestionRepository {
        return QuestionRepositoryImpl(
            apiService = apiService,
            requestWrapper = requestWrapper
        )
    }
}