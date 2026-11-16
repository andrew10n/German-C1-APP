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
            "Der geführte Kursweg ist abgeschlossen; aktuell ist keine Lektion verfügbar.",
            "The guided course path is complete; no lesson is currently available."
        )
    val signal = profile.top().firstOrNull { it.signal == decision.focusSignal }
    return when (decision.reasonCode) {
        AdaptiveReasonCode.PERSONAL_REPAIR_DUE -> language.text(
            "Ein persönlicher Übungsschritt ist fällig. Heute: $focus. Das ist Übungsnavigation, keine Kompetenzbewertung.",
            "A personal practice step is due. Today's focus: $focus. This guides practice; it is not a proficiency rating."
        )
        AdaptiveReasonCode.LESSON_REVIEW_DUE -> language.text(
            "Der Wiederholungsplan ist fällig. Heute beginnt die Lektion mit aktivem Abruf: $focus.",
            "A scheduled review is due. Today's lesson starts with active retrieval: $focus."
        )
        AdaptiveReasonCode.QUIZ_REPAIR -> language.text(
            "Die Verständnisprüfung braucht einen weiteren Versuch. Erst danach führt der Lernweg weiter.",
            "The comprehension check needs another attempt. The guided path continues after it."
        )
        AdaptiveReasonCode.RESPONSE_REPAIR -> language.text(
            "Ein lokaler Hinweis in einer gespeicherten Antwort führt zu gezielter Überarbeitung: $focus. Einzelne Textmuster belegen keine Gesamtkompetenz.",
            "A local signal in a saved response calls for focused revision: $focus. One text pattern does not establish overall ability."
        )
        AdaptiveReasonCode.FIRST_RETRIEVAL -> language.text(
            "Beginne ohne Vorlage mit aktivem Abruf. Danach führt dich der Plan Schritt für Schritt durch Input, Hören und Anwendung.",
            "Start with a no-notes retrieval attempt. The plan then guides you through input, listening, and application."
        )
        AdaptiveReasonCode.NEXT_INPUT -> language.text(
            "Dein erster Abruf ist gespeichert. Als Nächstes folgt der Lektionsinput: $focus.",
            "Your first retrieval attempt is saved. Next comes the lesson input: $focus."
        )
        AdaptiveReasonCode.NEXT_LISTENING -> language.text(
            "Der Lektionsinput ist bearbeitet. Als Nächstes folgt das integrierte Hörtraining: $focus.",
            "The lesson input is complete. Next comes integrated listening practice: $focus."
        )
        AdaptiveReasonCode.NEXT_COMPREHENSION_CHECK -> language.text(
            "Input und Hören sind bearbeitet. Prüfe jetzt das Textverständnis: $focus.",
            "Input and listening are complete. Now check your comprehension: $focus."
        )
        AdaptiveReasonCode.NEXT_CONTROLLED_PRACTICE -> language.text(
            "Das Verständnis ist gesichert. Jetzt folgt angeleitete Formulierung: $focus.",
            "Comprehension is supported. Now move to guided formulation: $focus."
        )
        AdaptiveReasonCode.NEXT_PRODUCTION -> language.text(
            "Jetzt folgt eine eigenständige Anwendung in einem weiteren Aufgabentyp: $focus.",
            "Now apply the lesson independently in another task type: $focus."
        )
        AdaptiveReasonCode.RECURRING_SIGNAL -> if (signal == null) {
            language.text(
                "Ein wiederkehrendes Übungssignal lenkt die nächste Aufgabe: $focus.",
                "A recurring practice signal guides the next task: $focus."
            )
        } else {
            language.text(
                "Das Übungssignal „${signalLabel(signal.signal, language)}“ trat in ${signal.sampleCount} Einträgen auf. Deshalb folgt $focus. Das ist keine Kompetenzbewertung.",
                "The practice signal “${signalLabel(signal.signal, language)}” appeared in ${signal.sampleCount} records, so next is $focus. This is not a proficiency rating."
            )
        }
        AdaptiveReasonCode.BALANCED_PRACTICE -> language.text(
            "Der Kernpfad ist bearbeitet. Als Nächstes wählen wir einen zuletzt weniger geübten Bereich: $focus.",
            "The core path is complete. Next, practice an area used less recently: $focus."
        )
        AdaptiveReasonCode.COURSE_COMPLETE -> language.text(
            "Der geführte Kursweg ist abgeschlossen; aktuell ist keine Lektion verfügbar.",
            "The guided course path is complete; no lesson is currently available."
        )
    }
}
