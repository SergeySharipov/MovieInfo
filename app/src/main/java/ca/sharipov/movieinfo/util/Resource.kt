package ca.sharipov.movieinfo.util

sealed class Resource<T>(
    val data: T? = null,
    val messageResId: Int? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(messageResId: Int, data: T? = null) : Resource<T>(data, messageResId)
    class Loading<T> : Resource<T>()
}