package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)

@Composable
fun ContractsScreen(
    onBackClick: () -> Unit
) {
    var contracts by remember { mutableStateOf<List<ContractData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            errorMessage = "Utilisateur non connecté."
            isLoading = false
        } else {
            FirebaseFirestore.getInstance()
                .collection("contrats")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    contracts = result.documents.map { doc ->
                        ContractData(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            typeAssurance = doc.getString("typeAssurance") ?: "",
                            numeroContrat = doc.getString("numeroContrat") ?: "",
                            statut = doc.getString("statut") ?: "",
                            dateDebut = doc.getString("dateDebut") ?: "",
                            dateFin = doc.getString("dateFin") ?: "",
                            documentUrl = doc.getString("documentUrl") ?: ""
                        )
                    }
                    isLoading = false
                }
                .addOnFailureListener { error ->
                    errorMessage = error.message ?: "Erreur de chargement."
                    isLoading = false
                }
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
                text = "Mes contrats",
                color = TextBlue,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Blue)
                }
            }

            errorMessage.isNotEmpty() -> {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }

            contracts.isEmpty() -> {
                Text(
                    text = "Aucun contrat trouvé.",
                    color = TextBlue.copy(alpha = 0.7f)
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(contracts) { contract ->
                        ContractCardItem(contract)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractCardItem(contract: ContractData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(38.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contract.typeAssurance,
                    color = TextBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = contract.numeroContrat,
                    color = TextBlue.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${contract.dateDebut} → ${contract.dateFin}",
                    color = TextBlue.copy(alpha = 0.55f),
                    fontSize = 13.sp
                )
            }

            Text(
                text = contract.statut,
                color = if (contract.statut == "Actif") Color(0xFF18B26B) else Color(0xFFFFA726),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}