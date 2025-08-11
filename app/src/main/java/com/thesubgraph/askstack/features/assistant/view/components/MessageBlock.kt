package com.thesubgraph.askstack.features.assistant.view.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thesubgraph.askstack.features.assistant.domain.model.ChatMessage
import com.thesubgraph.askstack.features.assistant.domain.model.MessageRole
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun MessageBlock(
    modifier: Modifier = Modifier,
    message: ChatMessage,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(message)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                UserType(message)
                RichMessageContent(
                    content = message.content,
                    isStreaming = message.role == MessageRole.ASSISTANT && message.isStreaming,
                    modifier = Modifier.fillMaxWidth()
                )
                if (message.sources.isNotEmpty()) {
                    SourcesSection(
                        sources = message.sources,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                if (message.role == MessageRole.ASSISTANT && message.isStreaming) {
                    TypingIndicator(
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Text(
                    text = formatTimestamp(message.timestamp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun UserType(message: ChatMessage) {
    Text(
        text = if (message.role == MessageRole.USER) "You" else "Assistant",
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun Icon(message: ChatMessage) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(GetUserIconBackgroundColor(message)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = GetUserTypeIcon(message),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = if (message.role == MessageRole.USER) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
    }
}

@Composable
private fun GetUserTypeIcon(message: ChatMessage): ImageVector = when (message.role) {
    MessageRole.USER -> {
        Icons.Default.Person
    }

    else -> {
        Icons.Default.SmartToy
    }
}

@Composable
private fun GetUserIconBackgroundColor(message: ChatMessage): Color = when (message.role) {
    MessageRole.USER -> {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    }

    else -> {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
    }
}

@OptIn(ExperimentalTime::class)
private fun formatTimestamp(timestamp: kotlin.time.Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.hour.toString().padStart(2, '0')}:${
        localDateTime.minute.toString().padStart(2, '0')
    }"
}
