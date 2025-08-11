package com.thesubgraph.askstack.application.navigation

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    data object Home : Destination()
    
    @Serializable
    data class ChatScreen(
        val conversationId: String? = null,
        val assistantId: String? = null
    ) : Destination()
    
    @Serializable
    data object Settings : Destination()
    
    @Serializable
    data object AssistantManagement : Destination()
    
    @Serializable
    data object CreateAssistant : Destination()
}