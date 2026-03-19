package com.hevsosapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hevsosapp.ui.screens.LoginScreen
import com.hevsosapp.ui.screens.MainSOSScreen
import com.hevsosapp.ui.screens.ProfileScreen
import com.hevsosapp.ui.screens.RegisterScreen
import com.hevsosapp.ui.screens.SettingsScreen

object Destinations {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN_SOS = "main_sos"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.LOGIN,
        modifier = modifier
    ) {
        composable(Destinations.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Destinations.MAIN_SOS) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Destinations.REGISTER)
                }
            )
        }

        composable(Destinations.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Destinations.MAIN_SOS) {
                        popUpTo(Destinations.LOGIN) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.MAIN_SOS) {
            MainSOSScreen(
                userName = "Felhasználó",
                onProfileClick = {
                    navController.navigate(Destinations.PROFILE)
                },
                onSettingsClick = {
                    navController.navigate(Destinations.SETTINGS)
                },
                onSOSActivated = {
                    // SOS logic handled in the existing backend
                },
                onCallEmergency = {
                    // Call 112 logic
                },
                onSendLocation = {
                    // Send location logic
                },
                onSendMessage = {
                    // Send message logic
                }
            )
        }

        composable(Destinations.PROFILE) {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
