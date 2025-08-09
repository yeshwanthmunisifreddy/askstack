package com.thesubgraph.askstack.base.utils.network


sealed class ValueResult<out T> {
    data class Success<T>(val data: T) : ValueResult<T>()
    data class Failure(val error: ErrorModel) : ValueResult<Nothing>()

    val value: T? get() = (this as? Success)?.data
}