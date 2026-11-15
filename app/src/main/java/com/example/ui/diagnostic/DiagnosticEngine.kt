package com.example.ui.diagnostic

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainApplication
import com.example.data.engine.TrainingPriorityEngine
import com.example.data.local.C1AppDatabase
import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.model.*
import com.example.data.repository.C1LearningRepository
import com.example.data.repository.DiagnosticRepository
import com.example.service.AudioSpeechHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Diagnostic Assessment Modes
 */
enum class DiagnosticMode {
    FULL_COMPETENCY,         // Legacy name: eight-question MCQ practice screening
    GRAMMAR_AND_VOCABULARY   // Focused grammar/vocabulary recognition practice
}

/**
 * Supported assessment question interaction types.
 */
enum class DiagnosticTaskType {
    AUDIO_LISTENING,       // Audio playback with dialect/reduction comprehension
    RECORDED_SPEAKING,     // Legacy name: model-utterance selection, no recording
    INTERACTION_DIALOGUE,  // Dialogue-response selection, not observed interaction
    READING_COMPREHENSION, // Text analysis for implicit meaning, tone & irony
    WRITING_PRODUCTION,    // Legacy name: register recognition, no text production
    GRAMMAR_PRECISION,     // Complex C1 syntax (Passiversatz, Konjunktiv, Partizipien)
    VOCABULARY_COLLOCATION,// Nomen-Verb-Verbindungen & idiomatic range
    MEDIATION_TRANSFER     // Transforming complex German into layperson German
}

/**
 * Diagnostic Task Definition.
 */
data class DiagnosticTask(
    val id: String,
    val taskType: DiagnosticTaskType,
    val skillCategory: SkillCategory,
    val headlineDe: String,
    val contextDe: String,
    val promptText: String,
    val spokenAudioText: String? = null,
    val speechSpeed: Float = 1.15f,
    val dialectNote: String? = null,
    val multipleChoiceOptions: List<String> = emptyList(),
    val correctOptionIndex: Int = -1,
    val analysisRationaleDe: String,
    val maxLatencyBenchmarkSeconds: Int = 3,
    val modelAnswerForFreeform: String? = null,
    val subTopicName: String = ""
)

/**
 * User submission for a single diagnostic task.
 */
data class DiagnosticUserAnswer(
    val selectedOptionIndex: Int? = null,
    val freeformText: String? = null,
    val recordedAudioLatencySeconds: Int = 0,
    val recordedDurationSeconds: Int = 0,
    val isEvaluatedCorrect: Boolean = false,
    val earnedScore: Int = 0 // 0 to 100
)

/**
 * State machine states for the diagnostic assessment process.
 */
sealed class DiagnosticUiState {
    object Loading : DiagnosticUiState()

    data class Idle(
        val savedProfile: BaselineProfileEntity?,
        val hasExistingDiagnostic: Boolean,
        val selectedMode: DiagnosticMode = DiagnosticMode.FULL_COMPETENCY
    ) : DiagnosticUiState()

    data class InProgress(
        val currentStep: Int,
        val totalSteps: Int,
        val currentTask: DiagnosticTask,
        val answers: Map<Int, DiagnosticUserAnswer>,
        val testMode: DiagnosticMode = DiagnosticMode.FULL_COMPETENCY,
        val isAudioPlaying: Boolean = false,
        val isRecordingVoice: Boolean = false,
        val responseLatencySeconds: Int = 0,
        val recordingDurationSeconds: Int = 0,
        val hasInterruptionTriggered: Boolean = false
    ) : DiagnosticUiState()

    object Evaluating : DiagnosticUiState()

    data class Completed(
        val profile: BaselineProfileEntity,
        val nineDimensionalProfile: Map<SkillCategory, SkillScore>,
        val isProvisional: Boolean,
        val confidenceScore: Float,
        val summaryDe: String,
        val grammarScore: Int = profile.grammarScore,
        val vocabularyScore: Int = profile.vocabularyScore,
        val overallScore: Int = profile.overallC1Score,
        val grammarTopicScores: Map<String, Int> = emptyMap(),
        val vocabTopicScores: Map<String, Int> = emptyMap(),
        val primaryLimitingFactor: String = profile.primaryLimitingFactor,
        val personalizedRoadmap: List<LearningPathMilestone> = emptyList(),
        val testMode: DiagnosticMode = DiagnosticMode.FULL_COMPETENCY,
        val assessedSkills: Set<SkillCategory> = emptySet(),
        val answeredTaskCount: Int = 0,
        val totalTaskCount: Int = 0
    ) : DiagnosticUiState()

    data class Error(val message: String) : DiagnosticUiState()
}

/**
 * Short MCQ practice screening, not a CEFR placement or productive-skill assessment.
 * Legacy score fields remain for compatibility; unassessed dimensions are zero.
 * Screening results are session-only and do not replace a measured baseline.
 */
