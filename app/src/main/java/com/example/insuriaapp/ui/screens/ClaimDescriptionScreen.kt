package com.example.insuriaapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material.icons.outlined.TextFields
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
import java.io.File

private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Green = Color(0xFF18B26B)
private val Red = Color(0xFFE53935)
private val SoftBlue = Color(0xFFE9F0FF)

@Composable
fun ClaimDescriptionScreen(
    onBackClick: () -> Unit,
    onNextClick: (String, Boolean, String) -> Unit
) {
    val context = LocalContext.current

    var description by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var hasAudio by remember { mutableStateOf(false) }
    var audioPath by remember { mutableStateOf("") }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(context.cacheDir, "claim_audio_${System.currentTimeMillis()}.m4a")
            val newRecorder = createRecorder(file.absolutePath)

            try {
                newRecorder.prepare()
                newRecorder.start()

                recorder = newRecorder
                audioPath = file.absolutePath
                isRecording = true
                hasAudio = false
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Impossible de démarrer l'enregistrement."
            }
        } else {
            errorMessage = "Permission micro refusée."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .padding(22.dp)
    ) {
        ClaimProgressHeader(
            title = "Description",
            stepText = "Étape 3 sur 5",
            progress = 0.60f,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Décrivez ce qui s’est passé",
            color = TextBlue,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 31.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Ajoutez une description écrite ou enregistrez un message vocal.",
            color = TextBlue.copy(alpha = 0.62f),
            fontSize = 15.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(26.dp))

        OutlinedTextField(
            value = description,
            onValueChange = {
                description = it
                errorMessage = ""
            },
            label = { Text("Description du sinistre") },
            placeholder = { Text("Ex : Accident léger sur un parking...") },
            leadingIcon = {
                Icon(Icons.Outlined.TextFields, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkBlue,
                unfocusedBorderColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = DarkBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        VoiceRecordCard(
            isRecording = isRecording,
            hasAudio = hasAudio,
            onClick = {
                if (!isRecording) {
                    val permission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    )

                    if (permission == PackageManager.PERMISSION_GRANTED) {
                        val file = File(context.cacheDir, "claim_audio_${System.currentTimeMillis()}.m4a")
                        val newRecorder = createRecorder(file.absolutePath)

                        try {
                            newRecorder.prepare()
                            newRecorder.start()

                            recorder = newRecorder
                            audioPath = file.absolutePath
                            isRecording = true
                            hasAudio = false
                            errorMessage = ""
                        } catch (e: Exception) {
                            errorMessage = "Impossible de démarrer l'enregistrement."
                        }
                    } else {
                        audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                } else {
                    try {
                        recorder?.stop()
                        recorder?.release()
                        recorder = null

                        isRecording = false
                        hasAudio = true
                        errorMessage = ""
                    } catch (e: Exception) {
                        isRecording = false
                        recorder?.release()
                        recorder = null
                        errorMessage = "Erreur lors de l'arrêt de l'enregistrement."
                    }
                }
            }
        )

        if (hasAudio) {
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Green.copy(alpha = 0.12f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = Green
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Message vocal enregistré localement.",
                        color = TextBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
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
                if (description.isBlank() && !hasAudio) {
                    errorMessage = "Veuillez ajouter une description ou un message vocal."
                    return@Button
                }

                val finalDescription =
                    if (description.isBlank() && hasAudio) {
                        "Description vocale ajoutée"
                    } else {
                        description
                    }

                onNextClick(finalDescription, hasAudio, audioPath)
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

    DisposableEffect(Unit) {
        onDispose {
            try {
                recorder?.release()
            } catch (_: Exception) {
            }
        }
    }
}

@Composable
private fun VoiceRecordCard(
    isRecording: Boolean,
    hasAudio: Boolean,
    onClick: () -> Unit
) {
    val selected = isRecording || hasAudio

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (selected) {
                    Modifier.border(
                        width = 2.dp,
                        color = if (isRecording) Red else DarkBlue,
                        shape = RoundedCornerShape(24.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isRecording -> Red.copy(alpha = 0.10f)
                hasAudio -> SoftBlue
                else -> Color.White
            }
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
                    .background(
                        when {
                            isRecording -> Red
                            hasAudio -> DarkBlue
                            else -> Blue.copy(alpha = 0.10f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Outlined.Stop else Icons.Outlined.Mic,
                    contentDescription = null,
                    tint = if (isRecording || hasAudio) Color.White else Blue,
                    modifier = Modifier.size(29.dp)
                )
            }

            Spacer(modifier = Modifier.width(15.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when {
                        isRecording -> "Enregistrement en cours"
                        hasAudio -> "Message vocal prêt"
                        else -> "Enregistrer un message vocal"
                    },
                    color = TextBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when {
                        isRecording -> "Appuyez pour arrêter l’enregistrement"
                        hasAudio -> "Audio enregistré sur l’appareil"
                        else -> "Appuyez pour démarrer l’enregistrement"
                    },
                    color = TextBlue.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            if (hasAudio) {
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

private fun createRecorder(outputPath: String): MediaRecorder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder()
    } else {
        @Suppress("DEPRECATION")
        MediaRecorder()
    }.apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(outputPath)
    }
}