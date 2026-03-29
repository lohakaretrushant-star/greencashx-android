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

    @POST("api/ai/chat")
    suspend fun chat(
        @Header("Authorization") token: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>

    // ── Trading ───────────────────────────────────────────────────────────────

    @GET("api/trading/price")
    suspend fun getActivePrice(
        @Header("Authorization") token: String
    ): Response<ActivePriceResponse>

    @POST("api/trading/order")
    suspend fun placeOrder(
        @Header("Authorization") token: String,
        @Body request: PlaceOrderRequest
    ): Response<PlaceOrderResponse>

    @GET("api/trading/orders")
    suspend fun getMyOrders(
        @Header("Authorization") token: String
    ): Response<OrdersResponse>

    @GET("api/trading/trades")
    suspend fun getMyTrades(
        @Header("Authorization") token: String
    ): Response<TradesResponse>

    @GET("api/trading/orderbook")
    suspend fun getOrderBook(
        @Header("Authorization") token: String
    ): Response<OrderBookResponse>

    @PUT("api/trading/order/{id}/cancel")
    suspend fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id") orderId: String
    ): Response<GenericResponse>

    // ── Ledger ────────────────────────────────────────────────────────────────

    @GET("api/ledger/me")
    suspend fun getLedger(
        @Header("Authorization") token: String
    ): Response<LedgerResponse>

    @GET("api/ledger/kpi")
    suspend fun getKpi(
        @Header("Authorization") token: String
    ): Response<KpiResponse>

    @GET("api/ledger/wallet")
    suspend fun getWallet(
        @Header("Authorization") token: String
    ): Response<WalletResponse>

    @POST("api/ledger/wallet/deposit")
    suspend fun deposit(
        @Header("Authorization") token: String,
        @Body request: DepositRequest
    ): Response<GenericResponse>

    @POST("api/ledger/discom/link")
    suspend fun linkDiscom(
        @Header("Authorization") token: String,
        @Body request: LinkDiscomRequest
    ): Response<GenericResponse>

    @POST("api/ledger/generate")
    suspend fun logGeneratedUnits(
        @Header("Authorization") token: String,
        @Body request: GenerateUnitsRequest
    ): Response<GenericResponse>
}