class DiagnosticEngine @JvmOverloads constructor(
    application: Application,
    private val appDatabase: C1AppDatabase = (application as? MainApplication)?.database ?: C1AppDatabase.getDatabase(application),
    val learningRepository: C1LearningRepository = (application as? MainApplication)?.learningRepository ?: C1LearningRepository()
) : AndroidViewModel(application) {

    private val diagnosticRepo = DiagnosticRepository(appDatabase.baselineProfileDao())
    val speechHelper = AudioSpeechHelper(application)
    val priorityEngine = TrainingPriorityEngine()

    private val _uiState = MutableStateFlow<DiagnosticUiState>(DiagnosticUiState.Loading)
    val uiState: StateFlow<DiagnosticUiState> = _uiState.asStateFlow()

    var activeMode: DiagnosticMode = DiagnosticMode.FULL_COMPETENCY
        private set

    // -------------------------------------------------------------------------
    // 1. Eight-question MCQ practice screening (legacy task IDs retained)
    // -------------------------------------------------------------------------
    val tasks: List<DiagnosticTask> = listOf(
        // 0. Listening (Hörverstehen)
        DiagnosticTask(
            id = "task_listening",
            taskType = DiagnosticTaskType.AUDIO_LISTENING,
            skillCategory = SkillCategory.HOERVERSTEHEN,
            headlineDe = "1. Hörverstehen: reduziertes Übungsdeutsch",
            contextDe = "Synthetisch vorgelesenes Übungsbeispiel im Werkstatt-Umfeld; fiktive, vereinfachte regionale Schreibannäherung, keine authentische Dialektaufnahme.",
            promptText = "Hören Sie die Äußerung des Werkstattleiters an: Welche implizite Botschaft und welcher Tonfall liegen primär vor?",
            spokenAudioText = "Also pass amol uff, Kerle! Wenn mer die Toleranze bei dera Nockawell net grad jetzt akkurat eistelle, no fliegt uns d'ganze Serie beim Prüfstand um d'Ohre! Des hamma doch geschtern scho g'habt!",
            speechSpeed = 1.20f,
            dialectNote = "Fiktive, vereinfachte südwestdeutsche Schreibannäherung mit Lautverschmelzungen; nicht regional validiert und keine Norm.",
            multipleChoiceOptions = listOf(
                "Sachliche Bitte um Überprüfung von Berechnungen",
                "Ungeduldige Frustration über wiederholten Schlampigkeitsfehler & Furcht vor Ausschuss",
                "Lob für einen geglückten Belastungstest",
                "Freude über vorzeitigen Feierabend"
            ),
            correctOptionIndex = 1,
            analysisRationaleDe = "Die Auswahlaufgabe prüft, ob Reduktionen wie 'des hamma' und der emotional-vorwurfsvolle Subtext im gegebenen Übungstext erkannt werden. Sie misst kein allgemeines Hörniveau und keine regionale Hörkompetenz.",
            subTopicName = "Reduktion & Subtext im Übungstext"
        ),
        // 1. Speaking (Spontanes Sprechen)
        DiagnosticTask(
            id = "task_speaking",
            taskType = DiagnosticTaskType.RECORDED_SPEAKING,
            skillCategory = SkillCategory.SPONTANES_SPRECHEN,
            headlineDe = "2. Formulierungen erkennen (kein Sprechtest)",
            contextDe = "Unerwartete Frage in der Geschäftsführungssitzung.",
            promptText = "Ihr Vorstand fragt ungeduldig: 'Können Sie die Verzögerung bei der Auslieferung stichhaltig begründen?' Welche vorgegebene Entgegnung passt am besten?",
            spokenAudioText = "Können Sie die Verzögerung bei der Auslieferung stichhaltig begründen?",
            maxLatencyBenchmarkSeconds = 3,
            multipleChoiceOptions = listOf(
                "Ähm, ja also, der Zulieferer war schuld und wir konnten da echt gar nichts machen.",
                "In Anbetracht der unvorhergesehenen Lieferengpässe mussten wir eine strategische Neupriorisierung vornehmen, um unsere Qualitätsstandards nicht zu gefährden.",
                "Ich habe eine Entscheidung gemacht und das ist so in Ordnung.",
                "Wir haben schließlich keine Lust mehr auf Überstunden gehabt."
            ),
            correctOptionIndex = 1,
            analysisRationaleDe = "Die Auswahlaufgabe erkennt eine formelle, begründende Antwort mit passendem Register und einer etablierten Kollokation. Sie beobachtet kein eigenes Sprechen, keine Latenz und keine Eloquenz der lernenden Person.",
            subTopicName = "Formelles Register & Begründung"
        ),
        // 2. Interaction (Interaktions-Intelligenz)
        DiagnosticTask(
            id = "task_interaction",
            taskType = DiagnosticTaskType.INTERACTION_DIALOGUE,
            skillCategory = SkillCategory.INTERAKTION,
            headlineDe = "3. Gesprächsreaktion erkennen",
            contextDe = "Konfrontativer Einwand im Projektmeeting.",
            promptText = "Ein Kollege unterbricht Sie plötzlich: 'Das ist doch ein reines Scheinargument!' Wie parieren Sie den Einwand deeskalierend, aber bestimmt?",
            spokenAudioText = "Moment mal! Das ist doch ein reines Scheinargument!",
            multipleChoiceOptions = listOf(
                "Sie haben doch selbst keine Ahnung von den Zahlen!",
                "Lassen Sie uns diesen Punkt sachlich differenzieren: Es geht hierbei keineswegs um Scheinargumente, sondern um gesetzlich bindende Vorgaben.",
                "Ich rede jetzt, Sie müssen schweigen.",
                "Eventuell haben Sie recht, aber ich weiß auch nicht."
            ),
            correctOptionIndex = 1,
            analysisRationaleDe = "Die Auswahlaufgabe erkennt eine deeskalierende Reaktion mit inhaltlicher Grenzziehung. Sie misst keine eigene Interaktionsleistung.",
            subTopicName = "Unterbrechungen parieren"
        ),
        // 3. Reading (Leseverstehen)
        DiagnosticTask(
            id = "task_reading",
            taskType = DiagnosticTaskType.READING_COMPREHENSION,
            skillCategory = SkillCategory.LESEVERSTEHEN,
            headlineDe = "4. Leseverstehen (Implizite Bedeutung & Stilmittel)",
            contextDe = "Auszug aus einem deutschen Leitartikel (Feuilleton).",
            promptText = "„Die vielbeschworene Digitalisierungsoffensive des Ministeriums entpuppte sich letztlich als Potemkinsches Dorf auf perforiertem Pergament.“ Was intendiert der Autor?",
            multipleChoiceOptions = listOf(
                "Ein Lob auf die umweltfreundliche Papierverwendung des Ministeriums",
                "Scharfe satirische Kritik an einer substanzlosen Fassade ohne reale digitale Umsetzung",
                "Die Ankündigung eines historischen Museumsbaus",
                "Eine technische Dokumentation über Behördennetzwerke"
            ),
            correctOptionIndex = 1,
            analysisRationaleDe = "Decodierung von Metaphern ('Potemkinsches Dorf') und alliterierender Ironie ('perforiertes Pergament') auf fortgeschrittenem C1-Niveau.",
            subTopicName = "Feuilleton-Ironie & Metaphern"
        ),
        // 4. Writing (Schreib-Präzision)
        DiagnosticTask(
            id = "task_writing",
            taskType = DiagnosticTaskType.WRITING_PRODUCTION,
            skillCategory = SkillCategory.SCHREIBEN,
            headlineDe = "5. Schreib-Präzision (Register & Textkohärenz)",
            contextDe = "Formeller Schlichtungsbericht im geschäftlichen Streitfall.",
            promptText = "Welche Formulierung wahrt das formelle B2B-Schlichtungsregister am treffendsten?",
            multipleChoiceOptions = listOf(
                "Der Streit zwischen den beiden Abteilungen war total nervig und muss aufhören.",
                "Zur Beilegung der Divergenzen empfiehlt die Schlichtungskommission eine verbindliche Modifikation der Zuständigkeitsordnung.",
                "Wir machen jetzt eine Vereinbarung weil das kaputt ist.",
                "Die Kollegen sollen sich mal nicht so anstellen."
            ),
            correctOptionIndex = 1,
            analysisRationaleDe = "Die Auswahlaufgabe erkennt ein formelles Register und sachliche Wortwahl. Sie misst keine eigene Schreibproduktion.",
            subTopicName = "Formelles B2B-Register"
        ),
        // 5. Grammar (Grammatik-Kontrolle)
        DiagnosticTask(
            id = "task_grammar",
            taskType = DiagnosticTaskType.GRAMMAR_PRECISION,
            skillCategory = SkillCategory.GRAMMATIK,
            headlineDe = "6. Grammatik-Kontrolle (Passiversatzformen)",
            contextDe = "Technische Dokumentation & Vorschriften.",
            promptText = "Welcher Satz drückt die Aussage 'Diese Anforderung kann nicht erfüllt werden' mit einer korrekten C1-Passiversatzform aus?",
            multipleChoiceOptions = listOf(
                "Diese Anforderung ist nicht zu erfüllen.",
                "Diese Anforderung wird nicht erfüllt können.",
                "Man kann diese Anforderung nicht machen.",
                "Diese Anforderung tut sich nicht erfüllen lassen."
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "Beherrschung von Passiversatzformen: 'sein + zu + Infinitiv' mit modaler Bedeutung von Unmöglichkeit.",
            subTopicName = "Passiversatzformen"
        ),
        // 6. Vocabulary (Wortschatz & Kollokationen)
        DiagnosticTask(
            id = "task_vocabulary",
            taskType = DiagnosticTaskType.VOCABULARY_COLLOCATION,
            skillCategory = SkillCategory.WORTSCHATZ,
            headlineDe = "7. Wortschatz & Kollokationen (Nomen-Verb-Verbindungen)",
            contextDe = "Verhandlungs- und Tagungssprache.",
            promptText = "Welche Nomen-Verb-Verbindung drückt 'ein heikles Thema ausdrücklich ansprechen' muttersprachlich präzise aus?",
            multipleChoiceOptions = listOf(
                "ein Thema in die Sprache bringen",
                "ein Thema zur Sprache bringen",
                "ein Thema nach Reden tun",
                "ein Thema zum Diskutieren machen"
            ),
            correctOptionIndex = 1,
            analysisRationaleDe = "Feste Nomen-Verb-Verbindung: 'zur Sprache bringen' (Standard für professionelle Themeninitiierung).",
            subTopicName = "Nomen-Verb-Verbindungen"
        ),
        // 7. Mediation (Mediation & Sprachmittlung)
        DiagnosticTask(
            id = "task_mediation",
            taskType = DiagnosticTaskType.MEDIATION_TRANSFER,
            skillCategory = SkillCategory.MEDIATION,
            headlineDe = "8. Mediation & Sprachmittlung (Bürgernah erklären)",
            contextDe = "Behördlicher Bescheid der Stadtverwaltung.",
            promptText = "„Die Inanspruchnahme der Sondernutzungserlaubnis setzt die vorherige Entrichtung der kommunalen Gebührenordnung voraus.“ Wie vermitteln Sie dies bürgernah an einen Marktstandbetreiber?",
            multipleChoiceOptions = listOf(
                "Die Entrichtung der Ordnung muss von Ihnen in Anspruch genommen werden.",
                "Bevor Sie Ihren Stand auf dem Marktplatz aufbauen dürfen, muss die Standgebühr bei der Stadt bezahlt sein.",
                "Sie dürfen gar nichts machen ohne Perforation.",
                "Der Paragraph verbietet jede Marktaktivität grundsätzlich."
            ),
            correctOptionIndex = 1,
            analysisRationaleDe = "Die Auswahlaufgabe erkennt eine handlungsorientierte Umschreibung von Verwaltungssprache. Sie misst keine eigene Mediation.",
            subTopicName = "Bürgernahe Sprachmittlung"
        )
    )

    // -------------------------------------------------------------------------
    // 2. Focused C1 Grammar & Vocabulary Diagnostic Battery (10 Tasks)
    // -------------------------------------------------------------------------
    val grammarAndVocabTasks: List<DiagnosticTask> = listOf(
        // Grammar 1: Passiversatzformen
        DiagnosticTask(
            id = "gv_task_1",
            taskType = DiagnosticTaskType.GRAMMAR_PRECISION,
            skillCategory = SkillCategory.GRAMMATIK,
            subTopicName = "Passiversatzformen (Modale Bedeutung)",
            headlineDe = "1. Passiversatzformen (Modale Notwendigkeit)",
            contextDe = "Rechtliche & behördliche Vorgaben im Sicherheitsbericht.",
            promptText = "Welcher Satz drückt die Aussage 'Diese Sicherheitsrichtlinie kann keinesfalls umgangen werden' mit einer grammatisch einwandfreien C1-Passiversatzform aus?",
            multipleChoiceOptions = listOf(
                "Diese Sicherheitsrichtlinie lässt sich keinesfalls umgehen.",
                "Diese Sicherheitsrichtlinie wird keinesfalls umgangen können.",
                "Man tut diese Sicherheitsrichtlinie nicht umgehen können.",
                "Diese Sicherheitsrichtlinie ist ohne Umgehen zu sein."
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "C1-Passiversatz: 'sich lassen + Infinitiv' drückt die modale Möglichkeit bzw. Notwendigkeit ('kann umgangen werden') präzise und elegant aus."
        ),
        // Grammar 2: Partizipialattribute
        DiagnosticTask(
            id = "gv_task_2",
            taskType = DiagnosticTaskType.GRAMMAR_PRECISION,
            skillCategory = SkillCategory.GRAMMATIK,
            subTopicName = "Erweiterte Partizipialkonstruktionen",
            headlineDe = "2. Partizipialkonstruktionen (Nominalstil)",
            contextDe = "Wissenschaftlicher Fachaufsatz der Wirtschaftsforschung.",
            promptText = "Welche Umformung des Relativsatzes 'die Gutachten, die von den unabhängigen Sachverständigen erst kürzlich vorgelegt worden sind' in ein erweitertes Partizipialattribut ist grammatisch fehlerfrei?",
            multipleChoiceOptions = listOf(
                "die von den unabhängigen Sachverständigen erst kürzlich vorgelegten Gutachten",
                "die erst kürzlich vorgelegte von den unabhängigen Sachverständigen Gutachten",
                "die vorgelegt gewordenen Gutachten von Sachverständigen",
                "die von Sachverständigen vorzulegenden Gutachten vor Kurzem"
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "Erweitertes Partizip II als Linksattribut: Die Attribute müssen vollständig zwischen Artikel ('die') und Nomen ('Gutachten') eingebettet sein."
        ),
        // Grammar 3: Konjunktiv I
        DiagnosticTask(
            id = "gv_task_3",
            taskType = DiagnosticTaskType.GRAMMAR_PRECISION,
            skillCategory = SkillCategory.GRAMMATIK,
            subTopicName = "Konjunktiv I & Indirekte Rede",
            headlineDe = "3. Konjunktiv I (Journalistische Distanzierung)",
            contextDe = "Pressemitteilung & Feuilleton-Berichterstattung.",
            promptText = "Welcher Satz gibt die Äußerung 'Wir haben die Zielvorgaben erfüllt und wir fordern neue Verhandlungen' korrekt im gehobenen Konjunktiv I der indirekten Rede wieder?",
            multipleChoiceOptions = listOf(
                "Die Sprecherin betonte, sie hätten die Zielvorgaben erfüllt und fordern Verhandlungen.",
                "Die Sprecherin betonte, man habe die Zielvorgaben erfüllt und fordere neue Verhandlungen.",
                "Die Sprecherin betonte, man hat die Zielvorgaben erfüllt und würde fordern.",
                "Die Sprecherin betonte, sie haben die Zielvorgaben erfüllt und verlangen Verhandlungen."
            ),
            correctOptionIndex = 1,
            analysisRationaleDe = "Im journalistischen C1-Standard verlangt die indirekte Rede Konjunktiv I: 'habe erfüllt' (3. P. Sg.) und 'fordere' (Distanzierung)."
        ),
        // Grammar 4: Konzessive Konnektoren
        DiagnosticTask(
            id = "gv_task_4",
            taskType = DiagnosticTaskType.GRAMMAR_PRECISION,
            skillCategory = SkillCategory.GRAMMATIK,
            subTopicName = "Gehobene Konnektoren & Satzgefüge",
            headlineDe = "4. Gehobene Subjunktionen (Konzessiver Diskurs)",
            contextDe = "Wirtschaftspolitischer Leitartikel.",
            promptText = "Welche Subjunktion verbindet die beiden Aussagen auf authentischem C1-Niveau konzessiv: '[...] die Konjunkturindikatoren spürbar schwächeln, bleibt die Investitionsbereitschaft stabil'?",
            multipleChoiceOptions = listOf(
                "Wenngleich",
                "Insofern als",
                "Trotz dass",
                "Weil doch"
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "'Wenngleich' leitet einen konzessiven Nebensatz im gehobenen Register ein. 'Trotz dass' gilt als stilistischer Fehler."
        ),
        // Grammar 5: Genitiv-Präpositionen
        DiagnosticTask(
            id = "gv_task_5",
            taskType = DiagnosticTaskType.GRAMMAR_PRECISION,
            skillCategory = SkillCategory.GRAMMATIK,
            subTopicName = "Feste Genitiv-Präpositionen",
            headlineDe = "5. Gehobene Präpositionen mit Genitiv",
            contextDe = "Juristischer Schiedsspruch & B2B-Vertragsrecht.",
            promptText = "Welche Genitiv-Präposition komplettiert die Feststellung stilgerecht: 'Der Antrag wurde [...] gravierender formeller Mängel abgewiesen'?",
            multipleChoiceOptions = listOf(
                "aufgrund",
                "wegen von",
                "dank mit",
                "anstelle über"
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "'aufgrund + Genitiv' nennt den Ablehnungsgrund: Es liegen gravierende formelle Mängel vor. 'Mangels' würde deren Fehlen ausdrücken und die beabsichtigte Bedeutung umkehren."
        ),
        // Vocab 6: Nomen-Verb-Verbindungen 1
        DiagnosticTask(
            id = "gv_task_6",
            taskType = DiagnosticTaskType.VOCABULARY_COLLOCATION,
            skillCategory = SkillCategory.WORTSCHATZ,
            subTopicName = "Nomen-Verb-Verbindungen (Initiierung)",
            headlineDe = "6. Funktionsverbgefüge (Themeninitiierung)",
            contextDe = "Moderation einer kontroversen Podiumsdiskussion.",
            promptText = "Welche Nomen-Verb-Verbindung drückt 'ein heikles Thema ausdrücklich vor allen ansprechen' muttersprachlich präzise aus?",
            multipleChoiceOptions = listOf(
                "ein Thema zur Sprache bringen",
                "ein Thema in die Sprache setzen",
                "ein Thema nach Rede stellen",
                "ein Thema zum Sagen machen"
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "Feste Nomen-Verb-Verbindung: 'zur Sprache bringen' bedeutet das gezielte Ansprechen eines Gegenstands im formellen Rahmen."
        ),
        // Vocab 7: Nomen-Verb-Verbindungen 2
        DiagnosticTask(
            id = "gv_task_7",
            taskType = DiagnosticTaskType.VOCABULARY_COLLOCATION,
            skillCategory = SkillCategory.WORTSCHATZ,
            subTopicName = "Nomen-Verb-Verbindungen (Strategische Prüfung)",
            headlineDe = "7. Funktionsverbgefüge (Entscheidungsfindung)",
            contextDe = "Strategieklausur der Geschäftsleitung.",
            promptText = "Welches Funktionsverb vervollständigt den gehobenen Satz korrekt: 'Wir müssen eine Neuausrichtung unseres Portfolios ernsthaft in Erwägung [...]'?",
            multipleChoiceOptions = listOf(
                "ziehen",
                "nehmen",
                "bringen",
                "stellen"
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "Das feste Funktionsverbgefüge lautet 'in Erwägung ziehen' (= prüfen / erwägen)."
        ),
        // Vocab 8: Bildungssprache
        DiagnosticTask(
            id = "gv_task_8",
            taskType = DiagnosticTaskType.VOCABULARY_COLLOCATION,
            skillCategory = SkillCategory.WORTSCHATZ,
            subTopicName = "Bildungssprachliche Synonymik",
            headlineDe = "8. Bildungssprachliche Präzision & Synonymik",
            contextDe = "Wissenschaftlicher Methodendiskurs.",
            promptText = "Welches bildungssprachliche Adjektiv charakterisiert eine Argumentation am treffendsten als 'in sich schlüssig, nachvollziehbar und überzeugend fundiert'?",
            multipleChoiceOptions = listOf(
                "plausibel",
                "populär",
                "arbiträr",
                "dekadent"
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "'plausibel' bezeichnet methodische Einleuchtendheit und Schlüssigkeit auf akademischem C1-Niveau."
        ),
        // Vocab 9: C1-Idiomatik
        DiagnosticTask(
            id = "gv_task_9",
            taskType = DiagnosticTaskType.VOCABULARY_COLLOCATION,
            skillCategory = SkillCategory.WORTSCHATZ,
            subTopicName = "C1-Metaphorik & Idiome",
            headlineDe = "9. C1-Metaphorik & Idiomatische Kollokationen",
            contextDe = "Finanz- und Budgetanalyse.",
            promptText = "Was bedeutet die Wendung: 'Die geringfügigen Wechselkursschwankungen fallen im Jahresabschluss kaum ins Gewicht'?",
            multipleChoiceOptions = listOf(
                "Sie spielen für das Gesamtergebnis eine vernachlässigbare, unbedeutende Rolle.",
                "Sie sind viel zu schwer, um gewogen zu werden.",
                "Sie führen unmittelbar zu einer Bilanzkrise.",
                "Sie müssen im Lager auf die Waage gelegt werden."
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "Die Redewendung 'ins Gewicht fallen' bedeutet 'von ausschlaggebender Relevanz sein'. 'Kaum ins Gewicht fallen' = vernachlässigbar."
        ),
        // Vocab 10: Registerkontrolle
        DiagnosticTask(
            id = "gv_task_10",
            taskType = DiagnosticTaskType.VOCABULARY_COLLOCATION,
            skillCategory = SkillCategory.WORTSCHATZ,
            subTopicName = "Registerkontrolle & Diplomatischer Ton",
            headlineDe = "10. Registerkontrolle & Diplomatischer B2B-Ton",
            contextDe = "Schlichtungsverhandlung bei Vertragsstreitigkeiten.",
            promptText = "Welche Formulierung wahrt die diplomatische Sachlichkeit und das formelle Schlichtungsregister am treffendsten?",
            multipleChoiceOptions = listOf(
                "Zur Beilegung der Divergenzen schlagen wir eine verbindliche Modifikation der Lieferfristen vor.",
                "Damit der nervige Streit aufhört, müssen wir jetzt die Lieferfristen anpassen.",
                "Wir haben absolut keine Lust auf eure Beschwerden und machen jetzt neue Fristen.",
                "Die Gegenseite soll mal eine Entscheidung machen."
            ),
            correctOptionIndex = 0,
            analysisRationaleDe = "Klassisches C1-Register: 'Divergenzen beilegen' und 'Modifikation vorschlagen' wahren Sachlichkeit ohne unprofessionelle Emotionalität."
        )
    )

    val currentTasks: List<DiagnosticTask>
        get() = if (activeMode == DiagnosticMode.GRAMMAR_AND_VOCABULARY) grammarAndVocabTasks else tasks

    init {
        loadPersistedProfile()
    }

    /**
     * Loads existing profile from Room on initialization.
     */
    fun loadPersistedProfile(): Job {
        return viewModelScope.launch {
            try {
                val existing = diagnosticRepo.getProfileDirect()
                if (_uiState.value !is DiagnosticUiState.Loading) {
                    return@launch
                }
                // Legacy rows cannot distinguish measured evidence from earlier proxy scores.
                // Preserve the row, but never present it here as a validated C1 assessment.
                _uiState.value = DiagnosticUiState.Idle(
                    savedProfile = existing,
                    hasExistingDiagnostic = existing != null,
                    selectedMode = DiagnosticMode.FULL_COMPETENCY
                )
            } catch (e: Exception) {
                _uiState.value = DiagnosticUiState.Error("Fehler beim Laden des Profils: ${e.message}")
            }
        }
    }

    /**
     * Starts the eight-question practice screening (legacy entry point).
     */
    fun startAssessment() {
        startFullAssessment()
    }

    /**
     * Starts the eight-question MCQ practice screening.
     */
    fun startFullAssessment() {
        activeMode = DiagnosticMode.FULL_COMPETENCY
        startAssessmentInternal(tasks, activeMode)
    }

    /**
     * Starts the focused C1 Grammar & Vocabulary Diagnostic Battery.
     */
    fun startGrammarVocabAssessment() {
        activeMode = DiagnosticMode.GRAMMAR_AND_VOCABULARY
        startAssessmentInternal(grammarAndVocabTasks, activeMode)
    }

    private fun startAssessmentInternal(taskList: List<DiagnosticTask>, mode: DiagnosticMode) {
        speechHelper.stop()
        val firstTask = taskList[0]
        _uiState.value = DiagnosticUiState.InProgress(
            currentStep = 0,
            totalSteps = taskList.size,
            currentTask = firstTask,
            answers = emptyMap(),
            testMode = mode
        )
        if (firstTask.spokenAudioText != null) {
            playTaskAudio(firstTask)
        }
    }

    fun playTaskAudio(task: DiagnosticTask) {
        val audioText = task.spokenAudioText ?: return
        speechHelper.speak(audioText, task.speechSpeed)
    }

    fun recordAnswer(taskIndex: Int, selectedOption: Int, freeformText: String? = null) {
        val currentState = _uiState.value as? DiagnosticUiState.InProgress ?: return
        val currentTaskList = currentTasks
        val task = currentTaskList.getOrNull(taskIndex) ?: return

        if (selectedOption !in task.multipleChoiceOptions.indices) return
        val isCorrect = selectedOption == task.correctOptionIndex
        val answer = DiagnosticUserAnswer(
            selectedOptionIndex = selectedOption,
            freeformText = freeformText,
            isEvaluatedCorrect = isCorrect,
            earnedScore = if (isCorrect) 100 else 0
        )

        val updatedAnswers = currentState.answers + (taskIndex to answer)
        _uiState.value = currentState.copy(answers = updatedAnswers)
    }

    fun nextTask() {
        speechHelper.stop()
        val currentState = _uiState.value as? DiagnosticUiState.InProgress ?: return
        val currentTaskList = currentTasks

        val nextIndex = currentState.currentStep + 1
        if (nextIndex < currentTaskList.size) {
            val nextTask = currentTaskList[nextIndex]
            _uiState.value = currentState.copy(
                currentStep = nextIndex,
                currentTask = nextTask,
                responseLatencySeconds = 0,
                recordingDurationSeconds = 0,
                isRecordingVoice = false,
                hasInterruptionTriggered = false
            )
            if (nextTask.spokenAudioText != null) {
                playTaskAudio(nextTask)
            }
        } else {
            completeAssessment(currentState.answers)
        }
    }

    fun previousTask() {
        speechHelper.stop()
        val currentState = _uiState.value as? DiagnosticUiState.InProgress ?: return
        if (currentState.currentStep > 0) {
            val prevIndex = currentState.currentStep - 1
            val prevTask = currentTasks[prevIndex]
            _uiState.value = currentState.copy(
                currentStep = prevIndex,
                currentTask = prevTask,
                responseLatencySeconds = 0,
                recordingDurationSeconds = 0,
                isRecordingVoice = false
            )
        }
    }

    /** Compatibility entry point: no recording or timing assessment is available. */
    fun toggleVoiceRecording() {
        val current = _uiState.value as? DiagnosticUiState.InProgress ?: return
        _uiState.value = current.copy(
            isRecordingVoice = false,
            responseLatencySeconds = 0,
            recordingDurationSeconds = 0
        )
    }

    /** Recompute correctness from valid choices, never from caller-supplied scores or timing. */
    fun completeAssessment(answers: Map<Int, DiagnosticUserAnswer>): Job {
        speechHelper.stop()
        val mode = activeMode
        val taskList = currentTasks
        _uiState.value = DiagnosticUiState.Evaluating

        return viewModelScope.launch {
            try {
                val validAnswers = answers.filter { (index, answer) ->
                    val task = taskList.getOrNull(index)
                    task != null && answer.selectedOptionIndex in task.multipleChoiceOptions.indices
                }
                fun itemScore(index: Int): Int =
                    if (validAnswers[index]?.selectedOptionIndex == taskList[index].correctOptionIndex) 100 else 0

                // Only receptive/recognition items support even a narrow MCQ screening score.
                // Selecting a model utterance is not speaking, writing, interaction or mediation.
                val scoredIndices = taskList.indices.filter { index ->
                    taskList[index].skillCategory in screeningSkills
                }
                val assessedSkills = scoredIndices.filter { it in validAnswers }
                    .map { taskList[it].skillCategory }.toSet()
                fun scoreFor(category: SkillCategory): Int {
                    val indices = scoredIndices.filter { taskList[it].skillCategory == category }
                    return if (category !in assessedSkills) 0
                    else indices.map { itemScore(it) }.average().toInt()
                }
                fun topicScores(category: SkillCategory): Map<String, Int> =
                    scoredIndices.filter { it in validAnswers && taskList[it].skillCategory == category }
                        .groupBy { taskList[it].subTopicName }
                        .mapValues { (_, indices) -> indices.map { itemScore(it) }.average().toInt() }

                val grammarScore = scoreFor(SkillCategory.GRAMMATIK)
                val vocabularyScore = scoreFor(SkillCategory.WORTSCHATZ)
                val profile = BaselineProfileEntity(
                    listeningScore = scoreFor(SkillCategory.HOERVERSTEHEN),
                    speakingScore = 0,
                    interactionScore = 0,
                    readingScore = scoreFor(SkillCategory.LESEVERSTEHEN),
                    writingScore = 0,
                    grammarScore = grammarScore,
                    vocabularyScore = vocabularyScore,
                    mediationScore = 0,
                    // These legacy fields cannot represent CEFR/readiness evidence from MCQs.
                    overallC1Score = 0,
                    functionalReadinessPct = 0,
                    primaryLimitingFactor = "Nicht bestimmt – kurze MCQ-Stichprobe",
                    recommendedStartingEngine = "Evidenzbasiertes Lernprogramm auf der Startseite",
                    isCompleted = false
                )
                diagnosticRepo.saveBaselineProfile(
                    profile,
                    grammarOnly = mode == DiagnosticMode.GRAMMAR_AND_VOCABULARY,
                    practiceScreening = true
                )
                // Do not seed the evidence programme or legacy skill mastery from proxy answers.
                val summary = "Vorläufiges Profil: kurzes MCQ-Übungsscreening. " +
                    "${validAnswers.size}/${taskList.size} Aufgaben beantwortet. " +
                    "Prozentwerte zeigen nur richtige Auswahlantworten in dieser Stichprobe; " +
                    "unbeantwortete Aufgaben zählen nicht als richtig. " +
                    "Keine GER-/CEFR-Einstufung, keine Messung produktiver Fertigkeiten. " +
                    "Sprechen, Interaktion, Schreiben, Mediation und Aussprache/Prosodie: nicht beurteilt. " +
                    "0 bei nicht beurteilten Bereichen bedeutet fehlende Evidenz, nicht fehlende Fähigkeit. " +
                    "Keine validierte Konfidenz oder C1-/Alltagsbereitschaft ableitbar. " +
                    "Ergebnis nur für diese Sitzung; ein vorhandenes Baseline-Profil bleibt unverändert."

                _uiState.value = DiagnosticUiState.Completed(
                    profile = profile,
                    nineDimensionalProfile = buildNineDimensionalProfile(profile, assessedSkills = assessedSkills),
                    isProvisional = true,
                    confidenceScore = 0f,
                    summaryDe = summary,
                    grammarTopicScores = topicScores(SkillCategory.GRAMMATIK),
                    vocabTopicScores = topicScores(SkillCategory.WORTSCHATZ),
                    testMode = mode,
                    assessedSkills = assessedSkills,
                    answeredTaskCount = validAnswers.size,
                    totalTaskCount = taskList.size
                )
            } catch (e: Exception) {
                Log.e("DiagnosticEngine", "Error evaluating practice screening", e)
                _uiState.value = DiagnosticUiState.Error("Fehler bei der Übungsauswertung: ${e.message}")
            }
        }
    }

    /** Compatibility API: MCQ percentages cannot prescribe a validated competency roadmap. */
    @Suppress("UNUSED_PARAMETER")
    fun generateRoadmapForScores(
        grammarScore: Int,
        vocabScore: Int,
        bottleneck: String
    ): List<LearningPathMilestone> = emptyList()

    /** Missing provenance is unassessed; legacy scores alone are not assessment evidence. */
    @Suppress("UNUSED_PARAMETER")
    fun buildNineDimensionalProfile(
        profile: BaselineProfileEntity,
        pronunciationOverride: Int? = null,
        assessedSkills: Set<SkillCategory> = emptySet()
    ): Map<SkillCategory, SkillScore> {
        val scores = mapOf(
            SkillCategory.HOERVERSTEHEN to profile.listeningScore,
            SkillCategory.LESEVERSTEHEN to profile.readingScore,
            SkillCategory.GRAMMATIK to profile.grammarScore,
            SkillCategory.WORTSCHATZ to profile.vocabularyScore
        )
        return SkillCategory.values().associateWith { category ->
            val assessed = category in assessedSkills && category in screeningSkills
            SkillScore(
                category = category,
                score = if (assessed) scores.getValue(category).coerceIn(0, 100) else 0,
                lastTestedDaysAgo = if (assessed) 0 else -1,
                trend = 0,
                criticalWeakness = if (assessed) "Nur MCQ-Stichprobe; keine Kompetenzdiagnose"
                    else "Nicht beurteilt (0 = keine Evidenz)",
                masteredAreas = emptyList()
            )
        }
    }

    private val screeningSkills = setOf(
        SkillCategory.HOERVERSTEHEN,
        SkillCategory.LESEVERSTEHEN,
        SkillCategory.GRAMMATIK,
        SkillCategory.WORTSCHATZ
    )
}
