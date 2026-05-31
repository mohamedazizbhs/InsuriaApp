package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)

@Composable
fun ClaimDescriptionScreen(
    onBackClick: () -> Unit,
    onNextClick: (String) -> Unit
) {
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(LightBg).padding(22.dp)
    ) {
        ClaimHeader("Description", "Étape 3 sur 5", onBackClick)

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Décrivez l’incident",
            color = TextBlue,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description du sinistre") },
            leadingIcon = { Icon(Icons.Outlined.TextFields, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().height(180.dp),
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(Icons.Outlined.Mic, contentDescription = null)
            Spacer(modifier = Modifier.width(10.dp))
            Text("Décrire avec la voix")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onNextClick(description)
            },
            enabled = description.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) {
            Text("Continuer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}