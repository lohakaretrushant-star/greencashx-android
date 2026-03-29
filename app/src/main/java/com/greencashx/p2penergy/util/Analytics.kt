package com.greencashx.p2penergy.util

// Analytics is a no-op stub for local testing (no Firebase dependency).
// Swap this file with a real Firebase Analytics implementation for production.
object Analytics {
    fun logLogin(method: String = "email") {}
    fun logSignUp(method: String = "email") {}
    fun logEnergyPurchase(energyType: String, kWh: Double, gcxAmount: Double) {}
    fun logEnergyListed(energyType: String, kWh: Double, pricePerKwh: Double) {}
    fun logKycStarted() {}
    fun logKycCompleted() {}
    fun logAiInsightViewed(insightType: String) {}
    fun logScreenView(screenName: String) {}
    fun logWalletAction(action: String) {}
}


