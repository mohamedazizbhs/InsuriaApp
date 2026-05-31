package com.example.insuriaapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insuriaapp.R
import com.google.firebase.auth.FirebaseAuth

private val Navy = Color(0xFF00133F)
private val Navy2 = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Navy2, Navy)
                    )
                )
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.insuria_logo),
                    contentDescription = "Logo Insuria",
                    modifier = Modifier
                        .fillMaxWidth(0.58f)
                        .height(90.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Bienvenue !",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Connectez-vous à votre compte",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-28).dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 34.dp,
                        topEnd = 34.dp
                    )
                )
                .background(Color.White)
                .padding(28.dp)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    message = ""
                },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(Icons.Outlined.Mail, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    message = ""
                },
                label = { Text("Mot de passe") },
                leadingIcon = {
                    Icon(Icons.Outlined.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible = !passwordVisible
                        }
                    ) {
                        Icon(
                            imageVector =
                                if (passwordVisible)
                                    Icons.Default.VisibilityOff
                                else
                                    Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation =
                    if (passwordVisible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    if (email.isBlank()) {
                        isError = true
                        message = "Entrez votre email pour réinitialiser le mot de passe."
                    } else {
                        FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(email)
                            .addOnSuccessListener {
                                isError = false
                                message = "Un email de réinitialisation a été envoyé."
                            }
                            .addOnFailureListener { error ->
                                isError = true
                                message = error.message ?: "Erreur lors de l'envoi de l'email."
                            }
                    }
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Mot de passe oublié ?", color = Blue)
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = message,
                    color = if (isError) MaterialTheme.colorScheme.error else Blue
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        isError = true
                        message = "Veuillez remplir tous les champs."
                        return@Button
                    }

                    isLoading = true
                    message = ""

                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            val user = authResult.user

                            if (user != null && user.isEmailVerified) {
                                isLoading = false
                                onLoginSuccess()
                            } else {
                                isLoading = false
                                isError = true
                                message = "Veuillez vérifier votre email avant de vous connecter."
                                FirebaseAuth.getInstance().signOut()
                            }
                        }
                        .addOnFailureListener { error ->
                            isLoading = false
                            isError = true
                            message = error.message ?: "Erreur de connexion."
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Se connecter",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}