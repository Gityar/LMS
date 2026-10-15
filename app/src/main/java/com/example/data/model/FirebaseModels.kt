package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirebaseUser(
    @DocumentId val userId: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "learner", // "learner", "instructor", "admin"
    val accountStatus: String = "active", // "active", "pending", "disabled"
    val hospital: String = "Ethiopian Neonatal Center",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirebaseCourse(
    @DocumentId val courseId: String = "",
    val courseTitle: String = "",
    val category: String = "Resuscitation",
    val description: String = "",
    val durationHours: Double = 4.0,
    val level: String = "Intermediate",
    val targetAudience: String = "Neonatal Nurses & Midwives",
    val instructorEmail: String = "",
    val isActive: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirebaseLesson(
    @DocumentId val lessonId: String = "",
    val courseId: String = "",
    val moduleNumber: Long = 1L,
    val lessonTitle: String = "",
    val contentType: String = "Text",
    val durationMins: Long = 20L,
    val contentBody: String = "",
    val videoUrl: String = "",
    val resourceUrl: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirebaseEnrollment(
    @DocumentId val enrollmentId: String = "",
    val userId: String = "",
    val courseId: String = "",
    val status: String = "enrolled", // "enrolled", "in_progress", "completed"
    val progressPercent: Double = 0.0,
    @ServerTimestamp val lastAccessed: Timestamp? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirebaseAttempt(
    @DocumentId val attemptId: String = "",
    val userId: String = "",
    val learnerName: String = "",
    val courseId: String = "",
    val assessmentId: String = "",
    val score: Double = 0.0,
    val total: Double = 0.0,
    val percentage: Double = 0.0,
    val passed: Boolean = false,
    val type: String = "quiz", // "quiz", "osce", "exam"
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirebaseFeedback(
    @DocumentId val feedbackId: String = "",
    val userId: String = "",
    val lessonId: String = "",
    val rating: Long = 5L,
    val comment: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirebaseActivityLog(
    @DocumentId val logId: String = "",
    val userId: String = "",
    val action: String = "",
    val deviceInfo: String = "Android Mobile App",
    @ServerTimestamp val createdAt: Timestamp? = null
)
