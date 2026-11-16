package com.example.programme

internal fun taskLabel(task: String, language: ProgrammeLanguage): String = when (task) {
    "listening" -> language.text("Hören", "Listening")
    "speaking" -> language.text("Sprechen", "Speaking")
    "writing" -> language.text("Schreiben", "Writing")
    "interaction" -> language.text("Interaktion", "Interaction")
    "controlled" -> language.text("Formulieren", "Formulation")
    "quiz" -> language.text("Verstehen", "Comprehension")
    "retrieval" -> language.text("Abruf", "Retrieval")
    "reading" -> language.text("Lesen", "Reading")
    else -> language.text("Transfer", "Transfer")
}

internal fun signalLabel(signal: WeaknessSignal, language: ProgrammeLanguage): String = when (signal) {
    WeaknessSignal.SOUND_RECOGNITION -> language.text("Laute in natürlicher Sprache erkennen", "Recognizing sounds in natural speech")
    WeaknessSignal.WORD_SEGMENTATION -> language.text("Wörter im Redefluss trennen", "Segmenting words in connected speech")
    WeaknessSignal.LISTENING_VOCABULARY -> language.text("Wortschatz beim Hören abrufen", "Recalling vocabulary while listening")
    WeaknessSignal.SITUATIONAL_INFERENCE -> language.text("Situationen und Absichten deuten", "Interpreting situations and intent")
    WeaknessSignal.INFORMATION_RETENTION -> language.text("Informationen über Äußerungen hinweg behalten", "Retaining information across turns")
    WeaknessSignal.ACTIVE_RETRIEVAL -> language.text("Ausdrücke aktiv abrufen", "Actively retrieving expressions")
    WeaknessSignal.SENTENCE_FORMULATION -> language.text("Sätze spontan formulieren", "Formulating sentences spontaneously")
    WeaknessSignal.GRAMMAR_UNDER_PRESSURE -> language.text("Grammatik unter Druck", "Grammar under pressure")
    WeaknessSignal.NATURALNESS -> language.text("Natürlich und idiomatisch formulieren", "Expressing ideas naturally and idiomatically")
    WeaknessSignal.REGISTER -> language.text("Register passend wählen", "Choosing an appropriate register")
    WeaknessSignal.RESPONSE_INITIATION -> language.text("Antworten beginnen", "Starting responses")
    WeaknessSignal.CONVERSATIONAL_REPAIR -> language.text("Nachfragen und Missverständnisse reparieren", "Asking follow-up questions and repairing misunderstandings")
    WeaknessSignal.INTERACTION_MANAGEMENT -> language.text("Unerwartete Gesprächsbeiträge steuern", "Handling unexpected turns")
    WeaknessSignal.IMPLIED_MEANING -> language.text("Andeutungen und Ironie einordnen", "Interpreting implication and irony")
    WeaknessSignal.MULTI_SPEAKER_TRACKING -> language.text("Mehrere Sprecher verfolgen", "Following multiple speakers")
}

internal fun adaptiveExplanation(
    decision: AdaptiveControlDecision,
    profile: WeaknessProfile,
    language: ProgrammeLanguage
): String {
    val focus = decision.primaryTaskId?.let { taskLabel(it, language) }
        ?: return language.text(
            "Der Kernpfad bleibt aktiv; es gibt noch kein klares Prioritätssignal.",
            "The core path continues; there is no clear priority signal yet."
        )
    if (decision.reason.startsWith("Fälliger persönlicher Reparaturschritt")) {
        return language.text(
            "Heute steht $focus im Fokus, weil ein persönlicher Reparaturschritt fällig ist. Das ist eine Übungsnavigation, keine Kompetenzbewertung.",
            "Today's practice focuses on $focus because a personal review step is due. This guides practice; it is not a proficiency rating."
        )
    }
    val signal = profile.top(1).firstOrNull()
    if (signal == null || !decision.reason.startsWith("Wiederkehrendes Signal")) {
        return language.text(
            "Heute steht $focus im Fokus. Das ist eine Übungsempfehlung, keine Kompetenzbewertung.",
            "Today's practice focuses on $focus. This is a practice recommendation, not a proficiency rating."
        )
    }
    return language.text(
        "Heute steht $focus im Fokus, weil das Signal „${signalLabel(signal.signal, language)}“ in ${signal.sampleCount} Übungshinweis(en) vorkam. Das lenkt die Übungsauswahl, bewertet aber nicht deine Gesamtkompetenz.",
        "Today's practice focuses on $focus because the signal “${signalLabel(signal.signal, language)}” appeared in ${signal.sampleCount} practice record(s). This guides practice selection; it does not rate overall proficiency."
    )
}
