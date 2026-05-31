package com.example.insuriaapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.insuriaapp.data.ClaimData
import com.example.insuriaapp.ui.screens.*

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val claimData = remember { ClaimData() }

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        composable("welcome") {
            WelcomeScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("home") {
            MainScreen(
                onDeclareClaimClick = {
                    claimData.typeSinistre = ""
                    claimData.preuveUri = ""
                    claimData.preuveType = ""
                    claimData.description = ""
                    claimData.localisation = ""
                    claimData.latitude = null
                    claimData.longitude = null
                    claimData.statut = "En attente"

                    navController.navigate("claim_type")
                }
            )
        }

        composable("claim_type") {
            ClaimTypeScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { selectedType ->
                    claimData.typeSinistre = selectedType
                    navController.navigate("claim_proof")
                }
            )
        }

        composable("claim_proof") {
            ClaimProofScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { proofUri, proofType ->
                    claimData.preuveUri = proofUri
                    claimData.preuveType = proofType
                    navController.navigate("claim_description")
                }
            )
        }

        composable("claim_description") {
            ClaimDescriptionScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { description ->
                    claimData.description = description
                    navController.navigate("claim_location")
                }
            )
        }

        composable("claim_location") {
            ClaimLocationScreen(
                onBackClick = { navController.popBackStack() },
                onNextClick = { localisation, latitude, longitude ->
                    claimData.localisation = localisation
                    claimData.latitude = latitude
                    claimData.longitude = longitude
                    navController.navigate("claim_recap")
                }
            )
        }

        composable("claim_recap") {
            ClaimRecapScreen(
                claimData = claimData,
                onBackClick = { navController.popBackStack() },
                onSubmitSuccess = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("contracts") {
            ContractsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}