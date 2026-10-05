package com.example.data.repository

import com.example.data.dao.LmsDao
import com.example.data.model.*
import com.example.data.util.PasswordHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

data class AuthResult(
    val success: Boolean,
    val message: String,
    val user: UserEntity? = null
)

data class QuizResult(
    val success: Boolean,
    val message: String,
    val earnedPoints: Int = 0,
    val totalPoints: Int = 0,
    val percentage: Int = 0,
    val passed: Boolean = false
)

data class DashboardStats(
    val totalCourses: Int = 0,
    val totalLessons: Int = 0,
    val totalUsers: Int = 0,
    val myCourses: Int = 0,
    val myEnrollments: Int = 0,
    val mySubmissions: Int = 0,
    val myAttempts: Int = 0,
    val totalSubmissions: Int = 0,
    val totalAttempts: Int = 0,
    val avgScore: String = "—"
)

class LmsRepository(private val dao: LmsDao) {

    // --- AUTHENTICATION ---
    suspend fun login(email: String, pwd: String): AuthResult {
        val em = email.trim().lowercase()
        val user = dao.getUserByEmail(em) ?: run {
            logActivity(em, "LOGIN_FAILED", "User email not found.")
            return AuthResult(false, "Invalid email or password.")
        }
        if (user.accountStatus.lowercase() != "active") {
            logActivity(em, "LOGIN_BLOCKED", "Account is disabled.")
            return AuthResult(false, "Your account is currently disabled.")
        }
        val hashed = PasswordHelper.hashPassword(pwd)
        if (user.passwordHash != hashed) {
            logActivity(em, "LOGIN_FAILED", "Incorrect password.")
            return AuthResult(false, "Invalid email or password.")
        }
        val updatedUser = user.copy(lastLogin = PasswordHelper.formattedNow())
        dao.updateUser(updatedUser)
        logActivity(em, "USER_LOGIN", "User logged in successfully.")
        return AuthResult(true, "Welcome back, ${user.fullName}!", updatedUser)
    }

    suspend fun register(name: String, email: String, role: String, pwd: String): AuthResult {
        val em = email.trim().lowercase()
        val r = role.lowercase()
        if (r !in listOf("learner", "instructor", "admin")) {
            return AuthResult(false, "Invalid role selected.")
        }
        val existing = dao.getUserByEmail(em)
        if (existing != null) {
            return AuthResult(false, "An account with this email already exists.")
        }
        val newUser = UserEntity(
            userId = PasswordHelper.genId("USR"),
            fullName = name.trim(),
            email = em,
            passwordHash = PasswordHelper.hashPassword(pwd),
            role = r,
            registrationDate = PasswordHelper.formattedNow(),
            accountStatus = "active"
        )
        dao.insertUser(newUser)
        logActivity(em, "USER_REGISTER", "Registered as $r")
        return AuthResult(true, "Registration successful! You can now log in.", newUser)
    }

    suspend fun requestPasswordReset(email: String): Pair<Boolean, String> {
        val em = email.trim().lowercase()
        val user = dao.getUserByEmail(em)
            ?: return Pair(true, "If that email exists in our system, a password reset token has been generated.")
        val token = PasswordHelper.genToken().substring(0, 8).uppercase()
        val updated = user.copy(
            passwordResetToken = token,
            tokenExpiry = PasswordHelper.nowIso()
        )
        dao.updateUser(updated)
        dao.insertReset(
            PasswordResetEntity(
                resetId = PasswordHelper.genId("RST"),
                userEmail = em,
                verificationToken = token,
                requestedAt = PasswordHelper.formattedNow(),
                expiresAt = "2 Hours"
            )
        )
        logActivity(em, "RESET_REQUESTED", "Reset token generated: $token")
        return Pair(true, "Password reset token generated: $token. You can use it in the reset screen.")
    }

