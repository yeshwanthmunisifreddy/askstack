package com.thesubgraph.askstack.base.utils.network

import android.content.Context

interface Mapper
interface ResponseDomainMapper<DomainModel>: Mapper {
    fun mapToDomain(): DomainModel?
}