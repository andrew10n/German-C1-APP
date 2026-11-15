package com.example.ui.vocabulary

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.MainApplication
import com.example.data.local.C1AppDatabase
import com.example.data.local.entity.VocabularyItemEntity
import com.example.data.repository.VocabularySrsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI State for the active SRS flashcard review drill.
 */
data class SrsCardReviewState(
    val currentCard: VocabularyItemEntity? = null,
    val isRevealed: Boolean = false,
    val currentIndex: Int = 0,
    val totalDue: Int = 0,
    val lastReviewedItem: VocabularyItemEntity? = null,
    val isSessionFinished: Boolean = false
)

/**
 * ViewModel managing the Spaced Repetition System (SRS) for C1 Vocabulary items.
 * Tracks reactive 'last reviewed' and 'next review' timestamps and handles review grading.
 */
class VocabularySrsViewModel @JvmOverloads constructor(
    application: Application,
    private val appDatabase: C1AppDatabase = (application as? MainApplication)?.database ?: C1AppDatabase.getDatabase(application),
    val repository: VocabularySrsRepository = (application as? MainApplication)?.vocabularySrsRepository ?: VocabularySrsRepository(appDatabase.vocabularyItemDao())
) : AndroidViewModel(application) {

    // Reactive stream of all vocabulary items in Room
    val allVocabulary: StateFlow<List<VocabularyItemEntity>> = repository.allVocabulary
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Reactive stream of currently due vocabulary items (nextReviewTimestamp <= now)
    private val _currentTime = MutableStateFlow(System.currentTimeMillis())
    val currentTime: StateFlow<Long> = _currentTime.asStateFlow()

    val dueVocabulary: StateFlow<List<VocabularyItemEntity>> = _currentTime
        .flatMapLatest { now -> repository.getDueVocabulary(now) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dueCount: StateFlow<Int> = _currentTime
        .flatMapLatest { now -> repository.getDueCount(now) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val masteredCount: StateFlow<Int> = repository.getMasteredCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Active Card Review Drill State
    private val _cardState = MutableStateFlow(SrsCardReviewState())
    val cardState: StateFlow<SrsCardReviewState> = _cardState.asStateFlow()

    init {
        initializeDatabase()
    }

    /**
     * Seeds initial C1 vocabulary and synchronizes card drill state.
     */
    fun initializeDatabase(): Job {
        return viewModelScope.launch {
            try {
                repository.seedInitialC1VocabularyIfEmpty()
                updateCurrentTime()
                val due = repository.getDueVocabularyDirect()
                _cardState.value = SrsCardReviewState(
                    currentCard = due.firstOrNull(),
                    isRevealed = false,
                    currentIndex = 0,
                    totalDue = due.size,
                    isSessionFinished = due.isEmpty()
                )
            } catch (e: Exception) {
                Log.e("VocabularySrsVM", "Failed to initialize SRS database", e)
            }
        }
    }

    fun updateCurrentTime() {
        _currentTime.value = System.currentTimeMillis()
    }

    /**
     * Reveals the meaning, example sentence, and register on the back of the card.
     */
    fun revealCard() {
        _cardState.update { it.copy(isRevealed = true) }
    }

    /**
     * Submits review rating for the active card, computes new interval and timestamps,
     * persists to Room, and advances to the next due card.
     *
     * @param qualityScore 0: Wiederholen (Blackout), 1: Schwer, 2: Gut, 3: Perfekt (Spontan)
     */
    fun submitReview(qualityScore: Int): Job {
        val activeCard = _cardState.value.currentCard ?: return viewModelScope.launch { }

        return viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                // 1. Compute and persist new SRS intervals and timestamps in Room
                val updatedItem = repository.recordReview(
                    itemId = activeCard.id,
                    qualityScore = qualityScore,
                    reviewTimestamp = now
                )

                // 2. Fetch remaining due items
                val remainingDue = repository.getDueVocabularyDirect(now)

                _cardState.value = SrsCardReviewState(
                    currentCard = remainingDue.firstOrNull(),
                    isRevealed = false,
                    currentIndex = _cardState.value.currentIndex + 1,
                    totalDue = remainingDue.size,
                    lastReviewedItem = updatedItem,
                    isSessionFinished = remainingDue.isEmpty()
                )

                _currentTime.value = now

                // Record daily streak activity
                (getApplication() as? MainApplication)?.dailyStreakRepository?.recordActivity("Wortschatz-Wiederholung")
            } catch (e: Exception) {
                Log.e("VocabularySrsVM", "Failed to submit SRS review rating", e)
            }
        }
    }

    /**
     * Adds a new custom C1 vocabulary or collocation item into Room.
     */
    fun addNewVocabulary(
        germanCollocation: String,
        meaningDe: String,
        exampleSentence: String,
        register: String = "Gehoben / C1",
        category: String = "Nomen-Verb-Verbindung"
    ): Job {
        return viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val newItem = VocabularyItemEntity(
                    id = "custom_voc_${now}",
                    germanCollocation = germanCollocation.trim(),
                    meaningDe = meaningDe.trim(),
                    exampleSentence = exampleSentence.trim(),
                    register = register.trim(),
                    category = category.trim(),
                    stage = 0,
                    intervalDays = 0,
                    repetitions = 0,
                    lastReviewedTimestamp = null,
                    nextReviewTimestamp = now, // Due immediately for first retention check
                    isMastered = false
                )
                repository.insertOrUpdate(newItem)
                updateCurrentTime()
            } catch (e: Exception) {
                Log.e("VocabularySrsVM", "Failed to add new vocabulary item", e)
            }
        }
    }

    /**
     * Deletes a vocabulary item from Room.
     */
    fun deleteVocabulary(id: String): Job {
        return viewModelScope.launch {
            try {
                repository.delete(id)
                updateCurrentTime()
            } catch (e: Exception) {
                Log.e("VocabularySrsVM", "Failed to delete vocabulary item", e)
            }
        }
    }
}