    suspend fun resetPassword(token: String, newPwd: String): Pair<Boolean, String> {
        val tk = token.trim()
        val users = dao.getAllUsers().first()
        val user = users.find { it.passwordResetToken.equals(tk, ignoreCase = true) }
            ?: return Pair(false, "Invalid or expired password reset token.")
        val updated = user.copy(
            passwordHash = PasswordHelper.hashPassword(newPwd),
            passwordResetToken = ""
        )
        dao.updateUser(updated)
        logActivity(user.email, "RESET_COMPLETED", "Password changed successfully")
        return Pair(true, "Password reset successful! You may now log in.")
    }

    // --- USERS & ADMIN ---
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun updateUserStatus(userId: String, newStatus: String, newRole: String, adminEmail: String): Boolean {
        val user = dao.getUserById(userId) ?: return false
        val updated = user.copy(accountStatus = newStatus, role = newRole)
        dao.updateUser(updated)
        logActivity(adminEmail, "UPDATE_USER", "Updated user $userId to status=$newStatus, role=$newRole")
        return true
    }

    // --- ACTIVITY LOGS ---
    fun getAllLogs(): Flow<List<ActivityLogEntity>> = dao.getAllLogs()

    suspend fun logActivity(email: String, action: String, message: String) {
        val log = ActivityLogEntity(
            activityId = PasswordHelper.genId("ACT"),
            userEmail = email.ifBlank { "anonymous" },
            actionType = action,
            timestamp = PasswordHelper.formattedNow(),
            ipAddress = "Android-Device",
            statusMessage = message
        )
        dao.insertLog(log)
    }

    // --- COURSES ---
    fun getAllCourses(): Flow<List<CourseEntity>> = dao.getAllCourses()

    suspend fun getCourseById(courseId: String): CourseEntity? = dao.getCourseById(courseId)

    suspend fun createCourse(title: String, category: String, description: String, user: UserEntity): CourseEntity {
        val course = CourseEntity(
            courseId = PasswordHelper.genId("CRS"),
            courseTitle = title.trim(),
            category = category.trim().ifBlank { "General" },
            instructorId = user.userId,
            instructorName = user.fullName,
            description = description.trim(),
            status = "active",
            createdDate = PasswordHelper.formattedNow()
        )
        dao.insertCourse(course)
        logActivity(user.email, "CREATE_COURSE", "Created course: ${course.courseTitle}")
        return course
    }

    suspend fun updateCourse(courseId: String, title: String, category: String, description: String, user: UserEntity): Boolean {
        val current = dao.getCourseById(courseId) ?: return false
        val updated = current.copy(
            courseTitle = title.trim().ifBlank { current.courseTitle },
            category = category.trim().ifBlank { current.category },
            description = description.trim()
        )
        dao.updateCourse(updated)
        logActivity(user.email, "UPDATE_COURSE", "Updated course: ${updated.courseTitle}")
        return true
    }

    suspend fun deleteCourse(courseId: String, user: UserEntity): Boolean {
        val current = dao.getCourseById(courseId) ?: return false
        dao.deleteCourseById(courseId)
        dao.deleteLessonsForCourse(courseId)
        logActivity(user.email, "DELETE_COURSE", "Deleted course: ${current.courseTitle}")
        return true
    }

    // --- ENROLLMENTS ---
    fun getEnrollmentsForLearner(learnerId: String): Flow<List<EnrollmentEntity>> =
        dao.getEnrollmentsForLearner(learnerId)

    suspend fun enrollInCourse(courseId: String, user: UserEntity): Boolean {
        val existing = dao.getEnrollment(user.userId, courseId)
        if (existing != null) return true
        val enrollment = EnrollmentEntity(
            enrollmentId = PasswordHelper.genId("ENR"),
            learnerId = user.userId,
            learnerName = user.fullName,
            courseId = courseId,
            enrollmentDate = PasswordHelper.formattedNow(),
            completionStatus = "In Progress",
            progressPercentage = 0,
            lastAccessed = PasswordHelper.formattedNow()
        )
        dao.insertEnrollment(enrollment)
        logActivity(user.email, "ENROLL_COURSE", "Enrolled in course: $courseId")
        return true
    }

