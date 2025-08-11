package com.thesubgraph.askstack.features.search.data.serialization

data class ResponseDto<DataDto>(
    val items: DataDto,
)