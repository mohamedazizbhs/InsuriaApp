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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)

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
        ClaimHeader("Preuves", "Étape 2 sur 5", onBackClick)

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Ajoutez une preuve du sinistre",
            color = TextBlue,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Photo ou vidéo sélectionnée localement.",
            color = TextBlue.copy(alpha = 0.65f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProofCard(
            title = "Prendre une photo",
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
            icon = Icons.Outlined.PhotoLibrary,
            selected = selectedType == "image_gallery",
            onClick = {
                galleryImageLauncher.launch("image/*")
            }
        )

        ProofCard(
            title = "Choisir une vidéo",
            icon = Icons.Outlined.Videocam,
            selected = selectedType == "video",
            onClick = {
                galleryVideoLauncher.launch("video/*")
            }
        )

        if (selectedUri != null || selectedBitmap != null) {
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Green.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Green)

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Preuve sélectionnée",
                        color = TextBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
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
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) {
            Text("Continuer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProofCard(
    title: String,
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
                    Modifier.border(2.dp, Blue, RoundedCornerShape(22.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Blue.copy(alpha = 0.08f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Blue)

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                color = TextBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            if (selected) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Green)
            }
        }
    }
}