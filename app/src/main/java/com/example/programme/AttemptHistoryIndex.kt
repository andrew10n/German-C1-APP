package com.example.programme

class AttemptHistoryIndex private constructor(
    val attempts: List<Attempt>,
    val byUnit: Map<String, List<Attempt>>,
    val incrementalUpdates: Int,
    val fullRebuilds: Int
) {
    fun update(next: List<Attempt>): AttemptHistoryIndex {
        if (attempts.isNotEmpty() &&
            next.size == attempts.size + 1 &&
            next.dropLast(1) == attempts
        ) {
            val added = next.last()
            return AttemptHistoryIndex(
                attempts = next,
                byUnit = byUnit + (added.unitId to (byUnit[added.unitId].orEmpty() + added)),
                incrementalUpdates = incrementalUpdates + 1,
                fullRebuilds = fullRebuilds
            )
        }
        if (next == attempts) return this
        return create(next, incrementalUpdates, fullRebuilds + 1)
    }

    companion object {
        fun empty() = AttemptHistoryIndex(emptyList(), emptyMap(), 0, 0)

        private fun create(
            attempts: List<Attempt>,
            incrementalUpdates: Int,
            fullRebuilds: Int
        ) = AttemptHistoryIndex(
            attempts = attempts,
            byUnit = attempts.groupBy { it.unitId },
            incrementalUpdates = incrementalUpdates,
            fullRebuilds = fullRebuilds
        )
    }
}
