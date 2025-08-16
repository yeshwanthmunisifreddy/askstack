package com.thesubgraph.askstack.features.search.viewmodel

import com.thesubgraph.askstack.base.utils.network.ErrorModel
import com.thesubgraph.askstack.base.utils.network.NetworkError
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.search.TestFixtures
import com.thesubgraph.askstack.features.search.domain.usecase.SearchQuestionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var searchQuestionUseCase: SearchQuestionUseCase
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = UnconfinedTestDispatcher()



    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        searchQuestionUseCase = mock()
        viewModel = HomeViewModel(searchQuestionUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() {
        assertEquals(HomeViewModel.ViewState.Initial, viewModel.viewState.value)
        assertEquals("", viewModel.searchQuery.value)
        assertTrue(viewModel.searchResults.value.isEmpty())
    }

    @Test
    fun `onChangeSearchQuery should update search query`() {
        val query = "kotlin"
        viewModel.onChangeSearchQuery(query)
        assertEquals(query, viewModel.searchQuery.value)
    }

    @Test
    fun `search should be triggered when query length is 3 or more characters`() = runTest {
        val query = "kotlin"
        whenever(searchQuestionUseCase.search(query)).thenReturn(
            flow { emit(ValueResult.Success(TestFixtures.mockQuestions)) }
        )
        viewModel.onChangeSearchQuery(query)
        advanceTimeBy(300)
        advanceUntilIdle()

        verify(searchQuestionUseCase).search(query)
        assertEquals(HomeViewModel.ViewState.Loaded, viewModel.viewState.value)
        assertEquals(TestFixtures.mockQuestions, viewModel.searchResults.value)
    }

    @Test
    fun `search should not be triggered when query length is less than 3`() = runTest {
        val query = "ko"
        viewModel.onChangeSearchQuery(query)
        advanceTimeBy(300)
        advanceUntilIdle()

        verify(searchQuestionUseCase, org.mockito.kotlin.never()).search(any())
        assertEquals(HomeViewModel.ViewState.Initial, viewModel.viewState.value)
        assertTrue(viewModel.searchResults.value.isEmpty())
    }

    @Test
    fun `searchQuestions should set loading state initially`() = runTest {
        val query = "kotlin"
        whenever(searchQuestionUseCase.search(query)).thenReturn(
            flow { emit(ValueResult.Success(TestFixtures.mockQuestions)) }
        )
        viewModel.searchQuestions(query)
        assertEquals(HomeViewModel.ViewState.Loading, viewModel.viewState.value)
    }

    @Test
    fun `onChangeSearchQuery should handle success result correctly`() = runTest {
        val query = "kotlin"
        whenever(searchQuestionUseCase.search(query)).thenReturn(
            flow { emit(ValueResult.Success(TestFixtures.mockQuestions)) }
        )
        viewModel.onChangeSearchQuery(query)
        advanceTimeBy(300)
        advanceUntilIdle()
        
        verify(searchQuestionUseCase).search(query)
        assertEquals(HomeViewModel.ViewState.Loaded, viewModel.viewState.value)
        assertEquals(TestFixtures.mockQuestions, viewModel.searchResults.value)
    }

    @Test
    fun `onChangeSearchQuery should handle error result correctly`() = runTest {
        val query = "kotlin"
        val errorModel = ErrorModel(
            type = NetworkError.NoInternet,
            message = "No internet connection"
        )
        whenever(searchQuestionUseCase.search(query)).thenReturn(
            flow { emit(ValueResult.Failure(errorModel)) }
        )

        viewModel.onChangeSearchQuery(query)
        advanceTimeBy(300)
        advanceUntilIdle()

        verify(searchQuestionUseCase).search(query)
        assertEquals(HomeViewModel.ViewState.Error(errorModel), viewModel.viewState.value)
    }

    @Test
    fun `onChangeSearchQuery should handle empty success result correctly`() = runTest {
        val query = "kotlin"
        whenever(searchQuestionUseCase.search(query)).thenReturn(
            flow { emit(ValueResult.Success(emptyList())) }
        )

        viewModel.onChangeSearchQuery(query)
        advanceTimeBy(300)
        advanceUntilIdle()

        verify(searchQuestionUseCase).search(query)
        assertEquals(HomeViewModel.ViewState.Loaded, viewModel.viewState.value)
        assertTrue(viewModel.searchResults.value.isEmpty())
    }

    @Test
    fun `retrySearch should call searchQuestions with current query`() = runTest {
        val query = "kotlin"
        whenever(searchQuestionUseCase.search(query)).thenReturn(
            flow { emit(ValueResult.Success(TestFixtures.mockQuestions)) }
        )
        
        viewModel.onChangeSearchQuery(query)
        advanceTimeBy(300)
        advanceUntilIdle()

        viewModel.retrySearch()
        advanceUntilIdle()

        verify(searchQuestionUseCase, org.mockito.kotlin.times(2)).search(query)
        assertEquals(HomeViewModel.ViewState.Loaded, viewModel.viewState.value)
    }

    @Test
    fun `debounce should prevent multiple rapid searches`() = runTest {
        whenever(searchQuestionUseCase.search(any())).thenReturn(
            flow { emit(ValueResult.Success(TestFixtures.mockQuestions)) }
        )

        viewModel.onChangeSearchQuery("kot")
        advanceTimeBy(100)
        viewModel.onChangeSearchQuery("kotl")
        advanceTimeBy(100)
        viewModel.onChangeSearchQuery("kotlin")
        advanceTimeBy(300)
        advanceUntilIdle()

        verify(searchQuestionUseCase, org.mockito.kotlin.times(1)).search("kotlin")
    }

    @Test
    fun `distinctUntilChanged should prevent duplicate searches`() = runTest {
        val query = "kotlin"
        whenever(searchQuestionUseCase.search(query)).thenReturn(
            flow { emit(ValueResult.Success(TestFixtures.mockQuestions)) }
        )

        viewModel.onChangeSearchQuery(query)
        advanceTimeBy(300)
        advanceUntilIdle()
        viewModel.onChangeSearchQuery(query)
        advanceTimeBy(300)
        advanceUntilIdle()

        verify(searchQuestionUseCase, org.mockito.kotlin.times(1)).search(query)
    }

    @Test
    fun `clearing query should reset results and state`() = runTest {
        val query = "kotlin"
        whenever(searchQuestionUseCase.search(query)).thenReturn(
            flow { emit(ValueResult.Success(TestFixtures.mockQuestions)) }
        )

        viewModel.onChangeSearchQuery(query)
        advanceTimeBy(300)
        advanceUntilIdle()

        viewModel.onChangeSearchQuery("")
        advanceTimeBy(300)
        advanceUntilIdle()

        assertEquals(HomeViewModel.ViewState.Initial, viewModel.viewState.value)
        assertTrue(viewModel.searchResults.value.isEmpty())
    }
}