package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.admin.ActivityScreen
import com.example.ui.admin.UsersScreen
import com.example.ui.components.LmsBadge
import com.example.ui.components.ToastOverlay
import com.example.ui.courses.CourseDetailScreen
import com.example.ui.courses.CoursesScreen
import com.example.ui.courses.LessonDetailScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.quiz.QuizScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLmsScreen(
    viewModel: LmsViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val toasts by viewModel.toasts.collectAsStateWithLifecycle()

    val isAdmin = currentUser?.role?.lowercase() == "admin"

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = "Neonatal LMS Logo",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "Neonatal LMS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = currentUser?.fullName ?: "User",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = (currentUser?.role ?: "Learner").uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (currentUser?.fullName?.firstOrNull() ?: 'U').uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            // Google Sheet integration button (Admin only)
                            if (isAdmin) {
                                IconButton(
                                    onClick = { viewModel.navigateTo(Screen.GoogleSheetHub) },
                                    modifier = Modifier.testTag("btn_top_google_sheet")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TableChart,
                                        contentDescription = "Google Sheets",
                                        tint = Color(0xFF0F9D58)
                                    )
                                }
                            }
                            // Logout button
                            IconButton(
                                onClick = { viewModel.logout() },
                                modifier = Modifier.testTag("btn_logout")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Logout",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                // Bottom navigation bar
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    val isDashboard = currentScreen is Screen.Dashboard
                    val isCourses = currentScreen is Screen.Courses ||
                            currentScreen is Screen.CourseDetail ||
                            currentScreen is Screen.LessonDetail ||
                            currentScreen is Screen.Quiz
                    val isUsers = currentScreen is Screen.Users
                    val isActivity = currentScreen is Screen.ActivityLogs

                    NavigationBarItem(
                        selected = isDashboard,
                        onClick = { viewModel.navigateTo(Screen.Dashboard) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        selected = isCourses,
                        onClick = { viewModel.navigateTo(Screen.Courses) },
                        icon = { Icon(Icons.Default.Book, contentDescription = "Courses") },
                        label = { Text("Courses", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_courses")
                    )

                    if (isAdmin) {
                        NavigationBarItem(
                            selected = isUsers,
                            onClick = { viewModel.navigateTo(Screen.Users) },
                            icon = { Icon(Icons.Default.People, contentDescription = "Users") },
                            label = { Text("Users", fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_users")
                        )
                        NavigationBarItem(
                            selected = isActivity,
                            onClick = { viewModel.navigateTo(Screen.ActivityLogs) },
                            icon = { Icon(Icons.Default.History, contentDescription = "Activity") },
                            label = { Text("Activity", fontSize = 11.sp) },
                            modifier = Modifier.testTag("nav_activity")
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val screen = currentScreen) {
                    is Screen.Dashboard -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onOpenCourse = { courseId ->
                                viewModel.navigateTo(Screen.CourseDetail(courseId))
                            }
                        )
                    }
                    is Screen.Courses -> {
                        CoursesScreen(
                            viewModel = viewModel,
                            onOpenCourse = { courseId ->
                                viewModel.navigateTo(Screen.CourseDetail(courseId))
                            }
                        )
                    }
                    is Screen.CourseDetail -> {
                        CourseDetailScreen(
                            courseId = screen.courseId,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() },
                            onOpenLesson = { lessonId ->
                                viewModel.navigateTo(Screen.LessonDetail(screen.courseId, lessonId))
                            },
                            onTakeQuiz = { assessmentId ->
                                viewModel.navigateTo(Screen.Quiz(screen.courseId, assessmentId))
                            }
                        )
                    }
                    is Screen.LessonDetail -> {
                        LessonDetailScreen(
                            courseId = screen.courseId,
                            lessonId = screen.lessonId,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is Screen.Quiz -> {
                        QuizScreen(
                            courseId = screen.courseId,
                            assessmentId = screen.assessmentId,
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is Screen.Users -> {
                        UsersScreen(viewModel = viewModel)
                    }
                    is Screen.ActivityLogs -> {
                        ActivityScreen(viewModel = viewModel)
                    }
                    is Screen.GoogleSheetHub -> {
                        com.example.ui.sheets.GoogleSheetHubScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }
            }
        }

        // Floating Toast container
        ToastOverlay(
            toasts = toasts,
            onDismiss = { toastId -> viewModel.dismissToast(toastId) }
        )
    }
}
