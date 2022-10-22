package com.tylerb.dragonvalesandbox.util

sealed class Result<out R> {
    object Default : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    object Loading : Result<Nothing>()
    data class Error(val exception: Exception) : Result<Nothing>()

    inline fun onSuccess(block: (R) -> Unit): Result<R> {
        if (this is Success) {
            block(data)
        }
        return this
    }

    inline fun onFailure(block: (Exception) -> Unit): Result<R> {
        if (this is Error) {
            block(exception)
        }
        return this
    }
}

inline fun <T> myResultRunCatching(block: () -> T): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        Result.Error(e)
    }
}