sealed class Result<out S, out F> {
    data class Success<out S>(val value: S) : Result<S, Nothing>()
    data class Failure<out F>(val value: F) : Result<Nothing, F>()
}

inline fun <S, F, T> Result<S, F>.flow(
        success: (S) -> T,
        failure: (F) -> T
): T = when (this) {
    is Result.Success -> success(value)
    is Result.Failure -> failure(value)
}

inline fun <S, F, T> Result<S, F>.mapSuccess(transform: (S) -> T): Result<T, F> = when (this) {
    is Result.Success -> Result.Success(transform(value))
    is Result.Failure -> this
}

inline fun <S> Result.Success<S>.filter(condition: (S) -> Boolean) = if (condition(value)) {
    Result.Success(value)
} else {
    null
}

inline fun <E, S : Iterable<E>> Result.Success<S>.filterItems(condition: (E) -> Boolean) =
        Result.Success(value.filter { condition(it) })

inline fun <S, T> Result.Success<S>.map(transform: (S) -> T) = Result.Success(transform(value))

inline fun <E, S : Iterable<E>, T> Result.Success<S>.mapItems(transform: (E) -> T) =
        Result.Success(value.map { transform(it) })