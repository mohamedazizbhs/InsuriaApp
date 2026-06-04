package com.example.insuriaapp.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Shield
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

private val DarkBlue = Color(0xFF00246E)
private val Blue = Color(0xFF1463FF)
private val LightBg = Color(0xFFF4F7FF)
private val TextBlue = Color(0xFF071D55)
private val Red = Color(0xFFE53935)

@Composable
fun AddContractScreen(
    onBackClick: () -> Unit,
    onContractAdded: () -> Unit
) {
    var typeAssurance by remember { mutableStateOf("") }
    var numeroContrat by remember { mutableStateOf("") }
    var statut by remember { mutableStateOf("Actif") }
    var dateDebut by remember { mutableStateOf("") }
    var dateFin by remember { mutableStateOf("") }
    var documentUrl by remember { mutableStateOf("") }

    var typeMenuExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val contractTypes = listOf(
        "Assurance voiture",
        "Assurance habitation"
    )

    val context = androidx.compose.ui.platform.LocalContext.current

    val dateDebutPicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                dateDebut = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                errorMessage = ""
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
    }

    val dateFinPicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                dateFin = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                errorMessage = ""
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(22.dp)
        ) {
            AddContractHeader(onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Box {
                        OutlinedTextField(
                            value = typeAssurance,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Type d'assurance") },
                            placeholder = { Text("Choisir un type") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Shield, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { typeMenuExpanded = true }) {
                                    Icon(Icons.Outlined.ExpandMore, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DarkBlue,
                                unfocusedBorderColor = LightBg,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = DarkBlue
                            )
                        )

                        DropdownMenu(
                            expanded = typeMenuExpanded,
                            onDismissRequest = { typeMenuExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.88f)
                        ) {
                            contractTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = type,
                                            color = TextBlue,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    },
                                    onClick = {
                                        typeAssurance = type
                                        typeMenuExpanded = false
                                        errorMessage = ""
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = numeroContrat,
                        onValueChange = {
                            numeroContrat = it
                            errorMessage = ""
                        },
                        label = { Text("Numéro de contrat") },
                        placeholder = { Text("Ex: AUTO-2026-001") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = LightBg,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = DarkBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = statut,
                        onValueChange = {
                            statut = it
                            errorMessage = ""
                        },
                        label = { Text("Statut") },
                        placeholder = { Text("Actif") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = LightBg,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = DarkBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = dateDebut,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Date début") },
                        placeholder = { Text("JJ/MM/AAAA") },
                        leadingIcon = {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
                        },
                        trailingIcon = {
                            TextButton(onClick = { dateDebutPicker.show() }) {
                                Text("Choisir", color = DarkBlue, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = LightBg,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = DarkBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = dateFin,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Date fin") },
                        placeholder = { Text("JJ/MM/AAAA") },
                        leadingIcon = {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
                        },
                        trailingIcon = {
                            TextButton(onClick = { dateFinPicker.show() }) {
                                Text("Choisir", color = DarkBlue, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = LightBg,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = DarkBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = documentUrl,
                        onValueChange = {
                            documentUrl = it
                            errorMessage = ""
                        },
                        label = { Text("Document URL optionnel") },
                        placeholder = { Text("Lien du document") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkBlue,
                            unfocusedBorderColor = LightBg,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = DarkBlue
                        )
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = errorMessage,
                    color = Red,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        Button(
            onClick = {
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                if (uid == null) {
                    errorMessage = "Utilisateur non connecté."
                    return@Button
                }

                if (
                    typeAssurance.isBlank() ||
                    numeroContrat.isBlank() ||
                    statut.isBlank() ||
                    dateDebut.isBlank() ||
                    dateFin.isBlank()
                ) {
                    errorMessage = "Veuillez remplir les champs obligatoires."
                    return@Button
                }

                isLoading = true
                errorMessage = ""

                val contract = hashMapOf(
                    "userId" to uid,
                    "typeAssurance" to typeAssurance,
                    "numeroContrat" to numeroContrat,
                    "statut" to statut,
                    "dateDebut" to dateDebut,
                    "dateFin" to dateFin,
                    "documentUrl" to documentUrl,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                FirebaseFirestore.getInstance()
                    .collection("contrats")
                    .add(contract)
                    .addOnSuccessListener {
                        isLoading = false
                        onContractAdded()
                    }
                    .addOnFailureListener { error ->
                        isLoading = false
                        errorMessage = error.message ?: "Erreur lors de l'ajout du contrat."
                    }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 16.dp)
                .height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Outlined.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Enregistrer le contrat",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AddContractHeader(
    onBackClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = null,
                tint = DarkBlue
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = "Ajouter un contrat",
                color = TextBlue,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Renseignez les informations principales",
                color = TextBlue.copy(alpha = 0.58f),
                fontSize = 13.sp
            )
        }
    }
}