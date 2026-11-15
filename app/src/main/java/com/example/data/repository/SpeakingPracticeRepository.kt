package com.example.data.repository

import com.example.data.engine.PronunciationAnalysisEngine
import com.example.data.model.PhoneticWordTip
import com.example.data.model.PronunciationAnalysisResult
import com.example.data.model.SpeakingPrompt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SpeakingPracticeRepository(
    private val analysisEngine: PronunciationAnalysisEngine = PronunciationAnalysisEngine()
) {

    private val _prompts = MutableStateFlow<List<SpeakingPrompt>>(initialSpeakingPrompts())
    val prompts: StateFlow<List<SpeakingPrompt>> = _prompts.asStateFlow()

    private val _currentPromptIndex = MutableStateFlow(0)
    val currentPromptIndex: StateFlow<Int> = _currentPromptIndex.asStateFlow()

    private val _latestAnalysisResult = MutableStateFlow<PronunciationAnalysisResult?>(null)
    val latestAnalysisResult: StateFlow<PronunciationAnalysisResult?> = _latestAnalysisResult.asStateFlow()

    fun selectPrompt(index: Int) {
        if (index in _prompts.value.indices) {
            _currentPromptIndex.value = index
            _latestAnalysisResult.value = null
        }
    }

    fun nextPrompt() {
        val next = (_currentPromptIndex.value + 1) % _prompts.value.size
        selectPrompt(next)
    }

    fun analyzeSpokenResponse(
        prompt: SpeakingPrompt,
        transcribedText: String,
        confidence: Float
    ): PronunciationAnalysisResult {
        // The legacy engine aligns text only; discard its synthetic acoustic/CEFR claims.
        val textMatch = analysisEngine.analyzePronunciation(prompt, transcribedText, confidence)
        val result = textMatch.copy(
            overallScore = textMatch.accuracyScore,
            fluencyScore = 0, // Not measured; retained only for model compatibility.
            prosodyScore = 0,
            feedbackHeadlineDe = "Transkript-Übereinstimmung",
            cefrBandDe = "Keine Aussprachemessung oder GER-Einstufung",
            wordResults = textMatch.wordResults.map { word ->
                word.copy(phoneticTip = prompt.phoneticFocusWords.firstOrNull {
                    it.word.equals(word.expectedWord, ignoreCase = true)
                }?.tipDe)
            },
            actionableTipsDe = listOf(
                "Verglichen werden Zieltext und erkanntes Transkript. Erkennungsfehler sind möglich.",
                "Artikulation, Phoneme, Sprechtempo und Prosodie werden nicht gemessen. Allgemeine Lauttipps sind Lernmaterial, keine Fehlerdiagnose.",
                "Auch vollständige Textübereinstimmung ist kein Kompetenz- oder Prüfungsnachweis."
            )
        )
        _latestAnalysisResult.value = result
        return result
    }

    fun clearResult() {
        _latestAnalysisResult.value = null
    }

    private fun initialSpeakingPrompts(): List<SpeakingPrompt> {
        return listOf(
            SpeakingPrompt(
                id = "sp_1",
                title = "Nomen-Verb-Verbindung: In Betracht ziehen",
                targetSentence = "Wir sollten alternative Antriebstechnologien ernsthaft in Betracht ziehen.",
                category = "Nomen-Verb-Verbindungen",
                contextBrief = "Strategiemeeting in der Automobilindustrie",
                difficultyLevel = "C1.1",
                phoneticFocusWords = listOf(
                    PhoneticWordTip(
                        word = "Betracht",
                        ipa = "[bəˈtʁaxt]",
                        focusArea = "Dunkler Ach-Laut [x]",
                        tipDe = "Nach dem Vokal 'a' tief am weichen Gaumensegel reiben. Nicht wie ein hartes 'k' abstoßen."
                    ),
                    PhoneticWordTip(
                        word = "ernsthaft",
                        ipa = "[ˈɛʁnsthaft]",
                        focusArea = "Konsonantencluster (-rnst-)",
                        tipDe = "Alle vier Konsonanten präzise artikulieren, ohne Zwischenvokale einzuschieben."
                    )
                ),
                audioPromptSpeed = 1.05f
            ),
            SpeakingPrompt(
                id = "sp_2",
                title = "Gewissheit & Argumentation: Außer Zweifel stehen",
                targetSentence = "Dass die Umstellung erhebliche Ressourcen erfordert, steht außer Zweifel.",
                category = "Debatte & Argumentation",
                contextBrief = "Finanzplanung & Transformationsdialog",
                difficultyLevel = "C1.1",
                phoneticFocusWords = listOf(
                    PhoneticWordTip(
                        word = "Zweifel",
                        ipa = "[ˈtsvaɪ̯fl̩]",
                        focusArea = "Affrikate [ts]",
                        tipDe = "Das 'Z' als explosive Lautverbindung aus [t] und [s] sprechen, niemals wie ein weiches englisches 'z'."
                    ),
                    PhoneticWordTip(
                        word = "Ressourcen",
                        ipa = "[ʁɛˈsʊʁsn̩]",
                        focusArea = "Silbenreduktion & Reibe-S",
                        tipDe = "Stimmloses, scharfes 'ss' mit zügigem Übergang in den vokalisierten Endsilbenlaut."
                    )
                ),
                audioPromptSpeed = 1.10f
            ),
            SpeakingPrompt(
                id = "sp_3",
                title = "Gehobene Temporalangabe: Binnen dreier Monate",
                targetSentence = "Binnen dreier Monate müssen wir ein belastbares Sanierungskonzept vorlegen.",
                category = "Gehobene Syntax & Genitiv",
                contextBrief = "Krisensitzung der Geschäftsleitung",
                difficultyLevel = "C1.2",
                phoneticFocusWords = listOf(
                    PhoneticWordTip(
                        word = "binnen",
                        ipa = "[ˈbɪnən]",
                        focusArea = "Kurzer geschlossener I-Vokal [ɪ]",
                        tipDe = "Das 'i' kurz und hell ansetzen, gefolgt von stimmhaftem Nasalkonsonanten."
                    ),
                    PhoneticWordTip(
                        word = "Sanierungskonzept",
                        ipa = "[zaˈniːʁʊŋs kɔnˌtsɛpt]",
                        focusArea = "Stimmhaftes Anlaut-S [z]",
                        tipDe = "Das Anfangs-S in 'Sanierung' wie ein sanftes Summen stimmhaft anstimmen."
                    )
                ),
                audioPromptSpeed = 1.00f
            ),
            SpeakingPrompt(
                id = "sp_4",
                title = "Diplomatische Einschränkung: Abstriche machen",
                targetSentence = "Bei der Datenintegrität dürfen wir unter keinen Umständen Abstriche machen.",
                category = "Verhandlung & Compliance",
                contextBrief = "Audit & IT-Sicherheitsausschuss",
                difficultyLevel = "C1.2",
                phoneticFocusWords = listOf(
                    PhoneticWordTip(
                        word = "Abstriche",
                        ipa = "[ˈapˌʃtʁɪçə]",
                        focusArea = "Weicher Ich-Laut [ç]",
                        tipDe = "Den Zungenrücken an den harten Vordergaumen anlegen und einen weichen Hauchlaut erzeugen."
                    ),
                    PhoneticWordTip(
                        word = "Integrität",
                        ipa = "[ɪnteɡʁiˈtɛːt]",
                        focusArea = "Endsilbenbetonung & langes Ä [ɛː]",
                        tipDe = "Der Hauptakzent liegt unmissverständlich auf der letzten Silbe '-tät'."
                    )
                ),
                audioPromptSpeed = 1.05f
            ),
            SpeakingPrompt(
                id = "sp_5",
                title = "Diplomatischer Einwand: Erlauben Sie mir",
                targetSentence = "Erlauben Sie mir darauf hinzuweisen, dass diese Maßnahme alternativlos ist.",
                category = "Diplomatisches Führungsdeutsch",
                contextBrief = "Aufsichtsratssitzung",
                difficultyLevel = "C1.2",
                phoneticFocusWords = listOf(
                    PhoneticWordTip(
                        word = "hinzuweisen",
                        ipa = "[hɪnˈtsuːˌvaɪ̯zn̩]",
                        focusArea = "Langes U [uː] und Diphthong [aɪ̯]",
                        tipDe = "Lippen für das 'u' stark runden, danach sanfter Übergang zum Diphthong."
                    ),
                    PhoneticWordTip(
                        word = "alternativlos",
                        ipa = "[altɛʁnaˈtiːfˌloːs]",
                        focusArea = "Auslautverhärtung [f] bei '-tiv'",
                        tipDe = "Das 'v' in '-tiv' wird im Deutschen stimmlos wie ein deutsches 'f' artikuliert."
                    )
                ),
                audioPromptSpeed = 1.10f
            ),
            SpeakingPrompt(
                id = "sp_6",
                title = "Formelle Konsultation: Ins Benehmen setzen",
                targetSentence = "Bevor die Richtlinie verabschiedet wird, müssen wir uns mit der Rechtsabteilung ins Benehmen setzen.",
                category = "Amts- & Rechtsdeutsch",
                contextBrief = "Behördenabstimmung & Governance",
                difficultyLevel = "C2",
                phoneticFocusWords = listOf(
                    PhoneticWordTip(
                        word = "Benehmen",
                        ipa = "[bəˈneːmən]",
                        focusArea = "Langes geschlossenes E [eː]",
                        tipDe = "Mundspalt schmal halten, Zungenränder an die oberen Backenzähne drücken, Vokal lang halten."
                    ),
                    PhoneticWordTip(
                        word = "verabschiedet",
                        ipa = "[fɛɐ̯ˈʔapˌʃiːdət]",
                        focusArea = "Knacklaut (Glottal Stop) [ʔ]",
                        tipDe = "Kurze Stimmlippensperre vor der Vorsilbe 'ab-', wie ein winziger Atemstopp."
                    )
                ),
                audioPromptSpeed = 1.15f
            ),
            SpeakingPrompt(
                id = "sp_7",
                title = "E-Jugend Coaching: Ermutigung & Spielintelligenz",
                targetSentence = "Lass den Kopf oben, such den freien Raum und trau dich mutig ins Eins-gegen-Eins!",
                category = "Fußball-Trainer (E-Jugend)",
                contextBrief = "Anfeuerung & positives Coaching am Spielfeldrand (U10/U11)",
                difficultyLevel = "C1.1",
                phoneticFocusWords = listOf(
                    PhoneticWordTip(
                        word = "freien Raum",
                        ipa = "[ˈfʁaɪ̯ən ʁaʊ̯m]",
                        focusArea = "Zäpfchen-R [ʁ] und Diphthonge [aɪ̯] / [aʊ̯]",
                        tipDe = "Das R weich im Rachen ansetzen und die Diphthonge klar ausformen, damit der Zuruf über den Platz trägt."
                    ),
                    PhoneticWordTip(
                        word = "trau dich",
                        ipa = "[ˈtʁaʊ̯ dɪç]",
                        focusArea = "Ich-Laut [ç] nach hellem Vokal",
                        tipDe = "Die Zungenspitze bleibt hinter den unteren Schneidezähnen; ein weicher Luftstrom entsteht."
                    )
                ),
                audioPromptSpeed = 1.05f
            ),
            SpeakingPrompt(
                id = "sp_8",
                title = "C-Jugend Taktik: Umschaltspiel & Kompaktheit",
                targetSentence = "Sobald wir den Ball verlieren, schließen wir sofort die Halbräume und rücken geschlossen nach!",
                category = "Fußball-Trainer (C-Jugend)",
                contextBrief = "Taktische Halbzeitansage für Großfeld-Umschaltspiel (U14/U15)",
                difficultyLevel = "C1.2",
                phoneticFocusWords = listOf(
                    PhoneticWordTip(
                        word = "Halbräume",
                        ipa = "[ˈhalpˌʁɔɪ̯mə]",
                        focusArea = "Auslautverhärtung [p] + Umlaut-Diphthong [ɔɪ̯]",
                        tipDe = "Das 'b' in 'Halb-' wird vor 'r' wie ein kurzes, klares 'p' gesprochen; 'äu' rund als [ɔɪ̯]."
                    ),
                    PhoneticWordTip(
                        word = "geschlossen",
                        ipa = "[ɡəˈʃlɔsn̩]",
                        focusArea = "Kurzes offenes O [ɔ] vor Doppel-S",
                        tipDe = "Das 'o' bleibt kurz und knackig, die Endung '-en' verschmilzt weich mit dem 's'."
                    )
                ),
                audioPromptSpeed = 1.12f
            )
        )
    }
}
