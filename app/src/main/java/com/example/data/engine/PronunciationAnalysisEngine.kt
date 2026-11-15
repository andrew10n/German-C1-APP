package com.example.data.engine

import com.example.data.model.PhoneticWordTip
import com.example.data.model.PronunciationAnalysisResult
import com.example.data.model.PronunciationWordResult
import com.example.data.model.SpeakingPrompt
import com.example.data.model.WordPronunciationStatus
import kotlin.math.max

class PronunciationAnalysisEngine {

    fun analyzePronunciation(
        prompt: SpeakingPrompt,
        transcribedSpeech: String,
        confidence: Float = 0.85f
    ): PronunciationAnalysisResult {
        val cleanExpectedWords = prompt.targetSentence.split(Regex("\\s+")).map { cleanPunctuation(it) }.filter { it.isNotBlank() }
        val cleanSpokenWords = transcribedSpeech.split(Regex("\\s+")).map { cleanPunctuation(it) }.filter { it.isNotBlank() }

        val wordResults = mutableListOf<PronunciationWordResult>()
        var correctWordsCount = 0
        var acceptableWordsCount = 0

        // Map phonetic tips by normalized word
        val phoneticMap: Map<String, PhoneticWordTip> = prompt.phoneticFocusWords.associateBy { cleanPunctuation(it.word).lowercase() }

        // Greedy matching and Levenshtein alignment
        var spokenIdx = 0
        for (expected in cleanExpectedWords) {
            val normalizedExpected = expected.lowercase()
            val tip = phoneticMap[normalizedExpected]

            if (spokenIdx < cleanSpokenWords.size) {
                val spoken = cleanSpokenWords[spokenIdx]
                val normalizedSpoken = spoken.lowercase()

                val similarity = calculateSimilarity(normalizedExpected, normalizedSpoken)

                when {
                    normalizedExpected == normalizedSpoken -> {
                        correctWordsCount++
                        spokenIdx++
                        wordResults.add(
                            PronunciationWordResult(
                                expectedWord = expected,
                                spokenWord = spoken,
                                status = WordPronunciationStatus.PERFECT,
                                confidenceScore = confidence,
                                ipa = tip?.ipa,
                                phoneticTip = tip?.tipDe
                            )
                        )
                    }
                    similarity >= 0.70f -> {
                        acceptableWordsCount++
                        spokenIdx++
                        wordResults.add(
                            PronunciationWordResult(
                                expectedWord = expected,
                                spokenWord = spoken,
                                status = WordPronunciationStatus.ACCEPTABLE,
                                confidenceScore = confidence * similarity,
                                ipa = tip?.ipa,
                                phoneticTip = tip?.tipDe ?: "Leichte Lautabweichung bei '$spoken'."
                            )
                        )
                    }
                    similarity >= 0.40f -> {
                        spokenIdx++
                        wordResults.add(
                            PronunciationWordResult(
                                expectedWord = expected,
                                spokenWord = spoken,
                                status = WordPronunciationStatus.MISPRONOUNCED,
                                confidenceScore = confidence * similarity,
                                ipa = tip?.ipa,
                                phoneticTip = tip?.tipDe ?: "Aussprache ungenau (erkannt als '$spoken')."
                            )
                        )
                    }
                    else -> {
                        // Lookahead: Did the user skip this word or pronounce a different word?
                        val nextSpokenMatches = (spokenIdx + 1 until cleanSpokenWords.size).any {
                            cleanSpokenWords[it].lowercase() == normalizedExpected
                        }
                        if (nextSpokenMatches) {
                            // User spoke something else first
                            spokenIdx++
                            wordResults.add(
                                PronunciationWordResult(
                                    expectedWord = expected,
                                    spokenWord = spoken,
                                    status = WordPronunciationStatus.MISPRONOUNCED,
                                    confidenceScore = 0.4f,
                                    ipa = tip?.ipa,
                                    phoneticTip = "Wortabweichung erkannt."
                                )
                            )
                        } else {
                            // Word was omitted or dropped
                            wordResults.add(
                                PronunciationWordResult(
                                    expectedWord = expected,
                                    spokenWord = null,
                                    status = WordPronunciationStatus.MISSING,
                                    confidenceScore = 0.0f,
                                    ipa = tip?.ipa,
                                    phoneticTip = "Dieses Wort wurde in der Aufnahme ausgelassen."
                                )
                            )
                        }
                    }
                }
            } else {
                // All remaining expected words are missing
                wordResults.add(
                    PronunciationWordResult(
                        expectedWord = expected,
                        spokenWord = null,
                        status = WordPronunciationStatus.MISSING,
                        confidenceScore = 0.0f,
                        ipa = tip?.ipa,
                        phoneticTip = "Ausgelassen."
                    )
                )
            }
        }

        val totalExpected = max(1, cleanExpectedWords.size)
        val rawAccuracy = ((correctWordsCount * 1.0f + acceptableWordsCount * 0.7f) / totalExpected * 100f).toInt().coerceIn(0, 100)

        // Fluency score incorporates confidence and completion ratio
        val completionRatio = (cleanSpokenWords.size.toFloat() / totalExpected).coerceIn(0f, 1.2f)
        val fluencyScore = ((rawAccuracy * 0.6f + (confidence * 100f) * 0.2f + (completionRatio.coerceAtMost(1f) * 100f) * 0.2f)).toInt().coerceIn(10, 98)

        // Prosody score: rhythmic cadence and intonation
        val prosodyScore = ((rawAccuracy * 0.7f + fluencyScore * 0.3f) * (if (cleanSpokenWords.size >= totalExpected * 0.8) 1.0f else 0.8f)).toInt().coerceIn(15, 96)

        val overallScore = ((rawAccuracy * 0.50f) + (fluencyScore * 0.30f) + (prosodyScore * 0.20f)).toInt().coerceIn(10, 99)

        val (headline, cefrBand) = when {
            overallScore >= 90 -> Pair("Hervorragende C1-Artikulation!", "C1.2 – Nahezu muttersprachliche Artikulation")
            overallScore >= 80 -> Pair("Sehr gute, flüssige Aussprache!", "C1.1 – Hohe Verständlichkeit & Souveränität")
            overallScore >= 68 -> Pair("Gutes C1-Verständnis, feine Nuancen üben.", "B2+ – Fließend mit leichten Akzent-Merkmalen")
            else -> Pair("Wiederholung empfohlen für mehr Klarheit.", "B2 – Artikulation & Lautbildung trainieren")
        }

        val actionableTips = mutableListOf<String>()

        // Generate specific German phonetic tips based on detected issues
        val mispronouncedOrMissing = wordResults.filter { it.status == WordPronunciationStatus.MISPRONOUNCED || it.status == WordPronunciationStatus.MISSING }
        if (mispronouncedOrMissing.isNotEmpty()) {
            val wordsToReview = mispronouncedOrMissing.take(2).map { "„${it.expectedWord}“" }.joinToString(" und ")
            actionableTips.add("Fokussieren Sie sich bei der nächsten Wiederholung gezielt auf $wordsToReview.")
        }

        // Add specific articulatory tips from phonetic tips in the prompt
        prompt.phoneticFocusWords.firstOrNull()?.let { focus ->
            actionableTips.add("${focus.focusArea} (${focus.ipa}): ${focus.tipDe}")
        }

        if (cleanSpokenWords.size < cleanExpectedWords.size) {
            actionableTips.add("Sprechen Sie den Satz vollständig zu Ende, um den prosodischen Satzbogen (Intonationskurve) zu wahren.")
        } else {
            actionableTips.add("Gute Satzmelodie: Die Betonung auf den inhaltstragenden Satzgliedern war stimmig.")
        }

        return PronunciationAnalysisResult(
            promptId = prompt.id,
            overallScore = overallScore,
            accuracyScore = rawAccuracy,
            fluencyScore = fluencyScore,
            prosodyScore = prosodyScore,
            transcribedText = transcribedSpeech.ifBlank { "Keine Spracheingabe erkannt." },
            wordResults = wordResults,
            feedbackHeadlineDe = headline,
            cefrBandDe = cefrBand,
            actionableTipsDe = actionableTips
        )
    }

    private fun cleanPunctuation(word: String): String {
        return word.replace(Regex("[.,!?:;\"'„“()—]"), "").trim()
    }

    private fun calculateSimilarity(s1: String, s2: String): Float {
        if (s1 == s2) return 1.0f
        val maxLen = max(s1.length, s2.length)
        if (maxLen == 0) return 1.0f
        val dist = levenshteinDistance(s1, s2)
        return (1.0f - dist.toFloat() / maxLen).coerceIn(0f, 1f)
    }

    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        return dp[s1.length][s2.length]
    }
}
