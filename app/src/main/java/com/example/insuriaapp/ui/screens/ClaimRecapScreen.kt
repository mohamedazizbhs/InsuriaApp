package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuriaapp.data.ClaimData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)

@Composable
fun ClaimRecapScreen(
    claimData: ClaimData,
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(22.dp)
    ) {
        ClaimHeader("Récapitulatif", "Étape 5 sur 5", onBackClick)

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Vérifiez votre déclaration",
            color = TextBlue,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                RecapLine("Type", claimData.typeSinistre)

                RecapLine(
                    "Preuve",
                    when (claimData.preuveType) {
                        "image_camera" -> "Photo prise avec caméra"
                        "image_gallery" -> "Image choisie depuis la galerie"
                        "video" -> "Vidéo choisie depuis la galerie"
                        else -> "Aucune preuve"
                    }
                )

                RecapLine("Description", claimData.description)
                RecapLine("Localisation", claimData.localisation)

                RecapLine(
                    "Latitude",
                    claimData.latitude?.toString() ?: "Non disponible"
                )

                RecapLine(
                    "Longitude",
                    claimData.longitude?.toString() ?: "Non disponible"
                )

                RecapLine("Statut", claimData.statut)
            }
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Green.copy(alpha = 0.12f))
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Green)

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Votre dossier sera transmis à l’assurance.",
                    color = TextBlue
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if (uid == null) {
                    errorMessage = "Utilisateur non connecté."
                    return@Button
                }

                isLoading = true
                errorMessage = ""

                val sinistre = hashMapOf(
                    "userId" to uid,
                    "typeSinistre" to claimData.typeSinistre,
                    "preuveUri" to claimData.preuveUri,
                    "preuveType" to claimData.preuveType,
                    "description" to claimData.description,
                    "localisation" to claimData.localisation,
                    "latitude" to claimData.latitude,
                    "longitude" to claimData.longitude,
                    "statut" to "En attente",
                    "dateDeclaration" to FieldValue.serverTimestamp()
                )

                FirebaseFirestore.getInstance()
                    .collection("sinistres")
                    .add(sinistre)
                    .addOnSuccessListener {
                        isLoading = false
                        onSubmitSuccess()
                    }
                    .addOnFailureListener { error ->
                        isLoading = false
                        errorMessage = error.message ?: "Erreur lors de l'enregistrement."
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Outlined.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    "Envoyer la déclaration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RecapLine(label: String, value: String) {
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