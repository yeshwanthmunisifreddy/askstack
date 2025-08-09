package com.thesubgraph.askstack.base.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.thesubgraph.askstack.base.theme.TextStyle_Size14_Weight400


@Composable
fun NetworkStatusBanner(
    isConnected: State<Boolean>,
    showConnectedMessage: State<Boolean>
) {
    AnimatedVisibility(
        visible = !isConnected.value || showConnectedMessage.value,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(300)
        ),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = when {
                        isConnected.value -> Color(0xFF4CAF50)
                        else -> Color(0xFFF44336)
                    }
                )
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isConnected.value) {
                    "Connected"
                } else "No Internet Connection",
                color = Color.White,
                style = TextStyle_Size14_Weight400
            )
        }
    }
}