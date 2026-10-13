package com.example.ui.courses

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LessonEntity
import com.example.ui.LmsViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LmsBadge
import com.example.ui.components.SectionHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    courseId: String,
    lessonId: String,
    viewModel: LmsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val lessons by viewModel.repository.getLessonsForCourse(courseId)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val lesson = remember(lessons, lessonId) {
        lessons.find { it.lessonId == lessonId }
    }

    val resources by viewModel.repository.getResourcesForLesson(lessonId)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    val canEdit = currentUser?.role?.lowercase() in listOf("admin", "instructor")

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUploadResourceDialog by remember { mutableStateOf(false) }

    var feedbackRating by remember { mutableStateOf(5) }
    var feedbackComment by remember { mutableStateOf("") }
    var isFeedbackSubmitted by remember { mutableStateOf(false) }

    val defaultSteps = remember(lesson?.lessonId) {
        val titleLower = (lesson?.lessonTitle ?: "").lowercase()
        when {
            titleLower.contains("sopa") || titleLower.contains("ventilation") || titleLower.contains("resuscitation") -> listOf(
                "M - Mask adjustment (Ensure airtight seal over nose and mouth)",
                "R - Reposition head (Slight extension in sniffing position)",
                "S - Suction mouth then nose (Clear obstructing secretions)",
                "O - Open mouth (Lift mandible gently forward)",
                "P - Pressure increase (Increase PIP by 5 cm H2O up to max 40)",
                "A - Alternative airway (Laryngeal mask airway or ETT placement)"
            )
            titleLower.contains("cpap") || titleLower.contains("respiratory") || titleLower.contains("distress") -> listOf(
                "1. Select appropriate binasal prong size (80% nares occlusion without blanching)",
                "2. Calibrate underwater seal bottle at 5 cm H2O depth",
                "3. Set gas flow rate to 6-8 L/min ensuring vigorous continuous bubbling",
                "4. Confirm heated humidifier set to 37°C with inline moisture traps",
                "5. Inspect nasal septum every 2 hours to prevent columella necrosis"
            )
            titleLower.contains("thermo") || titleLower.contains("heat") || titleLower.contains("warm") -> listOf(
                "1. Pre-warm radiant warmer mattress to 36.5°C prior to delivery",
                "2. Apply skin servo-control probe to right upper abdominal quadrant",
                "3. Place occlusive polyethylene wrap without drying if <32 weeks",
                "4. Check axillary temperature every 30 minutes until stable (36.5°C-37.5°C)",
                "5. Initiate Kangaroo Mother Care (KMC) as soon as newborn is stabilized"
            )
            else -> listOf(
                "1. Hand hygiene & aseptic technique verified",
                "2. Essential resuscitation equipment checked & operational",
                "3. Patient vitals monitored continuously (SpO2, Heart Rate)",
                "4. Evidence-based protocol followed in exact sequence",
                "5. Clinical documentation & debriefing completed"
            )
        }
    }

    var checkedSteps by remember(defaultSteps) {
        mutableStateOf(List(defaultSteps.size) { false })
    }
    var showCertificateDialog by remember { mutableStateOf(false) }
    var candidateNameInput by remember(currentUser) {
        mutableStateOf(currentUser?.fullName ?: "Nurse Clinician")
    }
    var isCertClaimed by remember(lesson?.lessonId) { mutableStateOf(false) }

    val totalSteps = defaultSteps.size
    val doneCount = checkedSteps.count { it }
    val progressPercent = if (totalSteps > 0) (doneCount * 100) / totalSteps else 0
    val threshold = 70
    val isPassed = progressPercent >= threshold

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top Back Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_to_lesson_list")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = lesson?.lessonTitle ?: "Lesson Content",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
            if (canEdit && lesson != null) {
                IconButton(onClick = { showEditDialog = true }, modifier = Modifier.testTag("btn_edit_lesson")) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.testTag("btn_delete_lesson")) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }

        if (lesson == null) {
            EmptyStateView(
                icon = Icons.Default.Description,
                title = "Lesson Not Found",
                description = "The requested lesson is unavailable."
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Lesson Metadata Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Module ${lesson.moduleNumber}: ${lesson.lessonTitle}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    LmsBadge(text = lesson.contentType, colorType = "primary")
                                    LmsBadge(text = "${lesson.durationMins} Mins", colorType = "neutral")
                                }
                            }

                            if (lesson.videoUrl.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lesson.videoUrl))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            viewModel.showToast("err", "Cannot Open Video", "Please verify video URL.")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("btn_watch_video")
                                ) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Watch Clinical Video Lecture", fontWeight = FontWeight.SemiBold)
                                }
                            }

                            if (lesson.resourceUrl.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lesson.resourceUrl))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            viewModel.showToast("err", "Cannot Open Link", "Invalid resource URL.")
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("btn_external_resource")
                                ) {
                                    Icon(Icons.Default.Link, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("External Protocol & Evidence Reference")
                                }
                            }
                        }
                    }
                }

                // Lesson Body Content Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Clinical Notes & Protocol Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = lesson.contentBody.ifBlank { "No textual notes provided for this lesson." },
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // OSCE Station Checklist & Rubric Card (70% Pass Rule)
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPassed) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isPassed) Color(0xFF86EFAC) else MaterialTheme.colorScheme.outlineVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("card_osce_rubric")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Checklist,
                                        contentDescription = "OSCE Rubric",
                                        tint = if (isPassed) Color(0xFF059669) else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "OSCE Station Checklist Rubric",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Accredited Clinical Evaluation · 70% Pass Rule",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                TextButton(
                                    onClick = {
                                        checkedSteps = List(defaultSteps.size) { false }
                                        isCertClaimed = false
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Reset", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Steps checklist
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                defaultSteps.forEachIndexed { index, stepText ->
                                    val checked = checkedSteps.getOrElse(index) { false }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = if (checked) Color(0xFFDCFCE7).copy(alpha = 0.5f) else Color(0xFFF8FAFC),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Checkbox(
                                            checked = checked,
                                            onCheckedChange = { isChecked ->
                                                val mutable = checkedSteps.toMutableList()
                                                if (index < mutable.size) mutable[index] = isChecked
                                                checkedSteps = mutable
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = Color(0xFF059669)
                                            ),
                                            modifier = Modifier.size(32.dp).testTag("osce_step_checkbox_$index")
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = stepText,
                                            fontSize = 12.sp,
                                            lineHeight = 16.sp,
                                            fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (checked) Color(0xFF065F46) else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Real-time Progress Bar & Benchmarking
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Progress: $doneCount of $totalSteps steps ($progressPercent%)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPassed) Color(0xFF059669) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Benchmark: ≥ 70%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { progressPercent / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = if (isPassed) Color(0xFF059669) else MaterialTheme.colorScheme.primary,
                                    trackColor = Color(0xFFE2E8F0)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Certification Status Callout
                            if (isPassed) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFDCFCE7),
                                    border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(text = "🏆", fontSize = 24.sp)
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "OSCE Clinical Competency Achieved! ($progressPercent%)",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF065F46)
                                                )
                                                Text(
                                                    text = "Candidate has met the ≥ 70% procedural benchmark. Accredited certificate is unlocked.",
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF047857)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = { showCertificateDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("btn_claim_osce_certificate")
                                        ) {
                                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (isCertClaimed) "View Claimed OSCE Certificate" else "📜 Claim OSCE Certificate",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            } else {
                                val remaining = kotlin.math.max(0, kotlin.math.ceil((threshold.toDouble() / 100.0) * totalSteps).toInt() - doneCount)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFFFFBEB),
                                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                        Text(
                                            text = "Complete $remaining more step(s) to reach 70% and earn your accredited OSCE certificate.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF92400E),
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Resources & Attachments Section
                item {
                    SectionHeader(
                        title = "Files & Clinical Attachments",
                        icon = Icons.Default.AttachFile,
                        actionButton = if (canEdit) {
                            {
                                Button(
                                    onClick = { showUploadResourceDialog = true },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("btn_upload_resource")
                                ) {
                                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Attachment", fontSize = 12.sp)
                                }
                            }
                        } else null
                    )
                }

                if (resources.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No supplementary files attached to this lesson.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(resources, key = { it.resourceId }) { res ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth().testTag("resource_item_${res.resourceId}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = res.fileName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${res.uploadedBy} · ${res.uploadedDate}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.fileUrl))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                viewModel.showToast("info", "File Resource", "File: ${res.fileName}")
                                            }
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Open", fontSize = 11.sp)
                                    }
                                    if (canEdit) {
                                        IconButton(
                                            onClick = { viewModel.deleteResource(res.resourceId) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Lesson Feedback & Rating Card (1 to 5 Stars & Comment to Google Sheet)
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_lesson_feedback")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Lesson Feedback & Rating",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Rate this clinical lesson (1-5 stars) and leave a short comment. Your feedback is synced directly to the Google Sheet.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // 1-5 Star interactive selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                for (star in 1..5) {
                                    IconButton(
                                        onClick = {
                                            feedbackRating = star
                                            isFeedbackSubmitted = false
                                        },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .testTag("btn_star_$star")
                                    ) {
                                        Icon(
                                            imageVector = if (star <= feedbackRating) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "$star stars",
                                            tint = if (star <= feedbackRating) Color(0xFFF59E0B) else Color(0xFFCBD5E1),
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = when (feedbackRating) {
                                    5 -> "Excellent (5/5) — Clear & actionable clinical protocol"
                                    4 -> "Very Good (4/5) — Well explained"
                                    3 -> "Good (3/5) — Adequate overview"
                                    2 -> "Fair (2/5) — Needs more clinical detail"
                                    else -> "Needs Improvement (1/5)"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = feedbackComment,
                                onValueChange = { feedbackComment = it },
                                label = { Text("Short comment or clinical note") },
                                placeholder = { Text("E.g., Great ventilation corrective steps, clear explanation...") },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_lesson_comment")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    viewModel.submitLessonFeedback(
                                        lessonId = lesson.lessonId,
                                        rating = feedbackRating,
                                        comment = feedbackComment
                                    )
                                    isFeedbackSubmitted = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("btn_submit_lesson_feedback")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Submit Feedback to Google Sheet", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            if (isFeedbackSubmitted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✓ Feedback recorded! Synced with Google Sheets.",
                                    color = Color(0xFF059669),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }

    // EDIT LESSON MODAL
    if (showEditDialog && lesson != null) {
        var editTitle by remember { mutableStateOf(lesson.lessonTitle) }
        var editModule by remember { mutableStateOf(lesson.moduleNumber.toString()) }
        var editDuration by remember { mutableStateOf(lesson.durationMins.toString()) }
        var editBody by remember { mutableStateOf(lesson.contentBody) }
        var editVideo by remember { mutableStateOf(lesson.videoUrl) }
        var editResource by remember { mutableStateOf(lesson.resourceUrl) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Lesson", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editModule,
                            onValueChange = { editModule = it },
                            label = { Text("Module #") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editDuration,
                            onValueChange = { editDuration = it },
                            label = { Text("Duration (min)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = editBody,
                        onValueChange = { editBody = it },
                        label = { Text("Content Notes") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editVideo,
                        onValueChange = { editVideo = it },
                        label = { Text("Video URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateLesson(
                            lessonId = lesson.lessonId,
                            moduleNumber = editModule.toIntOrNull() ?: lesson.moduleNumber,
                            title = editTitle,
                            contentType = lesson.contentType,
                            duration = editDuration.toIntOrNull() ?: lesson.durationMins,
                            body = editBody,
                            videoUrl = editVideo,
                            resourceUrl = editResource
                        )
                        showEditDialog = false
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }

    // DELETE LESSON CONFIRMATION
    if (showDeleteDialog && lesson != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Lesson?") },
            text = { Text("Are you sure you want to delete '${lesson.lessonTitle}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLesson(lesson.lessonId, courseId)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ATTACH RESOURCE DIALOG
    if (showUploadResourceDialog) {
        var resFileName by remember { mutableStateOf("Neonatal_Clinical_Reference.pdf") }
        var resFileUrl by remember { mutableStateOf("https://hospital.org/lms/docs/Neonatal_Reference.pdf") }

        AlertDialog(
            onDismissRequest = { showUploadResourceDialog = false },
            title = { Text("Attach Lesson Resource", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = resFileName,
                        onValueChange = { resFileName = it },
                        label = { Text("File Name (e.g. NRP_Card.pdf)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = resFileUrl,
                        onValueChange = { resFileUrl = it },
                        label = { Text("File URL or Drive Link") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resFileName.isBlank()) return@Button
                        viewModel.addResource(lessonId, resFileName, "PDF", resFileUrl)
                        showUploadResourceDialog = false
                    }
                ) {
                    Text("Attach File")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadResourceDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ACCREDITED OSCE COMPETENCY CERTIFICATE DIALOG
    if (showCertificateDialog && lesson != null) {
        Dialog(onDismissRequest = { showCertificateDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(2.dp, Color(0xFFD97706)),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gold Medal Seal
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(27.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏅", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "NEONATAL NURSING CLINICAL LMS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4338CA),
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Certificate of Clinical Competency",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "This officially accredits that the clinician:",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = candidateNameInput,
                        onValueChange = { candidateNameInput = it },
                        label = { Text("Candidate Full Name", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_osce_cert_candidate_name")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "has successfully demonstrated procedural mastery and surpassed the ≥ 70% OSCE benchmark in:",
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = lesson.lessonTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF1E293B),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3-Column stats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Score", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("$progressPercent%", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF059669))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Standard", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("≥ 70% Pass", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Result", fontSize = 10.sp, color = Color(0xFF64748B))
                            Text("ACCREDITED", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF059669))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                viewModel.recordOsceCertificate(
                                    lessonId = lesson.lessonId,
                                    lessonTitle = lesson.lessonTitle,
                                    courseId = courseId,
                                    candidateName = candidateNameInput,
                                    scorePercent = progressPercent,
                                    totalSteps = totalSteps,
                                    checkedSteps = doneCount
                                )
                                isCertClaimed = true
                                viewModel.showToast("ok", "Certificate Claimed!", "Accredited OSCE Certificate committed to Google Sheets & local DB.")
                                showCertificateDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("btn_save_osce_cert")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Claim & Sync to Google Sheets", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_SUBJECT,
                                            "Accredited OSCE Clinical Competency Certificate: ${lesson.lessonTitle}"
                                        )
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "🏅 Clinical Competency Certificate\nCandidate: $candidateNameInput\nStation: ${lesson.lessonTitle}\nScore Achieved: $progressPercent% (Standard: ≥ 70%)\nAccredited by Neonatal Resuscitation & Nursing LMS."
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Certificate"))
                                } catch (e: Exception) {
                                    viewModel.showToast("info", "Certificate", "Certificate ready for printing.")
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share / Print Certificate", fontSize = 12.sp)
                        }

                        TextButton(
                            onClick = { showCertificateDialog = false },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Close", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
