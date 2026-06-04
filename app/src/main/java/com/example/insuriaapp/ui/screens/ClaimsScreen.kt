package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuriaapp.data.ClaimListData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)
private val Orange = Color(0xFFFFA726)
private val Red = Color(0xFFE53935)
private val SoftBlue = Color(0xFFE9F0FF)

@Composable
fun ClaimsScreen(
    onClaimClick: (String) -> Unit
) {
    var claims by remember { mutableStateOf<List<ClaimListData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid == null) {
            errorMessage = "Utilisateur non connecté."
            isLoading = false
        } else {
            FirebaseFirestore.getInstance()
                .collection("sinistres")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    claims = result.documents.map { doc ->
                        ClaimListData(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            typeSinistre = doc.getString("typeSinistre") ?: "",
                            description = doc.getString("description") ?: "",
                            localisation = doc.getString("localisation") ?: "",
                            statut = doc.getString("statut") ?: "",
                            preuveType = doc.getString("preuveType") ?: ""
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
        ClaimsHeader(total = claims.size)

        Spacer(modifier = Modifier.height(22.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            }

            errorMessage.isNotEmpty() -> {
                ErrorCard(errorMessage)
            }

            claims.isEmpty() -> {
                EmptyClaimsCard()
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 18.dp)
                ) {
                    items(claims) { claim ->
                        ClaimItem(
                            claim = claim,
                            onClick = {
                                onClaimClick(claim.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClaimsHeader(total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FolderOpen,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Mes sinistres",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "$total dossier(s) déclaré(s)",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ClaimItem(
    claim: ClaimListData,
    onClick: () -> Unit
) {
    val statusColor = getStatusColor(claim.statut)
    val proofText = when (claim.preuveType) {
        "image_camera" -> "Photo caméra"
        "image_gallery" -> "Image galerie"
        "video" -> "Vidéo"
        else -> "Sans preuve"
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(statusColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ReportProblem,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(29.dp)
                    )
                }

                Spacer(modifier = Modifier.width(15.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = claim.typeSinistre.ifBlank { "Sinistre" },
                        color = TextBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = claim.localisation.ifBlank { "Localisation non renseignée" },
                        color = TextBlue.copy(alpha = 0.58f),
                        fontSize = 13.sp
                    )
                }

                StatusBadge(
                    text = claim.statut.ifBlank { "En attente" },
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChip(
                    text = proofText,
                    icon = Icons.Outlined.AttachFile
                )

                InfoChip(
                    text = "Voir détails",
                    icon = Icons.Outlined.ChevronRight
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun InfoChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        color = SoftBlue,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(15.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = text,
                color = TextBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyClaimsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.FolderOpen,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Aucun sinistre déclaré",
                color = TextBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Vos déclarations apparaîtront ici après envoi.",
                color = TextBlue.copy(alpha = 0.60f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Red.copy(alpha = 0.10f))
    ) {
        Text(
            text = message,
            color = Red,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(18.dp)
        )
    }
}

private fun getStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "validé", "valide", "clôturé", "cloture", "terminé", "termine" -> Green
        "refusé", "refuse" -> Red
        "en cours" -> Blue
        else -> Orange
    }
}