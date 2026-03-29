package com.greencashx.p2penergy.data.remote.dto

// ── Auth DTOs ─────────────────────────────────────────────────────────────────

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val phone: String?,
    val hasSolarPanel: Boolean
)

data class LoginRequest(
    val email: String,
    val password: String,
    val fcmToken: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: AuthData?
)

data class AuthData(
    val token: String,
    val userId: String?,
    val email: String?,
    val fullName: String?,
    val user: UserDto?
)

data class UserDto(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val walletAddress: String?,
    val gcxBalance: Double,
    val energyBalance: Double,
    val kycStatus: String,
    val hasSolarPanel: Boolean,
    val greenScore: Int,
    val carbonCredits: Int,
    val profileImageUrl: String?
)

// ── Energy DTOs ───────────────────────────────────────────────────────────────

data class EnergyListingDto(
    val id: String,
    val sellerId: String,
    val sellerName: String,
    val energyAmount: Double,
    val pricePerKwh: Double,
    val energyType: String,
    val location: String,
    val greenCertified: Boolean,
    val carbonCredits: Int,
    val status: String,
    val createdAt: String
)

data class ListingsResponse(
    val success: Boolean,
    val data: List<EnergyListingDto>,
    val total: Int
)

data class CreateListingRequest(
    val energyAmount: Double,
    val pricePerKwh: Double,
    val energyType: String,
    val location: String,
    val greenCertified: Boolean
)

data class BuyEnergyRequest(
    val listingId: String,
    val energyAmount: Double
)

data class BuyEnergyResponse(
    val success: Boolean,
    val message: String?,
    val data: BuyEnergyData?
)

data class BuyEnergyData(
    val transactionId: String,
    val txHash: String,
    val totalCost: Double,
    val platformFee: Double,
    val carbonOffset: Double,
    val carbonCreditsEarned: Int
)

data class TransactionDto(
    val id: String,
    val txHash: String?,
    val buyerId: String,
    val sellerId: String,
    val buyerName: String,
    val sellerName: String,
    val energyAmount: Double,
    val pricePerKwh: Double,
    val totalGcxAmount: Double,
    val energyType: String,
    val status: String,
    val blockNumber: Long?,
    val carbonOffset: Double,
    val createdAt: String
)

data class TransactionsResponse(
    val success: Boolean,
    val data: List<TransactionDto>
)

data class MarketStatsResponse(
    val success: Boolean,
    val data: MarketStatsDto?
)

data class MarketStatsDto(
    val activeListings: Int,
    val avgPricePerKwh: String,
    val totalEnergyAvailable: Double,
    val tradedLast24h: Double,
    val txCountLast24h: Int
)

// ── KYC DTOs ──────────────────────────────────────────────────────────────────

data class KycRequest(
    val documentType: String,
    val documentNumber: String,
    val fullName: String,
    val dateOfBirth: String,
    val address: String
)

data class KycResponse(
    val success: Boolean,
    val message: String?,
    val data: KycData?
)

data class KycData(
    val kycId: String?,
    val status: String
)

// ── AI DTOs ───────────────────────────────────────────────────────────────────

data class PricePredictionResponse(
    val success: Boolean,
    val data: PricePredictionData?
)

data class PricePredictionData(
    val currentPrice: String,
    val predictions: PricePredictions,
    val demandLevel: String,
    val recommendation: String,
    val confidence: Double,
    val aiNote: String
)

data class PricePredictions(
    val in4h: String,
    val in12h: String,
    val in24h: String
)

data class AiInsightsResponse(
    val success: Boolean,
    val data: List<AiInsightDto>
)

data class AiInsightDto(
    val type: String,
    val title: String,
    val description: String,
    val confidence: Double,
    val recommendation: String,
    val potentialGcx: Double?
)

// ── AI Chat DTOs ──────────────────────────────────────────────────────────────

data class ChatRequest(val message: String)

data class ChatData(
    val reply: String,
    val poweredBy: String?
)

data class ChatResponse(
    val success: Boolean,
    val data: ChatData?
)

// ── Generic ───────────────────────────────────────────────────────────────────

data class GenericResponse(
    val success: Boolean,
    val message: String?
)

