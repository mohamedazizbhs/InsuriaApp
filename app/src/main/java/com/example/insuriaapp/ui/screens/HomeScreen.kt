package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuriaapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val Navy = Color(0xFF00133F)
private val NavySoft = Color(0xFF08296D)
private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Orange = Color(0xFFFFA726)

@Composable
fun HomeScreen(
    onDeclareClaimClick: () -> Unit,
    onContractsClick: () -> Unit,
    onClaimsClick: () -> Unit,
    onAssistanceClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var prenom by remember { mutableStateOf("Utilisateur") }
    var contractsCount by remember { mutableStateOf(0) }
    var claimsCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()

            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    prenom = document.getString("prénom") ?: "Utilisateur"
                }

            db.collection("contrats")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    contractsCount = result.size()
                }

            db.collection("sinistres")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener { result ->
                    claimsCount = result.size()
                }
        }
    }

    Scaffold(
        containerColor = LightBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                HeroHeader(
                    prenom = prenom,
                    onLogoutClick = onLogoutClick
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .offset(y = (-34).dp)
                        .padding(horizontal = 22.dp)
                ) {
                    MainClaimCard(onDeclareClaimClick)

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        StatPill(
                            value = contractsCount.toString(),
                            label = "Contrats",
                            icon = Icons.Outlined.Description,
                            modifier = Modifier.weight(1f)
                        )

                        StatPill(
                            value = claimsCount.toString(),
                            label = "Sinistres",
                            icon = Icons.Outlined.FolderOpen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    SectionTitle("Accès rapide")

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ElegantActionCard(
                            title = "Contrats",
                            subtitle = "Voir mes garanties",
                            icon = Icons.Outlined.Description,
                            modifier = Modifier.weight(1f),
                            onClick = onContractsClick
                        )

                        ElegantActionCard(
                            title = "Sinistres",
                            subtitle = "Suivre mes dossiers",
                            icon = Icons.Outlined.FolderOpen,
                            modifier = Modifier.weight(1f),
                            onClick = onClaimsClick
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ElegantWideCard(
                        title = "Assistance",
                        subtitle = "Besoin d’aide ? Contactez le support ou l’assistant.",
                        icon = Icons.Outlined.SupportAgent,
                        onClick = onAssistanceClick
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    SectionTitle("Dernière activité")

                    Spacer(modifier = Modifier.height(14.dp))

                    LastClaimCard()

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
private fun HeroHeader(
    prenom: String,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(285.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavySoft, Navy)
                )
            )
            .padding(horizontal = 24.dp, vertical = 18.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.insuria_logo_white),
                    contentDescription = "Logo Insuria",
                    modifier = Modifier
                        .width(185.dp)
                        .height(88.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.13f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Bonjour $prenom 👋",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Gérez vos contrats et déclarez un sinistre en toute simplicité.",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun MainClaimCard(
    onDeclareClaimClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ReportProblem,
                        contentDescription = null,
                        tint = DarkBlue,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Déclarer un sinistre",
                        color = TextBlue,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Un parcours guidé en 5 étapes.",
                        color = TextBlue.copy(alpha = 0.58f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDeclareClaimClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Text(
                    text = "Commencer maintenant",
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
private fun StatPill(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(104.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(DarkBlue.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = DarkBlue)
            }

            Spacer(modifier = Modifier.width(13.dp))

            Column {
                Text(
                    text = value,
                    color = TextBlue,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = label,
                    color = TextBlue.copy(alpha = 0.58f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = TextBlue,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Composable
private fun ElegantActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(132.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(17.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DarkBlue,
                modifier = Modifier.size(31.dp)
            )

            Column {
                Text(
                    text = title,
                    color = TextBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = subtitle,
                    color = TextBlue.copy(alpha = 0.54f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ElegantWideCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Navy),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(19.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(17.dp))
                    .background(Color.White.copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.70f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Text("→", color = Color.White, fontSize = 25.sp)
        }
    }
}

@Composable
private fun LastClaimCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
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
                    .clip(RoundedCornerShape(17.dp))
                    .background(Orange.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReportProblem,
                    contentDescription = null,
                    tint = Orange
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Suivi de dossier",
                    color = TextBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Consultez l’évolution de vos sinistres.",
                    color = TextBlue.copy(alpha = 0.55f),
                    fontSize = 13.sp
                )
            }

            Text("Voir", color = DarkBlue, fontWeight = FontWeight.Bold)
        }
    }
}