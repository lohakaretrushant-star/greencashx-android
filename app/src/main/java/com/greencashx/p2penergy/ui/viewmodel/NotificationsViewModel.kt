package com.greencashx.p2penergy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greencashx.p2penergy.data.remote.ApiService
import com.greencashx.p2penergy.data.remote.dto.NotificationDto
import com.greencashx.p2penergy.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationDto> = emptyList(),
    val unreadCount: Int = 0,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsUiState())
    val state: StateFlow<NotificationsUiState> = _state

    private fun token() = authRepository.getToken()

    init { loadNotifications() }

    fun loadNotifications() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val notifRes = apiService.getNotifications(token())
                val unreadRes = apiService.getUnreadCount(token())
                if (notifRes.isSuccessful) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        notifications = notifRes.body()?.data ?: emptyList(),
                        unreadCount = unreadRes.body()?.data?.count ?: 0
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load notifications"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.localizedMessage}"
                )
            }
        }
    }

    fun markRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val res = apiService.markNotificationRead(token(), notificationId)
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(
                        notifications = _state.value.notifications.map { n ->
                            if (n.id == notificationId) n.copy(isRead = true) else n
                        },
                        unreadCount = maxOf(0, _state.value.unreadCount - 1)
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            try {
                val res = apiService.markAllNotificationsRead(token())
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(
                        notifications = _state.value.notifications.map { it.copy(isRead = true) },
                        unreadCount = 0,
                        successMessage = "All notifications marked as read"
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
    }
}
