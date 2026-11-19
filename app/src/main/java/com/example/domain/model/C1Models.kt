package com.example.domain.model

enum class C1Pillar(
    val id: String,
    val shortLabel: String,
    val titleDe: String,
    val subtitleEn: String,
    val examWeight: Float
) {
    NOMINALISIERUNG(
        id = "nominalisierung",
        shortLabel = "Nominalstil",
        titleDe = "Nominalisierung & Genitiv",
        subtitleEn = "Transform clauses into dense academic noun phrases",
        examWeight = 0.20f
    ),
    KONJUNKTIV(
        id = "konjunktiv",
        shortLabel = "Konjunktiv",
        titleDe = "Konjunktiv I & II",
        subtitleEn = "Reported speech, hedging & counterfactual precision",
        examWeight = 0.16f
    ),
    SATZGEFUEGE(
        id = "satzgefuege",
        shortLabel = "Satzgefüge",
        titleDe = "Satzklammer & Partizipien",
        subtitleEn = "Extended left-branching attributes & complex syntax",
        examWeight = 0.18f
    ),
    NOMEN_VERB(
        id = "nomen_verb",
        shortLabel = "Funktionsverb",
        titleDe = "Nomen-Verb-Verbindungen",
        subtitleEn = "C1 academic & institutional collocations",
        examWeight = 0.18f
    ),
    MODALPARTIKELN(
        id = "modalpartikeln",
        shortLabel = "Partikeln",
        titleDe = "Modalpartikeln & Nuancen",
        subtitleEn = "Pragmatic attitude, softening & rhetorical emphasis",
        examWeight = 0.12f
    ),
    RHETORIK(
        id = "rhetorik",
        shortLabel = "Rhetorik",
        titleDe = "Wissenschaftliche Rhetorik",
        subtitleEn = "Goethe & TestDaF argumentation, concession & synthesis",
        examWeight = 0.16f
    )
}

enum class ErrorArchetype(
    val id: String,
    val labelDe: String,
    val shortCode: String,
    val diagnosticTip: String
) {
    CASE_GOVERNMENT(
        id = "case_government",
        labelDe = "Rektion & Genitiv-Präpositionen",
        shortCode = "KASUS",
        diagnosticTip = "C1 prepositions (trotz, angesichts, infolge, mangels) demand strict Genitiv & exact verb rection."
    ),
    WORD_ORDER_KLAMMER(
        id = "word_order_klammer",
        labelDe = "Satzklammer & TeKaMoLo",
        shortCode = "SYNTAX",
        diagnosticTip = "Maintain the finite verb bracket and order Mittelfeld modifiers as Temporal-Kausal-Modal-Lokal."
    ),
    REGISTER_COLLOCATION(
        id = "register_collocation",
        labelDe = "Register & Kollokation",
        shortCode = "STIL",
        diagnosticTip = "Replace informal B2 verbs with precise C1 Nomen-Verb-Verbindungen and Bildungssprache."
    ),
    MOOD_MODALITY(
        id = "mood_modality",
        labelDe = "Modus & Abtönung",
        shortCode = "MODUS",
        diagnosticTip = "Distinguish Konjunktiv I (distanced reporting) from Konjunktiv II (counterfactual/politeness)."
    ),
    PARTICIPLE_AGREEMENT(
        id = "participle_agreement",
        labelDe = "Partizipialattribute & Kongruenz",
        shortCode = "PARTIZIP",
        diagnosticTip = "Partizip I = active/ongoing (-end); Partizip II = passive/completed; decline like regular adjectives."
    )
}

enum class VisualDiagramType {
    NOMINAL_TRANSFORM_FLOW,
    SENTENCE_BRACKET_ARCH,
    KONJUNKTIV_SPECTRUM,
    PARTICIPLE_UNPACK_TREE,
    MODAL_PARTICLE_MATRIX,
    ARGUMENT_PYRAMID,
    COLLOCATION_NETWORK,
    TEKAMOLO_PIPELINE
}

data class SlideCheckpoint(
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val feedback: String
)

data class VisualSlide(
    val id: String,
    val pillar: C1Pillar,
    val slideIndex: Int,
    val totalSlides: Int,
    val headline: String,
    val subheadline: String,
    val diagramType: VisualDiagramType,
    val formulaBefore: String,
    val formulaAfter: String,
    val highlightRule: String,
    val keyInsight: String,
    val microExampleDe: String,
    val microExampleGloss: String,
    val commonTrap: String,
    val checkpoint: SlideCheckpoint? = null,
    val isAiSynthesized: Boolean = false
)

