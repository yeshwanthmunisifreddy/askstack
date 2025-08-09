package com.thesubgraph.askstack.features.stackoverflow.data.serialization

import com.thesubgraph.askstack.base.utils.network.ResponseDomainMapper
import com.thesubgraph.askstack.features.stackoverflow.domain.model.Owner
import kotlinx.serialization.Serializable

@Serializable
data class OwnerDto(
    val account_id: Long? = null,
    val reputation: Long? = null,
    val user_id: Long? = null,
    val user_type: String? = null,
    val profile_image: String? = null,
    val display_name: String? = null,
    val link: String? = null
) : ResponseDomainMapper<Owner> {
    override fun mapToDomain(): Owner {
        return Owner(
            id = user_id ?: 0L,
            accountId = account_id ?: 0L,
            reputation = reputation ?: 0L,
            userType = user_type ?: "",
            profileImage = profile_image,
            displayName = display_name ?: "",
            link = link ?: ""
        )
    }
}