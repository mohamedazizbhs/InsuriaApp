package com.example.insuriaapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PurpleDark = Color(0xFF2D1457)
val PurpleMain = Color(0xFF6C3DF4)
val PurpleLight = Color(0xFFF3EEFF)
val TextDark = Color(0xFF1E1E2F)
val GreyText = Color(0xFF77758A)

private val LightColors = lightColorScheme(
    primary = PurpleMain,
    secondary = PurpleDark,
    background = PurpleLight,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun InsuriaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}