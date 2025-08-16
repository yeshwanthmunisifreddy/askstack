package com.thesubgraph.askstack.features.search.domain.usecase

import com.thesubgraph.askstack.base.utils.network.ErrorModel
import com.thesubgraph.askstack.base.utils.network.NetworkError
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.search.TestFixtures
import com.thesubgraph.askstack.features.search.domain.model.Question
import com.thesubgraph.askstack.features.search.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyBlocking
import org.mockito.kotlin.whenever

class SearchQuestionUseCaseTest {

    private lateinit var repository: QuestionRepository
    private lateinit var useCase: SearchQuestionUseCase



    @Before
    fun setUp() {
        repository = mock()
        useCase = SearchQuestionUseCase(repository)
    }

    @Test
    fun `search should return success result from repository`() = runTest {
        val query = "kotlin"
        val expectedResult = ValueResult.Success(TestFixtures.singleMockQuestion)
        whenever(repository.search(query)).thenReturn(
            flow { emit(expectedResult) }
        )

        val result = useCase.search(query).first()
        verifyBlocking(repository) { search(query) }
        assertTrue(result is ValueResult.Success)
        assertEquals(TestFixtures.singleMockQuestion, (result as ValueResult.Success).data)
    }

    @Test
    fun `search should return error result from repository`() = runTest {
        val query = "kotlin"
        val errorModel = ErrorModel(
            type = NetworkError.NoInternet,
            message = "No internet connection"
        )
        val expectedResult = ValueResult.Failure(errorModel)
        whenever(repository.search(query)).thenReturn(
            flow { emit(expectedResult) }
        )

        val result = useCase.search(query).first()
        verifyBlocking(repository){search(query)}
        assertTrue(result is ValueResult.Failure)
        assertEquals(errorModel, (result as ValueResult.Failure).error)
    }

    @Test
    fun `search should return empty list when repository returns empty`() = runTest {
        val query = "nonexistent"
        val expectedResult = ValueResult.Success(emptyList<Question>())
        whenever(repository.search(query)).thenReturn(
            flow { emit(expectedResult) }
        )

        val result = useCase.search(query).first()

        verify(repository).search(query)
        assertTrue(result is ValueResult.Success)
        assertTrue((result as ValueResult.Success).data.isEmpty())
    }

    @Test
    fun `search should pass query parameter correctly`() = runTest {
        val query = "android kotlin coroutines"
        whenever(repository.search(query)).thenReturn(
            flow { emit(ValueResult.Success(emptyList())) }
        )

        useCase.search(query).first()

        verify(repository).search(query)
    }
}