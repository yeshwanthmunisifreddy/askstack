package com.thesubgraph.askstack.base.utils.network

data class ErrorDto(
    val code: String?,
    val status: String?,
    val message: String?,
    val timestamp: String?,
    val errors: Map<String, List<String>>?,
)