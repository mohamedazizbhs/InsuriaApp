package com.example.insuriaapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices

private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)

@Composable
fun ClaimLocationScreen(
    onBackClick: () -> Unit,
    onNextClick: (String, Double?, Double?) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var locationText by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(
                fusedLocationClient = fusedLocationClient,
                onSuccess = { lat, lng ->
                    latitude = lat
                    longitude = lng
                    locationText = "Position actuelle"
                    errorMessage = ""
                },
                onError = {
                    errorMessage = it
                }
            )
        } else {
            errorMessage = "Permission de localisation refusée."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(22.dp)
    ) {
        ClaimHeader("Localisation", "Étape 4 sur 5", onBackClick)

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Où s’est produit le sinistre ?",
            color = TextBlue,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(22.dp))

        OutlinedTextField(
            value = locationText,
            onValueChange = {
                locationText = it
                errorMessage = ""
            },
            label = { Text("Adresse ou ville") },
            leadingIcon = {
                Icon(Icons.Outlined.LocationOn, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) {
            Icon(Icons.Outlined.MyLocation, contentDescription = null)

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Utiliser ma position actuelle",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.MyLocation,
                        contentDescription = null,
                        tint = Blue,
                        modifier = Modifier.size(42.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (latitude != null && longitude != null) {
                        Text(
                            text = "Latitude : $latitude",
                            color = TextBlue.copy(alpha = 0.75f)
                        )

                        Text(
                            text = "Longitude : $longitude",
                            color = TextBlue.copy(alpha = 0.75f)
                        )
                    } else {
                        Text(
                            text = "Aucune position sélectionnée",
                            color = TextBlue.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                onNextClick(locationText, latitude, longitude)
            },
            enabled = locationText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) {
            Text(
                text = "Continuer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onSuccess: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(location.latitude, location.longitude)
            } else {
                onError("Impossible de récupérer la position actuelle.")
            }
        }
        .addOnFailureListener { error ->
            onError(error.message ?: "Erreur localisation.")
        }
}