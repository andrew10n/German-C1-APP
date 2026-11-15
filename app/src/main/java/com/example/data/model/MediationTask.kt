package com.example.data.model

data class MediationTask(
    val id: String,
    val title: String,
    val mediationType: String, // "Fachsprache -> Bürgernah", "Englisch -> Idiomatisches Deutsch", "Konfliktmoderation"
    val sourceMaterial: String,
    val recipientContext: String,
    val criticalPitfallsToAvoid: List<String>,
    val targetReformulationModel: String
)
