package com.example.ui.courses

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.graphics.Color
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
}
