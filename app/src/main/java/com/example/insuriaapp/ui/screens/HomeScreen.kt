package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuriaapp.R
import androidx.compose.runtime.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val Navy = Color(0xFF00133F)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)
private val Orange = Color(0xFFFFA726)

@Composable
fun HomeScreen(
    onDeclareClaimClick: () -> Unit,
    onContractsClick: () -> Unit,
    onClaimsClick: () -> Unit,
    onAssistanceClick: () -> Unit
) {

    var prenom by remember {
        mutableStateOf("Utilisateur")
    }

    val currentUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {

        currentUser?.uid?.let { uid ->

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->

                    prenom =
                        document.getString("prénom")
                            ?: "Utilisateur"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
    ) {

        HomeHeader(prenom)

        Spacer(modifier = Modifier.height(24.dp))

        MainClaimCard(onDeclareClaimClick)

        // Garde tout le reste inchangé

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Actions rapides",
            color = TextBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SimpleActionCard(
                title = "Contrats",
                icon = Icons.Outlined.Description,
                modifier = Modifier.weight(1f),
                onClick = onContractsClick
            )

            SimpleActionCard(
                title = "Sinistres",
                icon = Icons.Outlined.FolderOpen,
                modifier = Modifier.weight(1f),
                onClick = onClaimsClick
            )

            SimpleActionCard(
                title = "Support",
                icon = Icons.Outlined.SupportAgent,
                modifier = Modifier.weight(1f),
                onClick = onAssistanceClick
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Aperçu",
            color = TextBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallStatCard(
                value = "3",
                label = "Contrats actifs",
                modifier = Modifier.weight(1f)
            )

            SmallStatCard(
                value = "1",
                label = "Sinistre en cours",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Dernier sinistre",
            color = TextBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        LastClaimCard()

        Spacer(modifier = Modifier.height(26.dp))

        AssistanceMinimalCard()

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun HomeHeader(
    prénom: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.insuria_logo),
                contentDescription = "Logo Insuria",
                modifier = Modifier
                    .width(130.dp)
                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Bonjour $prénom 👋",
                color = TextBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Votre espace assurance",
                color = TextBlue.copy(alpha = 0.6f),
                fontSize = 15.sp
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                tint = Blue
            )
        }
    }
}

@Composable
private fun MainClaimCard(
    onDeclareClaimClick: () -> Unit
)  {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Navy),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Déclarer un sinistre",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ajoutez vos preuves, décrivez l’incident et envoyez votre déclaration rapidement.",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDeclareClaimClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text(
                    text = "Commencer",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text("→", fontSize = 25.sp)
            }
        }
    }
}

@Composable
private fun SimpleActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(105.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = title,
                color = TextBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SmallStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                color = Blue,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = label,
                color = TextBlue.copy(alpha = 0.65f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun LastClaimCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Orange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReportProblem,
                    contentDescription = null,
                    tint = Orange
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Accident voiture",
                    color = TextBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Déclaré le 12/05/2026",
                    color = TextBlue.copy(alpha = 0.55f),
                    fontSize = 13.sp
                )
            }

            Text(
                text = "En cours",
                color = Orange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AssistanceMinimalCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.SupportAgent,
                contentDescription = null,
                tint = Blue,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Besoin d’aide ?",
                    color = TextBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Contactez le support ou utilisez l’assistant.",
                    color = TextBlue.copy(alpha = 0.55f),
                    fontSize = 13.sp
                )
            }

            Text(
                text = "→",
                color = Blue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}