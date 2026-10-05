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

@Composable
fun QuizScreen(
    courseId: String,
    assessmentId: String,
    viewModel: LmsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

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
                    text = "Passing: ${assessment?.passingScore ?: 80}% · Time: ${assessment?.timeLimitMins ?: 20}m",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (questions.isNotEmpty()) {
                LmsBadge(
                    text = "${selectedAnswers.size}/${questions.size} Answered",
                    colorType = if (selectedAnswers.size == questions.size) "success" else "warning"
                )
            }
        }

        if (questions.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Quiz,
                title = "No Questions in this Assessment",
                description = "Questions have not yet been published for this quiz.",
                actionButton = {
                    Button(onClick = onBack) {
                        Text("Back to Course")
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
                        onSelectOption = { optKey ->
                            selectedAnswers = selectedAnswers + (question.questionId to optKey)
                        }
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
                        Text("Submit Assessment Answers", fontWeight = FontWeight.Bold)
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
                    Text("Quiz Results", fontWeight = FontWeight.Bold, fontSize = 20.sp)
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
                        text = result.message,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Score: ${result.earnedPoints} / ${result.totalPoints} points",
                        fontSize = 13.sp,
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
}

@Composable
fun QuestionCard(
    index: Int,
    question: QuestionEntity,
    selectedOption: String?,
    onSelectOption: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("question_card_${question.questionId}")
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
                LmsBadge(text = "${question.points} pts", colorType = "primary")
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
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) Color(0xFFEEF2FF) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFE2E8F0)
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
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFFF1F5F9)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else Color(0xFF475569)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = text,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
