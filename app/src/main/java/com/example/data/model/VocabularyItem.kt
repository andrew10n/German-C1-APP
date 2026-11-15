package com.example.data.model

data class ActiveVocabularyItem(
    val id: String,
    val expression: String,
    val type: String, // "Nomen-Verb-Verbindung", "Kollokation", "Idiom", "Gehobenes Adjektiv"
    val meaningDe: String,
    val naturalExample: String,
    val informalOrClunkyEquivalent: String, // What learners say vs what native C1 speakers use
    val domain: String, // Alltag, Beruf, Technik, Debatte
    val isActivelyMastered: Boolean,
    val requiredInContext: String // Prompt forcing active usage
)
