package com.greencashx.p2penergy.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Dashboard : Screen("dashboard")
    data object MarketPlace : Screen("marketplace")
    data object ListingDetail : Screen("listing/{listingId}") {
        fun createRoute(listingId: String) = "listing/$listingId"
    }
    data object CreateListing : Screen("create_listing")
    data object AIInsights : Screen("ai_insights")
    data object Wallet : Screen("wallet")
    data object Transactions : Screen("transactions")
    data object Profile : Screen("profile")
    data object CarbonCredits : Screen("carbon_credits")
    data object SmartMeter : Screen("smart_meter")
    data object Notifications : Screen("notifications")
    data object Kyc : Screen("kyc")
    data object Services : Screen("services")
    data object SolarChat : Screen("solar_chat")
    data object Trading : Screen("trading")
    data object TradingKpi : Screen("trading_kpi")
}
