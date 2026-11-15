package com.example.data.repository

import com.example.data.local.dao.VocabularyItemDao
import com.example.data.local.entity.VocabularyItemEntity
import com.example.programme.CurriculumCatalogue
import com.example.programme.RetrievalPolicy
import kotlinx.coroutines.flow.Flow

/**
 * Repository responsible for the Spaced Repetition System (SRS) for C1 Vocabulary items.
 * Implements the SuperMemo SM-2 algorithm to compute 'last reviewed' and 'next review' timestamps.
 */
class VocabularySrsRepository(
    private val vocabularyDao: VocabularyItemDao
) {

    val allVocabulary: Flow<List<VocabularyItemEntity>> =
        vocabularyDao.getAllVocabularyFlow()

    fun getDueVocabulary(currentTimestamp: Long = System.currentTimeMillis()): Flow<List<VocabularyItemEntity>> =
        vocabularyDao.getDueVocabularyFlow(currentTimestamp)

    suspend fun getDueVocabularyDirect(currentTimestamp: Long = System.currentTimeMillis()): List<VocabularyItemEntity> =
        vocabularyDao.getDueVocabularyDirect(currentTimestamp)

    fun getDueCount(currentTimestamp: Long = System.currentTimeMillis()): Flow<Int> =
        vocabularyDao.getDueCountFlow(currentTimestamp)

    fun getMasteredCount(): Flow<Int> =
        vocabularyDao.getMasteredCountFlow()

    suspend fun getVocabularyById(id: String): VocabularyItemEntity? =
        vocabularyDao.getVocabularyById(id)

    suspend fun insertOrUpdate(item: VocabularyItemEntity) =
        vocabularyDao.insertOrUpdate(item)

    suspend fun delete(id: String) =
        vocabularyDao.deleteById(id)

    /**
     * Records a spaced repetition review result and computes updated intervals,
     * stage, and the precise 'last reviewed' and 'next review' timestamps.
     *
     * @param itemId ID of the vocabulary item
     * @param qualityScore Recall quality from 0 (Failure) to 3 (Spontaneous/Easy)
     *        0 = Blackout / Completely incorrect
     *        1 = Hard / Hesitation with errors
     *        2 = Good / Accurate recall with acceptable latency
     *        3 = Easy / Instant spontaneous recall
     * @param reviewTimestamp Timestamp when review took place (defaults to now)
     */
    suspend fun recordReview(
        itemId: String,
        qualityScore: Int,
        reviewTimestamp: Long = System.currentTimeMillis()
    ): VocabularyItemEntity? {
        val current = vocabularyDao.getVocabularyById(itemId) ?: return null
        val q = qualityScore.coerceIn(0, 3)

        val schedule = RetrievalPolicy.nextSchedule(
            quality = q,
            previousIntervalDays = current.intervalDays,
            previousEaseFactor = current.easeFactor,
            previousRepetitions = current.repetitions
        )
        val isSuccessful = q >= 2
        val newIntervalDays = schedule.intervalDays
        val newRepetitions = schedule.repetitions
        val newConsecutiveErrors = if (isSuccessful) 0 else current.consecutiveErrors + 1

        val newStage = when {
            newIntervalDays >= 30 -> 5 // Langzeit-Transfer (Mastered)
            newIntervalDays >= 14 -> 4 // Spontane Produktion
            newIntervalDays >= 7 -> 3  // Rekonstruktion
            newIntervalDays >= 3 -> 2  // Wiedererkennen
            else -> 1                  // Neu / Festigung
        }

        val isMastered = newStage >= 5
        val oneDayMillis = 86_400_000L
        val nextReview = reviewTimestamp + (newIntervalDays * oneDayMillis)

        val updated = current.copy(
            stage = newStage,
            easeFactor = schedule.easeFactor,
            intervalDays = newIntervalDays,
            repetitions = newRepetitions,
            lastReviewedTimestamp = reviewTimestamp,
            nextReviewTimestamp = nextReview,
            consecutiveErrors = newConsecutiveErrors,
            isMastered = isMastered
        )

        vocabularyDao.insertOrUpdate(updated)
        return updated
    }

    /**
     * Seeds initial authentic C1 vocabulary items into Room if the database is currently empty.
     */
    suspend fun seedInitialC1VocabularyIfEmpty(baseTimestamp: Long = System.currentTimeMillis()) {
        val count = vocabularyDao.getTotalCount()

        val initialItems = listOf(
            VocabularyItemEntity(
                id = "voc_zur_sprache_bringen",
                germanCollocation = "etw. zur Sprache bringen",
                meaningDe = "Ein heikles oder wichtiges Thema in einer Sitzung ansprechen",
                exampleSentence = "In der Klausurtagung brachte sie die ungelösten Personalengpässe mutig zur Sprache.",
                register = "Gehoben / B2B & Führung",
                category = "Nomen-Verb-Verbindung",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                lastReviewedTimestamp = null,
                nextReviewTimestamp = baseTimestamp, // Due immediately
                isMastered = false
            ),
            VocabularyItemEntity(
                id = "voc_in_erwaegung_ziehen",
                germanCollocation = "etw. in Erwägung ziehen",
                meaningDe = "Eine Option sorgfältig und rational abwägen / prüfen",
                exampleSentence = "Wir sollten eine strategische Neupriorisierung der Ressourcen ernsthaft in Erwägung ziehen.",
                register = "Professionell / Strategie",
                category = "Nomen-Verb-Verbindung",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                lastReviewedTimestamp = null,
                nextReviewTimestamp = baseTimestamp, // Due immediately
                isMastered = false
            ),
            VocabularyItemEntity(
                id = "voc_auf_die_lange_bank_schieben",
                germanCollocation = "etw. auf die lange Bank schieben",
                meaningDe = "Eine unangenehme Entscheidung unnötig aufschieben / verzögern",
                exampleSentence = "Diesen heiklen Compliance-Fall dürfen wir keinesfalls auf die lange Bank schieben.",
                register = "Idiomatisch / Verhandlung",
                category = "Redewendung & Idiom",
                stage = 1,
                intervalDays = 1,
                repetitions = 1,
                lastReviewedTimestamp = baseTimestamp - 86_400_000L,
                nextReviewTimestamp = baseTimestamp, // Due now
                isMastered = false
            ),
            VocabularyItemEntity(
                id = "voc_abstrich_machen",
                germanCollocation = "Abstriche machen bei etw.",
                meaningDe = "Qualitätseinbußen oder Kompromisse hinnehmen müssen",
                exampleSentence = "Bei der Produktsicherheit dürfen wir unter keinen Umständen Abstriche machen.",
                register = "Wirtschaft & Technik",
                category = "Nomen-Verb-Verbindung",
                stage = 2,
                intervalDays = 3,
                repetitions = 2,
                lastReviewedTimestamp = baseTimestamp - 4 * 86_400_000L,
                nextReviewTimestamp = baseTimestamp - 86_400_000L, // Overdue
                isMastered = false
            ),
            VocabularyItemEntity(
                id = "voc_ins_gewicht_fallen",
                germanCollocation = "schwer ins Gewicht fallen",
                meaningDe = "Ausschlaggebende oder gravierende Bedeutung haben",
                exampleSentence = "Die gestiegenen Energiekosten fallen bei der Gesamtrechnung schwer ins Gewicht.",
                register = "Analytisch / Feuilleton",
                category = "Nomen-Verb-Verbindung",
                stage = 3,
                intervalDays = 7,
                repetitions = 3,
                lastReviewedTimestamp = baseTimestamp - 2 * 86_400_000L,
                nextReviewTimestamp = baseTimestamp + 5 * 86_400_000L, // Due in 5 days
                isMastered = false
            ),
            VocabularyItemEntity(
                id = "voc_in_abrede_stellen",
                germanCollocation = "etw. in Abrede stellen",
                meaningDe = "Etwas bestreiten, leugnen oder öffentlich anzweifeln",
                exampleSentence = "Niemand stellt die fachliche Kompetenz des Projektteams ernsthaft in Abrede.",
                register = "Gehoben / Debatte",
                category = "Nomen-Verb-Verbindung",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_den_vorzug_geben",
                germanCollocation = "jmdm./etw. den Vorzug geben",
                meaningDe = "Eine Option oder Person gegenüber einer anderen bewusst bevorzugen",
                exampleSentence = "Bei gleicher Qualifikation geben wir dem internen Bewerber den Vorzug.",
                register = "Formell / HR & Führung",
                category = "Nomen-Verb-Verbindung",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_in_kauf_nehmen",
                germanCollocation = "etw. in Kauf nehmen",
                meaningDe = "Nachteile oder Risiken bewusst akzeptieren, um ein höheres Ziel zu erreichen",
                exampleSentence = "Für maximale Datensicherheit nehmen wir längere Ladezeiten in Kauf.",
                register = "Standard / Business",
                category = "Nomen-Verb-Verbindung",
                stage = 1,
                intervalDays = 1,
                repetitions = 1,
                lastReviewedTimestamp = baseTimestamp - 86_400_000L,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_ins_benehmen_setzen",
                germanCollocation = "sich mit jmdm. ins Benehmen setzen",
                meaningDe = "Sich formell und einvernehmlich mit einer zuständigen Stelle abstimmen",
                exampleSentence = "Vor Erlass der Richtlinie setzen wir uns mit dem Betriebsrat ins Benehmen.",
                register = "Amts- & Rechtsdeutsch",
                category = "Nomen-Verb-Verbindung",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_ausser_zweifel_stehen",
                germanCollocation = "außer Zweifel stehen",
                meaningDe = "Unbestreitbar sein, völlig sicher feststehen",
                exampleSentence = "Dass die Transformation erhebliche Investitionen erfordert, steht außer Zweifel.",
                register = "Gehoben / Argumentation",
                category = "Nomen-Verb-Verbindung",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_rechnung_tragen",
                germanCollocation = "etw. (Dat.) Rechnung tragen",
                meaningDe = "Einen Umstand oder ein Bedürfnis angemessen berücksichtigen",
                exampleSentence = "Der neue Tarifvertrag trägt der gestiegenen Inflation angemessen Rechnung.",
                register = "Gehoben / Wirtschaft",
                category = "Nomen-Verb-Verbindung",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_folge_leisten",
                germanCollocation = "einer Aufforderung Folge leisten",
                meaningDe = "Eine offizielle Anweisung oder Einladung befolgen",
                exampleSentence = "Alle Abteilungen leisteten der Einladung zur Krisensitzung unverzüglich Folge.",
                register = "Formell / Verwaltung",
                category = "Nomen-Verb-Verbindung",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_in_anspruch_nehmen",
                germanCollocation = "etw. in Anspruch nehmen",
                meaningDe = "Eine Leistung, Zeit oder Hilfe nutzen bzw. beanspruchen",
                exampleSentence = "Die Prüfung des Gutachtens wird mindestens drei Wochen in Anspruch nehmen.",
                register = "Standard / Beruf",
                category = "Nomen-Verb-Verbindung",
                stage = 1,
                intervalDays = 1,
                repetitions = 1,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_unter_beweis_stellen",
                germanCollocation = "etw. unter Beweis stellen",
                meaningDe = "Eine Fähigkeit oder Qualität praktisch belegen / demonstrieren",
                exampleSentence = "In der Verhandlung stellte sie ihr diplomatisches Geschick eindrucksvoll unter Beweis.",
                register = "Gehoben / Karriere",
                category = "Nomen-Verb-Verbindung",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_anbetracht_tatsache",
                germanCollocation = "in Anbetracht der Tatsache, dass",
                meaningDe = "Angesichts des Umstands, unter Berücksichtigung von",
                exampleSentence = "In Anbetracht der Tatsache, dass die Frist morgen endet, handeln wir sofort.",
                register = "Gehoben / Konnektor",
                category = "Konnektor (Kausal)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_nichtsdestotrotz",
                germanCollocation = "nichtsdestotrotz / gleichwohl",
                meaningDe = "Trotzdem, dennoch (gehobener konzessiver Anschluss)",
                exampleSentence = "Die Risiken sind erheblich; gleichwohl halten wir am Zeitplan fest.",
                register = "Gehoben / Schriftsprache",
                category = "Konnektor (Konzessiv)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_vorausgesetzt_dass",
                germanCollocation = "vorausgesetzt, dass / sofern",
                meaningDe = "Unter der Bedingung, dass (konditionale Einschränkung)",
                exampleSentence = "Wir genehmigen das Budget, sofern ein monatliches Controlling erfolgt.",
                register = "Präzise / Vertragssprache",
                category = "Konnektor (Konditional)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_geschweige_denn",
                germanCollocation = "geschweige denn",
                meaningDe = "Und noch viel weniger (Steigerung einer Verneinung)",
                exampleSentence = "Er hat die Präsentation nicht einmal gelesen, geschweige denn korrigiert.",
                register = "Idiomatisch / C1",
                category = "Konnektor (Restriktiv)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_zumal",
                germanCollocation = "zumal (+ Nebensatz)",
                meaningDe = "Besonders weil / erst recht da (verstärkende Begründung)",
                exampleSentence = "Ein Aufschub wäre fatal, zumal die Konkurrenz bereits testet.",
                register = "Gehoben / Debatte",
                category = "Konnektor (Kausal)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_augenwischerei",
                germanCollocation = "reine Augenwischerei sein",
                meaningDe = "Eine bewusste Täuschung oder Schönfärberei ohne echte Substanz",
                exampleSentence = "Die angebliche Mietsenkung entpuppte sich bei näherem Hinsehen als Augenwischerei.",
                register = "Idiomatisch / Kritik",
                category = "Redewendung & Idiom",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_auf_der_strecke_bleiben",
                germanCollocation = "auf der Strecke bleiben",
                meaningDe = "Benachteiligt werden, scheitern oder nicht mithalten können",
                exampleSentence = "Ohne gezielte Weiterbildung bleiben ältere Fachkräfte leicht auf der Strecke.",
                register = "Idiomatisch / Soziales",
                category = "Redewendung & Idiom",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_ueber_den_tellerrand",
                germanCollocation = "über den Tellerrand hinausblicken",
                meaningDe = "Über das eigene Fachgebiet oder den gewohnten Horizont hinausdenken",
                exampleSentence = "Interdisziplinäre Teams zwingen uns dazu, über den eigenen Tellerrand hinauszublicken.",
                register = "Gehobene Umgangssprache",
                category = "Redewendung & Idiom",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_stichhaltig_begruenden",
                germanCollocation = "etw. stichhaltig begründen",
                meaningDe = "Mit wasserdichten, logisch unangreifbaren Argumenten belegen",
                exampleSentence = "Können Sie den Mehraufwand gegenüber dem Aufsichtsrat stichhaltig begründen?",
                register = "Akademisch & Management",
                category = "Kollokation",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_modalpartikel_doch",
                germanCollocation = "Das liegt doch auf der Hand!",
                meaningDe = "Modalpartikel 'doch': Appell an offensichtliches, geteiltes Vorwissen",
                exampleSentence = "Wir können das Projekt doch nicht ohne Sicherheitsaudit freigeben!",
                register = "Pragmatisch / Mündlich",
                category = "Modalpartikel & Pragmatik",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_coach_spielintelligenz",
                germanCollocation = "die Spielintelligenz fördern",
                meaningDe = "Kindern und Jugendlichen helfen, auf dem Platz selbstständig kluge Entscheidungen zu treffen",
                exampleSentence = "Durch kleine Spielformen im Vier-gegen-Vier fördern wir die Spielintelligenz unserer E-Jugend.",
                register = "DFB-Ausbildungssprache",
                category = "Fußball-Trainer (E-/C-Jugend)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_coach_umschaltspiel",
                germanCollocation = "schnell umschalten / das Umschaltspiel",
                meaningDe = "Gedanklich und läuferisch sofort zwischen Ballbesitz und Ballverlust wechseln",
                exampleSentence = "Nach Ballverlust müssen wir in der C-Jugend geschlossen umschalten und die Tiefe sichern.",
                register = "Taktik & Vereinsfußball",
                category = "Fußball-Trainer (E-/C-Jugend)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_coach_halbraum",
                germanCollocation = "die Halbräume schließen / besetzen",
                meaningDe = "Den strategischen Raum zwischen Zentrum und Außenbahn taktisch abdecken oder anspielen",
                exampleSentence = "Wenn der Außenverteidiger einrückt, schließen wir den gefährlichen Halbraum.",
                register = "Taktik & Gruppenspiel",
                category = "Fußball-Trainer (E-/C-Jugend)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_coach_auf_augenhoehe",
                germanCollocation = "auf Augenhöhe coachen",
                meaningDe = "Spieler und Eltern respektvoll, konstruktiv und ohne Herablassung anleiten",
                exampleSentence = "Gerade in der C-Jugend ist es entscheidend, Jugendliche auf Augenhöhe zu coachen und einzubinden.",
                register = "Pädagogik & Führung",
                category = "Fußball-Trainer (E-/C-Jugend)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            ),
            VocabularyItemEntity(
                id = "voc_coach_altersgerecht",
                germanCollocation = "altersgerecht ausbilden",
                meaningDe = "Trainingsinhalte an den körperlichen und geistigen Entwicklungsstand (E- vs. C-Jugend) anpassen",
                exampleSentence = "Statt sturer Konditionsläufe bilden wir unsere E-Jugend altersgerecht mit Ball und Spielformen aus.",
                register = "DFB-Rahmenkonzept",
                category = "Fußball-Trainer (E-/C-Jugend)",
                stage = 0,
                intervalDays = 0,
                repetitions = 0,
                nextReviewTimestamp = baseTimestamp
            )
        )

        if (count == 0) {
            vocabularyDao.insertAll(initialItems)
        } else {
            val missingCoachItems = initialItems.filter { item ->
                item.id.startsWith("voc_coach_") && vocabularyDao.getVocabularyById(item.id) == null
            }
            if (missingCoachItems.isNotEmpty()) {
                vocabularyDao.insertAll(missingCoachItems)
            }
        }
    }

    suspend fun seedCurriculumExpressions(unitId: String? = null, now: Long = System.currentTimeMillis()): Int {
        val cards = CurriculumCatalogue.units.mapNotNull { unit ->
            if (unitId != null && unit.id != unitId) return@mapNotNull null
            val content = unit.content ?: return@mapNotNull null
            content.expressions.mapIndexed { index, expression ->
                val parts = expression.split(Regex("\\s+[–—-]\\s+"), limit = 2)
                val phrase = parts.first().trim()
                VocabularyItemEntity(
                    id = "course_${unit.id}_expression_$index",
                    germanCollocation = phrase,
                    meaningDe = parts.getOrNull(1)?.trim()
                        ?.takeIf(String::isNotBlank)
                        ?: "Kursausdruck aus der Einheit „${unit.title}“.",
                    exampleSentence = "Abrufauftrag: Verwende den Ausdruck in einer eigenen Aussage zur Einheit „${unit.title}“.",
                    register = "Kurs · ${unit.title}",
                    category = "Kursausdruck & Kollokation",
                    nextReviewTimestamp = now,
                    sourceUnitId = unit.id
                )
            }
        }.flatten()
        return if (cards.isEmpty()) 0
        else vocabularyDao.insertMissing(cards).count { it != -1L }
    }
}
