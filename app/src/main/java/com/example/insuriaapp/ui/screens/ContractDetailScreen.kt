package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuriaapp.data.ContractData
import com.google.firebase.firestore.FirebaseFirestore

private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)

@Composable
fun ContractDetailScreen(
    contractId: String,
    onBackClick: () -> Unit
) {
    var contract by remember { mutableStateOf<ContractData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(contractId) {
        FirebaseFirestore.getInstance()
            .collection("contrats")
            .document(contractId)
            .get()
            .addOnSuccessListener { doc ->
                contract = ContractData(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    typeAssurance = doc.getString("typeAssurance") ?: "",
                    numeroContrat = doc.getString("numeroContrat") ?: "",
                    statut = doc.getString("statut") ?: "",
                    dateDebut = doc.getString("dateDebut") ?: "",
                    dateFin = doc.getString("dateFin") ?: "",
                    documentUrl = doc.getString("documentUrl") ?: ""
                )
                isLoading = false
            }
            .addOnFailureListener { error ->
                errorMessage = error.message ?: "Erreur de chargement."
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = TextBlue)
            }

            Text(
                text = "Détail contrat",
                color = TextBlue,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Blue)
                }
            }

            errorMessage.isNotEmpty() -> {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }

            contract == null -> {
                Text("Contrat introuvable.", color = TextBlue)
            }

            else -> {
                ContractDetailCard(contract!!)
            }
        }
    }
}

@Composable
private fun ContractDetailCard(contract: ContractData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = contract.typeAssurance,
                color = TextBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            DetailLine("Numéro de contrat", contract.numeroContrat)
            DetailLine("Statut", contract.statut.ifBlank { "Non renseigné" })
            DetailLine("Date début", contract.dateDebut)
            DetailLine("Date fin", contract.dateFin)
            DetailLine("Document", contract.documentUrl.ifBlank { "Aucun document" })

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Ce contrat est actuellement ${contract.statut.lowercase()}.",
                color = if (contract.statut.lowercase() == "actif") Green else TextBlue.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = TextBlue.copy(alpha = 0.55f),
            fontSize = 13.sp
        )

        Text(
            text = value.ifBlank { "Non renseigné" },
            color = TextBlue,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}