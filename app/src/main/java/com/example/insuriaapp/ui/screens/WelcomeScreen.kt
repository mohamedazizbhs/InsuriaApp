package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuriaapp.R

private val Navy = Color(0xFF00133F)
private val Navy2 = Color(0xFF00246E)
private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
                    .padding(horizontal = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.insuria_logo),
                    contentDescription = "Logo Insuria",
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(290.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Votre assurance, simplement.",
                    color = TextBlue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Déclarez vos sinistres et suivez vos contrats depuis votre téléphone.",
                    color = TextBlue.copy(alpha = 0.62f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 42.dp,
                            topEnd = 42.dp
                        )
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(Navy2, Navy)
                        )
                    )
                    .padding(horizontal = 22.dp, vertical = 22.dp)
            ) {
                MainWelcomeCard(
                    onLoginClick = onLoginClick,
                    onRegisterClick = onRegisterClick
                )
            }
        }
    }
}

@Composable
private fun TopBrand() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .width(142.dp)
                .height(58.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.insuria_logo),
                    contentDescription = "Logo Insuria",
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .height(46.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.13f)
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(12.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun FeatureRow() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FeatureChip(
            text = "Sécurisé",
            icon = Icons.Outlined.Lock,
            modifier = Modifier.weight(1f)
        )

        FeatureChip(
            text = "Rapide",
            icon = Icons.Outlined.Speed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun FeatureChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.13f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(7.dp))

            Text(
                text = text,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MainWelcomeCard(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(34.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Blue.copy(alpha = 0.11f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = Blue,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Bienvenue sur Insuria",
                color = TextBlue,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Votre espace personnel pour gérer vos assurances simplement.",
                color = TextBlue.copy(alpha = 0.62f),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Icon(Icons.Outlined.Login, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Se connecter",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text("→", fontSize = 25.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue)
            ) {
                Icon(Icons.Outlined.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Créer mon compte",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text("→", fontSize = 25.sp)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Simple • Sécurisé • Disponible 24/7",
                color = TextBlue.copy(alpha = 0.48f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}