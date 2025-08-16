package com.thesubgraph.askstack.features.search.view.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.thesubgraph.askstack.base.components.ErrorHandle
import com.thesubgraph.askstack.features.search.view.home.HomeUiIntent
import com.thesubgraph.askstack.features.search.viewmodel.HomeViewModel

@Composable
internal fun LoadingAndErrorState(
    viewState: State<HomeViewModel.ViewState>,
    intent: (HomeUiIntent) -> Unit,
) {
    when (val state = viewState.value) {
        is HomeViewModel.ViewState.Error -> {
            ErrorHandle(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                errorType = state.error.type,
                onRetry = { intent(HomeUiIntent.Retry) })
        }

        HomeViewModel.ViewState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        else -> Unit
    }
}

