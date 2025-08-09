package com.thesubgraph.askstack.features.stackoverflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesubgraph.askstack.base.utils.network.ErrorModel
import com.thesubgraph.askstack.base.utils.network.ValueResult
import com.thesubgraph.askstack.features.stackoverflow.domain.model.Question
import com.thesubgraph.askstack.features.stackoverflow.domain.usecase.SearchQuestionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchQuestionUseCase: SearchQuestionUseCase
) : ViewModel() {
    sealed class ViewState {
        data object Initial : ViewState()
        data object Loading : ViewState()
        data object Loaded : ViewState()
        data class Error(val error: ErrorModel) : ViewState()
    }

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Initial)
    val viewState by lazy { _viewState.asStateFlow() }
    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    val searchQuery by lazy { _searchQuery.asStateFlow() }
    private val _searchResults: MutableStateFlow<List<Question>> = MutableStateFlow(emptyList())
    val searchResults by lazy { _searchResults.asStateFlow() }


    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.length >= 3) {
                        searchQuestions(query)
                    } else {
                        _searchResults.value = emptyList()
                        _viewState.value = ViewState.Initial
                    }
                }
        }
    }
    fun retrySearch() {
        searchQuestions(_searchQuery.value)
    }

    fun searchQuestions(query: String) {
        _viewState.value = ViewState.Loading
        viewModelScope.launch {
            searchQuestionUseCase.search(query).flowOn(Dispatchers.IO).collect { result ->
                when (result) {
                    is ValueResult.Failure -> {
                        _viewState.value = ViewState.Error(result.error)
                    }

                    is ValueResult.Success -> {
                        _searchResults.value = result.value ?: emptyList()
                        _viewState.value = ViewState.Loaded
                    }
                }
            }
        }
    }

    fun onChangeSearchQuery(query: String) {
        _searchQuery.value = query
    }
}