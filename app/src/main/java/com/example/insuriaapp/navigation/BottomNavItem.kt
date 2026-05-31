package com.example.insuriaapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("home", "Accueil", Icons.Outlined.Home),
    BottomNavItem("contracts", "Contrats", Icons.Outlined.Description),
    BottomNavItem("claims", "Sinistres", Icons.Outlined.FolderOpen),
    BottomNavItem("assistance", "Aide", Icons.Outlined.SupportAgent)
)