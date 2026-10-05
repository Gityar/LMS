package com.example.ui.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CourseEntity
import com.example.ui.LmsViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LmsBadge
import com.example.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    viewModel: LmsViewModel,
    onOpenCourse: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allCourses by viewModel.allCourses.collectAsStateWithLifecycle()
    val enrollments by viewModel.learnerEnrollments.collectAsStateWithLifecycle()

    val enrolledIds = remember(enrollments) { enrollments.map { it.courseId }.toSet() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Dialog states
    var showCreateDialog by remember { mutableStateOf(false) }
    var courseToEdit by remember { mutableStateOf<CourseEntity?>(null) }
    var courseToDelete by remember { mutableStateOf<CourseEntity?>(null) }

    val categories = remember(allCourses) {
        listOf("All") + allCourses.map { it.category }.distinct().filter { it.isNotBlank() }
    }

    val filteredCourses = remember(allCourses, searchQuery, selectedCategory) {
        allCourses.filter { course ->
            val matchesQuery = course.courseTitle.contains(searchQuery, ignoreCase = true) ||
                    course.description.contains(searchQuery, ignoreCase = true) ||
                    course.instructorName.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || course.category.equals(selectedCategory, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }

    val canCreateCourse = currentUser?.role?.lowercase() in listOf("admin", "instructor")

    Scaffold(
        floatingActionButton = {
            if (canCreateCourse) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("fab_create_course")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Course")
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionHeader(
                    title = "All Courses",
                    icon = Icons.Default.Book,
                    actionButton = {
                        if (canCreateCourse) {
                            Button(
                                onClick = { showCreateDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp).testTag("btn_new_course_header")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Course", fontSize = 12.sp)
                            }
                        }
                    }
                )

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search neonatal courses, protocols...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("course_search_bar")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("cat_chip_$cat")
                        )
                    }
                }
            }

            if (filteredCourses.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.MenuBook,
                        title = "No Courses Found",
                        description = if (searchQuery.isNotBlank()) "No courses match '$searchQuery'." else "No courses in this category.",
                        actionButton = if (canCreateCourse) {
                            {
                                Button(onClick = { showCreateDialog = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Create Course")
                                }
                            }
                        } else null
                    )
                }
            } else {
                items(filteredCourses, key = { it.courseId }) { course ->
                    val canEdit = currentUser?.role?.lowercase() == "admin" ||
                            (currentUser?.role?.lowercase() == "instructor" && course.instructorId == currentUser?.userId)

                    CourseListItemCard(
                        course = course,
                        isEnrolled = course.courseId in enrolledIds,
                        canEdit = canEdit,
                        onOpen = { onOpenCourse(course.courseId) },
                        onEdit = { courseToEdit = course },
                        onDelete = { courseToDelete = course }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // CREATE COURSE DIALOG
    if (showCreateDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Clinical Care") }
        var newDescription by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create New Course", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Course Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("dialog_course_title")
                    )
                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = { Text("Category (e.g. Clinical Care, Resuscitation)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("dialog_course_cat")
                    )
                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("Description & Syllabus Overview") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("dialog_course_desc")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isBlank()) {
                            viewModel.showToast("warn", "Missing Title", "Enter course title.")
                            return@Button
                        }
                        viewModel.createCourse(newTitle, newCategory, newDescription)
                        showCreateDialog = false
                    },
                    modifier = Modifier.testTag("dialog_course_save_btn")
                ) {
                    Text("Save Course")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // EDIT COURSE DIALOG
    if (courseToEdit != null) {
        val course = courseToEdit!!
        var editTitle by remember { mutableStateOf(course.courseTitle) }
        var editCategory by remember { mutableStateOf(course.category) }
        var editDescription by remember { mutableStateOf(course.description) }

        AlertDialog(
            onDismissRequest = { courseToEdit = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Course", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCategory,
                        onValueChange = { editCategory = it },
                        label = { Text("Category") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTitle.isBlank()) return@Button
                        viewModel.updateCourse(course.courseId, editTitle, editCategory, editDescription)
                        courseToEdit = null
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { courseToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DELETE COURSE CONFIRMATION DIALOG
    if (courseToDelete != null) {
        val course = courseToDelete!!
        AlertDialog(
            onDismissRequest = { courseToDelete = null },
            title = { Text("Delete Course?") },
            text = { Text("Are you sure you want to delete '${course.courseTitle}'? This will also remove associated lessons.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCourse(course.courseId)
                        courseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { courseToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CourseListItemCard(
    course: CourseEntity,
    isEnrolled: Boolean,
    canEdit: Boolean,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().testTag("course_item_${course.courseId}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.courseTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
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
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Button(
                        onClick = onOpen,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp).testTag("open_btn_${course.courseId}")
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Open", fontSize = 12.sp)
                    }
                    if (canEdit) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(34.dp).testTag("edit_btn_${course.courseId}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(34.dp).testTag("delete_btn_${course.courseId}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = course.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = course.instructorName.ifBlank { "Instructor" },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = course.createdDate,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
