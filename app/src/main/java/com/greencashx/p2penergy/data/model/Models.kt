package com.greencashx.p2penergy.data.model

import java.util.Date

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val walletAddress: String,
    val gcxBalance: Double,
    val energyBalance: Double, // kWh available
    val kycVerified: Boolean,
    val solarPanelConnected: Boolean,
    val profileImageUrl: String?,
    val createdAt: Date = Date()
)

data class EnergyListing(
    val id: String,
    val sellerId: String,
    val sellerName: String,
    val energyAmount: Double, // kWh
    val pricePerKwh: Double,  // in GCX tokens
    val energyType: EnergyType,
    val location: String,
    val distance: Double,     // km from buyer
    val availableFrom: Date,
    val availableUntil: Date,
    val greenCertified: Boolean,
    val carbonCredits: Int,
    val status: ListingStatus = ListingStatus.ACTIVE
)

data class EnergyTransaction(
    val id: String,
    val txHash: String,       // Blockchain transaction hash
    val buyerId: String,
    val sellerId: String,
    val energyAmount: Double,
    val pricePerKwh: Double,
    val totalGcxAmount: Double,
    val energyType: EnergyType,
    val status: TransactionStatus,
    val blockNumber: Long?,
    val timestamp: Date,
    val carbonOffset: Double   // kg CO2 saved
)

data class AIInsight(
    val id: String,
    val type: InsightType,
    val title: String,
    val description: String,
    val confidence: Float,    // 0.0 to 1.0
    val recommendation: String,
    val potentialSavings: Double?,
    val timestamp: Date = Date()
)

data class BlockchainWallet(
    val address: String,
    val gcxBalance: Double,
    val ethBalance: Double,
    val transactions: List<WalletTransaction>,
    val carbonCredits: Int,
    val greenScore: Int       // eco-reputation score
)

data class WalletTransaction(
    val hash: String,
    val type: WalletTxType,
    val amount: Double,
    val token: String,
    val from: String,
    val to: String,
    val status: TransactionStatus,
    val timestamp: Date,
    val gasUsed: Double?
)

data class EnergyStats(
    val totalEnergyTraded: Double,
    val totalCo2Saved: Double,
    val totalGcxEarned: Double,
    val activeBuyers: Int,
    val activeSellers: Int,
    val avgPricePerKwh: Double,
    val priceHistory: List<PricePoint>
)

data class PricePoint(
    val timestamp: Date,
    val price: Double,
    val volume: Double
)

enum class EnergyType(val displayName: String, val icon: String) {
    SOLAR("Solar", "☀️"),
    WIND("Wind", "💨"),
    HYDRO("Hydro", "💧"),
    BIOMASS("Biomass", "🌿"),
    MIXED("Mixed", "⚡")
}

enum class ListingStatus { ACTIVE, SOLD, EXPIRED, CANCELLED }
enum class TransactionStatus { PENDING, CONFIRMED, FAILED }
enum class InsightType { PRICE_ALERT, DEMAND_FORECAST, SELL_OPPORTUNITY, BUY_OPPORTUNITY, CARBON_MILESTONE, ANOMALY }
enum class WalletTxType { SEND, RECEIVE, TRADE, STAKE, REWARD }