    // --- LESSONS ---
    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>> = dao.getLessonsForCourse(courseId)

    suspend fun getLessonById(lessonId: String): LessonEntity? = dao.getLessonById(lessonId)

    suspend fun createLesson(
        courseId: String,
        moduleNumber: Int,
        title: String,
        contentType: String,
        duration: Int,
        body: String,
        videoUrl: String,
        resourceUrl: String,
        user: UserEntity
    ): LessonEntity {
        val lesson = LessonEntity(
            lessonId = PasswordHelper.genId("LES"),
            courseId = courseId,
            moduleNumber = moduleNumber,
            lessonTitle = title.trim(),
            contentType = contentType,
            durationMins = duration,
            contentBody = body,
            videoUrl = videoUrl.trim(),
            resourceUrl = resourceUrl.trim()
        )
        dao.insertLesson(lesson)
        logActivity(user.email, "CREATE_LESSON", "Created lesson: ${lesson.lessonTitle}")
        return lesson
    }

    suspend fun updateLesson(
        lessonId: String,
        moduleNumber: Int,
        title: String,
        contentType: String,
        duration: Int,
        body: String,
        videoUrl: String,
        resourceUrl: String,
        user: UserEntity
    ): Boolean {
        val current = dao.getLessonById(lessonId) ?: return false
        val updated = current.copy(
            moduleNumber = moduleNumber,
            lessonTitle = title.trim(),
            contentType = contentType,
            durationMins = duration,
            contentBody = body,
            videoUrl = videoUrl.trim(),
            resourceUrl = resourceUrl.trim()
        )
        dao.updateLesson(updated)
        logActivity(user.email, "UPDATE_LESSON", "Updated lesson: ${updated.lessonTitle}")
        return true
    }

    suspend fun deleteLesson(lessonId: String, user: UserEntity): Boolean {
        val current = dao.getLessonById(lessonId) ?: return false
        dao.deleteLessonById(lessonId)
        logActivity(user.email, "DELETE_LESSON", "Deleted lesson: ${current.lessonTitle}")
        return true
    }

    // --- RESOURCES ---
    fun getResourcesForLesson(lessonId: String): Flow<List<ResourceEntity>> = dao.getResourcesForLesson(lessonId)

    suspend fun addResource(
        lessonId: String,
        fileName: String,
        fileType: String,
        fileUrl: String,
        user: UserEntity
    ): ResourceEntity {
        val res = ResourceEntity(
            resourceId = PasswordHelper.genId("RES"),
            lessonId = lessonId,
            resourceType = fileType,
            fileName = fileName,
            mimeType = if (fileType.lowercase() == "pdf") "application/pdf" else "application/octet-stream",
            fileId = PasswordHelper.genId("DOC"),
            fileUrl = fileUrl.ifBlank { "https://hospital.org/lms/resources/$fileName" },
            uploadedBy = user.email,
            uploadedDate = PasswordHelper.formattedNow()
        )
        dao.insertResource(res)
        logActivity(user.email, "UPLOAD_RESOURCE", "Attached resource: $fileName")
        return res
    }

    suspend fun deleteResource(resourceId: String): Boolean {
        dao.deleteResourceById(resourceId)
        return true
    }

    // --- ASSIGNMENTS ---
    fun getAssignmentsForCourse(courseId: String): Flow<List<AssignmentEntity>> =
        dao.getAssignmentsForCourse(courseId)

    suspend fun createAssignment(
        courseId: String,
        moduleNumber: Int,
        title: String,
        instructions: String,
        dueDate: String,
        user: UserEntity
    ): AssignmentEntity {
        val asg = AssignmentEntity(
            assignmentId = PasswordHelper.genId("ASG"),
            courseId = courseId,
            moduleNumber = moduleNumber,
            assignmentTitle = title.trim(),
            instructions = instructions.trim(),
            dueDate = dueDate.trim(),
            createdBy = user.email,
            createdDate = PasswordHelper.formattedNow()
        )
        dao.insertAssignment(asg)
        logActivity(user.email, "CREATE_ASSIGNMENT", "Created assignment: ${asg.assignmentTitle}")
        return asg
    }

