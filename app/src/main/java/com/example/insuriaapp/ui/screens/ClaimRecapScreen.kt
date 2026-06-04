package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuriaapp.data.ClaimData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)
private val SoftBlue = Color(0xFFE9F0FF)

@Composable
fun ClaimRecapScreen(
    claimData: ClaimData,
    onBackClick: () -> Unit,
    onEditType: () -> Unit,
    onEditProof: () -> Unit,
    onEditDescription: () -> Unit,
    onEditLocation: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(22.dp)
        ) {
            ClaimProgressHeader(
                title = "Récapitulatif",
                stepText = "Étape 5 sur 5",
                progress = 1f,
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBlue),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(22.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Vérifiez votre déclaration",
                            color = Color.White,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Vous pouvez modifier chaque information avant l’envoi.",
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            RecapCard {
                RecapEditableLine(
                    label = "Contrat concerné",
                    value = claimData.contractLabel,
                    onEditClick = onEditType
                )
                RecapEditableLine(
                    label = "Type de sinistre",
                    value = claimData.typeSinistre,
                    onEditClick = onEditType
                )

                RecapEditableLine(
                    label = "Preuve",
                    value = when (claimData.preuveType) {
                        "image_camera" -> "Photo prise avec caméra"
                        "image_gallery" -> "Image choisie depuis la galerie"
                        "video" -> "Vidéo choisie depuis la galerie"
                        else -> "Aucune preuve"
                    },
                    onEditClick = onEditProof
                )

                RecapEditableLine(
                    label = "Description",
                    value = claimData.description,
                    onEditClick = onEditDescription
                )
                RecapEditableLine(
                    label = "Message vocal",
                    value = if (claimData.hasAudio) "Audio enregistré" else "Aucun audio",
                    onEditClick = onEditDescription
                )
                RecapEditableLine(
                    label = "Localisation",
                    value = claimData.localisation,
                    onEditClick = onEditLocation
                )



                RecapEditableLine(
                    label = "Statut initial",
                    value = "En attente",
                    onEditClick = null
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Green.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Green)

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Après envoi, votre dossier sera enregistré et suivi dans Mes sinistres.",
                        color = TextBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 18.sp
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

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
                    "contractId" to claimData.contractId,
                    "contractLabel" to claimData.contractLabel,
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
                .padding(horizontal = 22.dp, vertical = 16.dp)
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
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
                    text = "Envoyer la déclaration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RecapCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
private fun RecapEditableLine(
    label: String,
    value: String,
    onEditClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextBlue.copy(alpha = 0.52f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value.ifBlank { "Non renseigné" },
                color = TextBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 21.sp
            )
        }

        if (onEditClick != null) {
            TextButton(
                onClick = onEditClick,
                colors = ButtonDefaults.textButtonColors(contentColor = Blue)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Modifier",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    HorizontalDivider(color = SoftBlue)
}