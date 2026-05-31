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
    onDeclareClaimClick: () -> Unit
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
                    }
                )
            }

            composable("contracts") {
                ContractsScreen(
                    onBackClick = {
                        navController.navigate("home")
                    }
                )
            }

            composable("claims") {
                ClaimsScreen()
            }

            composable("assistance") {
                AssistanceScreen()
            }
        }
    }
}