    suspend fun deleteAssignment(assignmentId: String, user: UserEntity): Boolean {
        val asg = dao.getAssignmentById(assignmentId) ?: return false
        dao.deleteAssignmentById(assignmentId)
        logActivity(user.email, "DELETE_ASSIGNMENT", "Deleted assignment: ${asg.assignmentTitle}")
        return true
    }

    // --- SUBMISSIONS ---
    fun getSubmissionsForAssignment(assignmentId: String): Flow<List<SubmissionEntity>> =
        dao.getSubmissionsForAssignment(assignmentId)

    fun getSubmissionsForLearner(learnerId: String): Flow<List<SubmissionEntity>> =
        dao.getSubmissionsForLearner(learnerId)

    suspend fun submitAssignment(
        assignmentId: String,
        courseId: String,
        fileName: String,
        fileUrl: String,
        user: UserEntity
    ): SubmissionEntity {
        val sub = SubmissionEntity(
            submissionId = PasswordHelper.genId("SUB"),
            assignmentId = assignmentId,
            learnerId = user.userId,
            learnerName = user.fullName,
            courseId = courseId,
            fileName = fileName,
            mimeType = "application/pdf",
            fileId = PasswordHelper.genId("FILE"),
            fileUrl = fileUrl.ifBlank { "https://hospital.org/lms/submissions/$fileName" },
            submittedDate = PasswordHelper.formattedNow(),
            status = "Submitted"
        )
        dao.insertSubmission(sub)
        logActivity(user.email, "SUBMIT_ASSIGNMENT", "Submitted work for assignment $assignmentId")
        return sub
    }

    suspend fun gradeSubmission(
        submissionId: String,
        grade: String,
        feedback: String,
        user: UserEntity
    ): Boolean {
        val sub = dao.getSubmissionById(submissionId) ?: return false
        val updated = sub.copy(
            grade = grade.trim(),
            feedback = feedback.trim(),
            gradedBy = user.email,
            gradedDate = PasswordHelper.formattedNow(),
            status = "Graded"
        )
        dao.updateSubmission(updated)
        logActivity(user.email, "GRADE_SUBMISSION", "Graded submission $submissionId with $grade")
        return true
    }

    // --- ASSESSMENTS & QUIZZES ---
    fun getAssessmentsForCourse(courseId: String): Flow<List<AssessmentEntity>> =
        dao.getAssessmentsForCourse(courseId)

    suspend fun getAssessmentById(assessmentId: String): AssessmentEntity? =
        dao.getAssessmentById(assessmentId)

    suspend fun createAssessment(
        courseId: String,
        moduleNumber: Int,
        title: String,
        description: String,
        passingScore: Int,
        timeLimit: Int,
        user: UserEntity
    ): AssessmentEntity {
        val asm = AssessmentEntity(
            assessmentId = PasswordHelper.genId("ASM"),
            courseId = courseId,
            moduleNumber = moduleNumber,
            assessmentTitle = title.trim(),
            description = description.trim(),
            passingScore = passingScore,
            timeLimitMins = timeLimit,
            createdBy = user.email,
            createdDate = PasswordHelper.formattedNow()
        )
        dao.insertAssessment(asm)
        logActivity(user.email, "CREATE_ASSESSMENT", "Created assessment: ${asm.assessmentTitle}")
        return asm
    }

    fun getQuestionsForAssessment(assessmentId: String): Flow<List<QuestionEntity>> =
        dao.getQuestionsForAssessment(assessmentId)

    suspend fun addQuestion(
        assessmentId: String,
        text: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correct: String,
        points: Int,
        user: UserEntity
    ): QuestionEntity {
        val q = QuestionEntity(
            questionId = PasswordHelper.genId("QST"),
            assessmentId = assessmentId,
            questionText = text.trim(),
            optionA = optA.trim(),
            optionB = optB.trim(),
            optionC = optC.trim(),
            optionD = optD.trim(),
            correctAnswer = correct.uppercase().trim(),
            points = points
        )
        dao.insertQuestion(q)
        logActivity(user.email, "ADD_QUESTION", "Added question to assessment $assessmentId")
        return q
    }

