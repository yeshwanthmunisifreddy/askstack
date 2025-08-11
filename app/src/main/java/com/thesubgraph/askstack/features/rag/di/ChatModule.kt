package com.thesubgraph.askstack.features.rag.di

import android.content.Context
import com.thesubgraph.askstack.base.utils.network.RequestWrapper
import com.thesubgraph.askstack.features.rag.data.local.database.ChatDatabase
import com.thesubgraph.askstack.features.rag.data.local.database.dao.ConversationDao
import com.thesubgraph.askstack.features.rag.data.local.database.dao.MessageDao
import com.thesubgraph.askstack.features.rag.data.local.storage.SecurePreferences
import com.thesubgraph.askstack.features.rag.data.remote.OpenAIApiService
import com.thesubgraph.askstack.features.rag.data.remote.streaming.OpenAIStreamHandler
import com.thesubgraph.askstack.features.rag.data.repository.ChatRepositoryImpl
import com.thesubgraph.askstack.features.rag.domain.repository.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return ChatDatabase.create(context)
    }

    @Provides
    fun provideConversationDao(database: ChatDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    fun provideMessageDao(database: ChatDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Named("openai_base_url")
    fun provideOpenAIBaseUrl(): String = "https://api.openai.com/"

    @Provides
    @Singleton
    @Named("openai_retrofit")
    fun provideOpenAIRetrofit(
        @Named("openai_base_url") baseUrl: String,
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .build()

    @Provides
    @Singleton
    fun provideOpenAIApiService(@Named("openai_retrofit") retrofit: Retrofit): OpenAIApiService {
        return retrofit.create(OpenAIApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        openAIApiService: OpenAIApiService,
        conversationDao: ConversationDao,
        messageDao: MessageDao,
        requestWrapper: RequestWrapper,
        streamHandler: OpenAIStreamHandler,
        securePreferences: SecurePreferences
    ): ChatRepository {
        return ChatRepositoryImpl(
            openAIApiService = openAIApiService,
            conversationDao = conversationDao,
            messageDao = messageDao,
            requestWrapper = requestWrapper,
            streamHandler = streamHandler,
            securePreferences = securePreferences
        )
    }
}
