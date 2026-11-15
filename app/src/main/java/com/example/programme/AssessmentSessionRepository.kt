package com.example.programme

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.Index
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.BaselineProfileEntity
import com.example.data.local.entity.DailyStreakEntity
import com.example.data.local.entity.ProficiencySnapshotEntity
import com.example.data.local.entity.VocabularyItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

/** Persisted state for a resumable assessment. Responses are stored as evidence only after submission. */
@Entity(
    tableName = "assessment_sessions",
    indices = [Index(value = ["completed", "updatedAt"])]
)
data class AssessmentSessionEntity(
    @androidx.room.PrimaryKey val id: String,
    val kind: String,
    val createdAt: Long,
    val durationMinutes: Int,
    val tasksJson: String,
    val responsesJson: String,
    val currentIndex: Int,
    val completed: Boolean,
    val updatedAt: Long
)

@Dao
interface AssessmentSessionDao {
    @Query("SELECT * FROM assessment_sessions ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<AssessmentSessionEntity>>

    @Query("SELECT * FROM assessment_sessions WHERE completed = 0 ORDER BY updatedAt DESC LIMIT 1")
    fun observeActive(): Flow<AssessmentSessionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: AssessmentSessionEntity)

    @Query("UPDATE assessment_sessions SET completed = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun complete(id: String, updatedAt: Long)

    @Query("DELETE FROM assessment_sessions")
    suspend fun deleteAll()
}

data class PersistedAssessment(
    val session: AssessmentSession,
    val responses: Map<String, String>,
    val currentIndex: Int,
    val completed: Boolean,
    val updatedAt: Long
)

class AssessmentSessionRepository(private val dao: AssessmentSessionDao) {
    val active: Flow<PersistedAssessment?> = dao.observeActive().map { it?.toModel() }
    val history: Flow<List<PersistedAssessment>> = dao.observeAll().map { rows -> rows.map { it.toModel() } }

    suspend fun save(
        session: AssessmentSession,
        responses: Map<String, String>,
        currentIndex: Int,
        completed: Boolean,
        now: Long
    ) {
        dao.upsert(
            AssessmentSessionEntity(
                id = session.id,
                kind = session.kind.name,
                createdAt = session.createdAt,
                durationMinutes = session.durationMinutes,
                tasksJson = encodeTasks(session.tasks),
                responsesJson = encodeResponses(responses),
                currentIndex = currentIndex.coerceAtLeast(0),
                completed = completed,
                updatedAt = now
            )
        )
    }

    suspend fun markCompleted(id: String, now: Long) = dao.complete(id, now)
    suspend fun deleteAll() = dao.deleteAll()

    private fun AssessmentSessionEntity.toModel(): PersistedAssessment {
        val tasksArray = JSONArray(tasksJson)
        val tasks = List(tasksArray.length()) { index ->
            val value = tasksArray.getJSONObject(index)
            val task = DeliveredAssessmentTask(
                id = value.getString("id"),
                unitId = value.getString("unitId"),
                taskId = value.getString("taskId"),
                prompt = value.getString("prompt"),
                evidence = AssessmentEvidence.valueOf(value.getString("evidence")),
                context = ContextTrack.valueOf(value.getString("context")),
                unfamiliar = value.getBoolean("unfamiliar"),
                variationId = value.optString("variationId", "legacy"),
                taskVersion = value.optInt("taskVersion", 1),
                rubricVersion = value.optInt("rubricVersion", 1)
            )
            task.copy(
                evidence = if (task.taskVersion >= 3 && task.evidence == AssessmentEvidence.AUDIO_RESPONSE) {
                    AssessmentEvidence.AUDIO_RESPONSE
                } else {
                    AssessmentEvidence.TEXT_RESPONSE
                },
                unfamiliar = false
            )
        }
        val responseObject = JSONObject(responsesJson)
        val responses = responseObject.keys().asSequence().associateWith { responseObject.getString(it) }
        return PersistedAssessment(
            session = AssessmentSession(
                id = id,
                kind = AssessmentSessionKind.valueOf(kind),
                createdAt = createdAt,
                durationMinutes = durationMinutes,
                tasks = tasks
            ),
            responses = responses,
            currentIndex = currentIndex,
            completed = completed,
            updatedAt = updatedAt
        )
    }

