package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuriaapp.data.ClaimListData
import com.google.firebase.firestore.FirebaseFirestore

private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Orange = Color(0xFFFFA726)

@Composable
fun ClaimDetailScreen(
    claimId: String,
    onBackClick: () -> Unit
) {
    var claim by remember { mutableStateOf<ClaimListData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(claimId) {
        FirebaseFirestore.getInstance()
            .collection("sinistres")
            .document(claimId)
            .get()
            .addOnSuccessListener { doc ->
                claim = ClaimListData(
                    id = doc.id,
                    userId = doc.getString("userId") ?: "",
                    typeSinistre = doc.getString("typeSinistre") ?: "",
                    description = doc.getString("description") ?: "",
                    localisation = doc.getString("localisation") ?: "",
                    statut = doc.getString("statut") ?: "",
                    preuveType = doc.getString("preuveType") ?: ""
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
                text = "Détail sinistre",
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

            claim == null -> {
                Text("Sinistre introuvable.", color = TextBlue)
            }

            else -> {
                ClaimDetailCard(claim!!)
            }
        }
    }
}

@Composable
private fun ClaimDetailCard(claim: ClaimListData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Icon(
                imageVector = Icons.Outlined.ReportProblem,
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(44.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = claim.typeSinistre,
                color = TextBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            DetailLine("Description", claim.description)
            DetailLine("Localisation", claim.localisation)
            DetailLine("Preuve", claim.preuveType.ifBlank { "Aucune preuve" })
            DetailLine("Statut", claim.statut)
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column {
        Text(label, color = TextBlue.copy(alpha = 0.55f), fontSize = 13.sp)
        Text(
            text = value.ifBlank { "Non renseigné" },
            color = TextBlue,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}