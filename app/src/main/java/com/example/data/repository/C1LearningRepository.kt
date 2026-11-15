package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class C1LearningRepository {

    // 1. Live 9-Dimensional Skill Profile
    private val _skillScores = MutableStateFlow<Map<SkillCategory, SkillScore>>(initialSkillProfile())
    val skillScores: StateFlow<Map<SkillCategory, SkillScore>> = _skillScores.asStateFlow()

    // 2. Cross-cutting competencies
    private val _crossCutting = MutableStateFlow(
        listOf(
            CrossCuttingCompetence(
                "Pragmatische Kompetenz",
                "Situationsadäquate Tonalität & Diplomatie im Berufs- und Streitgespräch",
                0,
                "Nicht gemessen"
            ),
            CrossCuttingCompetence(
                "Registerkontrolle",
                "Mühelose Verschiebung zwischen Fachterminologie, Höflichkeit & Umgangssprache",
                0,
                "Nicht gemessen"
            )
        )
    )
    val crossCutting: StateFlow<List<CrossCuttingCompetence>> = _crossCutting.asStateFlow()

    // 3. Dual Benchmarks
    private val _dualBenchmark = MutableStateFlow(
        DualBenchmark(
            functionalC1Score = 0,
            functionalReadinessSummary = "Noch keine Einstufung. Nullwerte sind Platzhalter, keine gemessene Leistung oder Prüfungsprognose.",
            examC1Score = 0,
            goetheC1Readiness = 0,
            testDafReadiness = 0,
            telcC1Readiness = 0
        )
    )
    val dualBenchmark: StateFlow<DualBenchmark> = _dualBenchmark.asStateFlow()

    // 4. Structured Error Intelligence Database (Spaced Recycling)
    private val _errorDatabase = MutableStateFlow<List<ErrorIntelligenceItem>>(initialErrors())
    val errorDatabase: StateFlow<List<ErrorIntelligenceItem>> = _errorDatabase.asStateFlow()

    // 5. Active Vocabulary & Collocations
    private val _vocabularyList = MutableStateFlow<List<ActiveVocabularyItem>>(initialVocabulary())
    val vocabularyList: StateFlow<List<ActiveVocabularyItem>> = _vocabularyList.asStateFlow()

    // 6. Authentic Listening Laboratory Data
    private val _listeningItems = MutableStateFlow<List<ListeningLaboratoryItem>>(initialListeningLab())
    val listeningItems: StateFlow<List<ListeningLaboratoryItem>> = _listeningItems.asStateFlow()

    // 7. Conversation Scenarios
    private val _conversationScenarios = MutableStateFlow<List<ConversationScenario>>(initialConversations())
    val conversationScenarios: StateFlow<List<ConversationScenario>> = _conversationScenarios.asStateFlow()

    // 8. Mediation Tasks
    private val _mediationTasks = MutableStateFlow<List<MediationTask>>(initialMediationTasks())
    val mediationTasks: StateFlow<List<MediationTask>> = _mediationTasks.asStateFlow()

    // 9. Initial Baseline Diagnostic State
    private val _diagnosticReport = MutableStateFlow(
        BaselineDiagnosticReport(
            isCompleted = false,
            overallC1Score = 0,
            functionalReadinessPct = 0,
            primaryLimitingFactor = "Einstufungstest ausstehend",
            recommendedStartingPath = "Initial-Diagnose zur Personalisierung Ihres C1-Trainingsplans",
            skillRatings = emptyMap()
        )
    )
    val diagnosticReport: StateFlow<BaselineDiagnosticReport> = _diagnosticReport.asStateFlow()

    val priorityEngine = com.example.data.engine.TrainingPriorityEngine()

    // 10. Intelligent Training Priority Recommendation
    fun getTopTrainingPriority(): TrainingPriorityRecommendation {
        val scores = _skillScores.value.values.toList()
        val errors = _errorDatabase.value

        val activities = scores.map { skill ->
            val unresErrors = errors.count {
                it.category == skillCategoryToErrorCategory(skill.category) && it.masteryStage != MasteryStage.GEMEISTERT
            }
            priorityEngine.createActivityFromSkill(
                skill = skill,
                unresolvedErrorCount = unresErrors,
                recentPerformanceScore = 30.0
            )
        }

        val bestEvaluation = priorityEngine.selectHighestValueActivity(activities)
        val activity = bestEvaluation.activity

        return TrainingPriorityRecommendation(
            category = activity.category,
            calculatedPriority = bestEvaluation.normalizedPriorityPercent,
            priorityRank = 1,
            reasonHeadline = bestEvaluation.rationaleHeadline,
            detailedRationale = bestEvaluation.detailedRationaleDe,
            recommendedTaskTitle = activity.recommendedTaskTitle,
            sessionDurationMin = 18,
            targetEnvironment = activity.environment,
            weightBreakdown = PriorityWeights(
                severityOfWeaknessPct = activity.weaknessSeverity.toInt(),
                functionalImportancePct = activity.importanceForC1.toInt(),
                recurringErrorsPct = activity.recurringErrorsScore.toInt(),
                daysSinceTrainedPct = activity.timeSinceLastTraining.toInt(),
                fatiguePerformancePct = activity.recentPerformanceNeed.toInt()
            )
        )
    }

    /**
     * Connects baseline diagnostic results to the live 9-dimensional skill profile
     * and dual benchmark, directly feeding TrainingPriorityEngine.
     * Note: Historical error-recycling progress and vocabulary mastery are strictly preserved!
     */
    fun applyDiagnosticResults(
        profile: com.example.data.local.entity.BaselineProfileEntity,
        nineDimensionalScores: Map<SkillCategory, Int>
    ) {
        _skillScores.update { currentMap ->
            currentMap.mapValues { (cat, skill) ->
                val newScore = nineDimensionalScores[cat] ?: skill.score
                skill.copy(
                    score = newScore,
                    lastTestedDaysAgo = 0,
                    trend = 0
                )
            }
        }

        _dualBenchmark.update { current ->
            current.copy(
                functionalC1Score = profile.functionalReadinessPct,
                examC1Score = profile.overallC1Score,
                functionalReadinessSummary = "Heuristische Übungseinstufung, kein validierter C1- oder Prüfungsnachweis. Sprechen und Aussprache werden nicht akustisch bewertet. Primärer Übungshinweis: ${profile.primaryLimitingFactor}."
            )
        }

        _diagnosticReport.update {
            BaselineDiagnosticReport(
                isCompleted = true,
                overallC1Score = profile.overallC1Score,
                functionalReadinessPct = profile.functionalReadinessPct,
                primaryLimitingFactor = profile.primaryLimitingFactor,
                recommendedStartingPath = profile.recommendedStartingEngine,
                skillRatings = nineDimensionalScores
            )
        }
    }

    private fun skillCategoryToErrorCategory(skill: SkillCategory): ErrorCategory {
        return when (skill) {
            SkillCategory.GRAMMATIK -> ErrorCategory.PRAEPOSITIONEN_KASUS
            SkillCategory.WORTSCHATZ -> ErrorCategory.KOLLOKATIONEN
            SkillCategory.SCHREIBEN -> ErrorCategory.REGISTER_MISMATCH
            SkillCategory.SPONTANES_SPRECHEN, SkillCategory.INTERAKTION -> ErrorCategory.ZOEGERN_LATENZ
            else -> ErrorCategory.VERBKLAMMER_SATZSTRUKTUR
        }
    }

    private var sessionRotationIndex = 0

    fun generateDailySession(): DailyTrainingSession {
        val topPriority = getTopTrainingPriority()
        val errorsForCorrection = _errorDatabase.value
            .filter { it.masteryStage != MasteryStage.GEMEISTERT }
            .shuffled()
            .take(2)
            .ifEmpty { _errorDatabase.value.take(2) }

        val templateIdx = sessionRotationIndex % 6
        sessionRotationIndex++

        return when (templateIdx) {
            0 -> DailyTrainingSession(
                id = "session_${System.currentTimeMillis()}",
                title = "Tagestraining: ${topPriority.reasonHeadline}",
                targetCategory = topPriority.category,
                totalMinutes = 18,
                environment = topPriority.targetEnvironment,
                warmUpPrompt = WarmUpTask(
                    audioSpeakerText = "Da bin ich ganz anderer Meinung. Wir müssen das Vorhaben unverzüglich stoppen, sonst laufen uns die Kosten davon!",
                    accentInfo = "Schnelles Sprechtempo (1.15x), Berlin/Brandenburg, emotionale Dringlichkeit",
                    questionPrompt = "Welche unausgesprochene Haltung signalisiert der Sprecher primär?",
                    options = listOf(
                        "Konstruktive Verhandlungsbereitschaft mit Budgetvorbehalt",
                        "Kategorische Ablehnung und Druckaufbau durch Dramatisierung",
                        "Zustimmung unter der Bedingung personeller Aufstockung",
                        "Unsicherheit bezüglich der genauen Zahlen"
                    ),
                    correctIndex = 1,
                    diagnosticExplanation = "Inferenz-Diagnostik: Die Schärfe von 'unverzüglich stoppen' und 'sonst laufen uns die Kosten davon' demonstriert kompromisslose Blockadehaltung, nicht konstruktive Vorbehalte."
                ),
                mainChallenge = MainChallengeTask(
                    title = "Stresstest: Spontane Gegendarstellung im Führungskreis",
                    scenarioContext = "Im Budget-Meeting unterbricht Sie Ihr Kollege Herr Brandt und stellt die Wirtschaftlichkeit Ihres Konzepts öffentlich in Abrede.",
                    partnerStatement = "Moment mal, das klingt ja auf dem Papier ganz nett, aber seien wir doch ehrlich: Bei den aktuellen Rohstoffpreisen ist Ihr Entwurf schlichtweg utopisch. Da verbrennen wir nur Geld!",
                    stage1Prompt = "Stufe 1 (Souveränes Auffangen): Weisen Sie den Vorwurf 'utopisch' diplomatisch, aber bestimmt zurück, indem Sie die Risikoberechnung erwähnen.",
                    stage2Deepening = "Stufe 2 (Konter unter Druck): Nutzen Sie eine Nomen-Verb-Verbindung ('zur Sprache bringen' / 'in Erwägung ziehen'), um das Gespräch wieder auf die Fakten zu lenken.",
                    stage3StressInference = "Stufe 3 (Absicherung): Fordern Sie Herrn Brandt auf, eine konkrete Gegenrechnung vorzulegen, ohne aggressiv zu wirken.",
                    keyPointsExpected = listOf(
                        "Kein Rechtfertigungston, sondern strategische Deeskalation",
                        "Verwendung von gehobenen Konnektoren ('Ganz im Gegenteil', 'In Anbetracht der Tatsache')",
                        "Reaktionslatenz unter 3 Sekunden"
                    )
                ),
                productionTask = ProductionTask(
                    isSpeaking = true,
                    situationBrief = "Reagieren Sie jetzt direkt per Spracheingabe oder formulierter Antwort auf den Einwand von Herrn Brandt.",
                    timeLimitSeconds = 90,
                    keyEvaluationCriteria = listOf(
                        "Flüssigkeit & Sprechrhythmus (unter 2s Latenz)",
                        "Angemessenes Führungsregister (weder defensiv noch beleidigt)",
                        "Kohärente Argumentationskette"
                    ),
                    simulatedAudioPrompt = "Und was sagen Sie dazu? Können Sie das überhaupt stichhaltig belegen?"
                ),
                correctionTask = CorrectionTask(errorItems = errorsForCorrection),
                transferTask = TransferTask(
                    transferDomain = "Privates Umfeld: Mietpreisverhandlung oder WEG-Versammlung",
                    scenarioPrompt = "Übertragen Sie die soeben geübte Deeskalationsstruktur auf eine Diskussion mit Ihrem Vermieter über eine geplante Modernisierungsumlage.",
                    benchmarkExpectation = "Verwenden Sie die Zielstrukturen ohne Blick auf die vorherigen Notizen."
                )
            )
            1 -> DailyTrainingSession(
                id = "session_${System.currentTimeMillis()}",
                title = "Nominalstil & Passiversatz in der Fachkommunikation",
                targetCategory = SkillCategory.GRAMMATIK,
                totalMinutes = 18,
                environment = "Beruf & Akademie (35%)",
                warmUpPrompt = WarmUpTask(
                    audioSpeakerText = "Die Anlage ist bis Freitag einer umfassenden Sicherheitsprüfung zu unterziehen; etwaige Mängel sind unverzüglich abzustellen.",
                    accentInfo = "Kühles, präzises Standarddeutsch (1.10x), technisches Prüfregister",
                    questionPrompt = "Welche grammatische Bedeutung hat 'ist zu unterziehen' in diesem Kontext?",
                    options = listOf(
                        "Eine rein freiwillige Empfehlung ohne Zeitdruck",
                        "Eine zwingende Notwendigkeit / Verpflichtung ('muss unterzogen werden')",
                        "Eine abgeschlossene Handlung in der Vergangenheit",
                        "Eine persönliche Vermutung des Sprechers"
                    ),
                    correctIndex = 1,
                    diagnosticExplanation = "Passiversatzform 'sein + zu + Infinitiv' drückt hier eine klare modale Verpflichtung aus (muss geprüft werden)."
                ),
                mainChallenge = MainChallengeTask(
                    title = "Register-Transformation: Vom Verbal- zum Nominalstil",
                    scenarioContext = "Sie verfassen ein offizielles Status-Update für den technischen Vorstand nach einem Server-Zwischenfall.",
                    partnerStatement = "Unser Team muss die Protokolle kürzer und förmlicher schreiben. Wie fassen Sie zusammen: 'Weil das Backup ausgefallen ist, konnten wir die Daten nicht rechtzeitig wiederherstellen'?",
                    stage1Prompt = "Stufe 1 (Präpositionale Kausalität): Nutzen Sie 'Infolge + Genitiv' oder 'Aufgrund + Genitiv'.",
                    stage2Deepening = "Stufe 2 (Nominalisierung): Wandeln Sie 'wiederherstellen' in ein präzises Substantiv um ('Wiederherstellung').",
                    stage3StressInference = "Stufe 3 (Passiversatz): Ergänzen Sie eine Handlungsempfehlung mit 'lässt sich vermeiden' oder 'bleibt abzuwarten'.",
                    keyPointsExpected = listOf(
                        "Korrekte Genitiv-Rektion ('infolge des Backup-Ausfalls')",
                        "Vermeidung von 'weil'-Sätzen im formellen Berichtsstil",
                        "Präzise Fachterminologie ohne Anglizismen-Überladung"
                    )
                ),
                productionTask = ProductionTask(
                    isSpeaking = false,
                    situationBrief = "Formulieren Sie eine 2-sätzige Vorstandsnotiz im gehobenen Nominalstil über eine Lieferkettenverzögerung.",
                    timeLimitSeconds = 90,
                    keyEvaluationCriteria = listOf(
                        "Mindestens zwei Genitiv-Präpositionen ('ungeachtet', 'infolge', 'binnen')",
                        "Korrekte Passiversatzform",
                        "Objektiver, sachlicher Tonfall"
                    ),
                    simulatedAudioPrompt = "Fassen Sie die Ursache und die Konsequenz für den Quartalsbericht in zwei Sätzen zusammen."
                ),
                correctionTask = CorrectionTask(errorItems = errorsForCorrection),
                transferTask = TransferTask(
                    transferDomain = "Behördenkorrespondenz & Vertragswesen",
                    scenarioPrompt = "Schreiben Sie einen kurzen Widerspruch gegen eine fehlerhafte Nebenkostenabrechnung unter Verwendung von 'unter Bezugnahme auf' und 'ungeachtet'.",
                    benchmarkExpectation = "Klare juristische Sachlichkeit ohne emotionale Wertungen."
                )
            )
            2 -> DailyTrainingSession(
                id = "session_${System.currentTimeMillis()}",
                title = "Schnellsprech-Dekodierung & Ironie im Alltag",
                targetCategory = SkillCategory.HOERVERSTEHEN,
                totalMinutes = 18,
                environment = "Alltag & Soziales (40%)",
                warmUpPrompt = WarmUpTask(
                    audioSpeakerText = "Na toll, schon wieder Schienenersatzverkehr! Da kannste die Pünktlichkeit heute echt in der Pfeife rauchen.",
                    accentInfo = "Berliner Umgangssprache (1.25x), sarkastischer Unterton, Elisionen",
                    questionPrompt = "Was drückt die Redewendung 'etwas in der Pfeife rauchen können' aus?",
                    options = listOf(
                        "Man kann sich über die Situation entspannt freuen",
                        "Etwas ist völlig wertlos, unbrauchbar oder endgültig gescheitert",
                        "Man sollte eine Pause einlegen",
                        "Die Bahn fährt heute besonders schnell"
                    ),
                    correctIndex = 1,
                    diagnosticExplanation = "Sarkastisches Idiom: 'in der Pfeife rauchen können' signalisiert Frustration darüber, dass Pünktlichkeit heute völlig unmöglich ist."
                ),
                mainChallenge = MainChallengeTask(
                    title = "Subtext-Analyse: Zwischen den Zeilen hören",
                    scenarioContext = "Ein Kollege kommentiert Ihren Vorschlag mit einem scheinbar freundlichen Lächeln.",
                    partnerStatement = "Ach, das ist ja ein überaus ambitionierter Ansatz! Ich frage mich nur, ob die Geschäftsführung für derlei Experimente momentan den nötigen Spielraum sieht.",
                    stage1Prompt = "Stufe 1 (Subtext erkennen): Was meint der Kollege wirklich mit 'überaus ambitioniert' und 'derlei Experimente'?",
                    stage2Deepening = "Stufe 2 (Schlagfertig kontern): Reagieren Sie freundlich, aber nehmen Sie dem Wort 'Experiment' sofort den Wind aus den Segeln.",
                    stage3StressInference = "Stufe 3 (Verbündete gewinnen): Stellen Sie eine offene Rückfrage, die den Kollegen zwingt, konkrete Einwände zu benennen.",
                    keyPointsExpected = listOf(
                        "Erkennen von höflich verpackter Skepsis ('Experiment')",
                        "Souveränes Umdeuten ('pilotierter Praxistest' statt 'Experiment')",
                        "Ruhige, gelassene Stimmlage"
                    )
                ),
                productionTask = ProductionTask(
                    isSpeaking = true,
                    situationBrief = "Parieren Sie den verdeckten Zweifel Ihres Kollegen souverän und charmant.",
                    timeLimitSeconds = 75,
                    keyEvaluationCriteria = listOf(
                        "Direkte Adressierung des Subtexts ohne Konfrontation",
                        "Einsatz einer Modalpartikel ('doch', 'ja', 'eben') für Natürlichkeit",
                        "Klare Rückführung auf messbare Pilot-Daten"
                    ),
                    simulatedAudioPrompt = "Na ja, Sie werden ja sehen, ob das oben auf Gegenliebe stößt."
                ),
                correctionTask = CorrectionTask(errorItems = errorsForCorrection),
                transferTask = TransferTask(
                    transferDomain = "Nachbarschaft & Freundeskreis",
                    scenarioPrompt = "Reagieren Sie humorvoll, wenn jemand Ihre Urlaubsplanung als 'ziemlich gewagt' bezeichnet.",
                    benchmarkExpectation = "Gelassene, idiomatische Schlagfertigkeit auf Augenhöhe."
                )
            )
            3 -> DailyTrainingSession(
                id = "session_${System.currentTimeMillis()}",
                title = "Präzise Konnektoren & Nomen-Verb-Verbindungen",
                targetCategory = SkillCategory.WORTSCHATZ,
                totalMinutes = 18,
                environment = "Debatte & Gutachten (35%)",
                warmUpPrompt = WarmUpTask(
                    audioSpeakerText = "Die Auswertung fällt zwar ernüchternd aus; gleichwohl bietet sie uns einen belastbaren Ansatzpunkt für die Neuausrichtung.",
                    accentInfo = "Gehobenes Norddeutsches Standarddeutsch (1.05x)",
                    questionPrompt = "Welche logische Relation stellt 'gleichwohl' hier her?",
                    options = listOf(
                        "Eine zeitliche Gleichzeitigkeit",
                        "Einen konzessiven Gegensatz ('trotzdem / dennoch')",
                        "Eine kausale Begründung ('weil')",
                        "Eine bedingte Einschränkung ('falls')"
                    ),
                    correctIndex = 1,
                    diagnosticExplanation = "'Gleichwohl' ist ein gehobener konzessiver Konnektor (Synonym zu 'dennoch' oder 'nichtsdestotrotz')."
                ),
                mainChallenge = MainChallengeTask(
                    title = "Kollokations-Präzision statt Allerweltsverben",
                    scenarioContext = "Sie überarbeiten eine Projektpräsentation, die bisher zu viele B1-Verben ('machen', 'haben', 'sagen') enthält.",
                    partnerStatement = "Ersetzen Sie in freier Rede: 'Wir haben viele Probleme gehabt, aber am Ende haben wir eine gute Lösung gemacht.'",
                    stage1Prompt = "Stufe 1 (Nomen-Verb-Upgrade): Nutzen Sie 'auf Widerstand stoßen' oder 'mit Herausforderungen konfrontiert sein'.",
                    stage2Deepening = "Stufe 2 (Präzises Zielverb): Ersetzen Sie 'eine Lösung machen' durch 'einen tragfähigen Kompromiss erzielen' oder 'eine Lösung herbeiführen'.",
                    stage3StressInference = "Stufe 3 (Konnektor-Verknüpfung): Verbinden Sie beide Gedanken mit 'nichtsdestotrotz' oder 'schließlich'.",
                    keyPointsExpected = listOf(
                        "Kein 'machen / tun / haben' als Vollverb-Ersatz",
                        "Treffsichere Adjektiv-Nomen-Kollokation ('tragfähig', 'nachhaltig')",
                        "Flüssige Satzklammer"
                    )
                ),
                productionTask = ProductionTask(
                    isSpeaking = true,
                    situationBrief = "Sprechen Sie die auf C1-Niveau veredelte Version frei und überzeugend ein.",
                    timeLimitSeconds = 80,
                    keyEvaluationCriteria = listOf(
                        "Verwendung von mindestens zwei C1-Kollokationen",
                        "Korrekte Verbzweit- bzw. Verbendstellung nach Konnektoren",
                        "Überzeugende Betonung der Schlüsselwörter"
                    ),
                    simulatedAudioPrompt = "Wie würden Sie diesen Projektabschluss vor dem Beirat formulieren?"
                ),
                correctionTask = CorrectionTask(errorItems = errorsForCorrection),
                transferTask = TransferTask(
                    transferDomain = "Bewerbungsgespräch / Jahresgespräch",
                    scenarioPrompt = "Beschreiben Sie eine schwierige Situation aus Ihrem Arbeitsalltag mithilfe von 'in Kauf nehmen' und 'unter Beweis stellen'.",
                    benchmarkExpectation = "Natürliche Integration der Nomen-Verb-Verbindungen."
                )
            )
            4 -> DailyTrainingSession(
                id = "session_${System.currentTimeMillis()}",
                title = "Sprachmittlung & Konjunktiv I in der Berichterstattung",
                targetCategory = SkillCategory.MEDIATION,
                totalMinutes = 18,
                environment = "Mediation & Presse (25%)",
                warmUpPrompt = WarmUpTask(
                    audioSpeakerText = "Der Minister betonte, man habe die Warnungen der Gutachter keineswegs ignoriert; vielmehr sei eine Prüfung bereits eingeleitet worden.",
                    accentInfo = "Nachrichtensprecher-Tempo (1.15x), distanzierte Wiedergabe",
                    questionPrompt = "Warum verwendet der Sprecher hier 'habe ignoriert' und 'sei eingeleitet worden'?",
                    options = listOf(
                        "Um eine eigene Wunschvorstellung auszudrücken",
                        "Zur neutralen, distanzierten Kennzeichnung indirekter Rede (Konjunktiv I)",
                        "Weil die Handlung in der Zukunft liegt",
                        "Um Höflichkeit gegenüber dem Zuhörer zu zeigen"
                    ),
                    correctIndex = 1,
                    diagnosticExplanation = "Der Konjunktiv I signalisiert in der deutschen Presse- und Rechtssprache strikte Quellen-Distanz ('laut Aussage des Ministers')."
                ),
                mainChallenge = MainChallengeTask(
                    title = "Komplexe Sachverhalte adressatengerecht vermitteln",
                    scenarioContext = "Ein internationaler Partner fragt Sie nach der Kernaussage einer deutschen Behördenauflage.",
                    partnerStatement = "„Die Erteilung der Betriebserlaubnis steht unter dem Vorbehalt der fristgerechten Nachreichung des Brandschutzkonzepts.“",
                    stage1Prompt = "Stufe 1 (Entschlüsseln): Was bedeutet 'unter dem Vorbehalt' konkret für den Projektstart?",
                    stage2Deepening = "Stufe 2 (Adressatengerecht erklären): Formulieren Sie die Bedingung klar für einen Projektleiter ohne Juristen-Deutsch.",
                    stage3StressInference = "Stufe 3 (Handlungsschritt): Benennen Sie das Risiko bei Fristversäumnis präzise im Konjunktiv oder Konditionalsatz.",
                    keyPointsExpected = listOf(
                        "Klare Übersetzung von 'Vorbehalt' (Bedingung / vorläufige Zusage)",
                        "Vermeidung von Schachtelsätzen",
                        "Verbindlicher, handlungsorientierter Ton"
                    )
                ),
                productionTask = ProductionTask(
                    isSpeaking = false,
                    situationBrief = "Fassen Sie die Behördenauflage in 2–3 klaren Sätzen für Ihr Team zusammen.",
                    timeLimitSeconds = 90,
                    keyEvaluationCriteria = listOf(
                        "Vollständige inhaltliche Korrektheit ohne Juristen-Jargon",
                        "Klare Handlungsaufforderung mit Fristbezug",
                        "Stilsicheres Deutsch"
                    ),
                    simulatedAudioPrompt = "Dürfen wir also morgen schon mit dem Betrieb beginnen oder nicht?"
                ),
                correctionTask = CorrectionTask(errorItems = errorsForCorrection),
                transferTask = TransferTask(
                    transferDomain = "Kundenservice / B2B-Eskalation",
                    scenarioPrompt = "Erklären Sie einem Kunden verständlich, warum eine Rückerstattung 'vorbehaltlich einer technischen Prüfung' erfolgt.",
                    benchmarkExpectation = "Empathisch, transparent und dennoch rechtssicher."
                )
            )
            else -> DailyTrainingSession(
                id = "session_${System.currentTimeMillis()}",
                title = "Fußball-Trainer (E- & C-Jugend): Coaching-Präsenz & Taktikdeutsch",
                targetCategory = SkillCategory.SPONTANES_SPRECHEN,
                totalMinutes = 18,
                environment = "Sportplatz & Verein (E- & C-Jugend)",
                warmUpPrompt = WarmUpTask(
                    audioSpeakerText = "Trainer, warum spielt mein Sohn in der E-Jugend heute im Sturm und nicht hinten? Wir müssen doch die Tabelle halten!",
                    accentInfo = "Emotionaler Elternton an der Seitenlinie, direktes Ansprechen (1.15x)",
                    questionPrompt = "Welches pädagogische Grundprinzip des DFB-Kinderfußballs steht hier auf dem Spiel?",
                    options = listOf(
                        "Positionsspezialisierung ab 9 Jahren für maximale Punktzahl",
                        "Ganzheitliche Ausbildung durch Rotation und Spielfreude statt Ergebnisdruck",
                        "Ausschluss von Eltern vom gesamten Trainingsgelände",
                        "Sofortige Einführung der Abseitsfalle in der E-Jugend"
                    ),
                    correctIndex = 1,
                    diagnosticExplanation = "In der E-Jugend (U10/U11) gilt laut DFB-Ausbildungskonzept Rotation auf allen Positionen und Entwicklung vor Tabellenergebnis."
                ),
                mainChallenge = MainChallengeTask(
                    title = "Kabinen- & Platz-Coaching: Vom E-Jugend-Spaß zur C-Jugend-Taktik",
                    scenarioContext = "Sie trainieren sowohl eine E-Jugend (9–11 J.) als auch eine C-Jugend (13–15 J.). Formulieren Sie altersgerechte, aber sprachlich präzise Ansagen.",
                    partnerStatement = "In der C-Jugend-Halbzeit liegen Sie 0:2 hinten, weil Ihre Sechser bei Ballverlust zu weit aufgerückt waren und die Halbräume offen standen.",
                    stage1Prompt = "Stufe 1 (E-Jugend-Tonfall): Motivieren Sie einen 10-jährigen Spieler nach einem Fehlpass, ohne das Wort 'nicht' als Vorwurf zu nutzen.",
                    stage2Deepening = "Stufe 2 (C-Jugend-Taktik): Erklären Sie Ihrer C-Jugend in zwei klaren Sätzen, wie sie nach Ballverlust die Tiefe sichern und ins Gegenpressing gehen.",
                    stage3StressInference = "Stufe 3 (Seitenlinien-Diplomatie): Bitten Sie einen übereifrigen Vater ruhig, das Coaching Ihnen zu überlassen und ausschließlich positiv anzufeuern.",
                    keyPointsExpected = listOf(
                        "Altersgerechter Registerwechsel (spielerisch-ermutigend vs. taktisch-strukturiert)",
                        "Präzise DFB-Fachbegriffe ('Umschaltspiel', 'Halbraum', 'Spielintelligenz')",
                        "Souveräne, wertschätzende Abgrenzung gegenüber Eltern"
                    )
                ),
                productionTask = ProductionTask(
                    isSpeaking = true,
                    situationBrief = "Sprechen Sie eine 45-sekündige Halbzeitansprache für Ihre C-Jugend: Ruhig, fokussiert, mit klarem taktischem Auftrag für die zweite Hälfte.",
                    timeLimitSeconds = 60,
                    keyEvaluationCriteria = listOf(
                        "Kurze, bildhafte Kommandos statt langer Vorträge",
                        "Positive Handlungsziele ('Kopf hoch, Freilaufweg suchen') statt Verboten",
                        "Klare Betonung und Präsenz in der Stimme"
                    ),
                    simulatedAudioPrompt = "Trainer, wir laufen uns tot! Sollen wir jetzt höher anlaufen oder erst ab der Mittellinie pressen?"
                ),
                correctionTask = CorrectionTask(errorItems = errorsForCorrection),
                transferTask = TransferTask(
                    transferDomain = "Vereinskommunikation / Jugendvorstand",
                    scenarioPrompt = "Begründen Sie gegenüber der Jugendleitung, warum Sie in der E-Jugend auf Minifußball-Spielformen (3-gegen-3 / Funino) setzen.",
                    benchmarkExpectation = "Mehr Ballkontakte, schnellere Entscheidungsfindung und höhere Spielintelligenz pro Kind."
                )
            )
        }
    }

    // Legacy reveal/acknowledgement callers provide no evidence of mastery.
    @Suppress("UNUSED_PARAMETER")
    fun advanceErrorMastery(errorId: String) = Unit

    /**
     * Compatibility entry point for legacy playback/reveal callers.
     * Completion is engagement, not assessed proficiency. Track engagement via the streak repository.
     */
    @Suppress("UNUSED_PARAMETER")
    fun recordSessionCompleted(category: SkillCategory, scoreGained: Int) = Unit

    fun toggleVocabularyMastery(vocabId: String) {
        _vocabularyList.update { list ->
            list.map {
                if (it.id == vocabId) it.copy(isActivelyMastered = !it.isActivelyMastered) else it
            }
        }
    }

    // Baseline Data Initializers
    private fun initialSkillProfile(): Map<SkillCategory, SkillScore> =
        SkillCategory.values().associateWith { category ->
            SkillScore(
                category = category,
                score = 0,
                lastTestedDaysAgo = 0,
                trend = 0,
                criticalWeakness = "Noch nicht gemessen; Nullwert ist ein Platzhalter.",
                masteredAreas = emptyList()
            )
        }

    private fun initialErrors(): List<ErrorIntelligenceItem> {
        return listOf(
            ErrorIntelligenceItem(
                id = "err_1",
                category = ErrorCategory.KOLLOKATIONEN,
                userMistake = "Wir müssen eine Entscheidung machen.",
                nativeReformulation = "Wir müssen eine Entscheidung treffen / herbeiführen.",
                whyItSoundsForeign = "'Entscheidung machen' ist eine wörtliche Übersetzung aus dem Englischen ('make a decision'). Im gehobenen Deutsch kollokiert 'Entscheidung' fest mit 'treffen' oder 'fällen'.",
                ruleOrNuance = "Feste Nomen-Verb-Verbindung: eine Entscheidung treffen (Standard) / fällen (Gericht/Autorität).",
                contextSentence = "In Anbetracht der Frist müssen wir bis morgen eine verbindliche Entscheidung treffen.",
                transferChallengePrompt = "Formulieren Sie im neuen Kontext (Investitionsausschuss): Sagen Sie, dass das Gremium heute die endgültige Wahl bezüglich des Standorts beschließen muss.",
                transferTargetAnswer = "Das Gremium muss heute eine Richtungsentscheidung bezüglich des Standorts treffen.",
                masteryStage = MasteryStage.WIEDERHOLUNG_TAG_1,
                occurrencesCount = 4,
                lastOccurredDaysAgo = 1
            ),
            ErrorIntelligenceItem(
                id = "err_2",
                category = ErrorCategory.PRAEPOSITIONEN_KASUS,
                userMistake = "Ich beziehe mich über das Schreiben vom 12. Mai.",
                nativeReformulation = "Ich beziehe mich auf das Schreiben vom 12. Mai (Akkusativ).",
                whyItSoundsForeign = "Falsche Präposition. 'Sich beziehen' verlangt obligatorisch 'auf + Akkusativ'. 'Über' wird fälschlich von 'sprechen über' übertragen.",
                ruleOrNuance = "Sich beziehen AUF (+ Akk.) / verweisen AUF (+ Akk.).",
                contextSentence = "Bezugnehmend auf Ihre Anfrage möchten wir Ihnen folgendes Angebot unterbreiten.",
                transferChallengePrompt = "Formulieren Sie im neuen Kontext (Rückfrage bei Behörde): Verweisen Sie auf den ursprünglichen Antrag Ihres Mandanten.",
                transferTargetAnswer = "Ich beziehe mich hierbei auf den Erstantrag meines Mandanten vom Januar.",
                masteryStage = MasteryStage.TRANSFER_TAG_3,
                occurrencesCount = 3,
                lastOccurredDaysAgo = 3
            ),
            ErrorIntelligenceItem(
                id = "err_3",
                category = ErrorCategory.REGISTER_MISMATCH,
                userMistake = "Die Maschine ist kaputtgegangen und das ist voll blöd für uns.",
                nativeReformulation = "Aufgrund eines technischen Defekts an der Anlage ist es zu einem gravierenden Produktionsstillstand gekommen.",
                whyItSoundsForeign = "'voll blöd' ist jugendliche Umgangssprache und im geschäftlichen oder technischen Kontext grob deplatziert. C1 verlangt sachliche Präzision.",
                ruleOrNuance = "Verbalstil durch präzise Ursache-Wirkung-Relation ersetzen: 'aufgrund eines Defekts kam es zu...'",
                contextSentence = "Der Ausfall der Fräsmaschine führte bedauerlicherweise zu unvorhergesehenen Lieferverzögerungen.",
                transferChallengePrompt = "Formulieren Sie im neuen Kontext (Lieferverzug beim Kunden): Teilen Sie mit, dass der Server abgestürzt ist, im gehobenen B2B-Register.",
                transferTargetAnswer = "Infolge eines unerwarteten Systemausfalls verzögert sich die Auftragsabwicklung um wenige Stunden.",
                masteryStage = MasteryStage.NEU,
                occurrencesCount = 2,
                lastOccurredDaysAgo = 0
            ),
            ErrorIntelligenceItem(
                id = "err_4",
                category = ErrorCategory.FALSE_FRIENDS_FEINHEITEN,
                userMistake = "Eventuell komme ich um 15 Uhr, das ist ganz sicher.",
                nativeReformulation = "Möglicherweise komme ich um 15 Uhr / Ich werde voraussichtlich um 15 Uhr eintreffen.",
                whyItSoundsForeign = "'Eventuell' bedeutet 'vielleicht / unter Umständen', niemals 'schließlich / at the end' (False Friend zu engl. 'eventually'). Widerspruch zu 'das ist ganz sicher'.",
                ruleOrNuance = "eventuell = maybe / possibly. Schließlich / letztendlich = eventually.",
                contextSentence = "Wir werden das Gutachten voraussichtlich morgen abschließen.",
                transferChallengePrompt = "Sagen Sie auf Deutsch: 'After many delays, we eventually signed the contract' – ohne das Wort 'eventuell'!",
                transferTargetAnswer = "Nach zahlreichen Verzögerungen haben wir den Vertrag letztendlich unterzeichnet.",
                masteryStage = MasteryStage.GEFESTIGT_TAG_7,
                occurrencesCount = 5,
                lastOccurredDaysAgo = 7
            ),
            ErrorIntelligenceItem(
                id = "err_5",
                category = ErrorCategory.FALSE_FRIENDS_FEINHEITEN,
                userMistake = "Scheinbar hat er den Zug verpasst, denn er steht nicht am Gleis.",
                nativeReformulation = "Anscheinend hat er den Zug verpasst, denn er steht nicht am Gleis.",
                whyItSoundsForeign = "'Scheinbar' bedeutet, dass etwas nur zum Schein so ist (in Wahrheit aber anders!). Wenn Sie eine wahrscheinliche Vermutung ausdrücken, brauchen Sie 'anscheinend'.",
                ruleOrNuance = "anscheinend = allem Anschein nach (wahrscheinlich wahr); scheinbar = nur vorgetäuscht (in Wirklichkeit falsch).",
                contextSentence = "Anscheinend wurde der Termin kurzfristig in den Konferenzraum B verlegt.",
                transferChallengePrompt = "Korrigieren Sie: 'Die Zahlen sehen scheinbar gut aus, also haben wir Gewinn gemacht.'",
                transferTargetAnswer = "Die Zahlen sehen anscheinend gut aus, also haben wir Gewinn gemacht.",
                masteryStage = MasteryStage.NEU,
                occurrencesCount = 3,
                lastOccurredDaysAgo = 1
            ),
            ErrorIntelligenceItem(
                id = "err_6",
                category = ErrorCategory.VERBKLAMMER_SATZSTRUKTUR,
                userMistake = "Obwohl wir hatten wenig Zeit, das Ergebnis ist überzeugend.",
                nativeReformulation = "Obwohl wir wenig Zeit hatten, ist das Ergebnis überzeugend.",
                whyItSoundsForeign = "Im schriftlichen und formellen C1-Deutsch verlangt 'obwohl' zwingend Verbendstellung im Nebensatz und Inversion (Verb-Subjekt) im anschließenden Hauptsatz.",
                ruleOrNuance = "Nebensatz (Verb am Ende) + Hauptsatz (finites Verb direkt nach dem Komma auf Position 1).",
                contextSentence = "Obwohl die Finanzierung noch aussteht, schreiten die Planungen planmäßig voran.",
                transferChallengePrompt = "Verbinden Sie mit 'obwohl': 'Die Frist war knapp. Wir haben pünktlich geliefert.'",
                transferTargetAnswer = "Obwohl die Frist knapp war, haben wir pünktlich geliefert.",
                masteryStage = MasteryStage.WIEDERHOLUNG_TAG_1,
                occurrencesCount = 4,
                lastOccurredDaysAgo = 2
            ),
            ErrorIntelligenceItem(
                id = "err_7",
                category = ErrorCategory.PRAEPOSITIONEN_KASUS,
                userMistake = "Wegen dem schlechten Wetter und trotz den Problemen kamen alle.",
                nativeReformulation = "Wegen des schlechten Wetters und trotz der Probleme kamen alle.",
                whyItSoundsForeign = "Im gesprochenen Alltag hört man oft den Dativ, doch in C1-Prüfungen und Business-Texten verlangen 'wegen', 'trotz', 'während' und 'aufgrund' konsequent den Genitiv.",
                ruleOrNuance = "Präpositionen mit Genitiv: wegen, trotz, aufgrund, anhand, ungeachtet, binnen.",
                contextSentence = "Ungeachtet der heftigen Kritik hielt der Vorstand an seiner Strategie fest.",
                transferChallengePrompt = "Setzen Sie in den Genitiv: 'Aufgrund (die neuen Vorschriften) müssen wir umplanen.'",
                transferTargetAnswer = "Aufgrund der neuen Vorschriften müssen wir umplanen.",
                masteryStage = MasteryStage.NEU,
                occurrencesCount = 6,
                lastOccurredDaysAgo = 0
            ),
            ErrorIntelligenceItem(
                id = "err_8",
                category = ErrorCategory.VERBKLAMMER_SATZSTRUKTUR,
                userMistake = "Die Kollegin, dessen Namen ich vergessen habe, hat angerufen.",
                nativeReformulation = "Die Kollegin, deren Namen ich vergessen habe, hat angerufen.",
                whyItSoundsForeign = "Viele Lernende verwechseln im Relativsatz 'deren' (feminin Singular & Plural) mit 'dessen' (maskulin/neutrum Singular).",
                ruleOrNuance = "mask./neutr. Sg. -> dessen | fem. Sg. & alle Plurale -> deren (ohne Endung vor dem Nomen).",
                contextSentence = "Das Unternehmen, dessen Umsatz stark gestiegen ist, expandiert nach Wien.",
                transferChallengePrompt = "Ergänzen Sie das Relativpronomen: 'Die Abteilung, ___ Budget gekürzt wurde, protestiert.'",
                transferTargetAnswer = "Die Abteilung, deren Budget gekürzt wurde, protestiert.",
                masteryStage = MasteryStage.TRANSFER_TAG_3,
                occurrencesCount = 2,
                lastOccurredDaysAgo = 3
            ),
            ErrorIntelligenceItem(
                id = "err_9",
                category = ErrorCategory.REGISTER_MISMATCH,
                userMistake = "Der Chef hat gesagt, dass wir machen müssen das Projekt besser als wie letztes Jahr.",
                nativeReformulation = "Die Abteilungsleitung betonte, wir müssten das Projekt effizienter als im Vorjahr umsetzen.",
                whyItSoundsForeign = "Doppelter Fehler: 'als wie' ist regional-umgangssprachlich falsch (Komparativ immer nur 'als'), und in der indirekten Wiedergabe wirkt der Konjunktiv I/II deutlich souveräner.",
                ruleOrNuance = "Ungleichheit (Komparativ): größer ALS | Gleichheit: genauso groß WIE.",
                contextSentence = "Die diesjährige Bilanz fällt deutlich erfreulicher aus als die des Vorjahres.",
                transferChallengePrompt = "Korrigieren Sie: 'Dieses Verfahren ist doppelt so schnell als wie das alte.'",
                transferTargetAnswer = "Dieses Verfahren ist doppelt so schnell wie das alte.",
                masteryStage = MasteryStage.NEU,
                occurrencesCount = 3,
                lastOccurredDaysAgo = 1
            ),
            ErrorIntelligenceItem(
                id = "err_10",
                category = ErrorCategory.KOLLOKATIONEN,
                userMistake = "Wir müssen diese Frage jetzt in Betracht nehmen.",
                nativeReformulation = "Wir müssen diese Option jetzt in Betracht ziehen / in Erwägung ziehen.",
                whyItSoundsForeign = "Kontamination zweier Nomen-Verb-Verbindungen ('in Kauf nehmen' + 'in Betracht ziehen'). Deutsche Muttersprachler bemerken verwechselte Funktionsverben sofort.",
                ruleOrNuance = "in Betracht ZIEHEN | in Kauf NEHMEN | zur Sprache BRINGEN | Rechnung TRAGEN.",
                contextSentence = "Wir sollten auch eine schrittweise Einführung in Betracht ziehen.",
                transferChallengePrompt = "Wählen Sie das passende Verb: 'Bei der Qualität dürfen wir keine Abstriche ___.'",
                transferTargetAnswer = "Bei der Qualität dürfen wir keine Abstriche machen.",
                masteryStage = MasteryStage.WIEDERHOLUNG_TAG_1,
                occurrencesCount = 4,
                lastOccurredDaysAgo = 2
            ),
            ErrorIntelligenceItem(
                id = "err_11",
                category = ErrorCategory.ZOEGERN_LATENZ,
                userMistake = "Ähm... also... ich denke, vielleicht könnten wir eventuell sagen, dass...",
                nativeReformulation = "Meines Erachtens lässt sich festhalten, dass... / Aus meiner Sicht spricht vieles dafür, dass...",
                whyItSoundsForeign = "Mehrfache Heckenausdrücke ('ich denke, vielleicht, eventuell') + Pausenfüller ('ähm') signalisieren mangelnde Überzeugungskraft im C1-Gespräch.",
                ruleOrNuance = "Einstiegsanker fest automatisieren: 'Lassen Sie mich dazu Folgendes anmerken:' verschafft 2 Sekunden Denkzeit ohne 'ähm'.",
                contextSentence = "Hierbei gilt es jedoch zu bedenken, dass die Folgekosten noch nicht beziffert sind.",
                transferChallengePrompt = "Starten Sie eine spontane Erwiderung ohne 'Ich denke' oder 'vielleicht': Thema Klimaneutralität.",
                transferTargetAnswer = "In Anbetracht der aktuellen Datenlage steht außer Frage, dass wir schneller handeln müssen.",
                masteryStage = MasteryStage.NEU,
                occurrencesCount = 5,
                lastOccurredDaysAgo = 0
            ),
            ErrorIntelligenceItem(
                id = "err_12",
                category = ErrorCategory.PRAEPOSITIONEN_KASUS,
                userMistake = "Ich interessiere mich nicht für Politik, sondern ich beschäftige mich über Wirtschaft.",
                nativeReformulation = "Ich interessiere mich weniger für Politik, sondern befasse mich vorrangig mit Wirtschaftsfragen.",
                whyItSoundsForeign = "'Sich beschäftigen / befassen' verlangt zwingend 'mit + Dativ', niemals 'über'.",
                ruleOrNuance = "sich beschäftigen MIT (+ Dat.) | sich befassen MIT (+ Dat.) | sich auseinandersetzen MIT (+ Dat.).",
                contextSentence = "Der Ausschuss befasst sich morgen eingehend mit den neuen Compliance-Richtlinien.",
                transferChallengePrompt = "Ergänzen Sie: 'Wir müssen uns dringend ___ diesem Engpass auseinandersetzen.'",
                transferTargetAnswer = "Wir müssen uns dringend mit diesem Engpass auseinandersetzen.",
                masteryStage = MasteryStage.GEFESTIGT_TAG_7,
                occurrencesCount = 2,
                lastOccurredDaysAgo = 5
            ),
            ErrorIntelligenceItem(
                id = "err_13",
                category = ErrorCategory.REGISTER_MISMATCH,
                userMistake = "Spiel nicht so hektisch! Schieß nicht einfach blind nach vorne!",
                nativeReformulation = "Nimm dir ruhig die Sekunde am Ball, schau hoch und such die nächste Anspielstation!",
                whyItSoundsForeign = "Negative Imperative ('mach nicht...') erzeugen bei E- und C-Jugendlichen Stress und Blockaden, statt ein klares Bewegungsbild zu liefern.",
                ruleOrNuance = "DFB-Coaching-Prinzip: Positive Handlungsaufforderung statt Verbot ('Kopf hoch, Tiefe suchen' statt 'Nicht blind bolzen').",
                contextSentence = "Bleib mutig im Eins-gegen-Eins und biete dich nach dem Pass sofort wieder im freien Raum an.",
                transferChallengePrompt = "Formen Sie um: 'Verlier den Ball nicht im Dribbling!' -> positives E-Jugend-Kommando.",
                transferTargetAnswer = "Schütze den Ball mit dem Körper und halte den Blick für deinen Mitspieler offen!",
                masteryStage = MasteryStage.NEU,
                occurrencesCount = 3,
                lastOccurredDaysAgo = 1
            ),
            ErrorIntelligenceItem(
                id = "err_14",
                category = ErrorCategory.KOLLOKATIONEN,
                userMistake = "Wir müssen jetzt mehr Speed machen und vorne die Defender attackieren.",
                nativeReformulation = "Wir müssen jetzt das Tempo anziehen und die gegnerische Abwehrreihe geschlossen anlaufen.",
                whyItSoundsForeign = "Unreflektierte Anglizismen ('Speed machen', 'Defender') wirken in der deutschen Trainerausbildung (DFB-C-Lizenz) unsauber.",
                ruleOrNuance = "Echte deutsche Fußballfachsprache nutzen: anlaufen, verschieben, umschalten, Halbraum besetzen, Tiefe sichern.",
                contextSentence = "Bei Ballverlust schalten wir geschlossen um und setzen den ballführenden Gegenspieler sofort unter Druck.",
                transferChallengePrompt = "Ersetzen Sie 'high pressing & drop back' durch präzises C-Jugend-Trainerdeutsch.",
                transferTargetAnswer = "Wir laufen den Gegner hoch an und fallen bei Überspielen der ersten Kette geschlossen ins Mittelfeldpressing zurück.",
                masteryStage = MasteryStage.NEU,
                occurrencesCount = 2,
                lastOccurredDaysAgo = 2
            )
        )
    }

    private fun initialVocabulary(): List<ActiveVocabularyItem> {
        return listOf(
            ActiveVocabularyItem(
                id = "voc_1",
                expression = "etwas in Betracht ziehen",
                type = "Nomen-Verb-Verbindung",
                meaningDe = "Etwas erwägen, berücksichtigen, als Möglichkeit prüfen",
                naturalExample = "Wir sollten auch alternative Antriebstechnologien ernsthaft in Betracht ziehen.",
                informalOrClunkyEquivalent = "über etwas nachdenken / etwas vielleicht machen",
                domain = "Beruf & Strategie",
                isActivelyMastered = true,
                requiredInContext = "Schlagen Sie Ihrem Team vor, eine hybride Arbeitsweise als Option zu prüfen."
            ),
            ActiveVocabularyItem(
                id = "voc_2",
                expression = "außer Zweifel stehen",
                type = "Nomen-Verb-Verbindung",
                meaningDe = "Unbestreitbar sein, völlig sicher feststehen",
                naturalExample = "Dass die Umstellung erhebliche Ressourcen erfordert, steht außer Zweifel.",
                informalOrClunkyEquivalent = "das ist 100% sicher / niemand zweifelt daran",
                domain = "Debatte & Argumentation",
                isActivelyMastered = false,
                requiredInContext = "Betonen Sie in einer Diskussion, dass der Klimawandel unbestreitbar ist."
            ),
            ActiveVocabularyItem(
                id = "voc_3",
                expression = "zur Sprache bringen",
                type = "Nomen-Verb-Verbindung",
                meaningDe = "Ein Thema ansprechen, die Diskussion darüber eröffnen",
                naturalExample = "Ich möchte die wiederholten Lieferengpässe in der morgigen Sitzung zur Sprache bringen.",
                informalOrClunkyEquivalent = "über ein Problem reden wollen",
                domain = "Beruf & Meetings",
                isActivelyMastered = false,
                requiredInContext = "Sagen Sie Ihrem Vorgesetzten, dass Sie die Gehälter im Jahresgespräch ansprechen möchten."
            ),
            ActiveVocabularyItem(
                id = "voc_4",
                expression = "nachdrücklich darauf hinweisen",
                type = "Kollokation",
                meaningDe = "Mit großem Nachdruck und Entschiedenheit betonen",
                naturalExample = "Die Sicherheitsbeauftragte wies nachdrücklich auf die Einhaltung der Schutzvorschriften hin.",
                informalOrClunkyEquivalent = "sehr streng sagen, dass...",
                domain = "Technik & Sicherheit",
                isActivelyMastered = false,
                requiredInContext = "Weisen Sie einen Mitarbeiter formell und unmissverständlich auf eine Frist hin."
            ),
            ActiveVocabularyItem(
                id = "voc_5",
                expression = "stichhaltig begründen",
                type = "Kollokation",
                meaningDe = "Überzeugend, wasserdicht und fundiert argumentieren",
                naturalExample = "Können Sie die kalkulierten Mehrkosten gegenüber dem Kunden stichhaltig begründen?",
                informalOrClunkyEquivalent = "gut erklären mit Beweisen",
                domain = "Debatte & Gutachten",
                isActivelyMastered = true,
                requiredInContext = "Fordern Sie von einem Lieferanten eine fundierte Rechtfertigung für die Verzögerung."
            ),
            ActiveVocabularyItem(
                id = "voc_6",
                expression = "in Abrede stellen",
                type = "Nomen-Verb-Verbindung",
                meaningDe = "Etwas bestreiten, leugnen oder anzweifeln",
                naturalExample = "Niemand stellt die fachliche Kompetenz des Teams in Abrede.",
                informalOrClunkyEquivalent = "nicht glauben / abstreiten",
                domain = "Debatte & Kritik",
                isActivelyMastered = false,
                requiredInContext = "Reagieren Sie auf Kritik und betonen Sie, dass Sie das Engagement der Kollegen keineswegs abstreiten."
            ),
            ActiveVocabularyItem(
                id = "voc_7",
                expression = "in Kauf nehmen",
                type = "Nomen-Verb-Verbindung",
                meaningDe = "Nachteile oder Risiken bewusst akzeptieren, um ein größeres Ziel zu erreichen",
                naturalExample = "Für eine fehlerfreie Produkteinführung müssen wir moderate Verzögerungen in Kauf nehmen.",
                informalOrClunkyEquivalent = "ein schlechtes Ding akzeptieren",
                domain = "Projektmanagement",
                isActivelyMastered = true,
                requiredInContext = "Erklären Sie dem Kunden, dass höhere Anfangskosten für langfristige Stabilität unvermeidbar sind."
            ),
            ActiveVocabularyItem(
                id = "voc_8",
                expression = "den Vorzug geben",
                type = "Nomen-Verb-Verbindung",
                meaningDe = "Eine Option oder Person einer anderen vorziehen",
                naturalExample = "Bei gleicher Qualifikation sollten wir dem internen Bewerber den Vorzug geben.",
                informalOrClunkyEquivalent = "etwas lieber wollen / wählen",
                domain = "Führung & HR",
                isActivelyMastered = false,
                requiredInContext = "Begründen Sie, warum Sie der nachhaltigen Zulieferkette den Vorzug geben."
            ),
            ActiveVocabularyItem(
                id = "voc_9",
                expression = "Abstriche machen",
                type = "Nomen-Verb-Verbindung",
                meaningDe = "Ansprüche, Anforderungen oder Erwartungen reduzieren; Einbußen hinnehmen",
                naturalExample = "Bei der Datensicherheit dürfen wir unter keinen Umständen Abstriche machen.",
                informalOrClunkyEquivalent = "weniger verlangen / Kompromiss machen",
                domain = "Governance & Qualität",
                isActivelyMastered = false,
                requiredInContext = "Bestehen Sie unmissverständlich auf Einhaltung der Qualitätsstandards."
            ),
            ActiveVocabularyItem(
                id = "voc_10",
                expression = "ins Benehmen setzen",
                type = "Nomen-Verb-Verbindung",
                meaningDe = "Sich formell und einvernehmlich mit einer anderen Stelle abstimmen",
                naturalExample = "Bevor die Richtlinie verabschiedet wird, müssen wir uns mit der Revision ins Benehmen setzen.",
                informalOrClunkyEquivalent = "mit jemandem reden und fragen",
                domain = "Bürokratie & Recht",
                isActivelyMastered = false,
                requiredInContext = "Informieren Sie den Vorstand über die anstehende behördliche Abstimmung."
            )
        )
    }

    private fun initialListeningLab(): List<ListeningLaboratoryItem> {
        return listOf(
            ListeningLaboratoryItem(
                id = "lab_1",
                title = "Schwäbischer Werkstatt-Dialog: Toleranzen bei der Nockenwelle",
                environment = "Technik & Werkstatt (25%)",
                accentOrDialect = "Schwäbischer Zungenschlag & schnelle Fachreduktionen",
                nativeSpeechSpeed = 1.20f,
                durationSeconds = 48,
                audioSpeakerText = "Also pass amol uff, Kerle! Wenn mer die Toleranze bei dera Nockawell net grad jetzt akkurat eistelle, no fliegt uns d'ganze Serie beim Prüfstand um d'Ohre! Do brauchsch gar net so bleed gugge, des hamma doch geschtern scho g'habt!",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Welches unausgesprochene Gefühl treibt den Werkstattleiter primär an?",
                        options = listOf(
                            "Frustration über wiederholte Schlampigkeit und Furcht vor Ausschuss",
                            "Freude über einen geglückten Belastungstest",
                            "Gleichgültigkeit gegenüber den Hersteller-Vorgaben",
                            "Wunsch nach sofortigem Feierabend"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'des hamma doch geschtern scho g'habt' verweist auf einen wiederholten Fehler; 'fliegt uns um d'Ohre' unterstreicht die drohende Havarie."
                    ),
                    InferenceQuestion(
                        question = "Was bedeutet die Reduktion 'hamma' im Kontext?",
                        options = listOf(
                            "haben wir (kontrahiert im schnellen süddeutschen Sprechfluss)",
                            "Hammer (Werkzeug)",
                            "haben man",
                            "haben sie"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "Verschmelzung von Hilfsverb 'haben' und Personalpronomen 'wir' zu 'hamma'."
                    )
                ),
                transcriptWithReductions = "Also pass [amol uff] (einmal auf), Kerle! Wenn [mer] (wir) die Toleranzen bei [dera] (dieser) Nockenwelle [net] (nicht) grad jetzt akkurat einstellen, [no] (dann) fliegt uns [d'ganze] (die ganze) Serie beim Prüfstand um [d'Ohre] (die Ohren)! Da [brauchsch] (brauchst du) gar net so blöd [gugge] (schauen), des [hamma] (haben wir) doch gestern schon gehabt!",
                reductionNotes = listOf(
                    ReductionAnnotation("amol uff", "einmal auf", "Schwäbische Partikelreduktion"),
                    ReductionAnnotation("hamma", "haben wir", "Umgangssprachliche Verschmelzung im süddeutschen Raum"),
                    ReductionAnnotation("d'ganze Serie", "die ganze Serie", "Elision des Vokals vor Konsonant")
                ),
                contextAndSubtextAnalysis = "Dieser Dialog trainiert das Verstehen von authentischem Fachpersonal im Automobilbau. Es geht nicht um Standardhochdeutsch, sondern um das blitzschnelle Decodieren emotionaler Dringlichkeit unter Werkstattbedingungen."
            ),
            ListeningLaboratoryItem(
                id = "lab_2",
                title = "Berliner Kiez-Debatte: Mietpreisbremse & Milieuschutz",
                environment = "Alltag & Soziales (40%)",
                accentOrDialect = "Berliner Umgangssprache mit Ironie & schnellen Elisionen",
                nativeSpeechSpeed = 1.25f,
                durationSeconds = 55,
                audioSpeakerText = "Ick mein ja nur: Dit is doch reine Augenwischerei! Da klatschen se n bisschen Farbe an die Fassade und schwupps wird dit Ding als Luxussanierung deklariert. Wer soll n dit bitteschön noch berappen? Da bleibste doch uff der Strecke!",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Was signalisiert der Ausdruck 'reine Augenwischerei' im Hinblick auf die Haltung des Sprechers?",
                        options = listOf(
                            "Er hält die Maßnahmen für eine betrügerische Täuschung ohne echten Nutzen",
                            "Er lobt die optische Verbesserung des Hauses",
                            "Er befürwortet Luxussanierungen im Viertel",
                            "Er fordert mehr Fassadenfarben"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'Augenwischerei' ist ein festes Idiom für Täuschung oder Vorspiegelung falscher Tatsachen."
                    )
                ),
                transcriptWithReductions = "[Dit] (Das) is doch reine Augenwischerei! Da klatschen [se] (sie) ['n] (ein) bisschen Farbe an die Fassade und schwupps wird [dit Ding] (die Sache) als Luxussanierung deklariert. Wer soll ['n dit] (denn das) bitteschön noch [berappen] (bezahlen)? Da [bleibste] (bleibst du) doch [uff der Strecke] (auf der Strecke)!",
                reductionNotes = listOf(
                    ReductionAnnotation("dit", "das", "Berlinerischer Wandel von s zu t"),
                    ReductionAnnotation("berappen", "bezahlen", "Umgangssprachliches Idiom für hohe Zahlungen"),
                    ReductionAnnotation("bleibste", "bleibst du", "Enklitische Verschmelzung")
                ),
                contextAndSubtextAnalysis = "Alltägliche soziale Debatten in Deutschland sind voller bildhafter Metaphern ('Augenwischerei', 'auf der Strecke bleiben'). Ein C1-Sprecher muss die bittere Frustration hinter dem lockeren Tonfall erfassen."
            ),
            ListeningLaboratoryItem(
                id = "lab_3",
                title = "Krisensitzung: Strategiewechsel im Vorstandsbüro",
                environment = "Beruf & Führung (35%)",
                accentOrDialect = "Gehobenes Norddeutsches Hochdeutsch, kühle Distanz",
                nativeSpeechSpeed = 1.15f,
                durationSeconds = 62,
                audioSpeakerText = "Lassen Sie uns die Dinge beim Namen nennen. Wenn wir das Segment nicht binnen dreier Monate restrukturieren, wird der Aufsichtsrat personelle Konsequenzen ziehen. Ich erwarte von Ihnen keine Ausflüchte, sondern bis Freitag ein belastbares Sanierungskonzept.",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Welche subtile Drohung formuliert der Vorstandsvorsitzende?",
                        options = listOf(
                            "Entlassung des Führungsteams bei Nichterfüllung der Frist",
                            "Eine sofortige Kürzung der Forschungsbudgets",
                            "Die Verlegung der Firmenzentrale nach Hamburg",
                            "Eine Verschiebung des Termins auf nächste Woche"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'personelle Konsequenzen ziehen' ist die klassische gehobene Chiffre für Kündigungen im Top-Management."
                    )
                ),
                transcriptWithReductions = "Lassen Sie uns die Dinge beim Namen nennen. Wenn wir das Segment nicht binnen dreier Monate restrukturieren, wird der Aufsichtsrat personelle Konsequenzen ziehen. Ich erwarte von Ihnen keine Ausflüchte, sondern bis Freitag ein belastbares Sanierungskonzept.",
                reductionNotes = listOf(
                    ReductionAnnotation("binnen dreier Monate", "innerhalb von 3 Monaten", "Gehobener Genitiv nach der Präposition binnen"),
                    ReductionAnnotation("belastbar", "fundiert / stichhaltig", "Management-Jargon für verlässliche Daten")
                ),
                contextAndSubtextAnalysis = "Subtile, eiskalte Führungssprache. Kein Schreien, sondern präzise bürokratische Konsequenzen mit gehobenen Temporalangaben."
            ),
            ListeningLaboratoryItem(
                id = "lab_4",
                title = "Zürcher Banken-Finanzierungsdebatte: Konsortialkredit",
                environment = "Beruf & Akademie (35%)",
                accentOrDialect = "Schweizer Hochdeutsch mit Helvetismen & präziser Bankterminologie",
                nativeSpeechSpeed = 1.15f,
                durationSeconds = 52,
                audioSpeakerText = "Grüezi mitenand. Wir haben das Traktandum Konsortialkredit vorgezogen. Wenn der Zinsdeckungsgrad weiter absinkt, müssen wir eine Sistierung der Tranche ins Auge fassen. Bitte klären Sie allfällige Rückfragen noch heute vor Börsenschluss.",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Was bedeutet das Wort 'Traktandum' im Schweizer Hochdeutsch?",
                        options = listOf(
                            "Tagesordnungspunkt in einer Sitzung",
                            "Finanzvertrag",
                            "Protokollant",
                            "Darlehenszins"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'Traktandum' (Plural: Traktanden) ist der offizielle schweizerische Standardausdruck für Tagesordnungspunkt."
                    ),
                    InferenceQuestion(
                        question = "Welche Maßnahme droht der Sprecher bei sinkendem Zinsdeckungsgrad an?",
                        options = listOf(
                            "Eine vorübergehende Aussetzung (Sistierung) der Kreditauszahlung",
                            "Eine sofortige Kündigung aller Mitarbeiter",
                            "Die Übernahme durch eine deutsche Großbank",
                            "Eine Erhöhung der Dividende"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'Sistierung' bedeutet im Schweizer Sprachgebrauch das Aussetzen oder Einfrieren eines Verfahrens oder einer Zahlung."
                    )
                ),
                transcriptWithReductions = "[Grüezi mitenand] (Guten Tag zusammen). Wir haben das [Traktandum] (den Tagesordnungspunkt) Konsortialkredit vorgezogen. Wenn der Zinsdeckungsgrad weiter absinkt, müssen wir eine [Sistierung] (vorübergehende Aussetzung/Stoppung) der Tranche ins Auge fassen. Bitte klären Sie [allfällige] (etwaige/eventuelle) Rückfragen noch heute vor Börsenschluss.",
                reductionNotes = listOf(
                    ReductionAnnotation("Traktandum", "Tagesordnungspunkt", "Offizieller schweizerischer Standardausdruck (Helvetismus)"),
                    ReductionAnnotation("Sistierung", "Aussetzung / Unterbrechung", "Schweizer Amts- und Rechtssprache"),
                    ReductionAnnotation("allfällig", "eventuell / etwaig", "Fester Bestandteil des schweizerischen Schrift- und Geschäftsdeutschs")
                ),
                contextAndSubtextAnalysis = "Schweizer Hochdeutsch auf C1-Niveau zeichnet sich durch formelle Helvetismen und eine charakteristische Satzmelodie aus. Für geschäftliche Verhandlungen in Zürich oder Basel unverzichtbar."
            ),
            ListeningLaboratoryItem(
                id = "lab_5",
                title = "Wiener Ministerialbürokratie: Verwaltungsbeschwerde & Fristerstreckung",
                environment = "Alltag & Soziales (40%)",
                accentOrDialect = "Österreichisches Hochdeutsch (Amtssprache) mit Wiener Tonfall",
                nativeSpeechSpeed = 1.20f,
                durationSeconds = 58,
                audioSpeakerText = "Guten Tag. Bezüglich der anhängigen Beschwerde: Die Gegenseite hat eine Fristerstreckung urgiert. Wir gewähren Parteiengehör bis kommenden Dienstag. Erst danach ist der Bescheid spruchreif.",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Was hat die Gegenseite im Behördenverfahren unternommen ('urgiert')?",
                        options = listOf(
                            "Sie hat eine Fristverlängerung mit Nachdruck angemahnt bzw. beantragt",
                            "Sie hat die Klage vollständig zurückgezogen",
                            "Sie hat die Akten vernichtet",
                            "Sie hat den Sachbearbeiter angezeigt"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'Urgieren' ist ein in Österreich gebräuchlicher juristischer Begriff für nachdrückliches Mahnen oder Betreiben."
                    ),
                    InferenceQuestion(
                        question = "Wann gilt ein behördlicher Bescheid in Österreich als 'spruchreif'?",
                        options = listOf(
                            "Wenn der Sachverhalt vollständig geklärt und bereit zur Entscheidung ist",
                            "Wenn er im Radio vorgelesen wurde",
                            "Wenn die Widerspruchsfrist abgelaufen ist",
                            "Wenn der Minister persönlich unterschrieben hat"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'Spruchreif' bedeutet entscheidungsreif nach Abschluss des Ermittlungsverfahrens und Parteiengehörs."
                    )
                ),
                transcriptWithReductions = "Guten Tag. Bezüglich der anhängigen Beschwerde: Die Gegenseite hat eine [Fristerstreckung urgiert] (Fristverlängerung mit Nachdruck beantragt). Wir gewähren [Parteiengehör] (rechtliches Gehör) bis kommenden Dienstag. Erst danach ist der Bescheid [spruchreif] (fertig zur Entscheidung).",
                reductionNotes = listOf(
                    ReductionAnnotation("Fristerstreckung", "Fristverlängerung", "Österreichischer Rechts- und Verwaltungsterminus (Austriazismus)"),
                    ReductionAnnotation("urgieren", "nachdrücklich mahnen", "Gehobenes österreichisches Amtsdeutsch"),
                    ReductionAnnotation("spruchreif", "entscheidungsfertig", "Rechtsbegriff für den Abschluss des Ermittlungsverfahrens")
                ),
                contextAndSubtextAnalysis = "Österreichisches Standarddeutsch im öffentlichen Sektor und in Behörden. Höfliche, aber unerbittliche bürokratische Präzision mit traditionellen lateinstämmigen Fachausdrücken."
            ),
            ListeningLaboratoryItem(
                id = "lab_6",
                title = "Münchner Ingenieurs-Stammtisch: Lieferketten & Fertigungstoleranzen",
                environment = "Beruf & Alltag (35%)",
                accentOrDialect = "Bairisch gefärbtes Hochdeutsch mit zügigem Gesprächsfluss",
                nativeSpeechSpeed = 1.25f,
                durationSeconds = 54,
                audioSpeakerText = "Schau her, des is doch g'hupft wie g'sprungen, ob mir des Bauteil aus Augsburg oder aus Brno beziehen. Solang die Toleranz unter drei Mikrometer bleibt und der Zulieferer ned wieder mit Ausreden daherkommt, wink i des durch. Aber wehe, die Charge hat wieder Haarrisse!",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Was meint der Ingenieur mit der Redewendung 'des is doch g'hupft wie g'sprungen'?",
                        options = listOf(
                            "Es macht im Ergebnis überhaupt keinen Unterschied",
                            "Die Lieferung wird sich um mehrere Wochen verzögern",
                            "Die Bauteile müssen zweimal geprüft werden",
                            "Die Transportkosten sind in Augsburg doppelt so hoch"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "'Gehupft wie gesprungen' ist ein süddeutsches Idiom für 'völlig egal / gleichgültig im Resultat'."
                    )
                ),
                transcriptWithReductions = "Schau her, [des is] (das ist) doch [g'hupft wie g'sprungen] (völlig gleichgültig), ob [mir] (wir) des Bauteil aus Augsburg oder aus Brno beziehen. Solang die Toleranz unter drei Mikrometer bleibt und der Zulieferer [ned] (nicht) wieder mit Ausreden daherkommt, [wink i des durch] (genehmige ich das). Aber wehe, die Charge hat wieder Haarrisse!",
                reductionNotes = listOf(
                    ReductionAnnotation("mir", "wir", "Bairisch-süddeutsche Pronomenverschiebung in der 1. Person Plural"),
                    ReductionAnnotation("durchwinken", "ohne Einspruch genehmigen", "Umgangssprachlich-berufliches Idiom für zügige Freigabe")
                ),
                contextAndSubtextAnalysis = "In süddeutschen Industrieunternehmen mischen erfahrene Fachkräfte pragmatische Präzision ('Mikrometer', 'Charge') mit regionaler Kolloquialsprache. Wer nur Lehrbuch-Hochdeutsch versteht, überhört die klare Warnung am Ende."
            ),
            ListeningLaboratoryItem(
                id = "lab_7",
                title = "Akademisches Kolloquium: Algorithmische Transparenz & Haftungsfragen",
                environment = "Beruf & Akademie (35%)",
                accentOrDialect = "Präzises Wissenschaftsdeutsch, hohes Sprechtempo, dichte Nominalketten",
                nativeSpeechSpeed = 1.22f,
                durationSeconds = 66,
                audioSpeakerText = "Meine sehr geehrten Damen und Herren, die bloße Forderung nach Erklärbarkeit neuronaler Netze greift meines Erachtens zu kurz. Solange die Kausalitätszurechnung im Schadensfall rechtlich im Ungefähren verbleibt, verlagern wir die Haftungslücke lediglich auf den Endanwender – ein Zustand, der mit dem Vorsorgeprinzip unvereinbar ist.",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Welchen zentralen Einwand erhebt der Vortragende gegen die reine Forderung nach 'Erklärbarkeit'?",
                        options = listOf(
                            "Sie löst das rechtliche Problem der Haftungszuweisung im Schadensfall nicht",
                            "Neuronale Netze sind technisch grundsätzlich nicht dokumentierbar",
                            "Endanwender interessieren sich nicht für Datenschutz",
                            "Das Vorsorgeprinzip gilt ausschließlich im Umweltrecht"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "Der Sprecher kritisiert, dass ohne klare 'Kausalitätszurechnung' die rechtliche Verantwortung unfair auf den Nutzer abgewälzt wird."
                    )
                ),
                transcriptWithReductions = "Meine sehr geehrten Damen und Herren, die bloße Forderung nach Erklärbarkeit neuronaler Netze [greift zu kurz] (ist unzureichend). Solange die [Kausalitätszurechnung] (rechtliche Ursachenzuordnung) im Schadensfall rechtlich [im Ungefähren verbleibt] (unklar bleibt), verlagern wir die Haftungslücke lediglich auf den Endanwender – ein Zustand, der mit dem Vorsorgeprinzip unvereinbar ist.",
                reductionNotes = listOf(
                    ReductionAnnotation("zu kurz greifen", "nicht ausreichen / unvollständig sein", "Feste akademisch-publizistische Wendung"),
                    ReductionAnnotation("im Ungefähren verbleiben", "vage / unpräzise bleiben", "Gehobener Nominalstil in Rechts- und Ethikdebatten")
                ),
                contextAndSubtextAnalysis = "Typischer Duktus universitärer Vorträge und Podiumsstatements: Lange, hochverdichtete Nominalsyntagmen erfordern sofortiges Mitdenken bei Subjekt-Prädikat-Klammern."
            ),
            ListeningLaboratoryItem(
                id = "lab_8",
                title = "Trainingsplatz E-Jugend: Funino-Turnier & Coaching-Kommandos",
                environment = "Sportplatz & Verein (E-Jugend U10/U11)",
                accentOrDialect = "Lebendiges Vereinsdeutsch am Spielfeldrand mit kurzen Zurufen",
                nativeSpeechSpeed = 1.15f,
                durationSeconds = 48,
                audioSpeakerText = "Super mutig aufgedreht, Leo! Und jetzt alle mitgedacht: Wenn wir auf die vier Minitore spielen, wollen wir nicht alle auf einen Haufen rennen, sondern das Spielfeld breit machen! Nach jedem Tor wird durchrotiert, jeder kommt dran!",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Welches taktische Ziel verfolgt der E-Jugend-Trainer mit dem Hinweis 'das Spielfeld breit machen'?",
                        options = listOf(
                            "Verhindern, dass alle Kinder als Traube um den Ball stehen, um Passräume zu öffnen",
                            "Die Kinder sollen ausschließlich lange hohe Bälle schlagen",
                            "Nur die zwei schnellsten Spieler sollen Tore schießen",
                            "Das Training wegen Müdigkeit sofort beenden"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "In der E-Jugend neigen Kinder zur 'Traubenbildung'; 'breit machen' schafft Raum für Dribbling und einfache Pässe auf vier Minitore."
                    )
                ),
                transcriptWithReductions = "Super mutig [aufgedreht] (mit dem Ball zum Tor gedreht), Leo! Und jetzt alle mitgedacht: Wenn wir auf die vier Minitore spielen, wollen wir [nicht alle auf einen Haufen rennen] (keine Traubenbildung um den Ball), sondern das Spielfeld breit machen! Nach jedem Tor wird [durchrotiert] (gewechselt), jeder kommt dran!",
                reductionNotes = listOf(
                    ReductionAnnotation("aufdrehen", "sich mit Ball nach vorne orientieren", "Zentrales Coaching-Verb für Offensivaktionen"),
                    ReductionAnnotation("auf einen Haufen rennen", "Traubenbildung / fehlende Raumaufteilung", "Typisches Phänomen im Kinderfußball"),
                    ReductionAnnotation("durchrotieren", "gleichmäßig wechseln", "Fairplay-Prinzip im E-Jugend-Spielbetrieb")
                ),
                contextAndSubtextAnalysis = "Erfolgreiches E-Jugend-Coaching verbindet direktes Lob ('Super mutig aufgedreht') mit bildhaften, sofort umsetzbaren Raumhinweisen ohne komplizierten Taktikjargon."
            ),
            ListeningLaboratoryItem(
                id = "lab_9",
                title = "C-Jugend-Taktikbesprechung: Viererkette & Umschaltmoment",
                environment = "Sportplatz & Verein (C-Jugend U14/U15)",
                accentOrDialect = "Klares, dynamisches Trainerdeutsch in der Kabine",
                nativeSpeechSpeed = 1.20f,
                durationSeconds = 56,
                audioSpeakerText = "Jungs, in der ersten Viertelstunde waren unsere Abstände zwischen Abwehrkette und Sechserraum zu groß. Wenn der Gegner in den Halbraum spielt, muss der ballnahe Innenverteidiger herausrücken, während der Außenverteidiger einrückt und die Tiefe absichert. Sobald wir den Ball erobern, suchen wir sofort den ersten Kontakt nach vorne!",
                blindComprehensionQuestions = listOf(
                    InferenceQuestion(
                        question = "Wie soll sich die Abwehr verhalten, wenn der Innenverteidiger herausrückt?",
                        options = listOf(
                            "Der Außenverteidiger rückt nach innen ein und sichert den Raum dahinter ab",
                            "Alle Verteidiger laufen gleichzeitig auf den gegnerischen Torwart zu",
                            "Der Torwart übernimmt die Position des Mittelstürmers",
                            "Die Mannschaft bleibt komplett stehen und wartet auf den Schiedsrichter"
                        ),
                        correctIndex = 0,
                        rationaleWhyOthersAreWrong = "Das Prinzip des 'Durchsicherns' in der C-Jugend bedeutet, dass beim Herausstechen eines Spielers der Nebenmann einrückt ('einrücken & Tiefe absichern')."
                    )
                ),
                transcriptWithReductions = "Jungs, in der ersten Viertelstunde waren unsere Abstände zwischen Abwehrkette und [Sechserraum] (defensives Mittelfeld) zu groß. Wenn der Gegner in den [Halbraum] (Zone zwischen Zentrum und Flügel) spielt, muss der ballnahe Innenverteidiger [herausrücken] (entgegengehen), während der Außenverteidiger [einrückt] (nach innen zieht) und die Tiefe absichert.",
                reductionNotes = listOf(
                    ReductionAnnotation("Sechserraum", "Zone vor der Abwehr", "Benannt nach der klassischen Rückennummer 6"),
                    ReductionAnnotation("herausrücken / einrücken", "kompaktes Verschieben in der Kette", "Grundvokabular für C-Jugend-Gruppentaktik"),
                    ReductionAnnotation("Tiefe absichern", "Räume hinter der Abwehr schützen", "Verhindert Steilpässe in den Rücken der Kette")
                ),
                contextAndSubtextAnalysis = "Ab der C-Jugend (Großfeld 11-gegen-11) wechseln Spieler vom Kleinfeld auf Raumdeckung und Kettenverhalten. Klare räumliche Begriffe ('Halbraum', 'ballnah', 'Tiefe') sind entscheidend."
            )
        )
    }

    private fun initialConversations(): List<ConversationScenario> {
        return ConversationVariationEngine.expand(listOf(
            ConversationScenario(
                id = "conv_1",
                title = "Meeting-Debatte: Investition in KI-Infrastruktur",
                rolePartner = "Dr. Marianne Weber (CFO)",
                environment = "Beruf & Akademie (35%)",
                openingStatement = "Guten Morgen. Wir haben Ihren Antrag über 2,5 Millionen Euro für das On-Premise-Cluster erhalten. Aus kaufmännischer Sicht sehe ich hier vor allem unkalkulierbare Betriebskosten. Überzeugen Sie mich, warum Cloud-Lösungen nicht ausreichen.",
                turns = listOf(
                    ConversationTurn(
                        partnerSpeech = "Verstehen Sie mich nicht falsch: Wir wollen Innovation nicht ausbremsen. Aber ein ROI nach drei Jahren ist mir schlicht zu spät bei der derzeitigen Marktdynamik.",
                        expectedUserRolePrompt = "Argumentieren Sie für Datenschutz & IP-Schutz, ohne belehrend zu wirken.",
                        suggestedC1Connectors = listOf("In Anbetracht der Tatsache", "Hierbei gilt es zu bedenken", "Nichtsdestotrotz"),
                        targetRegister = "Diplomatisches Vorstandsniveau"
                    ),
                    ConversationTurn(
                        partnerSpeech = "Gut, die Datenschutzbedenken sind legitim. Aber wie federn wir den personellen Mehraufwand für die Wartung ab?",
                        expectedUserRolePrompt = "Legen Sie dar, dass das bestehende DevOps-Team durch Automatisierungskapazitäten entlastet wird.",
                        suggestedC1Connectors = listOf("Diesbezüglich", "Es steht außer Frage", "Ganz im Gegenteil"),
                        targetRegister = "Faktensicheres Führungsdeutsch"
                    )
                ),
                unexpectedInterruption = UnexpectedInterruption(
                    triggerAfterSecond = 6,
                    partnerInterruptSpeech = "Moment mal! Da muss ich kurz einhaken: Sie tun so, als ob Cloud-Anbieter keine DSGVO-Garantien böten. Das ist doch ein Scheinargument!",
                    reactionObjective = "Parieren Sie den Vorwurf 'Scheinargument' augenblicklich und differenzieren Sie zwischen Standard-DSGVO und Geschäftsgeheimnisschutz nach GeschGehG."
                ),
                evaluationFocus = "Sprechlatenz < 2.5s, keine 'ähm'-Füllwörter, diplomatisches Durchsetzungsvermögen."
            ),
            ConversationScenario(
                id = "conv_2",
                title = "Mittagspause mit Kollegen: Meinungsverschiedenheit über Remotearbeit",
                rolePartner = "Felix & Katja (Teamkollegen)",
                environment = "Alltag & Soziales (40%)",
                openingStatement = "Hey! Setz dich doch zu uns. Felix regt sich gerade furchtbar darüber auf, dass die Geschäftsleitung drei Tage Büro-Präsenz anordnen will. Wie siehst du das?",
                turns = listOf(
                    ConversationTurn(
                        partnerSpeech = "Ich finde das total rückschrittlich. Wer im Homeoffice sitzt, arbeitet doch genauso konzentriert – oft sogar mehr!",
                        expectedUserRolePrompt = "Wägen Sie beide Seiten ab: Spontaner Wissensaustausch auf dem Flur vs. ungestörte Deep-Work-Phasen zu Hause.",
                        suggestedC1Connectors = listOf("Einerseits ... andererseits", "Man darf jedoch nicht verkennen", "Meines Erachtens"),
                        targetRegister = "Natürliches, kollegiales Niveau mit gedanklicher Tiefe"
                    )
                ),
                unexpectedInterruption = UnexpectedInterruption(
                    triggerAfterSecond = 5,
                    partnerInterruptSpeech = "Ja, aber findest du nicht, dass das ein bisschen zu kurz gedacht ist? Wer den Flurfunk braucht, organisiert einfach seine Meetings schlecht!",
                    reactionObjective = "Reagieren Sie spontan mit einer persönlichen Beobachtung und widerlegen Sie die These mit feiner Selbstironie."
                ),
                evaluationFocus = "Spontanität, Vermeidung von Monologen, natürliche Einbindung von Gesprächspartnern."
            ),
            ConversationScenario(
                id = "conv_3",
                title = "Gehaltsverhandlung: Leistungsbeurteilung mit Gegenwind",
                rolePartner = "Dr. Harald Winter (Bereichsleiter)",
                environment = "Beruf & Führung (35%)",
                openingStatement = "Herr/Frau Kollege, Ihre Projektleistung im vergangenen Quartal war tadellos. Allerdings sind die Budgets für das kommende Geschäftsjahr extrem eingefroren. Eine Gehaltsanpassung um 12 Prozent sehe ich beim besten Willen nicht.",
                turns = listOf(
                    ConversationTurn(
                        partnerSpeech = "Wir haben konzernweit einen Einstellungsstopp. Warum sollte ich ausgerechnet in Ihrem Bereich eine Ausnahme beim Vorstand durchboxen?",
                        expectedUserRolePrompt = "Untermauern Sie Ihren Mehrwert mit konkreten Kosteneinsparungen und Kundenakquise, ohne defensiv zu wirken.",
                        suggestedC1Connectors = listOf("Maßgeblich hierfür ist", "Ungeachtet der Gesamtlage", "Erlauben Sie mir darauf hinzuweisen"),
                        targetRegister = "Selbstbewusstes, verhandlungssicheres Management-Deutsch"
                    )
                ),
                unexpectedInterruption = UnexpectedInterruption(
                    triggerAfterSecond = 6,
                    partnerInterruptSpeech = "Moment mal! Sie wissen genauso gut wie ich, dass der Betriebsrat den Gehaltsbändern enge Grenzen setzt. Wollen Sie etwa das gesamte Betriebsgefüge gefährden?",
                    reactionObjective = "Parieren Sie den Vorwurf mit Verweis auf leistungsorientierte Sondervergütungen oder variable Zielvereinbarungen."
                ),
                evaluationFocus = "Souveräne Diplomatie unter Druck, Vermeidung von Trotzreaktionen, argumentative Stringenz."
            ),
            ConversationScenario(
                id = "conv_4",
                title = "Eigentümerversammlung: Energetische Sanierung vs. Sonderumlage",
                rolePartner = "Herr Grube (Beiratsvorsitzender)",
                environment = "Alltag & Soziales (40%)",
                openingStatement = "Meine Damen und Herren, wir haben die Angebote für die Fassadendämmung und Wärmepumpen vorliegen. Das bedeutet eine Sonderumlage von knapp 35.000 Euro pro Wohneinheit. Wer soll das bitte noch berappen?",
                turns = listOf(
                    ConversationTurn(
                        partnerSpeech = "Ich lasse mir mein Erspartes nicht für ideologische Träumereien abknöpfen! Eine Reparatur der alten Gastherme tut es doch auch noch zehn Jahre!",
                        expectedUserRolePrompt = "Bringen Sie langfristige Wertsteigerung und staatliche KfW-Förderungen ein, um den emotionalen Widerstand zu versachlichen.",
                        suggestedC1Connectors = listOf("Es liegt auf der Hand, dass", "In Anbetracht der Sanierungspflichten", "Ganz im Gegenteil"),
                        targetRegister = "Moderierendes, deeskalierendes Versammlungsdeutsch"
                    )
                ),
                unexpectedInterruption = UnexpectedInterruption(
                    triggerAfterSecond = 5,
                    partnerInterruptSpeech = "Ach kommen Sie doch nicht mit Förderungen! Bis die bewilligt sind, ist das Haus pleite!",
                    reactionObjective = "Beweisen Sie Sachkenntnis über die Rechtslage bei Sanierungsbeschlüssen und fordern Sie ein unabhängiges Energieberater-Gutachten."
                ),
                evaluationFocus = "Empathische Entschärfung von Konflikten, klare juristisch fundierte Argumentationslinie."
            ),
            ConversationScenario(
                id = "conv_5",
                title = "Klinik-Alltag: Aufklärungsgespräch vor einer Operation",
                rolePartner = "Frau Lindner (besorgte Patientin)",
                environment = "Beruf & Soziales (35%)",
                openingStatement = "Herr/Frau Doktor, ich habe mir den Aufklärungsbogen durchgelesen und jetzt ehrlich gesagt panische Angst vor den Narkoserisiken. Muss dieser Eingriff denn wirklich schon nächste Woche stattfinden?",
                turns = listOf(
                    ConversationTurn(
                        partnerSpeech = "Aber meine Nachbarin hatte nach einer Vollnarkose wochenlang Kreislaufprobleme! Können wir nicht erst noch ein halbes Jahr abwarten und Physiotherapie machen?",
                        expectedUserRolePrompt = "Nehmen Sie die Angst ernst, erklären Sie medizinische Notwendigkeit verständlich und benennen Sie Alternativen ehrlich.",
                        suggestedC1Connectors = listOf("Ich kann Ihre Sorge gut nachvollziehen", "Aus medizinischer Sicht gilt es abzuwägen", "Gleichwohl"),
                        targetRegister = "Empathisches, beruhigendes Fachdeutsch ohne Arroganz"
                    )
                ),
                unexpectedInterruption = UnexpectedInterruption(
                    triggerAfterSecond = 5,
                    partnerInterruptSpeech = "Warten Sie kurz – heißt das jetzt, wenn ich nicht unterschreibe, lassen Sie mich einfach mit den Schmerzen nach Hause gehen?!",
                    reactionObjective = "Deeskalieren Sie sofort, stellen Sie die Entscheidungsfreiheit der Patientin klar und schlagen Sie eine Zweitmeinung oder Bedenkzeit vor."
                ),
                evaluationFocus = "Empathische Wortwahl, Übersetzung von Fachjargon in Alltagssprache, ruhige Deeskalation."
            ),
            ConversationScenario(
                id = "conv_6",
                title = "Podiumsdiskussion: Industriestandort vs. Klimaneutralität",
                rolePartner = "Moderator & Verbandsvertreter (Wirtschaftsforum)",
                environment = "Akademie & Öffentlichkeit (30%)",
                openingStatement = "Willkommen auf dem Podium! Die Energiepreise setzen dem Mittelstand zu, gleichzeitig drängt die EU auf strikte Dekarbonisierung. Droht Deutschland die schleichende Deindustrialisierung?",
                turns = listOf(
                    ConversationTurn(
                        partnerSpeech = "Schöne grüne Visionen bezahlen aber keine Löhne! Wenn unsere Betriebe abwandern, ist dem Weltklima überhaupt nicht geholfen.",
                        expectedUserRolePrompt = "Differenzieren Sie zwischen kurzfristigen Standortkosten und langfristigen Transformationschancen durch grüne Leitmärkte.",
                        suggestedC1Connectors = listOf("Dem ist entgegenzuhalten", "Unbeschadet dessen", "Langfristig betrachtet"),
                        targetRegister = "Prägnantes, druckvolles Debatten-Deutsch"
                    )
                ),
                unexpectedInterruption = UnexpectedInterruption(
                    triggerAfterSecond = 6,
                    partnerInterruptSpeech = "Da muss ich Ihnen ins Wort fallen: Das klingt verdächtig nach Subventionspolitik auf Kosten der Steuerzahler!",
                    reactionObjective = "Parieren Sie den Zwischenruf schlagfertig mit dem Verweis auf Planungssicherheit statt Dauersubventionen."
                ),
                evaluationFocus = "Rhetorische Schlagfertigkeit, präzise Konnektoren, Abgrenzung ohne Polemik."
            ),
            ConversationScenario(
                id = "conv_7",
                title = "E-Jugend Seitenlinie: Elterngespräch über Spielzeit & Fairplay",
                rolePartner = "Herr Becker (ehrgeiziger Fußballvater)",
                environment = "Sportplatz & Verein (E-Jugend)",
                openingStatement = "Trainer, mal unter uns: Warum wechselt du meinen Timmy beim Stand von 2:1 schon wieder aus? Wenn wir die Schwächeren einwechseln, verschenken wir doch noch den Turniersieg!",
                turns = listOf(
                    ConversationTurn(
                        partnerSpeech = "Aber die Jungs wollen doch auch gewinnen! In anderen Vereinen spielen die besten sieben Kinder durch!",
                        expectedUserRolePrompt = "Erklären Sie ruhig die DFB-Ausbildungsphilosophie in der E-Jugend: Entwicklung aller Kinder, Spielzeitgarantie und langfristige Motivation statt kurzfristiger Pokale.",
                        suggestedC1Connectors = listOf("Mir ist wichtig zu betonen", "Langfristig betrachtet", "Aus diesem Grund"),
                        targetRegister = "Herzliches, aber standfestes Trainerdeutsch auf Augenhöhe"
                    )
                ),
                unexpectedInterruption = UnexpectedInterruption(
                    triggerAfterSecond = 5,
                    partnerInterruptSpeech = "Ganz ehrlich, wenn Timmy hier nicht gefördert wird, melden wir ihn nächste Saison beim Leistungszentrum an!",
                    reactionObjective = "Bleiben Sie gelassen, würdigen Sie Timmys Talent und erklären Sie sachlich, warum Geduld, Vielseitigkeit und Spielfreude im E-Jugend-Alter die beste Basis sind."
                ),
                evaluationFocus = "Empathische Konfliktlösung am Spielfeldrand, pädagogische Klarheit ohne Rechthaberei."
            ),
            ConversationScenario(
                id = "conv_8",
                title = "C-Jugend-Derby: Halbzeitansprache & Schiedsrichter-Dialog",
                rolePartner = "Kapitän Lukas & Jungschiedsrichter",
                environment = "Sportplatz & Verein (C-Jugend)",
                openingStatement = "Trainer, der Schiri pfeift jeden Zweikampf gegen uns ab! Die Jungs sind komplett auf 180 und schreien sich auf dem Platz nur noch an!",
                turns = listOf(
                    ConversationTurn(
                        partnerSpeech = "Aber wenn der Gegner ständig unsere Außenbahn umhaut und keine Gelbe Karte sieht, müssen wir doch irgendwie Zeichen setzen!",
                        expectedUserRolePrompt = "Beruhigen Sie die C-Jugend-Mannschaft, lenken Sie den Fokus zurück auf das eigene Positionsspiel und verbieten Sie Frustfouls.",
                        suggestedC1Connectors = listOf("Ungeachtet der Schiedsrichterleistung", "Worauf es jetzt ankommt", "In aller Deutlichkeit"),
                        targetRegister = "Autoritative, fokussierte Mannschaftsführung ohne Eskalation"
                    )
                ),
                unexpectedInterruption = UnexpectedInterruption(
                    triggerAfterSecond = 5,
                    partnerInterruptSpeech = "Der Jungschiedsrichter kommt zur Kabinentür: 'Herr Trainer, Ihre Nummer 8 hat mich gerade beleidigt – noch ein Wort und es gibt Rot!'",
                    reactionObjective = "Übernehmen Sie sofort Verantwortung, entschuldigen Sie sich respektvoll beim Schiedsrichter und kündigen Sie eine interne Klärung mit dem Spieler an."
                ),
                evaluationFocus = "Vorbildfunktion als Jugendtrainer, Deeskalation gegenüber Unparteiischen, klare Führung."
            )
        ))
    }

    private fun initialMediationTasks(): List<MediationTask> {
        return listOf(
            MediationTask(
                id = "med_1",
                title = "Bauordnungsamt: Paragraph 34 BauGB bürgernah erklären",
                mediationType = "Fachsprache -> Bürgernah",
                sourceMaterial = "„Die Zulässigkeit eines Vorhabens im unbeplanten Innenbereich richtet sich nach § 34 BauGB. Ein solches Vorhaben ist zulässig, wenn es sich nach Art und Maß der baulichen Nutzung, der Bauweise und der Grundstücksfläche, die überbaut werden soll, in die Eigenart der näheren Umgebung einfügt und die Erschließung gesichert ist. Die Anforderungen an gesunde Wohn- und Arbeitsverhältnisse müssen gewahrt bleiben; das Ortsbild darf nicht beeinträchtigt werden.“",
                recipientContext = "Ihre Nachbarin Frau Müller versteht den Brief des Bauamts nicht und befürchtet, sie dürfe ihre Garage nicht aufstocken.",
                criticalPitfallsToAvoid = listOf(
                    "Juristische Floskeln unreflektiert übernehmen ('Art und Maß der baulichen Nutzung')",
                    "Falsche juristische Zusicherungen geben",
                    "Zu komplizierte Schachtelsätze verwenden"
                ),
                targetReformulationModel = "Frau Müller, nach diesem Text muss sich das Vorhaben unter anderem in Nutzung, Größe, Bauweise und bebauter Fläche in die nähere Umgebung einfügen. Auch die Erschließung, etwa die Anbindung an Wege und Versorgungsleitungen, muss gesichert sein. Gesunde Wohn- und Arbeitsbedingungen sowie das Ortsbild sind ebenfalls zu beachten. Ob Ihre Aufstockung zulässig ist, lässt sich aus dem Auszug allein nicht entscheiden; das muss das Bauamt im Einzelfall prüfen."
            ),
            MediationTask(
                id = "med_2",
                title = "Englischer Fachtext: 'Technical Debt & Architectural Drift' vermitteln",
                mediationType = "Englisch -> Idiomatisches Deutsch",
                sourceMaterial = "„Accumulated technical debt has led to severe architectural drift, rendering rapid feature deployment unsustainable without a comprehensive refactoring cycle.“",
                recipientContext = "Ein deutscher Geschäftsführer ohne Software-Hintergrund fragt, warum das nächste Feature 6 Wochen statt 1 Woche dauert.",
                criticalPitfallsToAvoid = listOf(
                    "Anglizismen wörtlich übernehmen ('wir haben technische Schulden und architektonischen Drift')",
                    "Zu abstrakt bleiben ohne greifbare Metapher"
                ),
                targetReformulationModel = "Vereinfacht gesagt: Aufgeschobene technische Verbesserungen haben dazu geführt, dass sich der Aufbau unserer Software stark vom vorgesehenen Entwurf entfernt hat. Deshalb können wir neue Funktionen nicht dauerhaft schnell bereitstellen, ohne den bestehenden Code umfassend zu überarbeiten. Warum dafür genau sechs Wochen nötig sind, erklärt der Auszug nicht."
            ),
            MediationTask(
                id = "med_3",
                title = "Medizinischer Fachbericht: Epikrise für Angehörige erklären",
                mediationType = "Medizinische Fachsprache -> Empathische Alltagssprache",
                sourceMaterial = "„Der Patient wies eine transitorische ischämische Attacke im Stromgebiet der A. cerebri media links auf. Kardioembolische Genese bei paroxysmalem Vorhofflimmern wahrscheinlich. Initiale therapeutische Antikoagulation eingeleitet, bildmorphologisch kein Nachweis eines hämorrhagischen Infarktareals.“",
                recipientContext = "Die besorgte Ehefrau des Patienten versteht den Arztbrief nicht und befürchtet eine dauerhafte Lähmung.",
                criticalPitfallsToAvoid = listOf(
                    "Medizinische Fachbegriffe wie 'Antikoagulation' oder 'kardioembolisch' unübersetzt lassen",
                    "Unbegründete Panik erzeugen oder voreilige Entwarnung ohne ärztlichen Vorbehalt geben"
                ),
                targetReformulationModel = "Frau Schneider, der Bericht beschreibt eine vorübergehende Durchblutungsstörung in einem Bereich der linken Gehirnhälfte. Als wahrscheinliche Ursache vermuten die Ärzte ein Blutgerinnsel aus dem Herzen bei zeitweise auftretendem unregelmäßigem Herzschlag. Sie haben eine Behandlung begonnen, die die Blutgerinnung hemmt. In den Bildern wurde kein blutiges Infarktgebiet nachgewiesen. Der Auszug allein erlaubt keine sichere Aussage zu bleibenden Folgen oder zur weiteren Entwicklung. Bitte besprechen Sie diese Fragen und die weitere Behandlung mit dem ärztlichen Team."
            ),
            MediationTask(
                id = "med_4",
                title = "Arbeitsrecht: Änderungskündigung konstruktiv vermitteln",
                mediationType = "Juristische Klausel -> Konstruktives Mitarbeitergespräch",
                sourceMaterial = "Fiktives Übungsschreiben: „Aufgrund betriebsorganisatorischer Umstrukturierungen entfällt Ihr bisheriger Aufgabenbereich. Wir sprechen hiermit eine ordentliche Änderungskündigung aus und bieten Ihnen zeitgleich die Fortsetzung des Arbeitsverhältnisses als Teamkoordinator am Standort Nord zu geänderten Vergütungskonditionen an. Einen Vorbehalt nach § 2 KSchG müssen Sie innerhalb der Kündigungsfrist, spätestens jedoch innerhalb von drei Wochen nach Zugang der Kündigung erklären.“",
                recipientContext = "Ein langjähriger Mitarbeiter fühlt sich durch den juristischen Brief vor den Kopf gestoßen und denkt an sofortige Klage.",
                criticalPitfallsToAvoid = listOf(
                    "Kalte Paragrafenreiter-Sprache wiederholen",
                    "Die Kündigungsfrist und die Höchstfrist von drei Wochen für den Vorbehalt verschweigen oder Nachteilsfreiheit versprechen"
                ),
                targetReformulationModel = "Herr Becker, das Schreiben kündigt Ihr bisheriges Arbeitsverhältnis und bietet zugleich eine Fortsetzung als Teamkoordinator am Standort Nord mit geänderten Vergütungskonditionen an. Sie können das Angebot unter dem Vorbehalt annehmen, dass die Änderungen sozial gerechtfertigt sind. Laut Text muss dieser Vorbehalt innerhalb der Kündigungsfrist, spätestens innerhalb von drei Wochen nach Zugang erklärt werden. Das bedeutet nicht, dass Ihnen sicher keine Nachteile entstehen. Lassen Sie Ihre Möglichkeiten und die geltenden Fristen zeitnah fachkundig prüfen; diese Sprachübung ersetzt keine Rechtsberatung."
            ),
            MediationTask(
                id = "med_5",
                title = "ESG-Nachhaltigkeitsbericht: Lieferketten-Audit für Kunden zusammenfassen",
                mediationType = "Technischer Compliance-Bericht -> Überzeugendes Kundenanschreiben",
                sourceMaterial = "„Im Berichtszeitraum wurden 94,2 % der Tier-1-Zulieferer gemäß ISO 14001 auditiert. Zwei nicht-konforme Vorlieferanten im Bereich Verpackungslogistik wurden einem verbindlichen Corrective Action Plan (CAP) unterzogen; bei Nichterfüllung binnen Q3 erfolgt die Auslistung.“",
                recipientContext = "Ein umweltbewusster Großkunde fragt kritisch nach, ob Ihre Lieferkette wirklich nachhaltig kontrolliert wird.",
                criticalPitfallsToAvoid = listOf(
                    "Unverständliche Abkürzungen wie 'Tier-1' oder 'CAP' unkommentiert übernehmen",
                    "Die zwei festgestellten Mängel vertuschen statt transparent als aktives Qualitätsmanagement darzustellen"
                ),
                targetReformulationModel = "Sehr geehrte Frau Dr. König, im Berichtszeitraum wurden 94,2 Prozent unserer direkten Zulieferer nach ISO 14001 geprüft. Bei zwei Vorlieferanten in der Verpackungslogistik wurden Abweichungen festgestellt und verbindliche Nachbesserungspläne vereinbart. Werden diese nicht bis zum Ende des dritten Quartals erfüllt, werden die betroffenen Lieferanten ausgelistet. Der Bericht belegt damit weder eine lückenlose Kontrolle noch die Nachhaltigkeit der gesamten Lieferkette."
            ),
            MediationTask(
                id = "med_6",
                title = "Fiktives Vereinstraining: Kleine Spielformen Eltern erklären",
                mediationType = "Übungstext -> Verständlicher Elternabend-Impuls",
                sourceMaterial = "Fiktiver Übungstext, kein DFB-Konzeptpapier: „In unserem E-Jugend-Training spielen die Kinder in kleinen Teams auf reduzierten Feldern mit vier Minitoren. Ziel sind viele Ballaktionen und eigene Entscheidungen. Altersgerechtes Lernen steht vor frühzeitiger taktischer Spezialisierung und ergebnisorientiertem Tabellendruck.“",
                recipientContext = "Beim Elternabend der E-Jugend fragen kritische Eltern, warum die Kinder auf vier kleine Tore statt auf ein großes Tor mit festem Torwart spielen.",
                criticalPitfallsToAvoid = listOf(
                    "Bürokratische Begriffe wie 'Aktionsdichte' oder 'kognitive Entscheidungsräume' unübersetzt lassen",
                    "Den Eindruck erwecken, Leistung oder Wettkampf seien nicht mehr erwünscht"
                ),
                targetReformulationModel = "Liebe Eltern, wir nutzen im E-Jugend-Training kleine Teams, kleinere Felder und vier Minitore, damit die Kinder oft am Ball sind und selbst entscheiden können, ob sie dribbeln oder abspielen. Im Mittelpunkt steht altersgerechtes Lernen, nicht frühe taktische Spezialisierung oder der Tabellenplatz. Der Text nennt ein Trainingsziel, aber keine gemessene Zahl zusätzlicher Ballkontakte und keine verbindliche Wettkampfregel."
            )
        )
    }
}
