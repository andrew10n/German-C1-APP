package com.example.programme

enum class RecordingRequirement {
    NATIVE_OR_HIGHLY_PROFICIENT_SPEAKERS,
    NATURAL_TURN_TAKING,
    VERIFIED_TRANSCRIPT,
    SPEAKER_METADATA,
    REGIONAL_BACKGROUND,
    APPLICATION_USE_RELEASE,
    EDITED_AND_RAW_DELIVERABLES
}

data class OwnedDialogueBrief(
    val id: String,
    val situation: String,
    val context: ContextTrack,
    val speakers: Int,
    val targetSignals: List<WeaknessSignal>,
    val requirements: Set<RecordingRequirement>
)

object OwnedDialogueLibrary {
    val briefs = listOf(
        brief("supermarket", "Doppelte Abbuchung an der Kasse", ContextTrack.GENERAL, 2,
            WeaknessSignal.CONVERSATIONAL_REPAIR, WeaknessSignal.REGISTER),
        brief("customer-service", "Reklamation mit missverständlicher Rückfrage", ContextTrack.SOCIAL, 2,
            WeaknessSignal.SITUATIONAL_INFERENCE, WeaknessSignal.RESPONSE_INITIATION),
        brief("supervisor", "Messwertfrage und unerwarteter Einwand der Leitung", ContextTrack.ENGINEERING, 2,
            WeaknessSignal.INFORMATION_RETENTION, WeaknessSignal.SENTENCE_FORMULATION),
        brief("automotive-review", "ECU/TCU-Review mit Safety- und Entwicklungsrolle", ContextTrack.ENGINEERING, 3,
            WeaknessSignal.MULTI_SPEAKER_TRACKING, WeaknessSignal.INTERACTION_MANAGEMENT),
        brief("football-children", "U13-Übung erklären und spontan vereinfachen", ContextTrack.COACHING, 2,
            WeaknessSignal.REGISTER, WeaknessSignal.SENTENCE_FORMULATION),
        brief("football-parent", "Elterngespräch mit widersprüchlichem Einwand", ContextTrack.COACHING, 2,
            WeaknessSignal.CONVERSATIONAL_REPAIR, WeaknessSignal.NATURALNESS),
        brief("informal-regional", "Informelles Gespräch mit regionalem Hintergrund", ContextTrack.SOCIAL, 3,
            WeaknessSignal.WORD_SEGMENTATION, WeaknessSignal.IMPLIED_MEANING)
    )

    private fun brief(
        id: String,
        situation: String,
        context: ContextTrack,
        speakers: Int,
        vararg signals: WeaknessSignal
    ) = OwnedDialogueBrief(
        id, situation, context, speakers, signals.toList(),
        RecordingRequirement.values().toSet()
    )
}
