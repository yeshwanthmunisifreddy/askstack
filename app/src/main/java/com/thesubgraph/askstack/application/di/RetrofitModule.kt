package com.thesubgraph.askstack.application.di

import android.content.Context
import com.google.gson.Gson
import com.thesubgraph.askstack.base.utils.network.RequestWrapper
import com.thesubgraph.askstack.features.search.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    fun provideBaseUrl(): String = "https://api.stackexchange.com/"

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS // Use HEADERS for streaming to avoid logging large response bodies
        }
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun providerRequestWrapper(gson: Gson): RequestWrapper {
        return RequestWrapper(gson = gson)
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        @ApplicationContext context: Context,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(0, TimeUnit.SECONDS) // Disable call timeout for streaming
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.SECONDS) // Disable read timeout for streaming
            .writeTimeout(30, TimeUnit.SECONDS)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(loggingInterceptor)
        return okHttpClient.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

}
