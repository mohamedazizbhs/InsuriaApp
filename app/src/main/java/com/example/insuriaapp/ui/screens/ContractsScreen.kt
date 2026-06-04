package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
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

private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)
private val Orange = Color(0xFFFFA726)
private val Red = Color(0xFFE53935)

@Composable
fun ContractsScreen(
    onBackClick: () -> Unit,
    onContractClick: (String) -> Unit,
    onAddContractClick: () -> Unit
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
        ContractsHeader(
            total = contracts.size,
            onAddContractClick = onAddContractClick
        )

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

            contracts.isEmpty() -> {
                EmptyContractsCard()
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 18.dp)
                ) {
                    items(contracts) { contract ->
                        ContractItem(
                            contract = contract,
                            onClick = {
                                onContractClick(contract.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractsHeader(
    total: Int,
    onAddContractClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mes contrats",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "$total contrat(s)",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onAddContractClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "+ Ajouter un contrat",
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ContractItem(
    contract: ContractData,
    onClick: () -> Unit
) {
    val statusColor =
        if (contract.statut.lowercase() == "actif") Green else Orange

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            statusColor.copy(alpha = 0.12f),
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(15.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contract.typeAssurance.ifBlank { "Contrat" },
                        color = TextBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = contract.numeroContrat.ifBlank { "Numéro non renseigné" },
                        color = TextBlue.copy(alpha = 0.58f),
                        fontSize = 13.sp
                    )
                }

                StatusBadge(
                    text = contract.statut.ifBlank { "Actif" },
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip(
                    text = contract.dateDebut.ifBlank { "Date début" },
                    icon = Icons.Outlined.CalendarMonth
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
        color = LightBg,
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
                fontSize = 12.sp,
                color = TextBlue,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmptyContractsCard() {
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
                imageVector = Icons.Outlined.Description,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Aucun contrat trouvé",
                color = TextBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Vos contrats apparaîtront ici après ajout.",
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