package com.greencashx.p2penergy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greencashx.p2penergy.data.remote.ApiResult
import com.greencashx.p2penergy.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
    fun getFullName(): String = authRepository.getFullName()
    fun getUserEmail(): String = authRepository.getUserEmail()
    fun getInitials(): String {
        val name = authRepository.getFullName().trim()
        if (name.isEmpty()) return "G"
        val parts = name.split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else name.take(2).uppercase()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.signInWithEmail(email, password)) {
                is ApiResult.Success -> _uiState.value = AuthUiState(isSuccess = true)
                is ApiResult.Error -> _uiState.value = AuthUiState(errorMessage = result.message)
                else -> Unit
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        phone: String?,
        hasSolarPanel: Boolean
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            when (val result = authRepository.registerWithEmail(fullName, email, password, phone, hasSolarPanel)) {
                is ApiResult.Success -> _uiState.value = AuthUiState(isSuccess = true)
                is ApiResult.Error -> _uiState.value = AuthUiState(errorMessage = result.message)
                else -> Unit
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
