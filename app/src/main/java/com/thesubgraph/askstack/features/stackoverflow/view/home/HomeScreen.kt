package com.thesubgraph.askstack.features.stackoverflow.view.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.thesubgraph.askstack.application.navigation.Router
import com.thesubgraph.askstack.base.components.EmptySearchResults
import com.thesubgraph.askstack.base.components.SearchBar
import com.thesubgraph.askstack.features.stackoverflow.domain.model.Question
import com.thesubgraph.askstack.features.stackoverflow.view.common.components.QuestionList
import com.thesubgraph.askstack.features.stackoverflow.view.home.components.LoadingAndErrorState
import com.thesubgraph.askstack.features.stackoverflow.view.home.components.SearchStateMessage
import com.thesubgraph.askstack.features.stackoverflow.view.home.components.TopBar
import com.thesubgraph.askstack.features.stackoverflow.viewmodel.HomeViewModel


sealed class HomeUiIntent() {
    data object OnBackPressed : HomeUiIntent()
    data class OnItemClick(val question: Question) :
        HomeUiIntent()

    data class OnChangeSearchQuery(val query: String) : HomeUiIntent()
    data object Retry : HomeUiIntent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(router: Router, viewModel: HomeViewModel) {
    val searchQuery = viewModel.searchQuery.collectAsState()
    val viewState = viewModel.viewState.collectAsState()
    val searchResults = viewModel.searchResults.collectAsState()
    val showBottomSheet = remember { mutableStateOf(false) }
    val selectedQuestion = remember { mutableStateOf<Question?>(null) }
    ScreenContent(
        viewState = viewState,
        searchQuery = searchQuery,
        searchResults = searchResults,
        showBottomSheet = showBottomSheet,
        selectedQuestion = selectedQuestion,
        router = router,
        intent = { intent ->
            performIntent(
                intent = intent,
                router = router,
                viewModel = viewModel,
                showBottomSheet = showBottomSheet,
                selectedQuestion = selectedQuestion
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class)
private fun performIntent(
    intent: HomeUiIntent,
    router: Router,
    viewModel: HomeViewModel,
    showBottomSheet: MutableState<Boolean>,
    selectedQuestion: MutableState<Question?>,
) {
    when (intent) {
        HomeUiIntent.OnBackPressed -> {
            router.navigateUp()
        }

        is HomeUiIntent.OnItemClick -> {
            selectedQuestion.value = intent.question
            showBottomSheet.value = true

        }

        is HomeUiIntent.OnChangeSearchQuery -> {
            viewModel.onChangeSearchQuery(intent.query)
        }

        HomeUiIntent.Retry -> {
            viewModel.retrySearch()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenContent(
    viewState: State<HomeViewModel.ViewState>,
    searchQuery: State<String>,
    searchResults: State<List<Question>>,
    showBottomSheet: MutableState<Boolean>,
    selectedQuestion: MutableState<Question?>,
    intent: (HomeUiIntent) -> Unit,
    router: Router
) {
    val state = rememberLazyListState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = { TopBar(router) }) { windowInsets ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(windowInsets)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                onValueChange = { query ->
                    intent(HomeUiIntent.OnChangeSearchQuery(query))
                },
                placeholderText = "Search for questions",
            )
            Content(
                searchQuery = searchQuery,
                viewState = viewState,
                searchResults = searchResults,
                state = state,
                intent = intent
            )

        }
        BottomSheetContent(
            showBottomSheet = showBottomSheet,
            selectedQuestion = selectedQuestion,
        )
    }
}

@Composable
private fun Content(
    searchQuery: State<String>,
    viewState: State<HomeViewModel.ViewState>,
    searchResults: State<List<Question>>,
    state: LazyListState,
    intent: (HomeUiIntent) -> Unit
) {
    val hasData by remember {
        derivedStateOf {
            viewState.value !is HomeViewModel.ViewState.Error && searchResults.value.isNotEmpty()
        }
    }
    val isEmptyResults by remember {
        derivedStateOf {
            searchResults.value.isEmpty() &&
                    searchQuery.value.length >= 3 &&
                    viewState.value is HomeViewModel.ViewState.Loaded
        }
    }
    val showSearchInfo by remember {
        derivedStateOf { searchQuery.value.length < 3 }
    }

    Box {
        when {
            showSearchInfo -> {
                SearchStateMessage(searchQuery.value)
            }

            hasData -> {
                QuestionList(
                    state = state, searchResults = searchResults,
                    onClick = { question ->
                        intent(HomeUiIntent.OnItemClick(question))
                    }
                )
            }
        }

        if (isEmptyResults) {
            EmptySearchResults(modifier = Modifier.fillMaxSize())
        }
        LoadingAndErrorState(
            viewState = viewState,
            intent = intent
        )
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    ScreenContent(
        viewState = remember { mutableStateOf(HomeViewModel.ViewState.Loading) },
        searchQuery = remember { mutableStateOf("") },
        searchResults = remember { mutableStateOf(emptyList()) },
        showBottomSheet = remember { mutableStateOf(false) },
        selectedQuestion = remember { mutableStateOf(null) },
        intent = {},
        router = Router(rememberNavController())
    )
}
