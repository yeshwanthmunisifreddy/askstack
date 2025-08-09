package com.thesubgraph.askstack.features.stackoverflow.data.repository

import com.thesubgraph.askstack.base.utils.network.NetworkError
import com.thesubgraph.askstack.base.utils.network.RequestWrapper
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.base.utils.network.toErrorDomain
import com.thesubgraph.askstack.features.stackoverflow.data.remote.ApiService
import com.thesubgraph.askstack.features.stackoverflow.domain.model.Question
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.verify
import retrofit2.Response

class QuestionRepositoryImplTest {

    private lateinit var apiService: ApiService
    private lateinit var requestWrapper: RequestWrapper
    private lateinit var repository: QuestionRepositoryImpl

    @Before
    fun setUp() {
        apiService = mock()
        requestWrapper = mock()
        repository = QuestionRepositoryImpl(apiService, requestWrapper)
    }

    @Test
    fun `search returns success result when api call succeeds`() = runTest {
        val query = "android kotlin"
        val expectedQuestions = listOf(
            Question(
                id = 1,
                title = "Test Question",
                body = "This is a test question body.",
                creationDate = kotlinx.datetime.LocalDateTime(2023, 1, 1, 12, 0),
                lastActivityDate = kotlinx.datetime.LocalDateTime(2023, 1, 1, 12, 0),
                lastEditDate = kotlinx.datetime.LocalDateTime(2023, 1, 1, 12, 0),
                score = 10,
                answerCount = 2,
                viewCount = 100,
                link = "https://stackoverflow.com/questions/1",
                isAnswered = true,
                tags = listOf("android", "kotlin")
            ),
            Question(
                id = 2,
                title = "Another Question",
                body = "Another test question body.",
                creationDate = kotlinx.datetime.LocalDateTime(2023, 1, 2, 12, 0),
                lastActivityDate = kotlinx.datetime.LocalDateTime(2023, 1, 2, 12, 0),
                lastEditDate = kotlinx.datetime.LocalDateTime(2023, 1, 2, 12, 0),
                score = 5,
                answerCount = 1,
                viewCount = 50,
                link = "https://stackoverflow.com/questions/2",
                isAnswered = false,
                tags = listOf("kotlin")
            )
        )
        val successResult = ValueResult.Success(expectedQuestions)

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> retrofit2.Response<Any>>()
            )
        ).thenReturn(successResult)

        // When
        val result = repository.search(query).first()

        // Then
        assertTrue(result is ValueResult.Success)
        assertEquals(expectedQuestions, (result as ValueResult.Success).data)
    }

    @Test
    fun `search returns error result when api call fails`() = runTest {
        // Given
        val query = "android kotlin"
        val errorMessage = "Network error"
        val errorResult = ValueResult.Failure(errorMessage.toErrorDomain(NetworkError.NoInternet))


        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> retrofit2.Response<Any>>()
            )
        ).thenReturn(errorResult)

        // When
        val result = repository.search(query).first()

        // Then
        assertTrue(result is ValueResult.Failure)
        assertEquals(errorMessage, (result as ValueResult.Failure).error.message)
        verify(requestWrapper).execute(
            mapper = any<Function1<Any, List<Question>?>>(),
            apiCall = any<suspend () -> Response<Any>>()
        )
    }

    @Test
    fun `execute should return server error for 500 response code`() = runTest {
        // Given
        val mockResponse = mock<Response<String>>()
        whenever(mockResponse.code()).thenReturn(500)
        whenever(mockResponse.body()).thenReturn(null)


        val result = requestWrapper.execute(mapper = any<Function1<Any, List<Question>?>>(),
            apiCall = any<suspend () -> Response<Any>>()) { mockResponse }


        // Then
        assertTrue(result is ValueResult.Failure)
        // Verify the error type matches WebServiceError.ServerError
    }

    @Test
    fun `execute should return authorization error for 403 response code`() = runTest {
        // Given
        val mockResponse = mockk<Response<String>>()
        every { mockResponse.code() } returns 403

        // When
        val result = requestWrapper.execute { it } { mockResponse }

        // Then
        assertTrue(result is ValueResult.Failure)
        // Verify the error type matches WebServiceError.Authorization
    }

    @Test
    fun `execute should return server not found for 404 response code`() = runTest {
        // Given
        val mockResponse = mockk<Response<String>>()
        every { mockResponse.code() } returns 404

        // When
        val result = requestWrapper.execute { it } { mockResponse }

        // Then
        assertTrue(result is ValueResult.Failure)
        // Verify the error type matches NetworkError.ServerNotFound
    }

    @Test
    fun `execute should return custom error for 422 response code`() = runTest {
        // Given
        val mockResponse = mock<Response<String>>()
        every { mockResponse.code() } returns 422

        // When
        val result = requestWrapper.execute { it } { mockResponse }

        // Then
        assertTrue(result is ValueResult.Failure)
        // Verify the error type matches WebServiceError.Custom
    }

    @Test
    fun `handleException should return timeout error for SocketTimeoutException`() {
        // Given
        val exception = java.net.SocketTimeoutException("Connection timeout")

        // When
        val result = requestWrapper.handleException(exception)

        // Then
        assertTrue(result is ValueResult.Failure)
        // Verify the error type matches NetworkError.RequestTimedOut
    }

    @Test
    fun `handleException should return no internet error for UnknownHostException`() {
        // Given
        val exception = java.net.UnknownHostException("No internet")

        // When
        val result = requestWrapper.handleException(exception)

        // Then
        assertTrue(result is ValueResult.Failure)
        // Verify the error type matches NetworkError.NoInternet
    }

    @Test
    fun `handleException should return server not found for ConnectException`() {
        // Given
        val exception = java.net.ConnectException("Connection refused")

        // When
        val result = requestWrapper.handleException(exception)

        // Then
        assertTrue(result is ValueResult.Failure)
        // Verify the error type matches NetworkError.ServerNotFound
    }

}