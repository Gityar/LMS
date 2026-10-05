package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.CourseEntity
import com.example.ui.LmsViewModel
import com.example.ui.Screen
import com.example.ui.components.LmsBadge
import com.example.ui.components.LmsStatCard
import com.example.ui.components.SectionHeader

@Composable
fun DashboardScreen(
    viewModel: LmsViewModel,
    onOpenCourse: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val allCourses by viewModel.allCourses.collectAsStateWithLifecycle()
    val enrollments by viewModel.learnerEnrollments.collectAsStateWithLifecycle()

    val enrolledCourseIds = remember(enrollments) {
        enrollments.map { it.courseId }.toSet()
    }

    LaunchedEffect(currentUser) {
        viewModel.refreshDashboard()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, ${currentUser?.fullName ?: "Nurse"} 👋",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (currentUser?.role?.lowercase()) {
                                "admin" -> "Administrator Portal · System Operations & Oversight"
                                "instructor" -> "Faculty Educator Portal · Curriculum & Grading"
                                else -> "Neonatal Clinical Training · Evidence-Based Practice"
                            },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    LmsBadge(
                        text = (currentUser?.role ?: "Learner").uppercase(),
                        colorType = "primary"
                    )
                }
            }
        }

        // Stats Grid
        item {
            val role = currentUser?.role?.lowercase() ?: "learner"
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (role) {
                        "learner" -> {
                            LmsStatCard(
                                value = stats.totalCourses.toString(),
                                label = "Available Courses",
                                icon = Icons.Default.Book,
                                iconBgColor = Color(0xFFE0E7FF),
                                iconColor = Color(0xFF4F46E5),
                                modifier = Modifier.weight(1f)
                            )
                            LmsStatCard(
                                value = stats.myEnrollments.toString(),
                                label = "My Enrollments",
                                icon = Icons.Default.School,
                                iconBgColor = Color(0xFFD1FAE5),
                                iconColor = Color(0xFF059669),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        "instructor" -> {
                            LmsStatCard(
                                value = stats.myCourses.toString(),
                                label = "My Courses",
                                icon = Icons.Default.Book,
                                iconBgColor = Color(0xFFE0E7FF),
                                iconColor = Color(0xFF4F46E5),
                                modifier = Modifier.weight(1f)
                            )
                            LmsStatCard(
                                value = stats.totalLessons.toString(),
                                label = "Total Lessons",
                                icon = Icons.Default.Layers,
                                iconBgColor = Color(0xFFF3E8FF),
                                iconColor = Color(0xFF7C3AED),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        else -> { // admin
                            LmsStatCard(
                                value = stats.totalCourses.toString(),
                                label = "Courses",
                                icon = Icons.Default.Book,
                                iconBgColor = Color(0xFFE0E7FF),
                                iconColor = Color(0xFF4F46E5),
                                modifier = Modifier.weight(1f)
                            )
                            LmsStatCard(
                                value = stats.totalLessons.toString(),
                                label = "Lessons",
                                icon = Icons.Default.Layers,
                                iconBgColor = Color(0xFFF3E8FF),
                                iconColor = Color(0xFF7C3AED),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    when (role) {
                        "learner" -> {
                            LmsStatCard(
                                value = stats.avgScore,
                                label = "Avg Quiz Score",
                                icon = Icons.Default.Star,
                                iconBgColor = Color(0xFFFEF3C7),
                                iconColor = Color(0xFFD97706),
                                modifier = Modifier.weight(1f)
                            )
                            LmsStatCard(
                                value = stats.mySubmissions.toString(),
                                label = "Submissions",
                                icon = Icons.Default.FileUpload,
                                iconBgColor = Color(0xFFF3E8FF),
                                iconColor = Color(0xFF7C3AED),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        "instructor" -> {
                            LmsStatCard(
                                value = stats.mySubmissions.toString(),
                                label = "Submissions",
                                icon = Icons.Default.AssignmentTurnedIn,
                                iconBgColor = Color(0xFFD1FAE5),
                                iconColor = Color(0xFF059669),
                                modifier = Modifier.weight(1f)
                            )
                            LmsStatCard(
                                value = stats.myAttempts.toString(),
                                label = "Quiz Attempts",
                                icon = Icons.Default.FactCheck,
                                iconBgColor = Color(0xFFFEF3C7),
                                iconColor = Color(0xFFD97706),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        else -> { // admin
                            LmsStatCard(
                                value = stats.totalUsers.toString(),
                                label = "Total Users",
                                icon = Icons.Default.People,
                                iconBgColor = Color(0xFFD1FAE5),
                                iconColor = Color(0xFF059669),
                                modifier = Modifier.weight(1f)
                            )
                            LmsStatCard(
                                value = stats.totalAttempts.toString(),
                                label = "Total Attempts",
                                icon = Icons.Default.FactCheck,
                                iconBgColor = Color(0xFFFEF3C7),
                                iconColor = Color(0xFFD97706),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions
        item {
            SectionHeader(title = "Quick Actions", icon = Icons.Default.Bolt)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isAdmin = currentUser?.role?.lowercase() == "admin"
                val isInstructor = currentUser?.role?.lowercase() == "instructor"

                if (isAdmin) {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Courses) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("qa_manage_courses_btn")
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Courses", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.GoogleSheetHub) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("qa_google_sheet_btn")
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, tint = Color(0xFF0F9D58), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sheets", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.Users) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("qa_manage_users_btn")
                    ) {
                        Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Users", fontSize = 12.sp)
                    }
                } else if (isInstructor) {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Courses) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("qa_manage_courses_btn")
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Manage Courses & Curriculum", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Courses) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("qa_browse_courses_btn")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Browse All Courses", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Courses Overview Section
        item {
            SectionHeader(
                title = "Courses Overview",
                icon = Icons.Default.AutoStories,
                actionButton = {
                    TextButton(onClick = { viewModel.navigateTo(Screen.Courses) }) {
                        Text("View All", fontSize = 13.sp)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            )
        }

        items(allCourses.take(4), key = { it.courseId }) { course ->
            DashboardCourseCard(
                course = course,
                isEnrolled = course.courseId in enrolledCourseIds,
                onOpen = { onOpenCourse(course.courseId) }
            )
        }
    }
}

@Composable
fun DashboardCourseCard(
    course: CourseEntity,
    isEnrolled: Boolean,
    onOpen: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("course_card_${course.courseId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = course.courseTitle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onOpen,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp).testTag("open_course_${course.courseId}")
                ) {
                    Text("Open", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LmsBadge(
                    text = course.status,
                    colorType = if (course.status.lowercase() == "active") "success" else "neutral"
                )
                if (course.category.isNotBlank()) {
                    LmsBadge(text = course.category, colorType = "primary")
                }
                if (isEnrolled) {
                    LmsBadge(text = "✓ Enrolled", colorType = "success")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = course.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = course.instructorName.ifBlank { "Faculty Instructor" },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
