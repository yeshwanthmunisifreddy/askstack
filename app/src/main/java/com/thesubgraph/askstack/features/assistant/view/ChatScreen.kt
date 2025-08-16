package com.thesubgraph.askstack.features.assistant.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thesubgraph.askstack.features.assistant.view.components.ChatInput
import com.thesubgraph.askstack.features.assistant.view.components.MessageBlock
import com.thesubgraph.askstack.features.assistant.viewmodel.ChatUiState
import com.thesubgraph.askstack.features.assistant.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String? = null,
    assistantId: String? = null,
    initialMessage: String? = null,
    onBackPressed: () -> Unit,
    viewModel: ChatViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val messageInput by viewModel.messageInput.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId, assistantId, initialMessage) {
        viewModel.initializeChat(conversationId, assistantId, initialMessage)
    }

    LaunchedEffect(uiState.messages.size, uiState.messages.lastOrNull()?.content?.length) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Content(
        onBackPressed = onBackPressed,
        messageInput = messageInput,
        viewModel = viewModel,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        listState = listState
    )
}

@Composable
private fun Content(
    onBackPressed: () -> Unit,
    messageInput: String,
    viewModel: ChatViewModel,
    uiState: ChatUiState,
    snackbarHostState: SnackbarHostState,
    listState: LazyListState
) {
    Scaffold(
        topBar = {
            TopBar(onBackPressed)
        },
        bottomBar = {
            ChatInput(
                value = messageInput,
                onValueChange = viewModel::setMessageInput,
                onSendClick = viewModel::sendMessage,
                onStopClick = viewModel::stopStreaming,
                enabled = !uiState.isSending && !uiState.isLoading,
                isStreaming = uiState.isSending || uiState.messages.any { it.isStreaming }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && uiState.messages.isEmpty()) {
                Loading()
            } else if (uiState.messages.isEmpty()) {
                EmptyContent()
            } else {
                MessageList(listState, paddingValues, uiState)
            }
        }
    }
}

@Composable
private fun MessageList(
    listState: LazyListState,
    paddingValues: PaddingValues,
    uiState: ChatUiState
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        items(
            items = uiState.messages,
            key = { it.id }
        ) { message ->
            MessageBlock(
                message = message,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TopBar(onBackPressed: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackPressed) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun Loading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Loading conversation...",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(48.dp)
        ) {
            Text(
                text = "Ask me anything",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Start a conversation by typing a message below",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
