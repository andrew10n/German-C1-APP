package com.example.data.curriculum

import android.content.Context
import com.example.domain.model.C1Pillar
import com.example.domain.model.C1QuizItem
import com.example.domain.model.C1VocabCard
import com.example.domain.model.ConversationScenarioItem
import com.example.domain.model.FootballTacticalScenario
import com.example.domain.model.MediaImmersionMission
import com.example.domain.model.MediationScenarioItem
import com.example.domain.model.PersonalizedExercise
import com.example.domain.model.PersonalizedLibraryBundle
import com.example.domain.model.RepoCurriculumUnit
import com.example.domain.model.VisualLessonModule
import com.example.domain.model.YearReleaseAuditReport

class C1CurriculumRepository(context: Context? = null) {
    private val bundle: LoadedRepoBundle = RepoBundleLoader.load(context)
    private val yearExpansion: LoadedYearExpansion = YearExpansionLoader.load(context)
    private val personalizedBundle: PersonalizedLibraryBundle = PersonalizedLibraryLoader.load(context)

    private val mergedVocab: List<C1VocabCard> = (
        yearExpansion.extraVocabCards +
            PersonalizedLibraryLoader.extractContextualExpressionCards(personalizedBundle) +
            bundle.repoVocabCards +
            C1VocabAndQuizData.vocabCards
        ).distinctBy { it.expression.lowercase().trim() }

    private val mergedQuiz: List<C1QuizItem> = (
        yearExpansion.extraQuizProbes +
            bundle.repoQuizItems +
            C1VocabAndQuizData.quizItems
        ).distinctBy { it.id }

    private val auditReport: YearReleaseAuditReport = yearExpansion.rawAuditReport.copy(
        totalMultiSpeakerScenarios = personalizedBundle.exercises.size,
        totalContextualVocabChunks = mergedVocab.size,
        totalAdaptiveIrtProbes = mergedQuiz.size,
        totalTextbookUnits = bundle.units.size,
        totalLiveMediaMissions = yearExpansion.mediaMissions.size
    )

    fun getStages(): List<String> = bundle.stages

    fun getAllRepoUnits(): List<RepoCurriculumUnit> = bundle.units

    fun getRepoUnitById(unitId: String): RepoCurriculumUnit? =
        bundle.units.find { it.id == unitId }

    fun getPersonalizedLibrary(): PersonalizedLibraryBundle = personalizedBundle

    fun getPersonalizedExerciseById(id: String): PersonalizedExercise? =
        personalizedBundle.exercises.find { it.id == id }

    fun getMediaImmersionMissions(): List<MediaImmersionMission> = yearExpansion.mediaMissions

    fun getMediaImmersionMissionById(id: String): MediaImmersionMission? =
        yearExpansion.mediaMissions.find { it.id == id }

    fun getYearAuditReport(): YearReleaseAuditReport = auditReport

    fun getConversations(): List<ConversationScenarioItem> = bundle.conversations

    fun getMediations(): List<MediationScenarioItem> = bundle.mediations

    fun getFootballScenarios(): List<FootballTacticalScenario> = bundle.footballScenarios

    fun getAllModules(): List<VisualLessonModule> = C1VisualModulesData.modules

    fun getModuleById(id: String): VisualLessonModule? =
        C1VisualModulesData.modules.find { it.id == id }

    fun getModuleForPillar(pillar: C1Pillar): VisualLessonModule =
        C1VisualModulesData.modules.first { it.pillar == pillar }

    fun getAllVocabCards(): List<C1VocabCard> = mergedVocab

    fun getAllQuizItems(): List<C1QuizItem> = mergedQuiz
}
