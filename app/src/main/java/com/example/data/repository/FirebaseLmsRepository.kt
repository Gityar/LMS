package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirebaseLmsRepository(
    val db: FirebaseFirestore
) {
    // Convenience constructor: resolves named database ID from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    // --- USERS ---
    suspend fun getUser(userId: String): FirebaseUser? {
        if (userId.isBlank()) return null
        val doc = db.collection("users").document(userId).get().await()
        return doc.toObject(FirebaseUser::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
    }

    suspend fun saveUser(user: FirebaseUser) {
        require(user.userId.isNotBlank()) { "User ID must not be blank" }
        db.collection("users").document(user.userId).set(user).await()
    }

    // --- COURSES ---
    fun observeCourses(): Flow<List<FirebaseCourse>> {
        return db.collection("courses")
            .snapshots()
            .map { snap ->
                snap.toObjects(FirebaseCourse::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
    }

    suspend fun saveCourse(course: FirebaseCourse) {
        require(course.courseId.isNotBlank()) { "Course ID must not be blank" }
        db.collection("courses").document(course.courseId).set(course).await()
    }

    // --- LESSONS ---
    fun observeLessons(courseId: String): Flow<List<FirebaseLesson>> {
        return db.collection("lessons")
            .whereEqualTo("courseId", courseId)
            .snapshots()
            .map { snap ->
                snap.toObjects(FirebaseLesson::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedBy { it.moduleNumber }
            }
    }

    suspend fun saveLesson(lesson: FirebaseLesson) {
        require(lesson.lessonId.isNotBlank()) { "Lesson ID must not be blank" }
        db.collection("lessons").document(lesson.lessonId).set(lesson).await()
    }

    // --- ENROLLMENTS ---
    fun observeEnrollments(userId: String): Flow<List<FirebaseEnrollment>> {
        return db.collection("enrollments")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snap ->
                snap.toObjects(FirebaseEnrollment::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
    }

    suspend fun saveEnrollment(enrollment: FirebaseEnrollment) {
        require(enrollment.enrollmentId.isNotBlank()) { "Enrollment ID must not be blank" }
        db.collection("enrollments").document(enrollment.enrollmentId).set(enrollment).await()
    }

    // --- ATTEMPTS & OSCE ---
    fun observeAttempts(userId: String): Flow<List<FirebaseAttempt>> {
        return db.collection("attempts")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snap ->
                snap.toObjects(FirebaseAttempt::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
    }

    suspend fun saveAttempt(attempt: FirebaseAttempt) {
        require(attempt.attemptId.isNotBlank()) { "Attempt ID must not be blank" }
        db.collection("attempts").document(attempt.attemptId).set(attempt).await()
    }

    // --- FEEDBACK ---
    suspend fun saveFeedback(feedback: FirebaseFeedback) {
        require(feedback.feedbackId.isNotBlank()) { "Feedback ID must not be blank" }
        db.collection("feedback").document(feedback.feedbackId).set(feedback).await()
    }

    // --- ACTIVITY AUDIT LOG ---
    suspend fun logActivity(log: FirebaseActivityLog) {
        require(log.logId.isNotBlank()) { "Log ID must not be blank" }
        db.collection("activityLogs").document(log.logId).set(log).await()
    }
}
