package com.example.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AssessmentEntity
import com.example.data.model.QuestionEntity
import com.example.ui.LmsViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LmsBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    courseId: String,
    assessmentId: String,
    viewModel: LmsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val canManage = currentUser?.role?.lowercase() in listOf("admin", "instructor")

    val assessments by viewModel.repository.getAssessmentsForCourse(courseId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val assessment = remember(assessments, assessmentId) {
        assessments.find { it.assessmentId == assessmentId }
    }

    val questions by viewModel.repository.getQuestionsForAssessment(assessmentId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val quizResult by viewModel.quizResult.collectAsStateWithLifecycle()

    // Map of questionId -> selected key ("A", "B", "C", "D")
    var selectedAnswers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Dialog states for Instructor / Admin
    var showEditSettingsDialog by remember { mutableStateOf(false) }
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var questionToEdit by remember { mutableStateOf<QuestionEntity?>(null) }
    var questionToDelete by remember { mutableStateOf<QuestionEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_from_quiz")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assessment?.assessmentTitle ?: "Clinical Assessment",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1
                )
                Text(
                    text = "Passing Threshold: ${assessment?.passingScore ?: 80}% · Time: ${assessment?.timeLimitMins ?: 20}m",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (canManage) {
                IconButton(
                    onClick = { showEditSettingsDialog = true },
                    modifier = Modifier.testTag("btn_quiz_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Exam Settings",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (questions.isNotEmpty()) {
                LmsBadge(
                    text = "${selectedAnswers.size}/${questions.size} Answered",
                    colorType = if (selectedAnswers.size == questions.size) "success" else "warning"
                )
            }
        }

        // Assessment Settings Banner for Instructors/Admins
        if (canManage && assessment != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⚙️ Exam Configuration",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            LmsBadge(text = "${assessment.passingScore}% Passing Grade", colorType = "primary")
                        }
                        Text(
                            text = "Admin/Instructor Studio: Set passing criteria, edit questions, and manage question bank.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Row {
                        FilledTonalButton(
                            onClick = { showEditSettingsDialog = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Edit Passing %", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = { showAddQuestionDialog = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Add Q", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        if (questions.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Quiz,
                title = "No Questions in this Assessment",
                description = if (canManage) "Click 'Add Question' above to publish your first exam question." else "Questions have not yet been published for this quiz.",
                actionButton = {
                    if (canManage) {
                        Button(onClick = { showAddQuestionDialog = true }) {
                            Text("+ Add New Question")
                        }
                    } else {
                        Button(onClick = onBack) {
                            Text("Back to Course")
                        }
                    }
                }
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(questions, key = { _, q -> q.questionId }) { index, question ->
                    QuestionCard(
                        index = index + 1,
                        question = question,
                        selectedOption = selectedAnswers[question.questionId],
                        canManage = canManage,
                        onSelectOption = { optKey ->
                            selectedAnswers = selectedAnswers + (question.questionId to optKey)
                        },
                        onEdit = { questionToEdit = question },
                        onDelete = { questionToDelete = question }
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (selectedAnswers.size < questions.size) {
                                viewModel.showToast(
                                    "warn",
                                    "Incomplete",
                                    "You have answered ${selectedAnswers.size} of ${questions.size} questions."
                                )
                            }
                            viewModel.submitQuiz(assessmentId, selectedAnswers)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_submit_quiz_answers")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (canManage) "Test Simulator Evaluation" else "Submit Assessment Answers",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // RESULTS MODAL
    if (quizResult != null) {
        val result = quizResult!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissQuizResult() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = if (result.passed) Icons.Default.EmojiEvents else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (result.passed) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Assessment Evaluation", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${result.percentage}%",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (result.passed) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (result.passed) "Competency Passed!" else "Needs Review",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (result.passed) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Score: ${result.earnedPoints} / ${result.totalPoints} points (Required: ${assessment?.passingScore ?: 80}%)",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (result.passed) "Official Certificate of Completion awarded to your portfolio." else "Please review the neonatal clinical protocol and re-test.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissQuizResult()
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Return to Course")
                }
            }
        )
    }

    // DIALOG: EDIT ASSESSMENT SETTINGS (Admin/Instructor)
    if (showEditSettingsDialog && assessment != null) {
        var editTitle by remember { mutableStateOf(assessment.assessmentTitle) }
        var editPassingScore by remember { mutableStateOf(assessment.passingScore.toString()) }

        AlertDialog(
            onDismissRequest = { showEditSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Assessment Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Assessment Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPassingScore,
                        onValueChange = { editPassingScore = it },
                        label = { Text("Passing Score Threshold (%)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Students achieving this score or higher will be granted their Clinical Certificate of Completion.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val score = editPassingScore.toIntOrNull() ?: 80
                        viewModel.updateAssessmentSettings(assessment.assessmentId, editTitle, score)
                        showEditSettingsDialog = false
                    }
                ) {
                    Text("Save Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: ADD QUESTION
    if (showAddQuestionDialog && assessment != null) {
        var qText by remember { mutableStateOf("") }
        var optA by remember { mutableStateOf("") }
        var optB by remember { mutableStateOf("") }
        var optC by remember { mutableStateOf("") }
        var optD by remember { mutableStateOf("") }
        var correct by remember { mutableStateOf("A") }
        var points by remember { mutableStateOf("25") }

        AlertDialog(
            onDismissRequest = { showAddQuestionDialog = false },
            title = { Text("Add Exam Question", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = qText,
                        onValueChange = { qText = it },
                        label = { Text("Question Scenario / Prompt") },
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = correct,
                            onValueChange = { correct = it.uppercase() },
                            label = { Text("Correct (A,B,C,D)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = points,
                            onValueChange = { points = it },
                            label = { Text("Points") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (qText.isBlank() || optA.isBlank() || optB.isBlank()) return@Button
                        viewModel.addQuestion(
                            assessmentId = assessment.assessmentId,
                            text = qText,
                            optA = optA,
                            optB = optB,
                            optC = optC,
                            optD = optD,
                            correct = correct,
                            points = points.toIntOrNull() ?: 25
                        )
                        showAddQuestionDialog = false
                    }
                ) {
                    Text("Add Question")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddQuestionDialog = false }) { Text("Cancel") }
            }
        )
    }

    // DIALOG: EDIT QUESTION
    if (questionToEdit != null && assessment != null) {
        val q = questionToEdit!!
        var qText by remember { mutableStateOf(q.questionText) }
        var optA by remember { mutableStateOf(q.optionA) }
        var optB by remember { mutableStateOf(q.optionB) }
        var optC by remember { mutableStateOf(q.optionC) }
        var optD by remember { mutableStateOf(q.optionD) }
        var correct by remember { mutableStateOf(q.correctAnswer) }
        var points by remember { mutableStateOf(q.points.toString()) }

        AlertDialog(
            onDismissRequest = { questionToEdit = null },
            title = { Text("Edit Test Question", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = qText,
                        onValueChange = { qText = it },
                        label = { Text("Question Prompt") },
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = correct,
                            onValueChange = { correct = it.uppercase() },
                            label = { Text("Correct (A,B,C,D)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = points,
                            onValueChange = { points = it },
                            label = { Text("Points") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateQuestion(
                            questionId = q.questionId,
                            assessmentId = q.assessmentId,
                            text = qText,
                            optA = optA,
                            optB = optB,
                            optC = optC,
                            optD = optD,
                            correct = correct,
                            points = points.toIntOrNull() ?: 25
                        )
                        questionToEdit = null
                    }
                ) {
                    Text("Update Question")
                }
            },
            dismissButton = {
                TextButton(onClick = { questionToEdit = null }) { Text("Cancel") }
            }
        )
    }

    // DIALOG: DELETE QUESTION CONFIRMATION
    if (questionToDelete != null) {
        val q = questionToDelete!!
        AlertDialog(
            onDismissRequest = { questionToDelete = null },
            title = { Text("Delete Question?") },
            text = { Text("Are you sure you want to delete this question: '${q.questionText}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteQuestion(q.questionId)
                        questionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { questionToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun QuestionCard(
    index: Int,
    question: QuestionEntity,
    selectedOption: String?,
    canManage: Boolean,
    onSelectOption: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_card_${question.questionId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "$index. ${question.questionText}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LmsBadge(text = "${question.points} pts", colorType = "primary")
                    if (canManage) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Question", modifier = Modifier.size(16.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Question", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val options = listOf(
                "A" to question.optionA,
                "B" to question.optionB,
                "C" to question.optionC,
                "D" to question.optionD
            ).filter { it.second.isNotBlank() }

            options.forEach { (key, text) ->
                val isSelected = selectedOption == key
                val isCorrect = question.correctAnswer.equals(key, ignoreCase = true)

                val surfaceColor = if (canManage && isCorrect) {
                    Color(0xFFECFDF5)
                } else if (isSelected) {
                    Color(0xFFEEF2FF)
                } else {
                    MaterialTheme.colorScheme.surface
                }

                val borderColor = if (canManage && isCorrect) {
                    Color(0xFF10B981)
                } else if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color(0xFFE2E8F0)
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = surfaceColor,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected || (canManage && isCorrect)) 2.dp else 1.dp,
                        color = borderColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSelectOption(key) }
                        .testTag("option_${key}_${question.questionId}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (canManage && isCorrect) Color(0xFF10B981)
                                    else if (isSelected) MaterialTheme.colorScheme.primary
                                    else Color(0xFFF1F5F9)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if ((canManage && isCorrect) || isSelected) Color.White else Color(0xFF475569)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = text,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        if (canManage && isCorrect) {
                            Text(
                                text = "✓ Correct",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                        }
                    }
                }
            }

            if (canManage && question.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Rationale: ${question.explanation}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
