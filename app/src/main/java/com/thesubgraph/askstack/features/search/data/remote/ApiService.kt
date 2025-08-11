package com.thesubgraph.askstack.features.search.data.remote

import com.thesubgraph.askstack.features.search.data.serialization.QuestionDto
import com.thesubgraph.askstack.features.search.data.serialization.ResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("2.3/search/advanced")
    suspend fun search(
        @Query("q") query: String,
        @Query("site") site: String = "stackoverflow",
        @Query("order") page: String = "desc",
        @Query("filter") filter: String = "withbody",
    ): Response<ResponseDto<List<QuestionDto>>>
}
