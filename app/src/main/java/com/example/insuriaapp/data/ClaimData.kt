package com.example.insuriaapp.data

data class ClaimData(
    var typeSinistre: String = "",
    var preuveUri: String = "",
    var preuveType: String = "",
    var description: String = "",
    var localisation: String = "",
    var hasAudio: Boolean = false,
    var audioPath: String = "",
    var contractId: String = "",
    var contractLabel: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    var statut: String = "En attente"
)