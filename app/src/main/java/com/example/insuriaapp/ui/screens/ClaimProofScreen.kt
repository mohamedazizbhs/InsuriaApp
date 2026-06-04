package com.example.insuriaapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)
private val SoftBlue = Color(0xFFE9F0FF)

@Composable
fun ClaimProofScreen(
    onBackClick: () -> Unit,
    onNextClick: (String, String) -> Unit
) {
    val context = LocalContext.current

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedType by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            selectedUri = null
            selectedType = "image_camera"
            errorMessage = ""
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            errorMessage = "Permission caméra refusée."
        }
    }

    val galleryImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            selectedBitmap = null
            selectedType = "image_gallery"
            errorMessage = ""
        }
    }

    val galleryVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            selectedBitmap = null
            selectedType = "video"
            errorMessage = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(22.dp)
    ) {
        ClaimProgressHeader(
            title = "Preuves",
            stepText = "Étape 2 sur 5",
            progress = 0.40f,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Ajoutez une preuve",
            color = TextBlue,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 31.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Une photo ou une vidéo aide à mieux traiter votre dossier.",
            color = TextBlue.copy(alpha = 0.62f),
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(26.dp))

        ProofCard(
            title = "Prendre une photo",
            subtitle = "Utiliser la caméra du téléphone",
            icon = Icons.Outlined.AddAPhoto,
            selected = selectedType == "image_camera",
            onClick = {
                val permission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                )

                if (permission == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(null)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        )

        ProofCard(
            title = "Choisir une image",
            subtitle = "Importer une photo depuis la galerie",
            icon = Icons.Outlined.PhotoLibrary,
            selected = selectedType == "image_gallery",
            onClick = {
                galleryImageLauncher.launch("image/*")
            }
        )

        ProofCard(
            title = "Choisir une vidéo",
            subtitle = "Importer une vidéo depuis la galerie",
            icon = Icons.Outlined.Videocam,
            selected = selectedType == "video",
            onClick = {
                galleryVideoLauncher.launch("video/*")
            }
        )

        if (selectedUri != null || selectedBitmap != null) {
            Spacer(modifier = Modifier.height(8.dp))

            SelectedProofCard(selectedType)
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (selectedUri == null && selectedBitmap == null) {
                    errorMessage = "Veuillez ajouter une preuve."
                    return@Button
                }

                val proofUri = selectedUri?.toString() ?: "camera_photo_temp"

                onNextClick(proofUri, selectedType)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkBlue
            )
        ) {
            Text(
                text = "Continuer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            Text("→", fontSize = 26.sp)
        }
    }
}

@Composable
private fun ProofCard(
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
                    Modifier.border(
                        width = 2.dp,
                        color = DarkBlue,
                        shape = RoundedCornerShape(24.dp)
                    )
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
                Text(
                    text = title,
                    color = TextBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = TextBlue.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            if (selected) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(DarkBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedProofCard(selectedType: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Green.copy(alpha = 0.12f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = Green
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Preuve sélectionnée",
                    color = TextBlue,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = when (selectedType) {
                        "image_camera" -> "Photo prise avec caméra"
                        "image_gallery" -> "Image choisie depuis la galerie"
                        "video" -> "Vidéo choisie depuis la galerie"
                        else -> "Fichier ajouté"
                    },
                    color = TextBlue.copy(alpha = 0.62f),
                    fontSize = 12.sp
                )
            }
        }
    }
}