package com.thesubgraph.askstack.features.search.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    val id: Long,
    val accountId: Long,
    val reputation: Long,
    val userType: String,
    val profileImage: String? = null,
    val displayName: String,
    val link: String
)
