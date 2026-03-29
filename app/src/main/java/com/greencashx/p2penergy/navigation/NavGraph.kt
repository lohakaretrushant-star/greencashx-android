package com.greencashx.p2penergy.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.greencashx.p2penergy.ui.screens.ai.AIInsightsScreen
import com.greencashx.p2penergy.ui.screens.ai.SolarChatScreen
import com.greencashx.p2penergy.ui.screens.trading.TradingScreen
import com.greencashx.p2penergy.ui.screens.trading.TradingKpiScreen
import com.greencashx.p2penergy.ui.screens.auth.LoginScreen
import com.greencashx.p2penergy.ui.screens.auth.RegisterScreen
import com.greencashx.p2penergy.ui.screens.blockchain.WalletScreen
import com.greencashx.p2penergy.ui.screens.dashboard.DashboardScreen
import com.greencashx.p2penergy.ui.screens.marketplace.CreateListingScreen
import com.greencashx.p2penergy.ui.screens.marketplace.MarketPlaceScreen
import com.greencashx.p2penergy.ui.screens.kyc.KycScreen
import com.greencashx.p2penergy.ui.screens.onboarding.OnboardingScreen
import com.greencashx.p2penergy.ui.screens.onboarding.SplashScreen
import com.greencashx.p2penergy.ui.screens.profile.ProfileScreen
import com.greencashx.p2penergy.ui.screens.services.ServicesScreen
import com.greencashx.p2penergy.ui.screens.transactions.TransactionScreen
import com.greencashx.p2penergy.ui.viewmodel.AuthViewModel

@Composable
fun GreenCashXNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            SplashScreen(
                onSplashComplete = {
                    // If already logged in, go straight to Dashboard
                    if (authViewModel.isLoggedIn()) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onLogin = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        composable(Screen.MarketPlace.route) {
            MarketPlaceScreen(
                navController = navController,
                onCreateListing = { navController.navigate(Screen.CreateListing.route) }
            )
        }

        composable(Screen.CreateListing.route) {
            CreateListingScreen(
                onListingCreated = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AIInsights.route) {
            AIInsightsScreen(navController = navController)
        }

        composable(Screen.Wallet.route) {
            WalletScreen(navController = navController)
        }

        composable(Screen.Transactions.route) {
            TransactionScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.Kyc.route) {
            KycScreen(navController = navController)
        }

        composable(Screen.Services.route) {
            ServicesScreen(navController = navController)
        }

        composable(Screen.SolarChat.route) {
            SolarChatScreen(navController = navController)
        }

        composable(Screen.Trading.route) {
            TradingScreen(navController = navController)
        }

        composable(Screen.TradingKpi.route) {
            TradingKpiScreen(navController = navController)
        }
    }
}
