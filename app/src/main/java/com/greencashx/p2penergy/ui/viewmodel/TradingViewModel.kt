package com.greencashx.p2penergy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greencashx.p2penergy.data.remote.ApiService
import com.greencashx.p2penergy.data.remote.dto.*
import com.greencashx.p2penergy.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TradingUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val activePrice: Double = 0.0,
    val kpi: KpiData? = null,
    val ledger: LedgerData? = null,
    val orders: List<OrderDto> = emptyList(),
    val trades: List<TradeDto> = emptyList(),
    val orderBook: OrderBookData? = null,
    val wallet: WalletData? = null,
    val isOrderPlacing: Boolean = false,
    val lastOrderResult: PlaceOrderData? = null
)

@HiltViewModel
class TradingViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TradingUiState())
    val state: StateFlow<TradingUiState> = _state

    private fun token() = authRepository.getToken()

    fun loadAll() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)
            try {
                val priceRes = apiService.getActivePrice(token())
                val kpiRes   = apiService.getKpi(token())
                val obRes    = apiService.getOrderBook(token())
                val ordRes   = apiService.getMyOrders(token())
                val tradeRes = apiService.getMyTrades(token())

                _state.value = _state.value.copy(
                    isLoading = false,
                    activePrice = priceRes.body()?.data?.unitPrice ?: 0.0,
                    kpi = kpiRes.body()?.data,
                    orderBook = obRes.body()?.data,
                    orders = ordRes.body()?.data ?: emptyList(),
                    trades = tradeRes.body()?.data ?: emptyList()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load trading data: ${e.localizedMessage}"
                )
            }
        }
    }

    fun loadKpi() {
        viewModelScope.launch {
            try {
                val res = apiService.getKpi(token())
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(
                        kpi = res.body()?.data,
                        activePrice = res.body()?.data?.activePrice ?: _state.value.activePrice
                    )
                }
            } catch (e: Exception) { /* silent */ }
        }
    }

    fun loadWallet() {
        viewModelScope.launch {
            try {
                val res = apiService.getWallet(token())
                if (res.isSuccessful) _state.value = _state.value.copy(wallet = res.body()?.data)
            } catch (e: Exception) { /* silent */ }
        }
    }

    fun placeOrder(orderType: String, units: Double) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isOrderPlacing = true, errorMessage = null, successMessage = null, lastOrderResult = null)
            try {
                val res = apiService.placeOrder(token(), PlaceOrderRequest(orderType = orderType, units = units))
                if (res.isSuccessful && res.body()?.success == true) {
                    _state.value = _state.value.copy(
                        isOrderPlacing = false,
                        successMessage = res.body()?.message ?: "Order placed successfully",
                        lastOrderResult = res.body()?.data
                    )
                    loadAll() // refresh KPI + orders
                } else {
                    val errorMsg = res.body()?.message ?: "Order failed. Please try again."
                    _state.value = _state.value.copy(isOrderPlacing = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isOrderPlacing = false,
                    errorMessage = "Network error: ${e.localizedMessage}"
                )
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                val res = apiService.cancelOrder(token(), orderId)
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(successMessage = "Order cancelled")
                    loadAll()
                } else {
                    _state.value = _state.value.copy(errorMessage = res.body()?.message ?: "Cancel failed")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Network error: ${e.localizedMessage}")
            }
        }
    }

    fun deposit(amount: Double) {
        viewModelScope.launch {
            try {
                val res = apiService.deposit(token(), DepositRequest(amount))
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(successMessage = "₹${amount.toInt()} deposited to wallet")
                    loadKpi()
                } else {
                    _state.value = _state.value.copy(errorMessage = res.body()?.message ?: "Deposit failed")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMessage = "Network error: ${e.localizedMessage}")
            }
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(errorMessage = null, successMessage = null)
    }
}
