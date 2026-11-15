package com.example.programme

/** One policy used by ordinary retrieval and delayed mastery planning. */
object RetrievalPolicy {
    data class Schedule(
        val intervalDays: Int,
        val easeFactor: Float,
        val repetitions: Int
    )

    /** Fixed minimum spacing required by the mastery contract; not a card SRS algorithm. */
    val intervalsDays: List<Int> = listOf(1, 3, 7, 21)
    const val errorRetryDays: Int = 1

    /** Adapt vocabulary and phrase-card intervals to recall quality and each card's history. */
    fun nextSchedule(
        quality: Int,
        previousIntervalDays: Int,
        previousEaseFactor: Float,
        previousRepetitions: Int
    ): Schedule {
        require(previousIntervalDays >= 0)
        require(previousEaseFactor.isFinite())
        require(previousRepetitions >= 0)
        val score = quality.coerceIn(0, 3)
        val difference = 3 - score
        val ease = (previousEaseFactor + (0.1f - difference * (0.08f + difference * 0.02f)))
            .coerceIn(1.3f, 2.8f)
        if (score < 2) return Schedule(1, ease, 0)

        val repetitions = previousRepetitions + 1
        val interval = when (repetitions) {
            1 -> if (score == 3) 2 else 1
            2 -> if (score == 3) 4 else 3
            else -> maxOf(
                (previousIntervalDays * ease + 0.5f).toInt(),
                previousIntervalDays + 1
            )
        }
        return Schedule(interval, ease, repetitions)
    }
}
