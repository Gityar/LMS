package com.example.ui.courses

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.LmsViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LmsBadge
import com.example.ui.components.SectionHeader
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

enum class CourseTab {
    LESSONS, ASSIGNMENTS, ASSESSMENTS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    viewModel: LmsViewModel,
    onBack: () -> Unit,
    onOpenLesson: (String) -> Unit,
    onTakeQuiz: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allCourses by viewModel.allCourses.collectAsStateWithLifecycle()
    val enrollments by viewModel.learnerEnrollments.collectAsStateWithLifecycle()

    val course = remember(allCourses, courseId) {
        allCourses.find { it.courseId == courseId }
    }

    val isEnrolled = remember(enrollments, courseId) {
        enrollments.any { it.courseId == courseId }
    }

    var selectedTab by remember { mutableStateOf(CourseTab.LESSONS) }

    val lessons by viewModel.repository.getLessonsForCourse(courseId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val assignments by viewModel.repository.getAssignmentsForCourse(courseId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val assessments by viewModel.repository.getAssessmentsForCourse(courseId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val canManage = currentUser?.role?.lowercase() in listOf("admin", "instructor")
    val isLearner = currentUser?.role?.lowercase() == "learner"

    // Dialog state
    var showCreateLessonDialog by remember { mutableStateOf(false) }
    var showCreateAssignmentDialog by remember { mutableStateOf(false) }
    var showCreateAssessmentDialog by remember { mutableStateOf(false) }
    var assignmentToSubmit by remember { mutableStateOf<AssignmentEntity?>(null) }
    var assignmentForSubmissions by remember { mutableStateOf<AssignmentEntity?>(null) }
    var assessmentToAddQuestion by remember { mutableStateOf<AssessmentEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Back Button & Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_to_courses")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = course?.courseTitle ?: "Course Details",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            if (isLearner && !isEnrolled) {
                Button(
                    onClick = { viewModel.enrollCourse(courseId) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp).testTag("btn_enroll_course")
                ) {
                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Enroll", fontSize = 12.sp)
                }
            }
        }

        // Course Info Banner
        if (course != null) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            LmsBadge(text = course.category, colorType = "primary")
                            if (isEnrolled) {
                                LmsBadge(text = "✓ Enrolled", colorType = "success")
                            }
                        }
                        Text(
                            text = "Instructor: ${course.instructorName}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = course.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Secondary Navigation Tabs: Lessons, Assignments, Assessments
        PrimaryTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == CourseTab.LESSONS,
                onClick = { selectedTab = CourseTab.LESSONS },
                text = { Text("Lessons (${lessons.size})", fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Layers, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == CourseTab.ASSIGNMENTS,
                onClick = { selectedTab = CourseTab.ASSIGNMENTS },
                text = { Text("Assignments (${assignments.size})", fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == CourseTab.ASSESSMENTS,
                onClick = { selectedTab = CourseTab.ASSESSMENTS },
                text = { Text("Quizzes (${assessments.size})", fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TAB CONTENTS
        when (selectedTab) {
            CourseTab.LESSONS -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        SectionHeader(
                            title = "Modules & Lessons",
                            icon = Icons.Default.Layers,
                            actionButton = if (canManage) {
                                {
                                    Button(
                                        onClick = { showCreateLessonDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp).testTag("btn_add_lesson")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add Lesson", fontSize = 12.sp)
                                    }
                                }
                            } else null
                        )
                    }

                    if (lessons.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Default.LayersClear,
                                title = "No Lessons Available",
                                description = "Lessons for this neonatal module will appear here.",
                                actionButton = if (canManage) {
                                    {
                                        Button(onClick = { showCreateLessonDialog = true }) {
                                            Text("Add First Lesson")
                                        }
                                    }
                                } else null
                            )
                        }
                    } else {
                        items(lessons, key = { it.lessonId }) { lesson ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onOpenLesson(lesson.lessonId) }
                                    .testTag("lesson_item_${lesson.lessonId}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Module ${lesson.moduleNumber}: ${lesson.lessonTitle}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            LmsBadge(text = lesson.contentType, colorType = "primary")
                                            LmsBadge(text = "${lesson.durationMins} mins", colorType = "neutral")
                                            if (lesson.videoUrl.isNotBlank()) {
                                                LmsBadge(text = "🎥 Video", colorType = "warning")
                                            }
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Open",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }

            CourseTab.ASSIGNMENTS -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        SectionHeader(
                            title = "Assignments",
                            icon = Icons.Default.Assignment,
                            actionButton = if (canManage) {
                                {
                                    Button(
                                        onClick = { showCreateAssignmentDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp).testTag("btn_new_assignment")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("New Assignment", fontSize = 12.sp)
                                    }
                                }
                            } else null
                        )
                    }

                    if (assignments.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Default.Task,
                                title = "No Assignments Created",
                                description = "Practical clinical cases and checklists will be listed here.",
                                actionButton = if (canManage) {
                                    {
                                        Button(onClick = { showCreateAssignmentDialog = true }) {
                                            Text("Create Assignment")
                                        }
                                    }
                                } else null
                            )
                        }
                    } else {
                        items(assignments, key = { it.assignmentId }) { asg ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth().testTag("assignment_card_${asg.assignmentId}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = asg.assignmentTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (asg.dueDate.isNotBlank()) {
                                            LmsBadge(text = "Due: ${asg.dueDate}", colorType = "warning")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = asg.instructions,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isLearner) {
                                            Button(
                                                onClick = { assignmentToSubmit = asg },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("btn_submit_asg_${asg.assignmentId}")
                                            ) {
                                                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Submit Work", fontSize = 12.sp)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            OutlinedButton(
                                                onClick = { assignmentForSubmissions = asg },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("btn_my_subs_${asg.assignmentId}")
                                            ) {
                                                Text("Status / Grade", fontSize = 12.sp)
                                            }
                                        } else {
                                            Button(
                                                onClick = { assignmentForSubmissions = asg },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("btn_view_submissions_${asg.assignmentId}")
                                            ) {
                                                Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Submissions", fontSize = 12.sp)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            IconButton(
                                                onClick = { viewModel.deleteAssignment(asg.assignmentId) },
                                                modifier = Modifier.size(34.dp)
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }

            CourseTab.ASSESSMENTS -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        SectionHeader(
                            title = "Quizzes & Certifications",
                            icon = Icons.Default.Quiz,
                            actionButton = if (canManage) {
                                {
                                    Button(
                                        onClick = { showCreateAssessmentDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp).testTag("btn_new_assessment")
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("New Quiz", fontSize = 12.sp)
                                    }
                                }
                            } else null
                        )
                    }

                    if (assessments.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Default.AssignmentLate,
                                title = "No Quizzes Configured",
                                description = "Clinical evaluations and assessments will appear here.",
                                actionButton = if (canManage) {
                                    {
                                        Button(onClick = { showCreateAssessmentDialog = true }) {
                                            Text("Create Quiz")
                                        }
                                    }
                                } else null
                            )
                        }
                    } else {
                        items(assessments, key = { it.assessmentId }) { asm ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth().testTag("assessment_card_${asm.assessmentId}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = asm.assessmentTitle,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            LmsBadge(text = "${asm.timeLimitMins} Mins", colorType = "primary")
                                            LmsBadge(text = "Pass: ${asm.passingScore}%", colorType = "success")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = asm.description.ifBlank { "Multiple-choice clinical competency assessment." },
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        if (isLearner) {
                                            Button(
                                                onClick = { onTakeQuiz(asm.assessmentId) },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("btn_take_quiz_${asm.assessmentId}")
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Take Quiz", fontSize = 12.sp)
                                            }
                                        } else {
                                            Button(
                                                onClick = { assessmentToAddQuestion = asm },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("btn_add_q_${asm.assessmentId}")
                                            ) {
                                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Add Question", fontSize = 12.sp)
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            OutlinedButton(
                                                onClick = { onTakeQuiz(asm.assessmentId) },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                                modifier = Modifier.height(34.dp).testTag("btn_preview_quiz_${asm.assessmentId}")
                                            ) {
                                                Text("Preview Quiz", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }

    // MODAL: CREATE LESSON
    if (showCreateLessonDialog) {
        var lessonTitle by remember { mutableStateOf("") }
        var moduleNumber by remember { mutableStateOf("1") }
        var contentType by remember { mutableStateOf("Text") }
        var duration by remember { mutableStateOf("30") }
        var bodyContent by remember { mutableStateOf("") }
        var videoUrl by remember { mutableStateOf("") }
        var resourceUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateLessonDialog = false },
            title = { Text("Add New Lesson", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = lessonTitle,
                        onValueChange = { lessonTitle = it },
                        label = { Text("Lesson Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("dialog_lesson_title")
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = moduleNumber,
                            onValueChange = { moduleNumber = it },
                            label = { Text("Module #") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Duration (min)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = bodyContent,
                        onValueChange = { bodyContent = it },
                        label = { Text("Clinical Lesson Content (Notes/Guide)") },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = videoUrl,
                        onValueChange = { videoUrl = it },
                        label = { Text("Video Lecture URL (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (lessonTitle.isBlank()) return@Button
                        viewModel.createLesson(
                            courseId = courseId,
                            moduleNumber = moduleNumber.toIntOrNull() ?: 1,
                            title = lessonTitle,
                            contentType = contentType,
                            duration = duration.toIntOrNull() ?: 30,
                            body = bodyContent,
                            videoUrl = videoUrl,
                            resourceUrl = resourceUrl
                        )
                        showCreateLessonDialog = false
                    },
                    modifier = Modifier.testTag("dialog_lesson_save_btn")
                ) {
                    Text("Save Lesson")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateLessonDialog = false }) { Text("Cancel") }
            }
        )
    }

    // MODAL: CREATE ASSIGNMENT
    if (showCreateAssignmentDialog) {
        var asgTitle by remember { mutableStateOf("") }
        var asgDue by remember { mutableStateOf("2026-10-31") }
        var asgInstructions by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateAssignmentDialog = false },
            title = { Text("Create Assignment", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = asgTitle,
                        onValueChange = { asgTitle = it },
                        label = { Text("Assignment Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = asgDue,
                        onValueChange = { asgDue = it },
                        label = { Text("Due Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = asgInstructions,
                        onValueChange = { asgInstructions = it },
                        label = { Text("Instructions") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (asgTitle.isBlank()) return@Button
                        viewModel.createAssignment(courseId, 1, asgTitle, asgInstructions, asgDue)
                        showCreateAssignmentDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAssignmentDialog = false }) { Text("Cancel") }
            }
        )
    }

    // MODAL: SUBMIT ASSIGNMENT
    if (assignmentToSubmit != null) {
        val asg = assignmentToSubmit!!
        var fileName by remember { mutableStateOf("${currentUser?.fullName?.replace(" ", "_")}_Submission.pdf") }
        var notesOrLink by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { assignmentToSubmit = null },
            title = { Text("Submit: ${asg.assignmentTitle}", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Attach completed assignment document or enter submission link:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("File Name / Submission Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notesOrLink,
                        onValueChange = { notesOrLink = it },
                        label = { Text("Online File URL / Cloud Document Link") },
                        placeholder = { Text("https://drive.google.com/...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fileName.isBlank()) return@Button
                        viewModel.submitAssignment(asg.assignmentId, courseId, fileName, notesOrLink)
                        assignmentToSubmit = null
                    }
                ) {
                    Text("Submit Work")
                }
            },
            dismissButton = {
                TextButton(onClick = { assignmentToSubmit = null }) { Text("Cancel") }
            }
        )
    }

    // MODAL: VIEW SUBMISSIONS & GRADING
    if (assignmentForSubmissions != null) {
        val asg = assignmentForSubmissions!!
        val submissionsFlow = if (isLearner) {
            viewModel.repository.getSubmissionsForLearner(currentUser?.userId ?: "")
        } else {
            viewModel.repository.getSubmissionsForAssignment(asg.assignmentId)
        }
        val subList by submissionsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
        val matchingSubs = remember(subList, asg.assignmentId) {
            subList.filter { it.assignmentId == asg.assignmentId }
        }

        var subToGrade by remember { mutableStateOf<SubmissionEntity?>(null) }

        AlertDialog(
            onDismissRequest = { assignmentForSubmissions = null },
            title = { Text("Submissions for ${asg.assignmentTitle}", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                if (matchingSubs.isEmpty()) {
                    Text("No submissions recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 340.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(matchingSubs) { sub ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = sub.learnerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        LmsBadge(
                                            text = if (sub.grade.isNotBlank()) "Grade: ${sub.grade}" else "Pending",
                                            colorType = if (sub.grade.isNotBlank()) "success" else "warning"
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "File: ${sub.fileName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    if (sub.feedback.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Feedback: ${sub.feedback}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (canManage) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Button(
                                            onClick = { subToGrade = sub },
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(28.dp).align(Alignment.End)
                                        ) {
                                            Text("Grade", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { assignmentForSubmissions = null }) { Text("Close") }
            }
        )

        // Sub-modal: Grade submission
        if (subToGrade != null) {
            val s = subToGrade!!
            var gradeInput by remember { mutableStateOf(s.grade) }
            var feedbackInput by remember { mutableStateOf(s.feedback) }

            AlertDialog(
                onDismissRequest = { subToGrade = null },
                title = { Text("Grade: ${s.learnerName}", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = gradeInput,
                            onValueChange = { gradeInput = it },
                            label = { Text("Grade / Score (e.g. 95/100 or A)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = feedbackInput,
                            onValueChange = { feedbackInput = it },
                            label = { Text("Instructor Clinical Feedback") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (gradeInput.isBlank()) return@Button
                            viewModel.gradeSubmission(s.submissionId, gradeInput, feedbackInput)
                            subToGrade = null
                        }
                    ) {
                        Text("Save Grade")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { subToGrade = null }) { Text("Cancel") }
                }
            )
        }
    }

    // MODAL: CREATE ASSESSMENT
    if (showCreateAssessmentDialog) {
        var asmTitle by remember { mutableStateOf("") }
        var passingScore by remember { mutableStateOf("80") }
        var timeLimit by remember { mutableStateOf("20") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateAssessmentDialog = false },
            title = { Text("Create Assessment Quiz", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = asmTitle,
                        onValueChange = { asmTitle = it },
                        label = { Text("Quiz Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = passingScore,
                            onValueChange = { passingScore = it },
                            label = { Text("Pass Score (%)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = timeLimit,
                            onValueChange = { timeLimit = it },
                            label = { Text("Time Limit (min)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Description & Instructions") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (asmTitle.isBlank()) return@Button
                        viewModel.createAssessment(
                            courseId = courseId,
                            moduleNumber = 1,
                            title = asmTitle,
                            description = desc,
                            passingScore = passingScore.toIntOrNull() ?: 80,
                            timeLimit = timeLimit.toIntOrNull() ?: 20
                        )
                        showCreateAssessmentDialog = false
                    }
                ) {
                    Text("Save Quiz")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAssessmentDialog = false }) { Text("Cancel") }
            }
        )
    }

    // MODAL: ADD QUESTION
    if (assessmentToAddQuestion != null) {
        val asm = assessmentToAddQuestion!!
        var qText by remember { mutableStateOf("") }
        var optA by remember { mutableStateOf("") }
        var optB by remember { mutableStateOf("") }
        var optC by remember { mutableStateOf("") }
        var optD by remember { mutableStateOf("") }
        var correct by remember { mutableStateOf("A") }

        AlertDialog(
            onDismissRequest = { assessmentToAddQuestion = null },
            title = { Text("Add Question to ${asm.assessmentTitle}", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = qText,
                        onValueChange = { qText = it },
                        label = { Text("Clinical Question Text") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = optA,
                        onValueChange = { optA = it },
                        label = { Text("Option A") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = optB,
                        onValueChange = { optB = it },
                        label = { Text("Option B") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = optC,
                        onValueChange = { optC = it },
                        label = { Text("Option C") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = optD,
                        onValueChange = { optD = it },
                        label = { Text("Option D") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Correct Answer:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        listOf("A", "B", "C", "D").forEach { key ->
                            FilterChip(
                                selected = correct == key,
                                onClick = { correct = key },
                                label = { Text(key) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (qText.isBlank() || optA.isBlank() || optB.isBlank()) return@Button
                        viewModel.addQuestion(
                            assessmentId = asm.assessmentId,
                            text = qText,
                            optA = optA,
                            optB = optB,
                            optC = optC,
                            optD = optD,
                            correct = correct,
                            points = 25
                        )
                        assessmentToAddQuestion = null
                    }
                ) {
                    Text("Add Question")
                }
            },
            dismissButton = {
                TextButton(onClick = { assessmentToAddQuestion = null }) { Text("Cancel") }
            }
        )
    }
}
