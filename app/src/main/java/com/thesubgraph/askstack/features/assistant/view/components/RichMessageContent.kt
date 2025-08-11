package com.thesubgraph.askstack.features.assistant.view.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RichMessageContent(
    modifier: Modifier = Modifier,
    content: String,
    isStreaming: Boolean = false,
) {
    val parsedElements = MessageParser.parseMessage(content)

    SelectionContainer {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            parsedElements.forEachIndexed { index, element ->
                when (element) {
                    is MessageElement.Text -> {
                        val showCursor = isStreaming && index == parsedElements.lastIndex

                        if (showCursor) {
                            StreamingText(
                                text = element.content,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = element.content,
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    is MessageElement.CodeBlock -> {
                        CodeBlock(
                            code = element.code,
                            language = element.language,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    is MessageElement.InlineCode -> {
                        InlineCode(
                            code = element.code
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreamingText(
    text: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    val annotatedText = buildAnnotatedString {
        append(text)
        if (text.isNotEmpty()) {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = cursorAlpha)
                )
            ) {
                append("▋")
            }
        }
    }

    Text(
        text = annotatedText,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}
