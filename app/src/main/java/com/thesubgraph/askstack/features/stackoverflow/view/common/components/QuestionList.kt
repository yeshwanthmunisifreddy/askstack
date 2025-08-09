package com.thesubgraph.askstack.features.stackoverflow.view.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thesubgraph.askstack.features.stackoverflow.domain.model.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionList(
    state: LazyListState,
    searchResults: State<List<Question>>,
    onClick: (Question) -> Unit
) {
    LazyColumn(
        state = state,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)

    ) {
        items(searchResults.value, key = { question -> question.id }) { question ->
            QuestionItem(question = question, onClick = {
                onClick(question)
            })
        }
    }
}
