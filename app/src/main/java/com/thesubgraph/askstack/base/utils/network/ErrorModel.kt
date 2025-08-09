package com.thesubgraph.askstack.base.utils.network

interface ApplicationError

data class ValidationError(
    val key: String,
    val messages: List<String>
): ApplicationError

enum class NetworkError: ApplicationError {
    ServerNotResponding,
    ServerNotFound,
    RequestTimedOut,
    NoInternet;
}

enum class WebServiceError: ApplicationError {
    Authentication,
    Authorization,
    DefaultsNotLoaded,
    ServerError,
    EncodeFailed,
    DecodeFailed,
    Custom,
    Unknown;
//    Cancelled,
//    Authorization,
}

enum class ServiceError(val code: String) {
    Forbidden("403")
}

data class ErrorModel(
    val type: ApplicationError,
    val message: String,
    val code: String = "",
    val errors: List<ValidationError> = listOf()
) {
    val fullMessage: String get() {
        return "$message\n${ errors.flatMap { it.messages }.joinToString("\n") }"
    }
}
