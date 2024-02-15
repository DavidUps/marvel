package com.davidups.core.functional

sealed class Either<out L, out R> {

    data class Left<out L>(val error: L) : Either<L, Nothing>()
    data class Right<out R>(val success: R) : Either<Nothing, R>()

    val isRight get() = this is Right<R>
    val isLeft get() = this is Left<L>

    fun <L> left(a: L) = Left(a)
    fun <R> right(b: R) = Right(b)

    fun fold(fnL: (L) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Left -> fnL(error)
            is Right -> fnR(success)
        }
}