    private fun encodeTasks(tasks: List<DeliveredAssessmentTask>): String = JSONArray().apply {
        tasks.forEach { task ->
            put(JSONObject().apply {
                put("id", task.id)
                put("unitId", task.unitId)
                put("taskId", task.taskId)
                put("prompt", task.prompt)
                put("evidence", task.evidence.name)
                put("context", task.context.name)
                put("unfamiliar", task.unfamiliar)
                put("variationId", task.variationId)
                put("taskVersion", task.taskVersion)
                put("rubricVersion", task.rubricVersion)
            })
        }
    }.toString()

    private fun encodeResponses(responses: Map<String, String>): String = JSONObject().apply {
        responses.forEach { (taskId, response) -> put(taskId, response) }
    }.toString()
}

/** User-controlled, local-only export payload. It is deliberately plain JSON and contains no secrets. */
object ProgrammeExport {
    fun create(
        attempts: List<Attempt>,
        experiences: List<RealLifeExperience>,
        assessments: List<PersistedAssessment>,
        externalListeningNotes: List<ExternalListeningDraft> = emptyList(),
        practiceDrafts: List<PracticeDraft> = emptyList(),
        feedbackChallenges: List<FeedbackChallenge> = emptyList(),
        humanReviews: List<HumanReviewSubmission> = emptyList(),
        reviewAssignments: List<ReviewAssignment> = emptyList(),
        reviewAuditEvents: List<ReviewAuditEvent> = emptyList(),
        reassessmentLinks: List<ReassessmentLink> = emptyList(),
        baselineProfile: BaselineProfileEntity? = null,
        vocabularyItems: List<VocabularyItemEntity> = emptyList(),
        proficiencySnapshots: List<ProficiencySnapshotEntity> = emptyList(),
        dailyStreak: DailyStreakEntity? = null,
        preferences: ProgrammePreferences = ProgrammePreferences(),
        bookmarkedUnitIds: Set<String> = emptySet(),
        assistedUnitIds: Set<String> = emptySet(),
        lastUnitId: String? = null,
        lastTaskId: String = "retrieval",
        onboardingComplete: Boolean = false
    ): String = JSONObject().apply {
        put("format", "c1-deutsch-local-export-v2")
        put("createdAt", System.currentTimeMillis())
        put("scope", "User-initiated export of local learner records.")
        put("media", JSONObject().apply {
            put("audioBytesIncluded", false)
            put("bookAudioContentUriIncluded", false)
            put("credentialsIncluded", false)
        })
        put("preferences", JSONObject().apply {
            put("theme", preferences.theme.name)
            put("language", preferences.language.name)
            put("learnerName", preferences.learnerName)
            put("compact", preferences.compact)
            put("dailyMinutes", preferences.dailyMinutes)
            put("lastUnitId", lastUnitId)
            put("lastTaskId", lastTaskId)
            put("onboardingComplete", onboardingComplete)
            put("bookmarkedUnitIds", JSONArray(bookmarkedUnitIds.sorted()))
            put("assistedUnitIds", JSONArray(assistedUnitIds.sorted()))
        })
        put("attempts", JSONArray().apply {
            attempts.forEach { attempt ->
                put(JSONObject().apply {
                    put("id", attempt.id)
                    put("unitId", attempt.unitId)
                    put("taskId", attempt.taskId)
                    put("timestamp", attempt.timestamp)
                    put("response", attempt.response)
                    put("source", attempt.source.name)
                    put("context", attempt.context.name)
                    put("assisted", attempt.assisted)
                    put("unfamiliar", attempt.unfamiliar)
                    put("independentProduction", attempt.independentProduction)
                    put("scores", JSONObject().apply {
                        attempt.scores.forEach { (dimension, score) -> put(dimension.name, score) }
                    })
                    put("errors", JSONArray(attempt.errors))
                    put("taskVersion", attempt.taskVersion)
                    put("rubricVersion", attempt.rubricVersion)
                })
            }
        })
        put("realLifeEntries", JSONArray().apply {
            experiences.forEach { entry ->
                put(JSONObject().apply {
                    put("id", entry.id)
                    put("createdAt", entry.createdAt)
                    put("context", entry.context.name)
                    put("situation", entry.situation)
                    put("heard", entry.heard)
                    put("intended", entry.intended)
                    put("said", entry.said)
                    put("issueType", entry.issueType.name)
                    put("reviewStage", entry.reviewStage)
                    put("nextReviewAt", entry.nextReviewAt)
                    put("reviewResponse", entry.reviewResponse)
                })
            }
        })
        put("assessmentSessions", JSONArray().apply {
            assessments.forEach { persisted ->
                put(JSONObject().apply {
                    put("id", persisted.session.id)
                    put("kind", persisted.session.kind.name)
                    put("createdAt", persisted.session.createdAt)
                    put("durationMinutes", persisted.session.durationMinutes)
                    put("currentIndex", persisted.currentIndex)
                    put("completed", persisted.completed)
                    put("updatedAt", persisted.updatedAt)
                    put("tasks", JSONArray().apply {
                        persisted.session.tasks.forEach { task ->
                            put(JSONObject().apply {
                                put("id", task.id)
                                put("unitId", task.unitId)
                                put("taskId", task.taskId)
                                put("prompt", task.prompt)
                                put("evidence", task.evidence.name)
                                put("context", task.context.name)
                                put("unfamiliar", task.unfamiliar)
                                put("variationId", task.variationId)
                                put("taskVersion", task.taskVersion)
                                put("rubricVersion", task.rubricVersion)
                            })
                        }
                    })
                    put(
                        "responses",
                        JSONObject(persisted.responses.filterKeys {
                            !it.startsWith(ASSESSMENT_AUDIO_RESPONSE_PREFIX)
                        })
                    )
                })
            }
        })
        put("feedbackChallenges", JSONArray().apply {
            feedbackChallenges.forEach { challenge ->
                put(JSONObject().apply {
                    put("id", challenge.id)
                    put("claimId", challenge.claimId)
                    put("attemptId", challenge.attemptId)
                    put("unitId", challenge.unitId)
                    put("taskId", challenge.taskId)
                    put("context", challenge.context.name)
                    put("responseExcerpt", challenge.responseExcerpt)
                    put("claimType", challenge.claimType)
                    put("proposedFeedback", challenge.proposedFeedback)
                    put("learnerDecision", challenge.learnerDecision)
                    put("correctedFeedback", challenge.correctedFeedback)
                    put("status", challenge.status.name)
                    put("createdAt", challenge.createdAt)
                    put("updatedAt", challenge.updatedAt)
                })
            }
        })
        put("humanReviews", JSONArray().apply {
            humanReviews.forEach { review ->
                put(JSONObject().apply {
                    put("id", review.id)
                    put("attemptId", review.attemptId)
                    put("reviewerId", review.reviewer.id)
                    put("reviewerName", review.reviewer.displayName)
                    put("reviewerVerified", review.reviewer.verified)
                    put("rubricVersion", review.rubricVersion)
                    put("scores", JSONObject().apply {
                        review.scores.forEach { (dimension, score) -> put(dimension.name, score) }
                    })
                    put("comment", review.comment)
                    put("createdAt", review.createdAt)
                })
            }
        })
        put("reviewAssignments", JSONArray().apply {
            reviewAssignments.forEach { assignment ->
                put(JSONObject().apply {
                    put("id", assignment.id)
                    put("attemptId", assignment.attemptId)
                    put("reviewerId", assignment.reviewerId)
                    put("reviewerName", assignment.reviewerName)
                    put("status", assignment.status)
                    put("createdAt", assignment.createdAt)
                    put("dueAt", assignment.dueAt)
                    put("completedAt", assignment.completedAt)
                })
            }
        })
        put("reviewAuditEvents", JSONArray().apply {
            reviewAuditEvents.forEach { event ->
                put(JSONObject().apply {
                    put("id", event.id)
                    put("assignmentId", event.assignmentId)
                    put("reviewId", event.reviewId)
                    put("actorId", event.actorId)
                    put("action", event.action)
                    put("details", event.details)
                    put("createdAt", event.createdAt)
                })
            }
        })
        put("reassessmentLinks", JSONArray().apply {
            reassessmentLinks.forEach { link ->
                put(JSONObject().apply {
                    put("id", link.id)
                    put("sourceAttemptId", link.sourceAttemptId)
                    put("sourceReviewId", link.sourceReviewId)
                    put("reassessmentUnitId", link.reassessmentUnitId)
                    put("reassessmentTaskId", link.reassessmentTaskId)
                    put("status", link.status)
                    put("createdAt", link.createdAt)
                    put("completedAt", link.completedAt)
                })
            }
        })
        put("legacyData", JSONObject().apply {
            put("baselineProfileUnvalidated", baselineProfile?.let { profile ->
                JSONObject().apply {
                    put("id", profile.id)
                    put("listeningScore", profile.listeningScore)
                    put("speakingScore", profile.speakingScore)
                    put("interactionScore", profile.interactionScore)
                    put("readingScore", profile.readingScore)
                    put("writingScore", profile.writingScore)
                    put("grammarScore", profile.grammarScore)
                    put("vocabularyScore", profile.vocabularyScore)
                    put("mediationScore", profile.mediationScore)
                    put("overallC1Score", profile.overallC1Score)
                    put("functionalReadinessPct", profile.functionalReadinessPct)
                    put("primaryLimitingFactor", profile.primaryLimitingFactor)
                    put("recommendedStartingEngine", profile.recommendedStartingEngine)
                    put("isCompleted", profile.isCompleted)
                    put("assessedAtTimestamp", profile.assessedAtTimestamp)
                    put("interpretation", "Legacy record; not validated or a certification result.")
                }
            })
            put("vocabularyItems", JSONArray().apply {
                vocabularyItems.forEach { item ->
                    put(JSONObject().apply {
                        put("id", item.id)
                        put("germanCollocation", item.germanCollocation)
                        put("meaningDe", item.meaningDe)
                        put("exampleSentence", item.exampleSentence)
                        put("register", item.register)
                        put("category", item.category)
                        put("stage", item.stage)
                        put("easeFactor", item.easeFactor)
                        put("intervalDays", item.intervalDays)
                        put("repetitions", item.repetitions)
                        put("lastReviewedTimestamp", item.lastReviewedTimestamp)
                        put("nextReviewTimestamp", item.nextReviewTimestamp)
                        put("consecutiveErrors", item.consecutiveErrors)
                        put("isMastered", item.isMastered)
                        put("sourceUnitId", item.sourceUnitId)
                    })
                }
            })
            put("proficiencySnapshots", JSONArray().apply {
                proficiencySnapshots.forEach { snapshot ->
                    put(JSONObject().apply {
                        put("id", snapshot.id)
                        put("timestamp", snapshot.timestamp)
                        put("periodLabel", snapshot.periodLabel)
                        put("overallScore", snapshot.overallScore)
                        put("functionalScore", snapshot.functionalScore)
                        put("listeningScore", snapshot.listeningScore)
                        put("speakingScore", snapshot.speakingScore)
                        put("readingScore", snapshot.readingScore)
                        put("writingScore", snapshot.writingScore)
                        put("grammarScore", snapshot.grammarScore)
                        put("vocabularyScore", snapshot.vocabularyScore)
                        put("interpretation", "Legacy record; not validated or a certification result.")
                    })
                }
            })
            put("dailyStreak", dailyStreak?.let { streak ->
                JSONObject().apply {
                    put("id", streak.id)
                    put("currentStreak", streak.currentStreak)
                    put("longestStreak", streak.longestStreak)
                    put("lastActiveEpochDay", streak.lastActiveEpochDay)
                    put("streakFreezesAvailable", streak.streakFreezesAvailable)
                    put("totalSessionsCompleted", streak.totalSessionsCompleted)
                    put("activeDaysBitmask", streak.activeDaysBitmask)
                    put("lastActivityTimestamp", streak.lastActivityTimestamp)
                }
            })
        })
        put("externalListeningNotes", JSONArray().apply {
            externalListeningNotes.forEach { draft ->
                put(JSONObject().apply {
                    put("sourceId", draft.sourceId)
                    put("itemReference", draft.itemReference)
                    put("firstPass", draft.firstPass)
                    put("secondPass", draft.secondPass)
                    put("comparisonOpened", draft.comparisonOpened)
                    put("updatedAt", draft.updatedAt)
                })
            }
        })
        put("practiceDrafts", JSONArray().apply {
            practiceDrafts.forEach { draft ->
                put(JSONObject().apply {
                    put("unitId", draft.unitId)
                    put("taskId", draft.taskId)
                    put("context", draft.context.name)
                    put("response", draft.response)
                    put("updatedAt", draft.updatedAt)
                })
            }
        })
    }.toString(2)
}
