package com.thesubgraph.askstack.base.utils.network

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.serialization.SerializationException
import retrofit2.Response
import javax.inject.Inject

class RequestWrapper @Inject constructor(
    private val gson: Gson,
) {
    suspend fun <T, U> execute(
        mapper: (T) -> U?,
        apiCall: suspend () -> Response<T>,
    ): ValueResult<U> {
        val result = execute(apiCall)
        val data = result.value?.let { mapper(it) }

        return data?.let { ValueResult.Success(it) }
            ?: (result as? ValueResult.Failure)?.let { ValueResult.Failure(result.error) }
            ?: ValueResult.Failure(WebServiceError.DecodeFailed.toDomain())
    }


    private suspend fun <T> execute(apiCall: suspend () -> Response<T>): ValueResult<T> {
        try {
            val response = apiCall()
            val code = response.code()
            val isAuthenticationError = (code == 401)

            // Handle authentication error by checking if we can refresh the token not implemented here later we will add
            if (isAuthenticationError) {
                return ValueResult.Failure(WebServiceError.Authentication.toDomain())
            }


            return when (response.code()) {
                in 200 until 300 -> {
                    val body = response.body()
                    body?.let {
                        ValueResult.Success(it)
                    } ?: ValueResult.Failure(WebServiceError.DecodeFailed.toDomain())
                }

                400 -> {
                    val error = ErrorMapper(response, gson).mapToDomain()
                    ValueResult.Failure(error)
                }

                401 -> {
                    ValueResult.Failure(WebServiceError.Authentication.toDomain())
                }

                403 -> ValueResult.Failure(WebServiceError.Authorization.toDomain())
                404 -> ValueResult.Failure(NetworkError.ServerNotFound.toDomain())
                422 -> ValueResult.Failure(WebServiceError.Custom.toDomain())
                500 -> ValueResult.Failure(WebServiceError.ServerError.toDomain())
                else -> ValueResult.Failure(WebServiceError.Unknown.toDomain())
            }

        } catch (e: Exception) {
            return handleException(e)
        }
    }


    fun handleException(e: Exception): ValueResult.Failure =
        when (e) {
            is SerializationException -> ValueResult.Failure(
                e.message?.toErrorDomain() ?: WebServiceError.DecodeFailed.toDomain()
            )

            is TimeoutCancellationException,
            is java.net.SocketTimeoutException ->
                ValueResult.Failure(NetworkError.RequestTimedOut.toDomain())

            is java.net.UnknownHostException ->
                ValueResult.Failure(NetworkError.NoInternet.toDomain())

            is java.net.ConnectException ->
                ValueResult.Failure(NetworkError.ServerNotFound.toDomain())

            else -> {
                Log.w("RequestWrapper", "Unhandled exception: ${e::class.simpleName}", e)
                ValueResult.Failure(
                    e.message?.toErrorDomain() ?: WebServiceError.Unknown.toDomain()
                )
            }
        }

}