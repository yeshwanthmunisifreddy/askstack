package com.thesubgraph.askstack.features.stackoverflow.view.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thesubgraph.askstack.application.navigation.Destination
import com.thesubgraph.askstack.application.navigation.Router
import com.thesubgraph.askstack.base.theme.TextStyle_Size16_Weight600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar(
    router: Router? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "AskStack",
                color = Color.Black,
                fontWeight = FontWeight.W600,
                style = TextStyle_Size16_Weight600
            )
        },
        actions = {
            if (router != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            router.navigateTo(Destination.ChatScreen())
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat Assistant",
                            tint = Color.Black
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            router.navigateTo(Destination.AssistantManagement)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Manage Assistants",
                            tint = Color.Black
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            router.navigateTo(Destination.Settings)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.Black
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
        ),
    )
}
