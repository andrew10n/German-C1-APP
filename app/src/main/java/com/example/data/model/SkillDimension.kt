package com.example.data.model

enum class SkillCategory(val titleDe: String, val descriptionDe: String, val iconName: String) {
    HOERVERSTEHEN(
        "Hörverstehen",
        "Verstehen bei natürlichem Sprechtempo, Reduktionen & Dialekten",
        "Hearing"
    ),
    SPONTANES_SPRECHEN(
        "Spontanes Sprechen",
        "Flüssigkeit, geringe Latenz, spontane Argumentation & Präzision",
        "RecordVoiceOver"
    ),
    INTERAKTION(
        "Interaktions-Intelligenz",
        "Diskussionen, Unterbrechungen parieren, Nachfragen & Reaktionen",
        "Forum"
    ),
    LESEVERSTEHEN(
        "Leseverstehen",
        "Komplexe Fachtexte, Feuilleton, implizite Bedeutung & Diskursstruktur",
        "MenuBook"
    ),
    SCHREIBEN(
        "Schreib-Präzision",
        "Berichte, differenzierte Argumentation, Gutachten & Registerkontrolle",
        "EditNote"
    ),
    GRAMMATIK(
        "Grammatik-Kontrolle",
        "Passiversatzformen, Konjunktiv I/II, Partizipialattribute, Subtilität",
        "Checklist"
    ),
    WORTSCHATZ(
        "Wortschatz & Kollokationen",
        "Nomen-Verb-Verbindungen, idiomatische Treffsicherheit, Nuancen",
        "AutoStories"
    ),
    MEDIATION(
        "Mediation & Sprachmittlung",
        "Komplexe Sachverhalte laienverständlich zusammenfassen & übertragen",
        "Transform"
    ),
    AUSSPRACHE_PROSODIE(
        "Aussprache & Prosodie",
        "Satzmelodie, sinntragende Betonung, Rhythmus & Lautverschmelzungen",
        "GraphicEq"
    )
}

data class SkillScore(
    val category: SkillCategory,
    val score: Int, // 0 - 100
    val lastTestedDaysAgo: Int,
    val trend: Int, // +3, -2 etc.
    val criticalWeakness: String,
    val masteredAreas: List<String>
)

data class CrossCuttingCompetence(
    val title: String,
    val description: String,
    val score: Int,
    val levelLabel: String
)

data class DualBenchmark(
    val functionalC1Score: Int, // Real-world confidence: Workplace, Debates, Social
    val functionalReadinessSummary: String,
    val examC1Score: Int, // Formal test readiness
    val goetheC1Readiness: Int, // 0-100%
    val testDafReadiness: Int, // 0-100%
    val telcC1Readiness: Int // 0-100%
)
