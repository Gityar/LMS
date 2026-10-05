package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LmsDao {
    // --- USERS ---
    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY registrationDate DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    // --- COURSES ---
    @Query("SELECT * FROM courses ORDER BY createdDate DESC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE courseId = :courseId LIMIT 1")
    suspend fun getCourseById(courseId: String): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Query("DELETE FROM courses WHERE courseId = :courseId")
    suspend fun deleteCourseById(courseId: String)

    // --- LESSONS ---
    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY orderIndex ASC, moduleNumber ASC")
    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getLessonById(lessonId: String): LessonEntity?

    @Query("SELECT COUNT(*) FROM lessons")
    suspend fun getTotalLessonCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Query("DELETE FROM lessons WHERE lessonId = :lessonId")
    suspend fun deleteLessonById(lessonId: String)

    @Query("DELETE FROM lessons WHERE courseId = :courseId")
    suspend fun deleteLessonsForCourse(courseId: String)

    // --- ENROLLMENTS ---
    @Query("SELECT * FROM enrollments WHERE learnerId = :learnerId ORDER BY enrollmentDate DESC")
    fun getEnrollmentsForLearner(learnerId: String): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments ORDER BY enrollmentDate DESC")
    fun getAllEnrollments(): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments WHERE learnerId = :learnerId AND courseId = :courseId LIMIT 1")
    suspend fun getEnrollment(learnerId: String, courseId: String): EnrollmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)

    @Update
    suspend fun updateEnrollment(enrollment: EnrollmentEntity)

    // --- RESOURCES ---
    @Query("SELECT * FROM resources WHERE lessonId = :lessonId ORDER BY uploadedDate DESC")
    fun getResourcesForLesson(lessonId: String): Flow<List<ResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: ResourceEntity)

    @Query("DELETE FROM resources WHERE resourceId = :resourceId")
    suspend fun deleteResourceById(resourceId: String)

    // --- ASSIGNMENTS ---
    @Query("SELECT * FROM assignments WHERE courseId = :courseId ORDER BY createdDate DESC")
    fun getAssignmentsForCourse(courseId: String): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE assignmentId = :assignmentId LIMIT 1")
    suspend fun getAssignmentById(assignmentId: String): AssignmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity)

    @Query("DELETE FROM assignments WHERE assignmentId = :assignmentId")
    suspend fun deleteAssignmentById(assignmentId: String)

    // --- SUBMISSIONS ---
    @Query("SELECT * FROM submissions WHERE assignmentId = :assignmentId ORDER BY submittedDate DESC")
    fun getSubmissionsForAssignment(assignmentId: String): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE learnerId = :learnerId ORDER BY submittedDate DESC")
    fun getSubmissionsForLearner(learnerId: String): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions ORDER BY submittedDate DESC")
    fun getAllSubmissions(): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE submissionId = :submissionId LIMIT 1")
    suspend fun getSubmissionById(submissionId: String): SubmissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: SubmissionEntity)

    @Update
    suspend fun updateSubmission(submission: SubmissionEntity)

    // --- ASSESSMENTS ---
    @Query("SELECT * FROM assessments WHERE courseId = :courseId ORDER BY createdDate DESC")
    fun getAssessmentsForCourse(courseId: String): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments WHERE assessmentId = :assessmentId LIMIT 1")
    suspend fun getAssessmentById(assessmentId: String): AssessmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity)

    @Query("DELETE FROM assessments WHERE assessmentId = :assessmentId")
    suspend fun deleteAssessmentById(assessmentId: String)

    // --- QUESTIONS ---
    @Query("SELECT * FROM questions WHERE assessmentId = :assessmentId ORDER BY orderIndex ASC")
    fun getQuestionsForAssessment(assessmentId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE assessmentId = :assessmentId ORDER BY orderIndex ASC")
    suspend fun getQuestionsForAssessmentList(assessmentId: String): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE questionId = :questionId")
    suspend fun deleteQuestionById(questionId: String)

    // --- ATTEMPTS ---
    @Query("SELECT * FROM attempts WHERE assessmentId = :assessmentId AND learnerId = :learnerId ORDER BY attemptNumber DESC")
    fun getAttemptsForAssessmentAndLearner(assessmentId: String, learnerId: String): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts WHERE assessmentId = :assessmentId AND learnerId = :learnerId ORDER BY attemptNumber DESC")
    suspend fun getAttemptsListForAssessmentAndLearner(assessmentId: String, learnerId: String): List<AttemptEntity>

    @Query("SELECT * FROM attempts WHERE learnerId = :learnerId ORDER BY submittedDate DESC")
    fun getAttemptsForLearner(learnerId: String): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts ORDER BY submittedDate DESC")
    fun getAllAttempts(): Flow<List<AttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: AttemptEntity)

    // --- ACTIVITY LOGS ---
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 200")
    fun getAllLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ActivityLogEntity)

    // --- PASSWORD RESETS ---
    @Query("SELECT * FROM password_resets WHERE verificationToken = :token LIMIT 1")
    suspend fun getResetByToken(token: String): PasswordResetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReset(reset: PasswordResetEntity)

    @Update
    suspend fun updateReset(reset: PasswordResetEntity)
}
