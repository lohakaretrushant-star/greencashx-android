package com.greencashx.p2penergy.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.greencashx.p2penergy.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _fullName = MutableStateFlow(authRepository.getFullName())
    val fullName: StateFlow<String> = _fullName

    val greeting: String get() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else      -> "Good Evening"
        }
    }

    fun getInitials(): String {
        val name = _fullName.value.trim()
        if (name.isEmpty()) return "G"
        val parts = name.split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else name.take(2).uppercase()
    }
}
