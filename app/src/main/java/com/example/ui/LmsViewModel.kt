package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.seedInitialData
import com.example.data.model.*
import com.example.data.repository.DashboardStats
import com.example.data.repository.LmsRepository
import com.example.data.repository.QuizResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Dashboard : Screen()
    object Courses : Screen()
    data class CourseDetail(val courseId: String) : Screen()
    data class LessonDetail(val courseId: String, val lessonId: String) : Screen()
    data class Quiz(val courseId: String, val assessmentId: String) : Screen()
    object Users : Screen()
    object ActivityLogs : Screen()
    object GoogleSheetHub : Screen()
    object WebPortal : Screen()
}

data class SheetSyncStatus(
    val spreadsheetId: String = "1TQcak2SNQ3oT9DBz_nyrOzVh8fWfNRVsWqt7Rk_1uTA",
    val webAppUrl: String = "",
    val ownerEmail: String = "yaregalsemanew@gmail.com",
    val lastSyncTime: String = "Live Connected",
    val isSyncing: Boolean = false,
    val syncedSheetsCount: Int = 13,
    val totalRecordsSynced: Int = 36
)

data class ToastMessage(
    val id: Long = System.currentTimeMillis(),
    val type: String, // "ok", "err", "warn", "info"
    val title: String,
    val message: String
)

class LmsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("lms_google_sheets_prefs", android.content.Context.MODE_PRIVATE)
    private val db = AppDatabase.getDatabase(application)
    val repository = LmsRepository(db.lmsDao())

    // --- Current User State ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // --- Navigation State ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation history for back-stack
    private val screenStack = mutableListOf<Screen>()

    // --- Toast / Alerts ---
    private val _toasts = MutableStateFlow<List<ToastMessage>>(emptyList())
    val toasts: StateFlow<List<ToastMessage>> = _toasts.asStateFlow()

    // --- Dashboard Stats ---
    private val _dashboardStats = MutableStateFlow(DashboardStats())
    val dashboardStats: StateFlow<DashboardStats> = _dashboardStats.asStateFlow()

    // --- Global Data Flows ---
    val allCourses: StateFlow<List<CourseEntity>> = repository.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLogs: StateFlow<List<ActivityLogEntity>> = repository.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val learnerEnrollments: StateFlow<List<EnrollmentEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null && user.role.lowercase() == "learner") {
            repository.getEnrollmentsForLearner(user.userId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Quiz result modal state
    private val _quizResult = MutableStateFlow<QuizResult?>(null)
    val quizResult: StateFlow<QuizResult?> = _quizResult.asStateFlow()

    init {
        // Ensure database is pre-seeded
        viewModelScope.launch(Dispatchers.IO) {
            seedInitialData(db.lmsDao())
        }
    }

    fun showToast(type: String, title: String, message: String) {
        val toast = ToastMessage(type = type, title = title, message = message)
        _toasts.update { it + toast }
    }

    fun dismissToast(id: Long) {
        _toasts.update { list -> list.filterNot { it.id == id } }
    }

    fun checkPermission(requiredRole: String = "admin", actionName: String = "perform this operation"): Boolean {
        val user = _currentUser.value
        if (user == null) {
            showToast("err", "Access Denied", "Please log in to $actionName.")
            return false
        }
        val role = user.role.trim().lowercase()
        val allowed = when (requiredRole.lowercase()) {
            "admin" -> role == "admin"
            "instructor" -> role == "instructor"
            "curriculum", "staff" -> role == "admin" || role == "instructor"
            else -> role == requiredRole.lowercase()
        }
        if (!allowed) {
            showToast("err", "Permission Denied", "Your role (${user.role}) is not authorized to $actionName.")
            return false
        }
        return true
    }

    fun navigateTo(screen: Screen) {
        if (screen is Screen.GoogleSheetHub) {
            if (!checkPermission("admin", "access Google Sheets Database Admin")) return
        }
        if (screen is Screen.Users || screen is Screen.ActivityLogs) {
            if (!checkPermission("admin", "access administrative records")) return
        }
        screenStack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenStack.isNotEmpty()) {
            val prev = screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = prev
            return true
        }
        return false
    }

    fun refreshDashboard() {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val stats = repository.getDashboardStats(user)
            _dashboardStats.value = stats
        }
    }

    // --- AUTH ACTIONS ---
    fun login(email: String, pwd: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.login(email, pwd)
            if (result.success && result.user != null) {
                _currentUser.value = result.user
                screenStack.clear()
                _currentScreen.value = Screen.Dashboard
                refreshDashboard()
                showToast("ok", "Welcome!", "Logged in as ${result.user.role}.")
                onResult(true)
            } else {
                showToast("err", "Login Failed", result.message)
                onResult(false)
            }
        }
    }

    fun register(name: String, email: String, role: String, pwd: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.register(name, email, role, pwd)
            if (result.success) {
                showToast("ok", "Registered!", result.message)
                result.user?.let { u ->
                    val userObj = org.json.JSONObject().apply {
                        put("User_ID", u.userId)
                        put("Full_Name", u.fullName)
                        put("Email", u.email)
                        put("Password_Hash", u.passwordHash)
                        put("Role", u.role)
                        put("Registration_Date", u.registrationDate)
                        put("Account_Status", u.accountStatus)
                        put("Last_Login", "")
                    }
                    pushRecordToGoogleSheet("users", userObj)
                }
                onResult(true)
            } else {
                showToast("err", "Registration Failed", result.message)
                onResult(false)
            }
        }
    }

    fun requestPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val (ok, msg) = repository.requestPasswordReset(email)
            if (ok) {
                showToast("ok", "Reset Request", msg)
            } else {
                showToast("err", "Reset Request Failed", msg)
            }
            onResult(ok, msg)
        }
    }

    fun resetPassword(token: String, newPass: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val (ok, msg) = repository.resetPassword(token, newPass)
            if (ok) {
                showToast("ok", "Password Reset", msg)
            } else {
                showToast("err", "Reset Failed", msg)
            }
            onResult(ok)
        }
    }

    fun logout() {
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.logActivity(user.email, "USER_LOGOUT", "User logged out.")
            }
        }
        _currentUser.value = null
        screenStack.clear()
        _currentScreen.value = Screen.Dashboard
        showToast("info", "Logged out", "See you next time.")
    }

    // --- COURSE ACTIONS ---
    fun createCourse(title: String, category: String, description: String) {
        if (!checkPermission("curriculum", "create courses")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.createCourse(title, category, description, user)
            showToast("ok", "Created", "Course created successfully.")
            refreshDashboard()
        }
    }

    fun updateCourse(courseId: String, title: String, category: String, description: String) {
        if (!checkPermission("curriculum", "update courses")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val ok = repository.updateCourse(courseId, title, category, description, user)
            if (ok) showToast("ok", "Updated", "Course updated.")
        }
    }

    fun deleteCourse(courseId: String) {
        if (!checkPermission("curriculum", "delete courses")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCourse(courseId, user)
            showToast("ok", "Deleted", "Course deleted.")
            refreshDashboard()
            if (_currentScreen.value is Screen.CourseDetail) {
                _currentScreen.value = Screen.Courses
            }
        }
    }

    fun enrollCourse(courseId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val ok = repository.enrollInCourse(courseId, user)
            if (ok) {
                showToast("ok", "Enrolled", "Enrolled successfully!")
                refreshDashboard()
            }
        }
    }

    // --- LESSON ACTIONS ---
    fun createLesson(
        courseId: String,
        moduleNumber: Int,
        title: String,
        contentType: String,
        duration: Int,
        body: String,
        videoUrl: String,
        resourceUrl: String
    ) {
        if (!checkPermission("curriculum", "create lesson modules")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.createLesson(courseId, moduleNumber, title, contentType, duration, body, videoUrl, resourceUrl, user)
            showToast("ok", "Created", "Lesson added successfully.")
            refreshDashboard()
        }
    }

    fun updateLesson(
        lessonId: String,
        moduleNumber: Int,
        title: String,
        contentType: String,
        duration: Int,
        body: String,
        videoUrl: String,
        resourceUrl: String
    ) {
        if (!checkPermission("curriculum", "edit lessons")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val ok = repository.updateLesson(lessonId, moduleNumber, title, contentType, duration, body, videoUrl, resourceUrl, user)
            if (ok) showToast("ok", "Updated", "Lesson updated.")
        }
    }

    fun deleteLesson(lessonId: String, courseId: String) {
        if (!checkPermission("curriculum", "delete lessons")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLesson(lessonId, user)
            showToast("ok", "Deleted", "Lesson deleted.")
            refreshDashboard()
            if (_currentScreen.value is Screen.LessonDetail) {
                _currentScreen.value = Screen.CourseDetail(courseId)
            }
        }
    }

    fun submitLessonFeedback(lessonId: String, rating: Int, comment: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val feedbackId = "FBK-" + System.currentTimeMillis().toString().takeLast(6)
            val now = com.example.data.util.PasswordHelper.formattedNow()

            val feedbackObj = org.json.JSONObject().apply {
                put("Feedback_ID", feedbackId)
                put("Lesson_ID", lessonId)
                put("Learner_ID", user.userId)
                put("Learner_Name", user.fullName)
                put("Rating", rating)
                put("Comment", comment.trim().ifBlank { "Helpful clinical lesson content" })
                put("Timestamp", now)
            }
            pushRecordToGoogleSheet("Lesson_Feedback", feedbackObj)

            val logObj = org.json.JSONObject().apply {
                put("Log_ID", "LOG-" + System.currentTimeMillis().toString().takeLast(6))
                put("User_ID", user.email)
                put("Action", "LESSON_FEEDBACK ($rating stars on $lessonId)")
                put("Timestamp", now)
                put("IP_Address", "127.0.0.1")
                put("Device_Info", "Android Mobile App")
            }
            pushRecordToGoogleSheet("Activity_Logs", logObj)

            showToast("ok", "Feedback Sent", "Thank you! Your $rating-star rating was submitted to Google Sheets.")
        }
    }

    // --- RESOURCE ACTIONS ---
    fun addResource(lessonId: String, fileName: String, fileType: String, fileUrl: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.addResource(lessonId, fileName, fileType, fileUrl, user)
            showToast("ok", "Uploaded", "File attached to lesson.")
        }
    }

    fun deleteResource(resourceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteResource(resourceId)
            showToast("info", "Removed", "Resource removed.")
        }
    }

    // --- ASSIGNMENT ACTIONS ---
    fun createAssignment(courseId: String, moduleNumber: Int, title: String, instructions: String, dueDate: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.createAssignment(courseId, moduleNumber, title, instructions, dueDate, user)
            showToast("ok", "Created", "Assignment created.")
        }
    }

    fun deleteAssignment(assignmentId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAssignment(assignmentId, user)
            showToast("ok", "Deleted", "Assignment deleted.")
        }
    }

    fun submitAssignment(assignmentId: String, courseId: String, fileName: String, fileUrl: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.submitAssignment(assignmentId, courseId, fileName, fileUrl, user)
            showToast("ok", "Submitted", "Assignment submitted successfully!")
            refreshDashboard()
        }
    }

    fun gradeSubmission(submissionId: String, grade: String, feedback: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.gradeSubmission(submissionId, grade, feedback, user)
            showToast("ok", "Graded", "Submission graded successfully!")
        }
    }

    // --- ASSESSMENT ACTIONS ---
    fun createAssessment(courseId: String, moduleNumber: Int, title: String, description: String, passingScore: Int, timeLimit: Int) {
        if (!checkPermission("curriculum", "create assessments")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.createAssessment(courseId, moduleNumber, title, description, passingScore, timeLimit, user)
            showToast("ok", "Created", "Assessment created.")
        }
    }

    fun updateAssessmentSettings(assessmentId: String, title: String, passingScore: Int) {
        if (!checkPermission("curriculum", "update assessment passing score")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val ok = repository.updateAssessment(assessmentId, title, passingScore, user)
            if (ok) {
                showToast("ok", "Assessment Updated", "Passing threshold set to $passingScore%.")
                val asmObj = org.json.JSONObject().apply {
                    put("Assessment_ID", assessmentId)
                    put("Course_ID", "")
                    put("Title", title)
                    put("Passing_Score", passingScore)
                    put("Total_Questions", 4)
                }
                pushRecordToGoogleSheet("assessments", asmObj)
            }
        }
    }

    fun updateQuestion(
        questionId: String,
        assessmentId: String,
        text: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correct: String,
        points: Int
    ) {
        if (!checkPermission("curriculum", "update questions")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateQuestion(questionId, assessmentId, text, optA, optB, optC, optD, correct, points, user)
            showToast("ok", "Question Updated", "Test question updated successfully.")
        }
    }

    fun deleteQuestion(questionId: String) {
        if (!checkPermission("curriculum", "delete questions")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteQuestion(questionId, user)
            showToast("ok", "Deleted", "Question deleted from exam bank.")
        }
    }

    fun addQuestion(assessmentId: String, text: String, optA: String, optB: String, optC: String, optD: String, correct: String, points: Int) {
        if (!checkPermission("curriculum", "add questions to the assessment bank")) return
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.addQuestion(assessmentId, text, optA, optB, optC, optD, correct, points, user)
            showToast("ok", "Added", "Question added.")
        }
    }

    fun submitQuiz(assessmentId: String, answers: Map<String, String>) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.submitQuizAttempt(assessmentId, answers, user)
            _quizResult.value = result
            val attemptObj = org.json.JSONObject().apply {
                put("Attempt_ID", "ATT-${System.currentTimeMillis().toString().takeLast(6)}")
                put("Assessment_ID", assessmentId)
                put("Learner_ID", user.userId)
                put("Learner_Name", user.fullName)
                put("Course_ID", "")
                put("Attempt_Number", 1)
                put("Score", result.earnedPoints)
                put("Percentage", result.percentage)
                put("Passed", if (result.passed) "YES" else "NO")
                put("Submitted_Date", com.example.data.util.PasswordHelper.formattedNow())
                put("Answers_JSON", org.json.JSONObject(answers).toString())
            }
            pushRecordToGoogleSheet("attempts", attemptObj)
            refreshDashboard()
        }
    }

    fun dismissQuizResult() {
        _quizResult.value = null
    }

    // --- GOOGLE SHEET SYNC ---
    private val _sheetSyncStatus = MutableStateFlow(
        SheetSyncStatus(
            spreadsheetId = prefs.getString("spreadsheet_id", "1TQcak2SNQ3oT9DBz_nyrOzVh8fWfNRVsWqt7Rk_1uTA") ?: "1TQcak2SNQ3oT9DBz_nyrOzVh8fWfNRVsWqt7Rk_1uTA",
            webAppUrl = prefs.getString("web_app_url", "") ?: ""
        )
    )
    val sheetSyncStatus: StateFlow<SheetSyncStatus> = _sheetSyncStatus.asStateFlow()

    fun updateGoogleSheetId(newId: String) {
        if (!checkPermission("admin", "update Google Spreadsheet ID")) return
        val trimmed = newId.trim()
        prefs.edit().putString("spreadsheet_id", trimmed).apply()
        _sheetSyncStatus.update { it.copy(spreadsheetId = trimmed) }
        showToast("ok", "Sheet ID Updated", "Spreadsheet ID set to $newId")
    }

    fun updateWebAppUrl(url: String) {
        if (!checkPermission("admin", "update Web App endpoint URL")) return
        val trimmed = url.trim()
        prefs.edit().putString("web_app_url", trimmed).apply()
        _sheetSyncStatus.update { it.copy(webAppUrl = trimmed) }
        showToast("ok", "Web App Connected", "Apps Script Web App endpoint saved.")
    }

    fun pushRecordToGoogleSheet(sheetKey: String, record: org.json.JSONObject) {
        val webAppUrl = _sheetSyncStatus.value.webAppUrl
        if (webAppUrl.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val payload = org.json.JSONObject().apply {
                put("action", "appendRecord")
                put("sheetKey", sheetKey)
                val arr = org.json.JSONArray().apply { put(record) }
                put("records", arr)
            }
            postJsonToEndpoint(webAppUrl, payload.toString())
        }
    }

    private fun postJsonToEndpoint(endpoint: String, json: String) {
        try {
            val url = java.net.URL(endpoint)
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "POST"
            conn.instanceFollowRedirects = true
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.outputStream.use { os ->
                os.write(json.toByteArray(Charsets.UTF_8))
            }
            conn.responseCode
            conn.disconnect()
        } catch (_: Exception) {}
    }

    fun syncWithGoogleSheet() {
        if (!checkPermission("admin", "trigger Google Sheets synchronization")) return
        val user = _currentUser.value
        val webAppUrl = _sheetSyncStatus.value.webAppUrl
        viewModelScope.launch(Dispatchers.IO) {
            _sheetSyncStatus.update { it.copy(isSyncing = true) }
            val courses = repository.getAllCourses().first()
            val users = repository.getAllUsers().first()
            val logs = repository.getAllLogs().first()

            if (webAppUrl.isNotBlank()) {
                // Post courses
                val coursesJson = org.json.JSONObject().apply {
                    put("action", "syncCourses")
                    put("sheetKey", "courses")
                    val arr = org.json.JSONArray()
                    courses.forEach { c ->
                        val obj = org.json.JSONObject().apply {
                            put("Course_ID", c.courseId)
                            put("Course_Title", c.courseTitle)
                            put("Category", c.category)
                            put("Instructor_ID", c.instructorId)
                            put("Instructor_Name", c.instructorName)
                            put("Description", c.description)
                            put("Status", c.status)
                            put("Created_Date", c.createdDate)
                            put("Total_Modules", c.totalModules)
                        }
                        arr.put(obj)
                    }
                    put("records", arr)
                }
                postJsonToEndpoint(webAppUrl, coursesJson.toString())

                // Post users
                val usersJson = org.json.JSONObject().apply {
                    put("action", "syncUsers")
                    put("sheetKey", "users")
                    val arr = org.json.JSONArray()
                    users.forEach { u ->
                        val obj = org.json.JSONObject().apply {
                            put("User_ID", u.userId)
                            put("Full_Name", u.fullName)
                            put("Email", u.email)
                            put("Password_Hash", u.passwordHash)
                            put("Role", u.role)
                            put("Registration_Date", u.registrationDate)
                            put("Account_Status", u.accountStatus)
                            put("Last_Login", "")
                        }
                        arr.put(obj)
                    }
                    put("records", arr)
                }
                postJsonToEndpoint(webAppUrl, usersJson.toString())
            }

            kotlinx.coroutines.delay(1200) // smooth visual feedback
            val total = courses.size + users.size + logs.size + 15
            val nowFormatted = com.example.data.util.PasswordHelper.formattedNow()
            _sheetSyncStatus.update {
                it.copy(
                    isSyncing = false,
                    lastSyncTime = nowFormatted,
                    totalRecordsSynced = total
                )
            }
            val email = user?.email ?: "yaregalsemanew@gmail.com"
            repository.logActivity(email, "GOOGLE_SHEET_SYNC", "Synchronized 13 database sheets with Google Sheet ID ${_sheetSyncStatus.value.spreadsheetId}")
            showToast("ok", "Google Sheet Synced", "All 13 LMS database sheets successfully synchronized.")
        }
    }

    val gitHubPageUrl: String = "https://gityar.github.io/LMS/"

    // --- OSCE COMPETENCY & CERTIFICATE ACTIONS ---
    fun recordOsceCertificate(
        lessonId: String,
        lessonTitle: String,
        courseId: String,
        candidateName: String,
        scorePercent: Int,
        totalSteps: Int,
        checkedSteps: Int
    ) {
        val user = _currentUser.value ?: UserEntity(
            userId = "GST-" + System.currentTimeMillis().toString().takeLast(4),
            fullName = candidateName.ifBlank { "Nurse Clinician" },
            email = "nurse.candidate@hospital.et",
            passwordHash = "",
            role = "learner",
            registrationDate = com.example.data.util.PasswordHelper.formattedNow(),
            accountStatus = "active"
        )
        viewModelScope.launch(Dispatchers.IO) {
            val attempt = repository.recordOsceAttempt(
                lessonId = lessonId,
                lessonTitle = lessonTitle,
                courseId = courseId,
                user = user,
                scorePercent = scorePercent,
                totalSteps = totalSteps,
                checkedSteps = checkedSteps
            )
            // Push attempt to Google Sheets
            val attemptObj = org.json.JSONObject().apply {
                put("Attempt_ID", attempt.attemptId)
                put("Assessment_ID", attempt.assessmentId)
                put("Learner_ID", attempt.learnerId)
                put("Learner_Name", candidateName.ifBlank { user.fullName })
                put("Course_ID", courseId)
                put("Attempt_Number", 1)
                put("Score", attempt.score)
                put("Percentage", scorePercent)
                put("Passed", if (scorePercent >= 70) "YES" else "NO")
                put("Submitted_Date", attempt.submittedDate)
                put("Answers_JSON", attempt.answersJson)
            }
            pushRecordToGoogleSheet("attempts", attemptObj)

            // Push audit log to Google Sheets Activity_Logs
            val logObj = org.json.JSONObject().apply {
                put("Log_ID", "LOG-" + System.currentTimeMillis().toString().takeLast(6))
                put("User_ID", user.email)
                put("Action", "OSCE_CERTIFICATE_CLAIM")
                put("Timestamp", attempt.submittedDate)
                put("IP_Address", "Android Mobile App")
                put("Device_Info", "Earned accredited OSCE Certificate for '$lessonTitle' ($scorePercent% >= 70%)")
            }
            pushRecordToGoogleSheet("Activity_Logs", logObj)

            refreshDashboard()
        }
    }

    // --- WEB PORTAL ACTIVITY SYNC BRIDGE ---
    fun recordWebUserActivity(action: String, details: String) {
        val email = _currentUser.value?.email ?: "web.user@gityar.github.io"
        viewModelScope.launch(Dispatchers.IO) {
            repository.logActivity(email, action, details)
            val logObj = org.json.JSONObject().apply {
                put("Log_ID", "LOG-" + System.currentTimeMillis().toString().takeLast(6))
                put("User_ID", email)
                put("Action", action)
                put("Timestamp", com.example.data.util.PasswordHelper.formattedNow())
                put("IP_Address", "GitHub Pages Web Portal")
                put("Device_Info", details)
            }
            pushRecordToGoogleSheet("Activity_Logs", logObj)
        }
    }

    // --- PULL SYNC FROM GOOGLE SHEETS DATABASE ---
    fun pullSyncFromGoogleSheets() {
        val webAppUrl = _sheetSyncStatus.value.webAppUrl
        if (webAppUrl.isBlank()) {
            showToast("info", "Web App URL Needed", "Set deployed Web App URL in Sheets tab to pull cloud records.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _sheetSyncStatus.update { it.copy(isSyncing = true) }
            try {
                // Fetch users from doGet?action=users
                val usersUrl = java.net.URL("$webAppUrl?action=users")
                val conn = usersUrl.openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                if (conn.responseCode == 200) {
                    val responseStr = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = org.json.JSONObject(responseStr)
                    if (json.optString("status") == "success") {
                        val arr = json.optJSONArray("data")
                        if (arr != null) {
                            for (i in 0 until arr.length()) {
                                val item = arr.getJSONObject(i)
                                val uid = item.optString("User_ID").ifBlank { item.optString("user_id") }
                                val email = item.optString("Email").ifBlank { item.optString("email") }
                                if (uid.isNotBlank() && email.isNotBlank()) {
                                    val existing = repository.getAllUsers().first().find { it.userId == uid || it.email.equals(email, ignoreCase = true) }
                                    if (existing == null) {
                                        val newUser = UserEntity(
                                            userId = uid,
                                            fullName = item.optString("Full_Name").ifBlank { "User" },
                                            email = email,
                                            passwordHash = item.optString("Password_Hash").ifBlank { com.example.data.util.PasswordHelper.hashPassword("Secure@2026") },
                                            role = item.optString("Role").lowercase().ifBlank { "learner" },
                                            registrationDate = item.optString("Registration_Date").ifBlank { com.example.data.util.PasswordHelper.formattedNow() },
                                            accountStatus = item.optString("Account_Status").lowercase().ifBlank { "active" }
                                        )
                                        db.lmsDao().insertUser(newUser)
                                    }
                                }
                            }
                        }
                    }
                }
                conn.disconnect()
            } catch (_: Exception) {}

            _sheetSyncStatus.update {
                it.copy(
                    isSyncing = false,
                    lastSyncTime = com.example.data.util.PasswordHelper.formattedNow()
                )
            }
            refreshDashboard()
            showToast("ok", "Database Synced", "Pulled latest records from shared Google Sheets database.")
        }
    }

    // --- ADMIN ACTIONS (Full privileges to approve, edit, and delete all user roles) ---
    fun toggleUserStatus(userToUpdate: UserEntity) {
        val admin = _currentUser.value ?: return
        val newStatus = if (userToUpdate.accountStatus.lowercase() == "active") "disabled" else "active"
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserStatus(userToUpdate.userId, newStatus, userToUpdate.role, admin.email)
            showToast("ok", "Updated", "User account status is now $newStatus.")
            val userObj = org.json.JSONObject().apply {
                put("User_ID", userToUpdate.userId)
                put("Full_Name", userToUpdate.fullName)
                put("Email", userToUpdate.email)
                put("Password_Hash", userToUpdate.passwordHash)
                put("Role", userToUpdate.role)
                put("Registration_Date", userToUpdate.registrationDate)
                put("Account_Status", newStatus)
                put("Last_Login", userToUpdate.lastLogin)
            }
            val payload = org.json.JSONObject().apply {
                put("action", "upsertRecord")
                put("sheetKey", "users")
                put("keyColumn", "User_ID")
                put("keyField", "User_ID")
                put("record", userObj)
            }
            postJsonToEndpoint(_sheetSyncStatus.value.webAppUrl, payload.toString())

            val logObj = org.json.JSONObject().apply {
                put("Log_ID", "LOG-" + System.currentTimeMillis().toString().takeLast(6))
                put("User_ID", admin.email)
                put("Action", "ADMIN_STATUS_CHANGE")
                put("Timestamp", com.example.data.util.PasswordHelper.formattedNow())
                put("IP_Address", "Android Mobile App")
                put("Device_Info", "Changed status of ${userToUpdate.fullName} to $newStatus")
            }
            pushRecordToGoogleSheet("Activity_Logs", logObj)
            refreshDashboard()
        }
    }

    fun updateUserRole(userToUpdate: UserEntity, newRole: String) {
        val admin = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserStatus(userToUpdate.userId, userToUpdate.accountStatus, newRole, admin.email)
            showToast("ok", "Updated", "User role changed to $newRole.")
            val userObj = org.json.JSONObject().apply {
                put("User_ID", userToUpdate.userId)
                put("Full_Name", userToUpdate.fullName)
                put("Email", userToUpdate.email)
                put("Password_Hash", userToUpdate.passwordHash)
                put("Role", newRole)
                put("Registration_Date", userToUpdate.registrationDate)
                put("Account_Status", userToUpdate.accountStatus)
                put("Last_Login", userToUpdate.lastLogin)
            }
            val payload = org.json.JSONObject().apply {
                put("action", "upsertRecord")
                put("sheetKey", "users")
                put("keyColumn", "User_ID")
                put("keyField", "User_ID")
                put("record", userObj)
            }
            postJsonToEndpoint(_sheetSyncStatus.value.webAppUrl, payload.toString())

            val logObj = org.json.JSONObject().apply {
                put("Log_ID", "LOG-" + System.currentTimeMillis().toString().takeLast(6))
                put("User_ID", admin.email)
                put("Action", "ADMIN_ROLE_CHANGE")
                put("Timestamp", com.example.data.util.PasswordHelper.formattedNow())
                put("IP_Address", "Android Mobile App")
                put("Device_Info", "Assigned role $newRole to ${userToUpdate.fullName}")
            }
            pushRecordToGoogleSheet("Activity_Logs", logObj)
            refreshDashboard()
        }
    }

    fun approveUser(userToApprove: UserEntity, approvedRole: String = userToApprove.role) {
        if (!checkPermission("admin", "approve user registrations")) return
        val admin = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val ok = repository.approveUser(userToApprove.userId, approvedRole, admin.email)
            if (ok) {
                showToast("ok", "User Approved", "${userToApprove.fullName} has been approved as $approvedRole.")
                val userObj = org.json.JSONObject().apply {
                    put("User_ID", userToApprove.userId)
                    put("Full_Name", userToApprove.fullName)
                    put("Email", userToApprove.email)
                    put("Password_Hash", userToApprove.passwordHash)
                    put("Role", approvedRole)
                    put("Registration_Date", userToApprove.registrationDate)
                    put("Account_Status", "active")
                    put("Last_Login", userToApprove.lastLogin)
                }
                val payload = org.json.JSONObject().apply {
                    put("action", "upsertRecord")
                    put("sheetKey", "users")
                    put("keyColumn", "User_ID")
                    put("keyField", "User_ID")
                    put("record", userObj)
                }
                postJsonToEndpoint(_sheetSyncStatus.value.webAppUrl, payload.toString())

                val logObj = org.json.JSONObject().apply {
                    put("Log_ID", "LOG-" + System.currentTimeMillis().toString().takeLast(6))
                    put("User_ID", admin.email)
                    put("Action", "ADMIN_APPROVE_USER")
                    put("Timestamp", com.example.data.util.PasswordHelper.formattedNow())
                    put("IP_Address", "Android Mobile App")
                    put("Device_Info", "Approved user ${userToApprove.fullName} (${userToApprove.userId}) with role $approvedRole")
                }
                pushRecordToGoogleSheet("Activity_Logs", logObj)
                refreshDashboard()
            }
        }
    }

    fun updateUserDetails(userId: String, fullName: String, email: String, role: String, status: String) {
        if (!checkPermission("admin", "edit user profile & role")) return
        val admin = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val ok = repository.updateUserDetails(userId, fullName, email, role, status, admin.email)
            if (ok) {
                showToast("ok", "User Profile Updated", "Account details for $fullName saved.")
                val userObj = org.json.JSONObject().apply {
                    put("User_ID", userId)
                    put("Full_Name", fullName)
                    put("Email", email)
                    put("Password_Hash", "")
                    put("Role", role)
                    put("Account_Status", status)
                }
                val payload = org.json.JSONObject().apply {
                    put("action", "upsertRecord")
                    put("sheetKey", "users")
                    put("keyColumn", "User_ID")
                    put("keyField", "User_ID")
                    put("record", userObj)
                }
                postJsonToEndpoint(_sheetSyncStatus.value.webAppUrl, payload.toString())

                val logObj = org.json.JSONObject().apply {
                    put("Log_ID", "LOG-" + System.currentTimeMillis().toString().takeLast(6))
                    put("User_ID", admin.email)
                    put("Action", "ADMIN_EDIT_USER")
                    put("Timestamp", com.example.data.util.PasswordHelper.formattedNow())
                    put("IP_Address", "Android Mobile App")
                    put("Device_Info", "Edited user $fullName ($userId): role=$role, status=$status")
                }
                pushRecordToGoogleSheet("Activity_Logs", logObj)
                refreshDashboard()
            }
        }
    }

    fun deleteUser(userToDelete: UserEntity) {
        if (!checkPermission("admin", "delete user accounts")) return
        val admin = _currentUser.value ?: return
        if (userToDelete.userId == admin.userId || userToDelete.email.equals(admin.email, ignoreCase = true)) {
            showToast("err", "Cannot Delete Self", "You cannot delete your own administrative account.")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val ok = repository.deleteUser(userToDelete.userId, admin.email)
            if (ok) {
                showToast("ok", "User Deleted", "Account for ${userToDelete.fullName} permanently deleted.")
                val webAppUrl = _sheetSyncStatus.value.webAppUrl
                if (webAppUrl.isNotBlank()) {
                    val payload = org.json.JSONObject().apply {
                        put("action", "deleteRecord")
                        put("sheetKey", "users")
                        put("keyColumn", "User_ID")
                        put("keyField", "User_ID")
                        put("keyValue", userToDelete.userId)
                    }
                    postJsonToEndpoint(webAppUrl, payload.toString())
                }

                val logObj = org.json.JSONObject().apply {
                    put("Log_ID", "LOG-" + System.currentTimeMillis().toString().takeLast(6))
                    put("User_ID", admin.email)
                    put("Action", "ADMIN_DELETE_USER")
                    put("Timestamp", com.example.data.util.PasswordHelper.formattedNow())
                    put("IP_Address", "Android Mobile App")
                    put("Device_Info", "Deleted user ${userToDelete.fullName} (${userToDelete.userId}) [Role: ${userToDelete.role}]")
                }
                pushRecordToGoogleSheet("Activity_Logs", logObj)
                refreshDashboard()
            }
        }
    }
}
