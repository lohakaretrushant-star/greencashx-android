package com.greencashx.p2penergy.data.repository

import com.greencashx.p2penergy.data.remote.ApiResult
import com.greencashx.p2penergy.data.remote.ApiService
import com.greencashx.p2penergy.data.remote.dto.*
import com.greencashx.p2penergy.data.remote.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnergyRepository @Inject constructor(
    private val apiService: ApiService,
    private val authRepository: AuthRepository
) {

    suspend fun getListings(
        type: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        offset: Int = 0
    ): ApiResult<ListingsResponse> = safeApiCall {
        apiService.getListings(authRepository.getToken(), type, minPrice, maxPrice, offset = offset)
    }

    suspend fun createListing(request: CreateListingRequest): ApiResult<GenericResponse> =
        safeApiCall { apiService.createListing(authRepository.getToken(), request) }

    suspend fun buyEnergy(listingId: String, energyAmount: Double): ApiResult<BuyEnergyResponse> =
        safeApiCall { apiService.buyEnergy(authRepository.getToken(), BuyEnergyRequest(listingId, energyAmount)) }

    suspend fun getTransactions(offset: Int = 0): ApiResult<TransactionsResponse> =
        safeApiCall { apiService.getTransactions(authRepository.getToken(), offset = offset) }

    suspend fun getMarketStats(): ApiResult<MarketStatsResponse> =
        safeApiCall { apiService.getMarketStats(authRepository.getToken()) }

    suspend fun getPricePrediction(): ApiResult<PricePredictionResponse> =
        safeApiCall { apiService.getPricePrediction(authRepository.getToken()) }

    suspend fun getAiInsights(): ApiResult<AiInsightsResponse> =
        safeApiCall { apiService.getAiInsights(authRepository.getToken()) }

    suspend fun submitKyc(request: KycRequest): ApiResult<KycResponse> =
        safeApiCall { apiService.submitKyc(authRepository.getToken(), request) }

    suspend fun getKycStatus(): ApiResult<KycResponse> =
        safeApiCall { apiService.getKycStatus(authRepository.getToken()) }
}
