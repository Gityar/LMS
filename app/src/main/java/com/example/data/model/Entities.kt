package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val role: String, // "admin", "instructor", "learner"
    val registrationDate: String,
    val accountStatus: String, // "active", "disabled"
    val lastLogin: String = "",
    val passwordResetToken: String = "",
    val tokenExpiry: String = ""
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val courseId: String,
    val courseTitle: String,
    val category: String,
    val instructorId: String,
    val instructorName: String,
    val description: String,
    val status: String = "active",
    val createdDate: String,
    val totalModules: Int = 1
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val lessonId: String,
    val courseId: String,
    val moduleNumber: Int = 1,
    val lessonTitle: String,
    val contentType: String = "Text", // "Text", "Video", "PDF"
    val durationMins: Int = 30,
    val orderIndex: Int = 1,
    val contentBody: String = "",
    val videoUrl: String = "",
    val resourceUrl: String = ""
)

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey val enrollmentId: String,
    val learnerId: String,
    val learnerName: String,
    val courseId: String,
    val enrollmentDate: String,
    val completionStatus: String = "In Progress", // "In Progress", "Completed"
    val progressPercentage: Int = 0,
    val lastAccessed: String
)

@Entity(tableName = "resources")
data class ResourceEntity(
    @PrimaryKey val resourceId: String,
    val lessonId: String,
    val resourceType: String,
    val fileName: String,
    val mimeType: String,
    val fileId: String,
    val fileUrl: String,
    val uploadedBy: String,
    val uploadedDate: String,
    val status: String = "active"
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey val assignmentId: String,
    val courseId: String,
    val moduleNumber: Int = 1,
    val assignmentTitle: String,
    val instructions: String = "",
    val dueDate: String = "",
    val allowedFileTypes: String = "PDF, DOCX, TXT",
    val status: String = "active",
    val createdBy: String,
    val createdDate: String
)

@Entity(tableName = "submissions")
data class SubmissionEntity(
    @PrimaryKey val submissionId: String,
    val assignmentId: String,
    val learnerId: String,
    val learnerName: String,
    val courseId: String,
    val fileName: String,
    val mimeType: String,
    val fileId: String = "",
    val fileUrl: String = "",
    val submittedDate: String,
    val grade: String = "",
    val feedback: String = "",
    val gradedBy: String = "",
    val gradedDate: String = "",
    val status: String = "Submitted" // "Submitted", "Graded"
)

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey val assessmentId: String,
    val courseId: String,
    val moduleNumber: Int = 1,
    val assessmentTitle: String,
    val description: String = "",
    val passingScore: Int = 70,
    val timeLimitMins: Int = 30,
    val attemptsAllowed: Int = 3,
    val status: String = "active",
    val createdBy: String,
    val createdDate: String
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val questionId: String,
    val assessmentId: String,
    val questionType: String = "MCQ",
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String, // "A", "B", "C", "D"
    val points: Int = 10,
    val explanation: String = "",
    val orderIndex: Int = 1
)

@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey val attemptId: String,
    val assessmentId: String,
    val learnerId: String,
    val learnerName: String,
    val courseId: String,
    val attemptNumber: Int = 1,
    val score: String,
    val percentage: Int,
    val passed: Boolean,
    val submittedDate: String,
    val answersJson: String = "{}"
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val activityId: String,
    val userEmail: String,
    val actionType: String,
    val timestamp: String,
    val ipAddress: String = "Android-Device",
    val statusMessage: String = ""
)

@Entity(tableName = "password_resets")
data class PasswordResetEntity(
    @PrimaryKey val resetId: String,
    val userEmail: String,
    val verificationToken: String,
    val requestedAt: String,
    val expiresAt: String,
    val isUsed: Boolean = false,
    val resetCompletedAt: String = ""
)
