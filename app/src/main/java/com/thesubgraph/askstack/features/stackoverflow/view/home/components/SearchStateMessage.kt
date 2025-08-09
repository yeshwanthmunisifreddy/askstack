package com.thesubgraph.askstack.features.stackoverflow.view.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thesubgraph.askstack.base.theme.SlateGrey
import com.thesubgraph.askstack.base.theme.TextStyle_Size14_Weight400

@Composable
fun SearchStateMessage(searchQuery: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        val message = getMessage(searchQuery)
        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = SlateGrey,
                style = TextStyle_Size14_Weight400,
            )
        }
    }
}

@Composable
private fun getMessage(searchQuery: String): String = when (searchQuery.length) {
    0 -> "Enter three or more characters"
    1 -> "Two more characters required"
    2 -> "One more character required"
    else -> ""
}