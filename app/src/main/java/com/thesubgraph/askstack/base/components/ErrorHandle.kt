package com.thesubgraph.askstack.base.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.thesubgraph.askstack.base.utils.network.ApplicationError
import com.thesubgraph.askstack.base.utils.network.NetworkError
import com.thesubgraph.askstack.base.utils.network.WebServiceError

@Composable
fun ErrorHandle(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    errorType: ApplicationError,
    onRetry: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        when (errorType) {
            NetworkError.NoInternet -> {
                NoInternet(onRetry = onRetry)
            }

            NetworkError.ServerNotResponding,
            NetworkError.ServerNotFound,
            NetworkError.RequestTimedOut,
            WebServiceError.DefaultsNotLoaded,
            WebServiceError.ServerError,
            WebServiceError.EncodeFailed,
            WebServiceError.DecodeFailed,
                -> {
                SomethingWentWrong(onRetry = onRetry)
            }

            WebServiceError.Custom,
            WebServiceError.Unknown,
                -> {
                SomethingWentWrong(onRetry = onRetry)// TODO handle custom errors
            }

            else -> {
                SomethingWentWrong(onRetry = onRetry)
            }
        }
    }
}