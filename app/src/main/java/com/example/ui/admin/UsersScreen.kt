package com.example.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.UserEntity
import com.example.ui.LmsViewModel
import com.example.ui.components.LmsBadge
import com.example.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: LmsViewModel,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedRoleFilter by remember { mutableStateOf("All") }

    // Dialog states
    var userToEdit by remember { mutableStateOf<UserEntity?>(null) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    val filteredUsers = remember(allUsers, searchQuery, selectedRoleFilter) {
        allUsers.filter { u ->
            val matchesSearch = u.fullName.contains(searchQuery, ignoreCase = true) ||
                    u.email.contains(searchQuery, ignoreCase = true) ||
                    u.role.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedRoleFilter) {
                "All" -> true
                "Pending" -> u.accountStatus.lowercase() != "active"
                else -> u.role.equals(selectedRoleFilter, ignoreCase = true)
            }
            matchesSearch && matchesFilter
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "User Directory & Role Administration",
                icon = Icons.Default.ManageAccounts
            )
            Text(
                text = "Admin privileges: Approve registrations, edit roles & profiles, or remove accounts.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search users by name, email or role") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_users"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("All", "Admin", "Instructor", "Learner", "Pending").forEach { role ->
                    FilterChip(
                        selected = selectedRoleFilter == role,
                        onClick = { selectedRoleFilter = role },
                        label = { Text(role, fontSize = 11.sp) }
                    )
                }
            }
        }

        if (filteredUsers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No users found matching current filters.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        items(filteredUsers, key = { it.userId }) { user ->
            val isSelf = currentUser?.userId == user.userId
            val isPending = user.accountStatus.lowercase() != "active"

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPending) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_item_${user.userId}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelf) {
                                    Text(
                                        text = "(You)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                text = user.email,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            LmsBadge(
                                text = user.role.uppercase(),
                                colorType = when (user.role.lowercase()) {
                                    "admin" -> "primary"
                                    "instructor" -> "success"
                                    else -> "neutral"
                                }
                            )
                            LmsBadge(
                                text = user.accountStatus.uppercase(),
                                colorType = if (user.accountStatus.lowercase() == "active") "success" else "danger"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Joined: ${user.registrationDate.take(10)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Approve Button (If pending or disabled)
                            if (isPending) {
                                FilledTonalButton(
                                    onClick = { viewModel.approveUser(user) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp).testTag("btn_approve_${user.userId}")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve", fontSize = 11.sp)
                                }
                            }

                            // Edit Button
                            OutlinedButton(
                                onClick = { userToEdit = user },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp).testTag("btn_edit_user_${user.userId}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", fontSize = 11.sp)
                            }

                            // Delete Button (Protected against self-deletion)
                            if (!isSelf) {
                                IconButton(
                                    onClick = { userToDelete = user },
                                    modifier = Modifier.size(30.dp).testTag("btn_delete_user_${user.userId}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete User",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // --- EDIT USER & ROLES DIALOG ---
    if (userToEdit != null) {
        val u = userToEdit!!
        var editName by remember { mutableStateOf(u.fullName) }
        var editEmail by remember { mutableStateOf(u.email) }
        var editRole by remember { mutableStateOf(u.role) }
        var editStatus by remember { mutableStateOf(u.accountStatus) }

        AlertDialog(
            onDismissRequest = { userToEdit = null },
            title = {
                Text(
                    text = "Edit User Profile & Role",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Assign Role:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("learner", "instructor", "admin").forEach { role ->
                            FilterChip(
                                selected = editRole.equals(role, ignoreCase = true),
                                onClick = { editRole = role },
                                label = { Text(role.replaceFirstChar { it.uppercase() }, fontSize = 11.sp) }
                            )
                        }
                    }

                    Text("Account Status:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("active", "disabled").forEach { status ->
                            FilterChip(
                                selected = editStatus.equals(status, ignoreCase = true),
                                onClick = { editStatus = status },
                                label = { Text(status.replaceFirstChar { it.uppercase() }, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserDetails(
                            userId = u.userId,
                            fullName = editName,
                            email = editEmail,
                            role = editRole,
                            status = editStatus
                        )
                        userToEdit = null
                    }
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // --- DELETE CONFIRMATION DIALOG ---
    if (userToDelete != null) {
        val u = userToDelete!!
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.error) },
            title = { Text("Delete User Account?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete '${u.fullName}' (${u.email})? This user will be removed from local storage and deleted from the Google Sheet users tab.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(u)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
