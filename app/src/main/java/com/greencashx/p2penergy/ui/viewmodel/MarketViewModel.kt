package com.greencashx.p2penergy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greencashx.p2penergy.data.remote.ApiResult
import com.greencashx.p2penergy.data.remote.dto.*
import com.greencashx.p2penergy.data.repository.EnergyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarketUiState(
    val isLoading: Boolean = false,
    val listings: List<EnergyListingDto> = emptyList(),
    val transactions: List<TransactionDto> = emptyList(),
    val marketStats: MarketStatsDto? = null,
    val pricePrediction: PricePredictionData? = null,
    val aiInsights: List<AiInsightDto> = emptyList(),
    val errorMessage: String? = null,
    val buySuccess: BuyEnergyData? = null
)

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val energyRepository: EnergyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState

    init {
        loadMarketData()
    }

    fun loadMarketData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Load in parallel conceptually — sequential for simplicity
            loadListings()
            loadMarketStats()
            loadPricePrediction()
            loadAiInsights()

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun loadListings(type: String? = null) {
        viewModelScope.launch {
            when (val result = energyRepository.getListings(type = type)) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(listings = result.data.data)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(errorMessage = result.message)
                else -> Unit
            }
        }
    }

    fun loadTransactions() {
        viewModelScope.launch {
            when (val result = energyRepository.getTransactions()) {
                is ApiResult.Success -> _uiState.value = _uiState.value.copy(transactions = result.data.data)
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(errorMessage = result.message)
                else -> Unit
            }
        }
    }

    fun buyEnergy(listingId: String, energyAmount: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = energyRepository.buyEnergy(listingId, energyAmount)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        buySuccess = result.data.data
                    )
                    loadListings() // Refresh listings after purchase
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
                else -> Unit
            }
        }
    }

    fun createListing(request: CreateListingRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = energyRepository.createListing(request)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    loadListings()
                }
                is ApiResult.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.message
                )
                else -> Unit
            }
        }
    }

    private suspend fun loadMarketStats() {
        when (val result = energyRepository.getMarketStats()) {
            is ApiResult.Success -> _uiState.value = _uiState.value.copy(marketStats = result.data.data)
            else -> Unit
        }
    }

    private suspend fun loadPricePrediction() {
        when (val result = energyRepository.getPricePrediction()) {
            is ApiResult.Success -> _uiState.value = _uiState.value.copy(pricePrediction = result.data.data)
            else -> Unit
        }
    }

    private suspend fun loadAiInsights() {
        when (val result = energyRepository.getAiInsights()) {
            is ApiResult.Success -> _uiState.value = _uiState.value.copy(aiInsights = result.data.data)
            else -> Unit
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }
    fun clearBuySuccess() { _uiState.value = _uiState.value.copy(buySuccess = null) }
}