// ── Trading DTOs ──────────────────────────────────────────────────────────────

data class ActivePriceResponse(
    val success: Boolean,
    val data: ActivePriceData?
)

data class ActivePriceData(
    val priceId: String,
    val unitPrice: Double,
    val effectiveFrom: String?,
    val notes: String?
)

data class PlaceOrderRequest(
    val orderType: String,   // "BUY" or "SELL"
    val units: Double
)

data class PlaceOrderResponse(
    val success: Boolean,
    val message: String?,
    val data: PlaceOrderData?,
    val code: String?
)

data class PlaceOrderData(
    val orderId: String,
    val orderType: String,
    val units: Double,
    val pricePerUnit: Double,
    val totalEstimate: Double,
    val status: String
)

data class OrderDto(
    val id: String,
    val orderType: String,
    val units: Double,
    val unitsRemaining: Double,
    val pricePerUnit: Double,
    val status: String,
    val rejectionReason: String?,
    val placedAt: String
)

data class OrdersResponse(
    val success: Boolean,
    val data: List<OrderDto>?
)

data class TradeDto(
    val id: String,
    val buyOrderId: String,
    val sellOrderId: String,
    val buyerId: String,
    val buyerName: String,
    val sellerId: String,
    val sellerName: String,
    val units: Double,
    val pricePerUnit: Double,
    val totalAmount: Double,
    val buyerFee: Double,
    val sellerFee: Double,
    val buyerPayable: Double,
    val sellerReceivable: Double,
    val status: String,
    val matchedAt: String?,
    val settledAt: String?,
    val role: String
)

data class TradesResponse(
    val success: Boolean,
    val data: List<TradeDto>?
)

data class OrderBookResponse(
    val success: Boolean,
    val data: OrderBookData?
)

data class OrderBookData(
    val buyOrders: List<OrderBookEntry>,
    val sellOrders: List<OrderBookEntry>
)

data class OrderBookEntry(
    val id: String,
    val units: Double,
    val unitsRemaining: Double,
    val pricePerUnit: Double,
    val name: String,
    val placedAt: String?
)

// ── Ledger + KPI DTOs ─────────────────────────────────────────────────────────

data class LedgerResponse(
    val success: Boolean,
    val data: LedgerData?
)

data class DiscomInfo(
    val consumerId: String?,
    val gridArea: String?,
    val lastBillUnits: Double,
    val tariff: String?,
    val sanctionedLoad: Double,
    val isVerified: Boolean
)

data class LedgerData(
    val userType: String,
    val unitsGenerated: Double,
    val unitsPurchased: Double,
    val unitsSold: Double,
    val unitsConsumed: Double,
    val unitsAvailable: Double,
    val maxBuyUnits: Double,
    val unitsAlreadyBought: Double,
    val remainingBuyLimit: Double,
    val walletInr: Double,
    val walletLocked: Double,
    val discom: DiscomInfo,
    val lastUpdated: String?
)

data class KpiResponse(
    val success: Boolean,
    val data: KpiData?
)

data class KpiUnits(
    val generated: Double,
    val purchased: Double,
    val sold: Double,
    val available: Double,
    val availableValue: Double
)

data class KpiBuyLimit(
    val max: Double,
    val used: Double,
    val remaining: Double,
    val lastBillUnits: Double
)

data class KpiData(
    val userType: String,
    val walletInr: Double,
    val activePrice: Double,
    val openOrders: Int,
    val settledTrades: Int,
    val totalSpent: Double,
    val totalEarned: Double,
    val units: KpiUnits,
    val buyLimit: KpiBuyLimit,
    val gridArea: String
)

data class WalletResponse(
    val success: Boolean,
    val data: WalletData?
)

data class WalletTxDto(
    val side: String,
    val amount: Double,
    val units: Double,
    val timestamp: String?,
    val status: String
)

data class WalletData(
    val balance: Double,
    val locked: Double,
    val available: Double,
    val recentTransactions: List<WalletTxDto>
)

data class LinkDiscomRequest(
    val consumerId: String,
    val gridArea: String
)

data class GenerateUnitsRequest(
    val units: Double
)

data class DepositRequest(
    val amount: Double
)

