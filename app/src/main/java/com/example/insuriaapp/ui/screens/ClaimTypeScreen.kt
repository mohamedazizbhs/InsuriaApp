package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val Navy = Color(0xFF00133F)
private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val SoftBlue = Color(0xFFE9F0FF)
private val Red = Color(0xFFE53935)

data class ClaimContractOption(
    val id: String = "",
    val label: String = ""
)

@Composable
fun ClaimTypeScreen(
    onBackClick: () -> Unit,
    onNextClick: (String, String, String) -> Unit
) {
    var selectedType by remember { mutableStateOf("") }

    var contracts by remember { mutableStateOf<List<ClaimContractOption>>(emptyList()) }
    var selectedContractId by remember { mutableStateOf("") }
    var selectedContractLabel by remember { mutableStateOf("") }
    var contractMenuExpanded by remember { mutableStateOf(false) }

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
                        val type = doc.getString("typeAssurance") ?: "Contrat"
                        val numero = doc.getString("numeroContrat") ?: ""
                        ClaimContractOption(
                            id = doc.id,
                            label = if (numero.isNotBlank()) "$type - $numero" else type
                        )
                    }
                    isLoading = false
                }
                .addOnFailureListener { error ->
                    errorMessage = error.message ?: "Erreur chargement contrats."
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
        ClaimProgressHeader(
            title = "Type de sinistre",
            stepText = "Étape 1 sur 5",
            progress = 0.20f,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Quel contrat est concerné ?",
            color = TextBlue,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 31.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sélectionnez d’abord le contrat lié au sinistre.",
            color = TextBlue.copy(alpha = 0.62f),
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            isLoading -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = DarkBlue,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Chargement des contrats...", color = TextBlue)
                    }
                }
            }

            contracts.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Red.copy(alpha = 0.10f))
                ) {
                    Text(
                        text = "Aucun contrat trouvé. Vous devez ajouter un contrat avant de déclarer un sinistre.",
                        color = Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }

            else -> {
                Box {
                    OutlinedTextField(
                        value = selectedContractLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Contrat concerné") },
                        placeholder = { Text("Choisir un contrat") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Description, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { contractMenuExpanded = true }) {
                                Icon(Icons.Outlined.ExpandMore, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = DarkBlue
                        )
                    )

                    DropdownMenu(
                        expanded = contractMenuExpanded,
                        onDismissRequest = { contractMenuExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.88f)
                    ) {
                        contracts.forEach { contract ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = contract.label,
                                        color = TextBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                },
                                onClick = {
                                    selectedContractId = contract.id
                                    selectedContractLabel = contract.label
                                    contractMenuExpanded = false
                                    errorMessage = ""
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Quel incident souhaitez-vous déclarer ?",
            color = TextBlue,
            fontSize = 23.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(18.dp))

        ClaimChoiceCard(
            title = "Accident voiture",
            subtitle = "Collision, accrochage ou dommage véhicule",
            icon = Icons.Outlined.DirectionsCar,
            selected = selectedType == "Accident voiture"
        ) {
            selectedType = "Accident voiture"
        }

        ClaimChoiceCard(
            title = "Dégât habitation",
            subtitle = "Fuite, dégât des eaux ou dommage maison",
            icon = Icons.Outlined.Home,
            selected = selectedType == "Dégât habitation"
        ) {
            selectedType = "Dégât habitation"
        }

        ClaimChoiceCard(
            title = "Incendie",
            subtitle = "Début d’incendie, fumée ou dommage matériel",
            icon = Icons.Outlined.LocalFireDepartment,
            selected = selectedType == "Incendie"
        ) {
            selectedType = "Incendie"
        }

        ClaimChoiceCard(
            title = "Autre",
            subtitle = "Autre type de sinistre à préciser",
            icon = Icons.Outlined.MoreHoriz,
            selected = selectedType == "Autre"
        ) {
            selectedType = "Autre"
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(errorMessage, color = Red, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (selectedContractId.isBlank()) {
                    errorMessage = "Veuillez sélectionner un contrat."
                    return@Button
                }

                if (selectedType.isBlank()) {
                    errorMessage = "Veuillez sélectionner un type de sinistre."
                    return@Button
                }

                onNextClick(selectedContractId, selectedContractLabel, selectedType)
            },
            enabled = contracts.isNotEmpty() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkBlue,
                disabledContainerColor = DarkBlue.copy(alpha = 0.30f)
            )
        ) {
            Text("Continuer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text("→", fontSize = 26.sp)
        }
    }
}

@Composable
fun ClaimProgressHeader(
    title: String,
    stepText: String,
    progress: Float,
    onBackClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = Navy)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextBlue, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                Text(stepText, color = TextBlue.copy(alpha = 0.55f), fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = DarkBlue,
            trackColor = Color.White
        )
    }
}

@Composable
private fun ClaimChoiceCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
            .then(
                if (selected) {
                    Modifier.border(2.dp, DarkBlue, RoundedCornerShape(24.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) SoftBlue else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 7.dp else 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(if (selected) DarkBlue else Blue.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else Blue,
                    modifier = Modifier.size(29.dp)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextBlue, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = TextBlue.copy(alpha = 0.55f), fontSize = 12.sp)
            }

            if (selected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(DarkBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}