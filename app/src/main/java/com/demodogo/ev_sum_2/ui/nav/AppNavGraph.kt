package com.demodogo.ev_sum_2.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demodogo.ev_sum_2.services.AuthService
import com.demodogo.ev_sum_2.ui.auth.LoginScreen
import com.demodogo.ev_sum_2.ui.auth.RecoverScreen
import com.demodogo.ev_sum_2.ui.auth.RegisterScreen
import com.demodogo.ev_sum_2.ui.home.HomeScreen
import com.demodogo.ev_sum_2.ui.location.DeviceLocationScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val authService = remember { AuthService()   }

    val userState by authService.authStateFlow().collectAsState(initial = FirebaseAuth.getInstance().currentUser)
    val isLoggedIn = userState != null

    NavHost(
        navController = navController,
        startDestination = "router"
    ) {
        composable("router") {
            RouterScreen(
                isLoggedIn = isLoggedIn,
                onGoHome = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo("router") { inclusive = true }
                    }
                },
                onGoLogin = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo("router") { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(AppRoutes.REGISTER)
                },
                onRecoverClick = {
                    navController.navigate(AppRoutes.RECOVER)
                }
            )
        }

        composable(AppRoutes.REGISTER) {
            RegisterScreen(
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.RECOVER) {
            RecoverScreen(
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(AppRoutes.HOME) {
            HomeScreen(
                onLogout = {
                    authService.logout()
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onOpenLocation = {
                    navController.navigate(AppRoutes.LOCATION)
                }
            )
        }

        composable(AppRoutes.LOCATION) {
            DeviceLocationScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
