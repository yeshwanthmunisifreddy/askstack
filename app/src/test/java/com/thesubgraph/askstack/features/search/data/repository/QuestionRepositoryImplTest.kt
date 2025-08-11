package com.thesubgraph.askstack.features.search.data.repository

import com.thesubgraph.askstack.base.utils.network.NetworkError
import com.thesubgraph.askstack.base.utils.network.RequestWrapper
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.base.utils.network.WebServiceError
import com.thesubgraph.askstack.base.utils.network.toErrorDomain
import com.thesubgraph.askstack.features.search.TestFixtures
import com.thesubgraph.askstack.features.search.data.remote.ApiService
import com.thesubgraph.askstack.features.search.domain.model.Question
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.times
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.verifyBlocking
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
        val successResult = ValueResult.Success(TestFixtures.mockQuestions)

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(successResult)

        val result = repository.search(query).first()
        assertTrue(result is ValueResult.Success)
        assertEquals(TestFixtures.mockQuestions, (result as ValueResult.Success).data)
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
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(errorResult)

        val result = repository.search(query).first()
        assertTrue(result is ValueResult.Failure)
        assertEquals(errorMessage, (result as ValueResult.Failure).error.message)
        verifyBlocking(requestWrapper) {
            execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        }
    }

    @Test
    fun `search returns empty list when api returns empty response`() = runTest {
        val query = "nonexistent query"
        val emptyResult = ValueResult.Success(emptyList<Question>())

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(emptyResult)

        val result = repository.search(query).first()
        assertTrue(result is ValueResult.Success)
        assertTrue((result as ValueResult.Success).data.isEmpty())
    }

    @Test
    fun `search handles empty query string`() = runTest {
        val emptyQuery = ""
        val successResult = ValueResult.Success(TestFixtures.mockQuestions)

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(successResult)

        val result = repository.search(emptyQuery).first()
        assertTrue(result is ValueResult.Success)
        
        verifyBlocking(requestWrapper) {
            execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        }
    }

    @Test
    fun `search handles special characters in query`() = runTest {
        val specialQuery = "android & kotlin @ #test"
        val successResult = ValueResult.Success(TestFixtures.mockQuestions)

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(successResult)

        val result = repository.search(specialQuery).first()
        assertTrue(result is ValueResult.Success)
        assertEquals(TestFixtures.mockQuestions, (result as ValueResult.Success).data)
    }

    @Test
    fun `search returns different error types correctly`() = runTest {
        val query = "test query"
        val serverError = ValueResult.Failure("Server Error".toErrorDomain(WebServiceError.ServerError))

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(serverError)

        val result = repository.search(query).first()
        assertTrue(result is ValueResult.Failure)
        assertEquals(WebServiceError.ServerError, (result as ValueResult.Failure).error.type)
    }

    @Test
    fun `search flow emits single value per call`() = runTest {
        val query = "kotlin"
        val successResult = ValueResult.Success(TestFixtures.mockQuestions)

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(successResult)

        val results = repository.search(query).toList()
        assertEquals(1, results.size)
        assertTrue(results.first() is ValueResult.Success)
    }

    @Test
    fun `search with long query string works correctly`() = runTest {
        val longQuery = "a".repeat(1000) // Very long query
        val successResult = ValueResult.Success(TestFixtures.singleMockQuestion)

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(successResult)

        val result = repository.search(longQuery).first()
        assertTrue(result is ValueResult.Success)
        assertEquals(TestFixtures.singleMockQuestion, (result as ValueResult.Success).data)
    }

    @Test
    fun `search handles timeout error correctly`() = runTest {
        val query = "android"
        val timeoutError = ValueResult.Failure("Request timed out".toErrorDomain(NetworkError.RequestTimedOut))

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(timeoutError)

        val result = repository.search(query).first()
        assertTrue(result is ValueResult.Failure)
        assertEquals(NetworkError.RequestTimedOut, (result as ValueResult.Failure).error.type)
    }

    @Test
    fun `multiple calls to search create independent flows`() = runTest {
        val query1 = "kotlin"
        val query2 = "android"
        val result1 = ValueResult.Success(TestFixtures.singleMockQuestion)
        val result2 = ValueResult.Success(TestFixtures.mockQuestions)

        whenever(
            requestWrapper.execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        ).thenReturn(result1).thenReturn(result2)

        val flow1Result = repository.search(query1).first()
        val flow2Result = repository.search(query2).first()

        assertTrue(flow1Result is ValueResult.Success)
        assertTrue(flow2Result is ValueResult.Success)
        
        verifyBlocking(requestWrapper, times(2)) {
            execute(
                mapper = any<Function1<Any, List<Question>?>>(),
                apiCall = any<suspend () -> Response<Any>>()
            )
        }
    }
}