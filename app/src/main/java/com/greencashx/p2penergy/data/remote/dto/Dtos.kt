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

// ── Generic ───────────────────────────────────────────────────────────────────

data class GenericResponse(
    val success: Boolean,
    val message: String?
)
