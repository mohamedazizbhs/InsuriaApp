package com.example.insuriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.insuriaapp.ui.screens.HomeScreen
import com.example.insuriaapp.ui.screens.LoginScreen
import com.example.insuriaapp.ui.screens.RegisterScreen
import com.example.insuriaapp.ui.screens.WelcomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        composable("welcome") {
            WelcomeScreen(
                onLoginClick = {
                    navController.navigate("login")
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("home") {
            HomeScreen()
        }
    }
}