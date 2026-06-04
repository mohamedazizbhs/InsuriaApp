package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.insuriaapp.navigation.bottomNavItems

@Composable
fun MainScreen(
    onDeclareClaimClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo("home")
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(item.icon, contentDescription = item.title)
                        },
                        label = {
                            Text(item.title)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(
                    onDeclareClaimClick = onDeclareClaimClick,
                    onContractsClick = {
                        navController.navigate("contracts")
                    },
                    onClaimsClick = {
                        navController.navigate("claims")
                    },
                    onAssistanceClick = {
                        navController.navigate("assistance")
                    },
                    onLogoutClick = onLogoutClick
                )
            }

            composable("contracts") {
                ContractsScreen(
                    onBackClick = {
                        navController.navigate("home")
                    },
                    onContractClick = { contractId ->
                        navController.navigate("contract_detail/$contractId")
                    },
                    onAddContractClick = {
                        navController.navigate("add_contract")
                    }
                )
            }
            composable("contract_detail/{contractId}") { backStackEntry ->
                val contractId = backStackEntry.arguments?.getString("contractId") ?: ""

                ContractDetailScreen(
                    contractId = contractId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable("add_contract") {
                AddContractScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onContractAdded = {
                        navController.navigate("contracts") {
                            popUpTo("contracts") { inclusive = true }
                        }
                    }
                )
            }

            composable("claims") {
                ClaimsScreen(
                    onClaimClick = { claimId ->
                        navController.navigate("claim_detail/$claimId")
                    }
                )
            }
            composable("claim_detail/{claimId}") { backStackEntry ->
                val claimId = backStackEntry.arguments?.getString("claimId") ?: ""

                ClaimDetailScreen(
                    claimId = claimId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable("assistance") {
                AssistanceScreen()
            }
        }
    }
}