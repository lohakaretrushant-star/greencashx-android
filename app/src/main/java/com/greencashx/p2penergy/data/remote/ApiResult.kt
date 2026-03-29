package com.greencashx.p2penergy.data.remote

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful && response.body() != null) {
            ApiResult.Success(response.body()!!)
        } else {
            ApiResult.Error(
                message = response.errorBody()?.string() ?: "Request failed",
                code = response.code()
            )
        }
    } catch (e: java.net.UnknownHostException) {
        ApiResult.Error("No internet connection. Please check your network.")
    } catch (e: java.net.SocketTimeoutException) {
        ApiResult.Error("Connection timed out. Please try again.")
    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: "An unexpected error occurred")
    }
}
