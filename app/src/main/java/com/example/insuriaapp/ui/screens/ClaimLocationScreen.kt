package com.example.insuriaapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.CheckCircle
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
import com.google.android.gms.location.LocationServices
import android.location.Geocoder
import java.util.Locale
private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)
private val SoftBlue = Color(0xFFE9F0FF)

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
    var gpsSelected by remember { mutableStateOf(false) }
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
                    locationText = getCityName(context, lat, lng)
                    gpsSelected = true
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
        ClaimProgressHeader(
            title = "Localisation",
            stepText = "Étape 4 sur 5",
            progress = 0.80f,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Où s’est produit le sinistre ?",
            color = TextBlue,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 31.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Saisissez une adresse ou utilisez votre position actuelle pour faciliter le traitement.",
            color = TextBlue.copy(alpha = 0.62f),
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(26.dp))

        OutlinedTextField(
            value = locationText,
            onValueChange = {
                locationText = it
                gpsSelected = false
                latitude = null
                longitude = null
                errorMessage = ""
            },
            label = { Text("Adresse ou ville") },
            placeholder = { Text("Ex : Le Mans, Rue Nationale") },
            leadingIcon = {
                Icon(Icons.Outlined.LocationOn, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkBlue,
                unfocusedBorderColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = DarkBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LocationOptionCard(
            selected = gpsSelected,
            onClick = {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        )

        if (latitude != null && longitude != null) {
            Spacer(modifier = Modifier.height(12.dp))
            CoordinatesCard(locationText = locationText)
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
                onNextClick(locationText, latitude, longitude)
            },
            enabled = locationText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkBlue,
                disabledContainerColor = DarkBlue.copy(alpha = 0.30f)
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
private fun LocationOptionCard(
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
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
                    imageVector = Icons.Outlined.MyLocation,
                    contentDescription = null,
                    tint = if (selected) Color.White else Blue,
                    modifier = Modifier.size(29.dp)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Utiliser ma position actuelle",
                    color = TextBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Récupérer automatiquement les coordonnées GPS",
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
private fun CoordinatesCard(
    locationText: String
) {
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
                    text = "Position récupérée",
                    color = TextBlue,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = locationText,
                    color = TextBlue.copy(alpha = 0.62f),
                    fontSize = 12.sp
                )
            }
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
                onError("Impossible de récupérer la position actuelle. Activez le GPS puis réessayez.")
            }
        }
        .addOnFailureListener { error ->
            onError(error.message ?: "Erreur localisation.")
        }
}
private fun getCityName(
    context: android.content.Context,
    latitude: Double,
    longitude: Double
): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]

            val city = address.locality
                ?: address.subAdminArea
                ?: address.adminArea
                ?: "Position actuelle"

            val country = address.countryName ?: ""

            if (country.isNotBlank()) {
                "$city, $country"
            } else {
                city
            }
        } else {
            "Position actuelle"
        }
    } catch (e: Exception) {
        "Position actuelle"
    }
}