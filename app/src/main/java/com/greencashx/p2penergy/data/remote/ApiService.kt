package com.greencashx.p2penergy.data.remote

import com.greencashx.p2penergy.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<AuthResponse>

    @DELETE("api/auth/account")
    suspend fun deleteAccount(@Header("Authorization") token: String): Response<GenericResponse>

    // ── Energy ────────────────────────────────────────────────────────────────

    @GET("api/energy/listings")
    suspend fun getListings(
        @Header("Authorization") token: String,
        @Query("type") type: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<ListingsResponse>

    @POST("api/energy/listings")
    suspend fun createListing(
        @Header("Authorization") token: String,
        @Body request: CreateListingRequest
    ): Response<GenericResponse>

    @POST("api/energy/buy")
    suspend fun buyEnergy(
        @Header("Authorization") token: String,
        @Body request: BuyEnergyRequest
    ): Response<BuyEnergyResponse>

    @GET("api/energy/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Response<TransactionsResponse>

    @GET("api/energy/market-stats")
    suspend fun getMarketStats(
        @Header("Authorization") token: String
    ): Response<MarketStatsResponse>

    // ── KYC ───────────────────────────────────────────────────────────────────

    @POST("api/kyc/submit")
    suspend fun submitKyc(
        @Header("Authorization") token: String,
        @Body request: KycRequest
    ): Response<KycResponse>

    @GET("api/kyc/status")
    suspend fun getKycStatus(
        @Header("Authorization") token: String
    ): Response<KycResponse>

    // ── AI ────────────────────────────────────────────────────────────────────

    @GET("api/ai/price-prediction")
    suspend fun getPricePrediction(
        @Header("Authorization") token: String
    ): Response<PricePredictionResponse>

    @GET("api/ai/insights")
    suspend fun getAiInsights(
        @Header("Authorization") token: String
    ): Response<AiInsightsResponse>
}
