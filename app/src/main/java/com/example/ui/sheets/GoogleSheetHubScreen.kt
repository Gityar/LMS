package com.example.ui.sheets

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.LmsViewModel
import com.example.ui.components.LmsBadge
import com.example.ui.components.SectionHeader

data class SheetSchemaInfo(
    val sheetName: String,
    val description: String,
    val rowCount: Int,
    val columns: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleSheetHubScreen(
    viewModel: LmsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val syncStatus by viewModel.sheetSyncStatus.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allCourses by viewModel.allCourses.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allLogs by viewModel.allLogs.collectAsStateWithLifecycle()

    val isAdmin = currentUser?.role?.lowercase() == "admin"
    if (!isAdmin) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Access Restricted",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Access Restricted",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Google Sheet management is reserved exclusively for the system administrator (yaregalsemanew@gmail.com).",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onBack) {
                Text("Return to Dashboard")
            }
        }
        return
    }

    var showEditSheetDialog by remember { mutableStateOf(false) }
    var selectedSheetPreview by remember { mutableStateOf<SheetSchemaInfo?>(null) }

    val sheetUrl = "https://docs.google.com/spreadsheets/d/${syncStatus.spreadsheetId}/edit"

    val sheetsList = listOf(
        SheetSchemaInfo("Users", "Learner, Instructor, and Admin credentials & roles", allUsers.size, listOf("User_ID","Full_Name","Email","Role","Account_Status","Last_Login")),
        SheetSchemaInfo("Courses", "Clinical neonatal curriculum courses", allCourses.size, listOf("Course_ID","Course_Title","Category","Instructor_Name","Status","Total_Modules")),
        SheetSchemaInfo("Lessons", "Curriculum modules and lesson contents", 8, listOf("Lesson_ID","Course_ID","Module_Number","Lesson_Title","Content_Type","Duration_Mins")),
        SheetSchemaInfo("Enrollments", "Course enrollment records and learner progress", 4, listOf("Enrollment_ID","Learner_ID","Learner_Name","Course_ID","Progress_Percentage")),
        SheetSchemaInfo("Activity_Logs", "System activity & security audit trail", allLogs.size, listOf("Activity_ID","User_Email","Action_Type","Timestamp","Status_Message")),
        SheetSchemaInfo("Lesson_Content", "Clinical lecture texts and reference URLs", 8, listOf("Lesson_ID","Content_Body","Video_URL","Resource_URL","Status")),
        SheetSchemaInfo("Lesson_Resources", "Uploaded clinical guides and protocol cards", 4, listOf("Resource_ID","Lesson_ID","File_Name","Mime_Type","File_URL")),
        SheetSchemaInfo("Assignments", "Clinical simulations and practical checklists", 3, listOf("Assignment_ID","Course_ID","Assignment_Title","Due_Date","Status")),
        SheetSchemaInfo("Assignment_Submissions", "Learner submitted work, grades, and faculty feedback", 2, listOf("Submission_ID","Assignment_ID","Learner_Name","Grade","Feedback","Status")),
        SheetSchemaInfo("Assessments", "Module quizzes and certifications", 2, listOf("Assessment_ID","Course_ID","Assessment_Title","Passing_Score","Time_Limit_Mins")),
        SheetSchemaInfo("Questions", "Multiple-choice clinical questions and answer keys", 6, listOf("Question_ID","Assessment_ID","Question_Text","Option_A","Option_B","Correct_Answer")),
        SheetSchemaInfo("Assessment_Attempts", "Learner quiz attempt logs, scores, and pass status", 3, listOf("Attempt_ID","Assessment_ID","Learner_Name","Score","Percentage","Passed")),
        SheetSchemaInfo("Password_Resets", "Verification tokens and recovery requests", 1, listOf("Reset_ID","User_Email","Verification_Code/Token","Requested_At","Is_Used"))
    )

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
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_from_sheets")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Google Sheet Hub",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Owner: ${syncStatus.ownerEmail}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LmsBadge(text = "Connected", colorType = "success")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Main Connection Status Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F9D58)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = "Google Sheets",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Neonatal LMS Database",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Google Sheets & Drive Integration",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { showEditSheetDialog = true }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(14.dp))

                        // Details grid
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Owner", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(syncStatus.ownerEmail, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Spreadsheet ID", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${syncStatus.spreadsheetId.take(12)}...", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Active Sheets", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${syncStatus.syncedSheetsCount} Sheets Verified", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF059669))
                            }
                            Column {
                                Text("Last Synced", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(syncStatus.lastSyncTime, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: Open in Browser & Sync Now
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sheetUrl))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        viewModel.showToast("info", "Sheet URL", sheetUrl)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("btn_open_google_sheet")
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Google Sheet", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.syncWithGoogleSheet() },
                                enabled = !syncStatus.isSyncing,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("btn_sync_google_sheet")
                            ) {
                                if (syncStatus.isSyncing) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Syncing...", fontSize = 13.sp)
                                } else {
                                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Sync Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Copy Link button
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Google Sheet URL", sheetUrl)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showToast("ok", "Copied", "Google Sheet URL copied to clipboard.")
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Sheet URL", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Sheets Schema Breakdown
            item {
                SectionHeader(
                    title = "Database Sheets (13 Verified)",
                    icon = Icons.Default.ViewList
                )
                Text(
                    text = "Real-time records mapped directly to the Google Sheet tabs:",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(sheetsList) { sheet ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sheet_card_${sheet.sheetName}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GridOn,
                                    contentDescription = null,
                                    tint = Color(0xFF0F9D58),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = sheet.sheetName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            LmsBadge(text = "${sheet.rowCount} Records", colorType = "primary")
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = sheet.description,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Columns: ${sheet.columns.joinToString(", ")}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }

    // EDIT SHEET ID & WEB APP ENDPOINT DIALOG
    if (showEditSheetDialog) {
        var inputId by remember { mutableStateOf(syncStatus.spreadsheetId) }
        var inputWebAppUrl by remember { mutableStateOf(syncStatus.webAppUrl) }

        AlertDialog(
            onDismissRequest = { showEditSheetDialog = false },
            title = { Text("Configure Google Sheet & Web App", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Sheet Owner: ${syncStatus.ownerEmail}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = inputId,
                        onValueChange = { inputId = it },
                        label = { Text("Spreadsheet ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = inputWebAppUrl,
                        onValueChange = { inputWebAppUrl = it },
                        label = { Text("Web App Deployment URL (Optional)") },
                        placeholder = { Text("https://script.google.com/macros/s/.../exec", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Paste your Apps Script Web App URL to push entries straight to your sheets when clicking 'Sync Now'.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputId.isNotBlank()) {
                            viewModel.updateGoogleSheetId(inputId.trim())
                        }
                        viewModel.updateWebAppUrl(inputWebAppUrl.trim())
                        showEditSheetDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditSheetDialog = false }) { Text("Cancel") }
            }
        )
    }
}
