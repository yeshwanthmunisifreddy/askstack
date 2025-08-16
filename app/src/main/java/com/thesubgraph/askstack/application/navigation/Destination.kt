package com.thesubgraph.askstack.application.navigation

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    data object Home : Destination()
    
    @Serializable
    data class ChatScreen(
        val conversationId: String? = null,
        val assistantId: String? = null,
        val initialMessage: String? = null
    ) : Destination()
}