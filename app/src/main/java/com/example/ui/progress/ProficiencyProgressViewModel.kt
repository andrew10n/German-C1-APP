package com.example.ui.progress

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainApplication
import com.example.data.local.C1AppDatabase
import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.local.entity.ProficiencySnapshotEntity
import com.example.data.repository.ProficiencyProgressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ProficiencyMetric(val titleDe: String, val subtitleDe: String) {
    OVERALL_C1("C1 Gesamt-Score", "Aggregierter CEFR C1 Leistungsstand"),
    FUNCTIONAL_READINESS("Funktional (Echtzeit)", "Spontaneität & Latenz unter Druck"),
    HOERVERSTEHEN("Hör-Intelligenz", "Schnellsprech & Dialekt-Inferenz"),
    SPRECHEN("Sprech-Spontaneität", "Argumentation & Reaktionszeit"),
    WORTSCHATZ("Wortschatz-Gedächtnis", "Kollokationen & Spaced Repetition")
}

enum class ProgressTimeframe(val label: String) {
    ALL("Gesamt (6 Wochen)"),
    LAST_4_WEEKS("Letzte 4 Wochen"),
    LAST_2_WEEKS("Letzte 14 Tage")
}

data class ProficiencyProgressUiState(
    val snapshots: List<ProficiencySnapshotEntity> = emptyList(),
    val filteredSnapshots: List<ProficiencySnapshotEntity> = emptyList(),
    val selectedMetric: ProficiencyMetric = ProficiencyMetric.OVERALL_C1,
    val selectedTimeframe: ProgressTimeframe = ProgressTimeframe.ALL,
    val activeHoverIndex: Int? = null,
    val baselineProfile: BaselineProfileEntity? = null,
    val totalGrowthPoints: Int = 0,
    val totalGrowthPercentage: Int = 0,
    val isInitialized: Boolean = false
)

class ProficiencyProgressViewModel @JvmOverloads constructor(
    application: Application,
    private val appDatabase: C1AppDatabase = (application as? MainApplication)?.database ?: C1AppDatabase.getDatabase(application),
    val repository: ProficiencyProgressRepository = (application as? MainApplication)?.proficiencyProgressRepository ?: ProficiencyProgressRepository(
        appDatabase.proficiencySnapshotDao(),
        appDatabase.baselineProfileDao(),
        appDatabase.vocabularyItemDao()
    )
) : AndroidViewModel(application) {

    private val _selectedMetric = MutableStateFlow(ProficiencyMetric.OVERALL_C1)
    private val _selectedTimeframe = MutableStateFlow(ProgressTimeframe.ALL)
    private val _activeHoverIndex = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<ProficiencyProgressUiState> = combine(
        repository.allSnapshotsFlow,
        repository.baselineProfileFlow,
        _selectedMetric,
        _selectedTimeframe,
        _activeHoverIndex
    ) { snapshots, baseline, metric, timeframe, hoverIndex ->
        val filtered = applyTimeframeFilter(snapshots, timeframe)
        val initialScore = snapshots.firstOrNull()?.overallScore ?: 50
        val latestScore = snapshots.lastOrNull()?.overallScore ?: initialScore
        val growthPoints = latestScore - initialScore
        val growthPct = if (initialScore > 0) ((growthPoints.toFloat() / initialScore) * 100).toInt() else 0

        ProficiencyProgressUiState(
            snapshots = snapshots,
            filteredSnapshots = filtered,
            selectedMetric = metric,
            selectedTimeframe = timeframe,
            activeHoverIndex = hoverIndex ?: if (filtered.isNotEmpty()) filtered.size - 1 else null,
            baselineProfile = baseline,
            totalGrowthPoints = growthPoints,
            totalGrowthPercentage = growthPct,
            isInitialized = snapshots.isNotEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ProficiencyProgressUiState()
    )

    private var initJob: Job? = null

    init {
        initializeData()
    }

    fun initializeData(): Job {
        return initJob ?: viewModelScope.launch {
            try {
                repository.seedHistoricalProficiencyIfEmpty()
            } catch (e: Exception) {
                Log.e("ProficiencyProgressVM", "Failed to initialize proficiency data", e)
            }
        }.also { initJob = it }
    }

    fun selectMetric(metric: ProficiencyMetric) {
        _selectedMetric.value = metric
    }

    fun selectTimeframe(timeframe: ProgressTimeframe) {
        _selectedTimeframe.value = timeframe
        _activeHoverIndex.value = null
    }

    fun setHoverIndex(index: Int?) {
        _activeHoverIndex.value = index
    }

    private fun applyTimeframeFilter(
        snapshots: List<ProficiencySnapshotEntity>,
        timeframe: ProgressTimeframe
    ): List<ProficiencySnapshotEntity> {
        return when (timeframe) {
            ProgressTimeframe.ALL -> snapshots
            ProgressTimeframe.LAST_4_WEEKS -> snapshots.takeLast(4)
            ProgressTimeframe.LAST_2_WEEKS -> snapshots.takeLast(2)
        }
    }

    /**
     * Persists a new proficiency milestone to Room.
     */
    fun recordNewProficiencySnapshot(
        label: String,
        overallScore: Int,
        functionalScore: Int
    ): Job {
        return viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val existing = repository.baselineProfileFlow.firstOrNull()
                val snapshot = ProficiencySnapshotEntity(
                    timestamp = now,
                    periodLabel = label,
                    overallScore = overallScore,
                    functionalScore = functionalScore,
                    listeningScore = existing?.listeningScore ?: overallScore,
                    speakingScore = existing?.speakingScore ?: functionalScore,
                    readingScore = existing?.readingScore ?: overallScore,
                    writingScore = existing?.writingScore ?: functionalScore,
                    grammarScore = existing?.grammarScore ?: overallScore,
                    vocabularyScore = existing?.vocabularyScore ?: overallScore
                )
                repository.insertSnapshot(snapshot)
            } catch (e: Exception) {
                Log.e("ProficiencyProgressVM", "Failed to record proficiency snapshot", e)
            }
        }
    }
}
