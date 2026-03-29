package com.greencashx.p2penergy.data.repository

import android.content.SharedPreferences
import com.greencashx.p2penergy.data.remote.ApiResult
import com.greencashx.p2penergy.data.remote.ApiService
import com.greencashx.p2penergy.data.remote.dto.*
import com.greencashx.p2penergy.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefs: SharedPreferences
) {

    fun isLoggedIn(): Boolean = prefs.getString("jwt_token", null) != null

    fun getToken(): String = "Bearer ${prefs.getString("jwt_token", "")}"

    fun getSavedUserId(): String? = prefs.getString("user_id", null)

    // ── Sign In — calls backend with email + password ─────────────────────────
    suspend fun signInWithEmail(email: String, password: String): ApiResult<AuthResponse> {
        val result = safeApiCall {
            apiService.login(LoginRequest(email = email.trim(), password = password))
        }
        if (result is ApiResult.Success) {
            result.data.data?.token?.let { saveSession(it, result.data.data.user?.id) }
            result.data.data?.user?.fullName?.let { saveFullName(it) }
            saveUserEmail(email.trim())
        }
        return result
    }

    // ── Register — calls backend with email + password ────────────────────────
    suspend fun registerWithEmail(
        fullName: String,
        email: String,
        password: String,
        phone: String?,
        hasSolarPanel: Boolean
    ): ApiResult<AuthResponse> {
        val result = safeApiCall {
            apiService.register(
                RegisterRequest(
                    fullName      = fullName,
                    email         = email.trim(),
                    password      = password,
                    phone         = phone,
                    hasSolarPanel = hasSolarPanel
                )
            )
        }
        if (result is ApiResult.Success) {
            result.data.data?.token?.let { saveSession(it, result.data.data.userId) }
            val name = result.data.data?.user?.fullName ?: result.data.data?.fullName
            name?.let { saveFullName(it) }
            saveUserEmail(email.trim())
        }
        return result
    }

    // ── Sign Out ──────────────────────────────────────────────────────────────
    fun signOut() {
        prefs.edit().clear().apply()
    }

    // ── Get Current User Profile ──────────────────────────────────────────────
    suspend fun getMyProfile(): ApiResult<AuthResponse> {
        return safeApiCall { apiService.getMe(getToken()) }
    }

    fun getFullName(): String = prefs.getString("user_full_name", "") ?: ""
    fun getUserEmail(): String = prefs.getString("user_email", "") ?: ""

    private fun saveFullName(name: String) {
        prefs.edit().putString("user_full_name", name).apply()
    }

    private fun saveUserEmail(email: String) {
        prefs.edit().putString("user_email", email).apply()
    }

    private fun saveSession(token: String, userId: String?) {
        prefs.edit()
            .putString("jwt_token", token)
            .putString("user_id", userId)
            .apply()
    }
}
