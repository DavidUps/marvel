package com.davidups.core.platform.service

import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either
import retrofit2.Response
import retrofit2.Response.error
import javax.inject.Inject

class Request @Inject constructor(private val networkHandler: NetworkHandler) {

    fun <T> launch(call: Response<T>): Either<Failure, T> = if (networkHandler.isConnected) {
        call.runCatching {
            if (this.isSuccessful && this.body() != null) {
                Either.Right(this.body()!!)
            } else {
                Either.Left(Failure.ServerError(this.code(), this.message()))
            }
        }.getOrElse {
            Either.Left(error = Failure.Throwable(it))
        }
    } else {
        Either.Left(Failure.NetworkConnection)
    }
}
