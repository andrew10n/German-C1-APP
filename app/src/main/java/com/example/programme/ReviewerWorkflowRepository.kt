package com.example.programme

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Entity(tableName = "review_assignments")
data class ReviewAssignmentEntity(
    @androidx.room.PrimaryKey val id: String,
    val attemptId: String,
    val reviewerId: String,
    val reviewerName: String,
    val status: String,
    val createdAt: Long,
    val dueAt: Long?,
    val completedAt: Long?
)

@Entity(tableName = "review_audit_events")
data class ReviewAuditEventEntity(
    @androidx.room.PrimaryKey val id: String,
    val assignmentId: String,
    val reviewId: String?,
    val actorId: String,
    val action: String,
    val details: String,
    val createdAt: Long
)

@Entity(tableName = "reassessment_links")
data class ReassessmentLinkEntity(
    @androidx.room.PrimaryKey val id: String,
    val sourceAttemptId: String,
    val sourceReviewId: String?,
    val reassessmentUnitId: String,
    val reassessmentTaskId: String,
    val status: String,
    val createdAt: Long,
    val completedAt: Long?
)

@Dao
interface ReviewerWorkflowDao {
    @Query("SELECT * FROM review_assignments ORDER BY createdAt DESC")
    fun observeAssignments(): Flow<List<ReviewAssignmentEntity>>

    @Query("SELECT * FROM review_audit_events ORDER BY createdAt DESC")
    fun observeAuditEvents(): Flow<List<ReviewAuditEventEntity>>

    @Query("SELECT * FROM reassessment_links ORDER BY createdAt DESC")
    fun observeReassessments(): Flow<List<ReassessmentLinkEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAssignment(assignment: ReviewAssignmentEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAuditEvent(event: ReviewAuditEventEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReassessment(link: ReassessmentLinkEntity)

    @Query("UPDATE review_assignments SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateAssignmentStatus(id: String, status: String, completedAt: Long?)

    @Query("UPDATE reassessment_links SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateReassessmentStatus(id: String, status: String, completedAt: Long?)

    @Query("DELETE FROM review_assignments")
    suspend fun deleteAssignments()

    @Query("DELETE FROM review_audit_events")
    suspend fun deleteAuditEvents()

    @Query("DELETE FROM reassessment_links")
    suspend fun deleteReassessments()
}

data class ReviewAssignment(
    val id: String,
    val attemptId: String,
    val reviewerId: String,
    val reviewerName: String,
    val status: String,
    val createdAt: Long,
    val dueAt: Long?,
    val completedAt: Long?
)

data class ReviewAuditEvent(
    val id: String,
    val assignmentId: String,
    val reviewId: String?,
    val actorId: String,
    val action: String,
    val details: String,
    val createdAt: Long
)

data class ReassessmentLink(
    val id: String,
    val sourceAttemptId: String,
    val sourceReviewId: String?,
    val reassessmentUnitId: String,
    val reassessmentTaskId: String,
    val status: String,
    val createdAt: Long,
    val completedAt: Long?
)

class ReviewerWorkflowRepository(private val dao: ReviewerWorkflowDao) {
    val assignments: Flow<List<ReviewAssignment>> = dao.observeAssignments().map { rows -> rows.map { it.toModel() } }
    val auditEvents: Flow<List<ReviewAuditEvent>> = dao.observeAuditEvents().map { rows -> rows.map { it.toModel() } }
    val reassessments: Flow<List<ReassessmentLink>> = dao.observeReassessments().map { rows -> rows.map { it.toModel() } }

    suspend fun assign(attemptId: String, reviewer: HumanReviewer, dueAt: Long?, now: Long): ReviewAssignment {
        require(attemptId.isNotBlank()) { "Ein Versuch ist für die Zuweisung erforderlich." }
        require(reviewer.id.isNotBlank() && reviewer.displayName.isNotBlank()) { "Reviewer-ID und Name sind erforderlich." }
        val assignment = ReviewAssignment(
            id = "assignment-${attemptId}-${reviewer.id}-${now}",
            attemptId = attemptId,
            reviewerId = reviewer.id.trim(),
            reviewerName = reviewer.displayName.trim(),
            status = "ASSIGNED",
            createdAt = now,
            dueAt = dueAt,
            completedAt = null
        )
        dao.insertAssignment(assignment.toEntity())
        dao.insertAuditEvent(ReviewAuditEventEntity(
            id = "audit-${assignment.id}-created",
            assignmentId = assignment.id,
            reviewId = null,
            actorId = reviewer.id,
            action = "ASSIGNED",
            details = "Review assignment created for attempt $attemptId",
            createdAt = now
        ))
        return assignment
    }

    suspend fun recordAudit(assignmentId: String, reviewId: String?, actorId: String, action: String, details: String, now: Long) {
        require(assignmentId.isNotBlank() && actorId.isNotBlank() && action.isNotBlank())
        dao.insertAuditEvent(ReviewAuditEventEntity(
            id = "audit-$assignmentId-$now-${action.hashCode()}",
            assignmentId = assignmentId,
            reviewId = reviewId,
            actorId = actorId,
            action = action.trim().uppercase().take(60),
            details = details.trim().take(2_000),
            createdAt = now
        ))
    }

    suspend fun linkReassessment(sourceAttemptId: String, sourceReviewId: String?, unitId: String, taskId: String, now: Long): ReassessmentLink {
        require(sourceAttemptId.isNotBlank() && unitId.isNotBlank() && taskId.isNotBlank())
        val link = ReassessmentLink(
            id = "reassessment-$sourceAttemptId-$now",
            sourceAttemptId = sourceAttemptId,
            sourceReviewId = sourceReviewId,
            reassessmentUnitId = unitId,
            reassessmentTaskId = taskId,
            status = "PENDING",
            createdAt = now,
            completedAt = null
        )
        dao.insertReassessment(link.toEntity())
        return link
    }

    suspend fun completeAssignment(id: String, now: Long) = dao.updateAssignmentStatus(id, "COMPLETED", now)
    suspend fun completeReassessment(id: String, now: Long) = dao.updateReassessmentStatus(id, "COMPLETED", now)

    suspend fun deleteAll() {
        dao.deleteAuditEvents()
        dao.deleteReassessments()
        dao.deleteAssignments()
    }

    private fun ReviewAssignment.toEntity() = ReviewAssignmentEntity(id, attemptId, reviewerId, reviewerName, status, createdAt, dueAt, completedAt)
    private fun ReviewAssignmentEntity.toModel() = ReviewAssignment(id, attemptId, reviewerId, reviewerName, status, createdAt, dueAt, completedAt)
    private fun ReviewAuditEventEntity.toModel() = ReviewAuditEvent(id, assignmentId, reviewId, actorId, action, details, createdAt)
    private fun ReassessmentLink.toEntity() = ReassessmentLinkEntity(id, sourceAttemptId, sourceReviewId, reassessmentUnitId, reassessmentTaskId, status, createdAt, completedAt)
    private fun ReassessmentLinkEntity.toModel() = ReassessmentLink(id, sourceAttemptId, sourceReviewId, reassessmentUnitId, reassessmentTaskId, status, createdAt, completedAt)
}
