package com.davidups.core.extensions

import com.davidups.core.exception.Failure
import com.davidups.core.functional.Either

inline fun <T, R> T.runCatchingEither(block: T.() -> R): Either<Failure, R> {
    return try {
        Either.Right(block())
    } catch (ex: Throwable) {
        Either.Left(Failure.Throwable(ex))
    }
}