    suspend fun submitQuizAttempt(
        assessmentId: String,
        answers: Map<String, String>,
        user: UserEntity
    ): QuizResult {
        val asm = dao.getAssessmentById(assessmentId)
            ?: return QuizResult(false, "Assessment not found.")
        val questions = dao.getQuestionsForAssessmentList(assessmentId)
        if (questions.isEmpty()) return QuizResult(false, "No questions found for this quiz.")

        var totalPoints = 0
        var earnedPoints = 0

        for (q in questions) {
            totalPoints += q.points
            val selected = answers[q.questionId]?.trim()?.uppercase()
            if (selected != null && selected == q.correctAnswer) {
                earnedPoints += q.points
            }
        }

        val percentage = if (totalPoints > 0) (earnedPoints * 100) / totalPoints else 0
        val passed = percentage >= asm.passingScore

        val priorAttempts = dao.getAttemptsListForAssessmentAndLearner(assessmentId, user.userId)
        val attemptNum = priorAttempts.size + 1

        val attempt = AttemptEntity(
            attemptId = PasswordHelper.genId("ATT"),
            assessmentId = assessmentId,
            learnerId = user.userId,
            learnerName = user.fullName,
            courseId = asm.courseId,
            attemptNumber = attemptNum,
            score = "$earnedPoints / $totalPoints",
            percentage = percentage,
            passed = passed,
            submittedDate = PasswordHelper.formattedNow()
        )
        dao.insertAttempt(attempt)
        logActivity(user.email, "SUBMIT_QUIZ", "Quiz score: $percentage% on ${asm.assessmentTitle}")

        val msg = if (passed) {
            "Congratulations! You passed with $percentage%."
        } else {
            "Keep trying! You scored $percentage%, passing score is ${asm.passingScore}%."
        }
        return QuizResult(true, msg, earnedPoints, totalPoints, percentage, passed)
    }

    suspend fun getDashboardStats(user: UserEntity): DashboardStats {
        val courses = dao.getAllCourses().first()
        val totalLessons = dao.getTotalLessonCount()

        return when (user.role.lowercase()) {
            "learner" -> {
                val enrollments = dao.getEnrollmentsForLearner(user.userId).first()
                val subs = dao.getSubmissionsForLearner(user.userId).first()
                val attempts = dao.getAttemptsForLearner(user.userId).first()
                val avg = if (attempts.isNotEmpty()) {
                    val sum = attempts.sumOf { it.percentage }
                    "${sum / attempts.size}%"
                } else "—"

                DashboardStats(
                    totalCourses = courses.size,
                    totalLessons = totalLessons,
                    myEnrollments = enrollments.size,
                    mySubmissions = subs.size,
                    avgScore = avg
                )
            }
            "instructor" -> {
                val myC = courses.filter { it.instructorId == user.userId }
                val myCids = myC.map { it.courseId }.toSet()
                val allSubs = dao.getAllSubmissions().first().filter { it.courseId in myCids }
                val allAtts = dao.getAllAttempts().first().filter { it.courseId in myCids }

                DashboardStats(
                    totalCourses = courses.size,
                    totalLessons = totalLessons,
                    myCourses = myC.size,
                    mySubmissions = allSubs.size,
                    myAttempts = allAtts.size
                )
            }
            else -> {
                // admin
                val users = dao.getAllUsers().first()
                val subs = dao.getAllSubmissions().first()
                val attempts = dao.getAllAttempts().first()

                DashboardStats(
                    totalCourses = courses.size,
                    totalLessons = totalLessons,
                    totalUsers = users.size,
                    totalSubmissions = subs.size,
                    totalAttempts = attempts.size
                )
            }
        }
    }
}
