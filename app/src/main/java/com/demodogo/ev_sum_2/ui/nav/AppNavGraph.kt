package com.demodogo.ev_sum_2.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demodogo.ev_sum_2.ui.home.HomeScreen
import com.demodogo.ev_sum_2.ui.auth.LoginScreen
import com.demodogo.ev_sum_2.ui.auth.RegisterScreen
import com.example.sm_1.ui.recover.RecoverScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN
    ) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(AppRoutes.HOME) {
                    popUpTo(AppRoutes.LOGIN) { inclusive = true }
                }
            },
                onRegisterClick = { navController.navigate(AppRoutes.REGISTER) },
                onRecoverClick = { navController.navigate(AppRoutes.RECOVER) },
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
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
