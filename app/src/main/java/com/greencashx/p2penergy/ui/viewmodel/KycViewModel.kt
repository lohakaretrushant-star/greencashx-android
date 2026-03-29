package com.greencashx.p2penergy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greencashx.p2penergy.data.remote.ApiResult
import com.greencashx.p2penergy.data.remote.dto.KycRequest
import com.greencashx.p2penergy.data.repository.EnergyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KycUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val kycStatus: String = "pending",
    val errorMessage: String? = null
)

@HiltViewModel
class KycViewModel @Inject constructor(
    private val energyRepository: EnergyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KycUiState())
    val uiState: StateFlow<KycUiState> = _uiState

    init { checkKycStatus() }

    fun checkKycStatus() {
        viewModelScope.launch {
            when (val result = energyRepository.getKycStatus()) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(
                    kycStatus = result.data.data?.status ?: "pending"
                )
                else -> Unit
            }
        }
    }

    fun submitKyc(
        documentType: String,
        documentNumber: String,
        fullName: String,
        dateOfBirth: String,
        address: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val request = KycRequest(documentType, documentNumber, fullName, dateOfBirth, address)
            when (val result = energyRepository.submitKyc(request)) {
                is ApiResult.Success -> _uiState.value = KycUiState(
                    isSuccess = true,
                    kycStatus = result.data.data?.status ?: "verified"
                )
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
                else -> Unit
            }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }
}
