package com.davidups.core.exception

sealed class Failure {
    object NetworkConnection : Failure()
    class ServerError(val errorCode: Int, val message: String) : Failure()

    data class Throwable(val throwable: kotlin.Throwable?) : Failure()
    data class CustomError(val errorCode: Int, val errorMessage: String?) : Failure()
}

fun Failure.toView(): FailureView {
    return when (this) {
        is Failure.Throwable -> FailureView(ErrorType.SERVER)
        is Failure.CustomError -> FailureView(ErrorType.SERVER)
        is Failure.ServerError -> FailureView(ErrorType.SERVER)
        is Failure.NetworkConnection -> FailureView(ErrorType.NETWORK_CONNECTION)
    }
}

data class FailureView(val errorType: ErrorType)

enum class ErrorType { NETWORK_CONNECTION, SERVER }
