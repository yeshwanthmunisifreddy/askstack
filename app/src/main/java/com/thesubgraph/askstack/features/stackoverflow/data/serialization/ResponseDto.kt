package com.thesubgraph.askstack.features.stackoverflow.data.serialization

data class ResponseDto<DataDto>(
    val items: DataDto,
)