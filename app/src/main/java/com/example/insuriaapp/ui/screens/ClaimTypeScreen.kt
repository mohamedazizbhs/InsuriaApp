package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF00133F)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)

@Composable
fun ClaimTypeScreen(
    onBackClick: () -> Unit,
    onNextClick: (String) -> Unit
) {
    var selectedType by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(22.dp)
    ) {
        ClaimHeader("Type de sinistre", "Étape 1 sur 5", onBackClick)

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Quel type de sinistre voulez-vous déclarer ?",
            color = TextBlue,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(22.dp))

        ClaimChoiceCard("Accident voiture", Icons.Outlined.DirectionsCar, selectedType == "Accident voiture") {
            selectedType = "Accident voiture"
        }

        ClaimChoiceCard("Dégât habitation", Icons.Outlined.Home, selectedType == "Dégât habitation") {
            selectedType = "Dégât habitation"
        }

        ClaimChoiceCard("Incendie", Icons.Outlined.LocalFireDepartment, selectedType == "Incendie") {
            selectedType = "Incendie"
        }

        ClaimChoiceCard("Autre", Icons.Outlined.MoreHoriz, selectedType == "Autre") {
            selectedType = "Autre"
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onNextClick(selectedType)
            },
            enabled = selectedType.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) {
            Text("Continuer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ClaimHeader(title: String, step: String, onBackClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = Navy)
        }

        Column {
            Text(title, color = Navy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(step, color = Navy.copy(alpha = 0.55f), fontSize = 14.sp)
        }
    }
}

@Composable
private fun ClaimChoiceCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Blue else Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) Color.White else Blue)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                color = if (selected) Color.White else TextBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}