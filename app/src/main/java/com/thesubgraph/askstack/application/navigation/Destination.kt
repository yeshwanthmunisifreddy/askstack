package com.thesubgraph.askstack.application.navigation

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    data object Home : Destination()
}