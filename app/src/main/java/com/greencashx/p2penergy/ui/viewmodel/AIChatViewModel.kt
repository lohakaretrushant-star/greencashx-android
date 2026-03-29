package com.greencashx.p2penergy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greencashx.p2penergy.data.remote.ApiService
import com.greencashx.p2penergy.data.remote.dto.ChatRequest
import com.greencashx.p2penergy.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun sendMessage(text: String) {
        if (text.isBlank() || _isLoading.value) return

        val userMsg = ChatMessage(text = text.trim(), isUser = true)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                val response = apiService.chat(token, ChatRequest(message = text.trim()))
                if (response.isSuccessful) {
                    val reply = response.body()?.data?.reply ?: "I couldn't generate a response. Please try again."
                    _messages.value = _messages.value + ChatMessage(text = reply, isUser = false)
                } else {
                    _messages.value = _messages.value + ChatMessage(
                        text = "Service unavailable. Please check your connection.",
                        isUser = false
                    )
                }
            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    text = "Network error: ${e.localizedMessage ?: "Unable to connect. Please try again."}",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
