package com.thesubgraph.askstack.features.search.view.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thesubgraph.askstack.application.navigation.Router
import com.thesubgraph.askstack.features.assistant.view.components.ChatInput

@Composable
fun NewChatInterface(
    router: Router,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val messageInput = remember { mutableStateOf("") }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Main content area - welcome message
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(48.dp)
            ) {
                Text(
                    text = "Ask me anything",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "Start a conversation with AI assistance",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Chat input at bottom
        ChatInput(
            value = messageInput.value,
            onValueChange = { messageInput.value = it },
            onSendClick = {
                if (messageInput.value.isNotBlank()) {
                    onSendMessage(messageInput.value)
                    messageInput.value = ""
                }
            },
            placeholder = "Ask anything...",
            isStreaming = false, // No streaming in new chat interface
            modifier = Modifier
        )
    }
}