data class VisualLessonModule(
    val id: String,
    val pillar: C1Pillar,
    val title: String,
    val germanSubtitle: String,
    val estimatedMinutes: Int,
    val keyVisualDiagram: VisualDiagramType,
    val slides: List<VisualSlide>
)

data class C1QuizItem(
    val id: String,
    val pillar: C1Pillar,
    val difficulty: Int, // 1 = B2+, 2 = C1.1, 3 = C1.2, 4 = C1+
    val archetype: ErrorArchetype,
    val promptDe: String,
    val contextSnippet: String,
    val options: List<String>,
    val correctIndex: Int,
    val visualDiagram: VisualDiagramType,
    val explanationDe: String,
    val explanationEn: String,
    val trapNote: String,
    val isAiGenerated: Boolean = false
)

data class C1VocabCard(
    val id: String,
    val pillar: C1Pillar,
    val expression: String,
    val grammarTag: String,
    val register: String,
    val meaningEn: String,
    val exampleDe: String,
    val trapWarning: String,
    val visualHint: String,
    val diagramType: VisualDiagramType,
    val isAiGenerated: Boolean = false
)

enum class ControllerStrategyMode(
    val id: String,
    val title: String,
    val description: String,
    val targetDifficultyBias: Float,
    val remediationWeight: Float
) {
    PARETO_80_20(
        id = "pareto_80_20",
        title = "Pareto 80/20 High-ROI",
        description = "Laser-focuses on the vital 20% pillars & mistakes yielding 80% of C1 exam gains; prunes low-ROI fluff.",
        targetDifficultyBias = 0.25f,
        remediationWeight = 1.65f
    ),
    BALANCED(
        id = "balanced",
        title = "Balanced Synthesis",
        description = "Harmonizes weak-spot remediation with visual concept discovery.",
        targetDifficultyBias = 0.0f,
        remediationWeight = 1.0f
    ),
    INTENSIVE_REPAIR(
        id = "intensive_repair",
        title = "Intensive Mistake Repair",
        description = "Aggressively targets active mistakes, lowest-mastery pillars & Stolpersteine.",
        targetDifficultyBias = -0.3f,
        remediationWeight = 1.9f
    ),
    EXAM_SPRINT(
        id = "exam_sprint",
        title = "Goethe / TestDaF Sprint",
        description = "Prioritizes Difficulty 3–4 academic rhetoric, Nominalstil & precision.",
        targetDifficultyBias = 0.65f,
        remediationWeight = 1.25f
    ),
    VISUAL_DISCOVERY(
        id = "visual_discovery",
        title = "Visual Immersion",
        description = "Low-fatigue visual story decks paired with Socratic scaffolding.",
        targetDifficultyBias = -0.2f,
        remediationWeight = 0.9f
    )
}

enum class CognitiveLoadLevel(val label: String) {
    FRESH("Peak Focus"),
    OPTIMAL("Optimal Flow"),
    ELEVATED("Rising Load"),
    FATIGUED("Scaffolding Mode")
}

data class WritingTransformation(
    val originalPhrase: String,
    val upgradedPhrase: String,
    val ruleTag: String,
    val explanation: String
)

data class WritingDiagnosticResult(
    val estimatedLevel: String,
    val c1Score: Int,
    val upgradedText: String,
    val diagnosticFeedback: String,
    val detectedArchetypes: List<ErrorArchetype>,
    val transformations: List<WritingTransformation>
)

data class PillarDifficultyAdjustment(
    val pillar: C1Pillar,
    val newDifficultyTier: Int,
    val roiReason: String
)

data class AiOrchestrationPlan(
    val executiveSummary: String,
    val paretoInsight: String,
    val recommendedStrategy: ControllerStrategyMode,
    val nextFocusPillar: C1Pillar,
    val difficultyAdjustments: List<PillarDifficultyAdjustment>,
    val pruneCandidateIds: List<String>,
    val newVisualSlide: VisualSlide,
    val newVocabCard: C1VocabCard,
    val newRemediationProbes: List<C1QuizItem>
)

data class AiMutationReport(
    val executiveSummary: String,
    val paretoInsight: String,
    val nextFocusPillar: C1Pillar,
    val adjustedPillarsCount: Int,
    val addedSlideHeadline: String,
    val addedVocabExpression: String,
    val addedProbesCount: Int,
    val prunedItemsCount: Int
)
