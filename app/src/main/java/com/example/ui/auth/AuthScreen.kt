package com.example.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LmsViewModel

enum class AuthTab {
    LOGIN, REGISTER, FORGOT, RESET
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: LmsViewModel,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(AuthTab.LOGIN) }

    // Login state
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var isLoggingIn by remember { mutableStateOf(false) }

    // Register state
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regRole by remember { mutableStateOf("learner") }
    var regRoleExpanded by remember { mutableStateOf(false) }
    var regPassword by remember { mutableStateOf("") }
    var regPassword2 by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    // Forgot state
    var forgotEmail by remember { mutableStateOf("") }
    var isSendingReset by remember { mutableStateOf(false) }

    // Reset state
    var resetTokenInput by remember { mutableStateOf("") }
    var resetNewPassword by remember { mutableStateOf("") }
    var resetNewPassword2 by remember { mutableStateOf("") }
    var isResetting by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 440.dp)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo Icon
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "Neonatal Logo",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Neonatal Nursing LMS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = when (activeTab) {
                        AuthTab.LOGIN -> "Sign in to access clinical courses"
                        AuthTab.REGISTER -> "Create your healthcare learning account"
                        AuthTab.FORGOT -> "Reset your account password"
                        AuthTab.RESET -> "Enter new password"
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tab Switcher (Login / Register)
                if (activeTab == AuthTab.LOGIN || activeTab == AuthTab.REGISTER) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (activeTab == AuthTab.LOGIN) MaterialTheme.colorScheme.surface else Color.Transparent,
                                shadowElevation = if (activeTab == AuthTab.LOGIN) 2.dp else 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { activeTab = AuthTab.LOGIN }
                                    .testTag("tab_login")
                            ) {
                                Text(
                                    text = "Login",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (activeTab == AuthTab.LOGIN) MaterialTheme.colorScheme.primary else Color(0xFF64748B),
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (activeTab == AuthTab.REGISTER) MaterialTheme.colorScheme.surface else Color.Transparent,
                                shadowElevation = if (activeTab == AuthTab.REGISTER) 2.dp else 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { activeTab = AuthTab.REGISTER }
                                    .testTag("tab_register")
                            ) {
                                Text(
                                    text = "Register",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    color = if (activeTab == AuthTab.REGISTER) MaterialTheme.colorScheme.primary else Color(0xFF64748B),
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Demo Accounts Quick Fill Chip Row
                    Text(
                        text = "Quick Demo Profiles (1-Tap Fill):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssistChip(
                            onClick = {
                                activeTab = AuthTab.LOGIN
                                loginEmail = "yaregalsemanew@gmail.com"
                                loginPassword = "Admin@2026"
                            },
                            label = { Text("Admin", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            modifier = Modifier.weight(1f).testTag("demo_admin_btn")
                        )
                        AssistChip(
                            onClick = {
                                activeTab = AuthTab.LOGIN
                                loginEmail = "nurse.sarah@hospital.org"
                                loginPassword = "Instructor@2026"
                            },
                            label = { Text("Instructor", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            modifier = Modifier.weight(1f).testTag("demo_instructor_btn")
                        )
                        AssistChip(
                            onClick = {
                                activeTab = AuthTab.LOGIN
                                loginEmail = "learner@hospital.org"
                                loginPassword = "Learner@2026"
                            },
                            label = { Text("Learner", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            modifier = Modifier.weight(1f).testTag("demo_learner_btn")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // TAB 1: LOGIN
                if (activeTab == AuthTab.LOGIN) {
                    OutlinedTextField(
                        value = loginEmail,
                        onValueChange = { loginEmail = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingIcon = {
                            IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                Icon(
                                    imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password"
                                )
                            }
                        },
                        visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (loginEmail.isBlank() || loginPassword.isBlank()) {
                                viewModel.showToast("warn", "Missing Fields", "Enter both email and password.")
                                return@Button
                            }
                            isLoggingIn = true
                            viewModel.login(loginEmail, loginPassword) {
                                isLoggingIn = false
                            }
                        },
                        enabled = !isLoggingIn,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_submit_btn")
                    ) {
                        if (isLoggingIn) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Signing In...")
                        } else {
                            Icon(Icons.Default.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { activeTab = AuthTab.FORGOT },
                        modifier = Modifier.testTag("forgot_pwd_link")
                    ) {
                        Text(
                            text = "Forgot Password?",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                // TAB 2: REGISTER
                if (activeTab == AuthTab.REGISTER) {
                    OutlinedTextField(
                        value = regName,
                        onValueChange = { regName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = { regEmail = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ExposedDropdownMenuBox(
                        expanded = regRoleExpanded,
                        onExpandedChange = { regRoleExpanded = !regRoleExpanded }
                    ) {
                        OutlinedTextField(
                            value = regRole.replaceFirstChar { it.uppercase() },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Role") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = regRoleExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("reg_role_select")
                        )
                        ExposedDropdownMenu(
                            expanded = regRoleExpanded,
                            onDismissRequest = { regRoleExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Learner (Nurse / Student)") },
                                onClick = {
                                    regRole = "learner"
                                    regRoleExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Instructor (NNP / Educator)") },
                                onClick = {
                                    regRole = "instructor"
                                    regRoleExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { regPassword = it },
                        label = { Text("Password (Min 6 chars)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_pwd_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = regPassword2,
                        onValueChange = { regPassword2 = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reg_pwd2_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (regName.isBlank() || regEmail.isBlank() || regPassword.isBlank() || regPassword2.isBlank()) {
                                viewModel.showToast("warn", "Missing Fields", "Please complete all fields.")
                                return@Button
                            }
                            if (regPassword != regPassword2) {
                                viewModel.showToast("err", "Mismatch", "Passwords do not match.")
                                return@Button
                            }
                            if (regPassword.length < 6) {
                                viewModel.showToast("warn", "Weak Password", "Must be at least 6 characters.")
                                return@Button
                            }
                            isRegistering = true
                            viewModel.register(regName, regEmail, regRole, regPassword) { ok ->
                                isRegistering = false
                                if (ok) {
                                    loginEmail = regEmail
                                    activeTab = AuthTab.LOGIN
                                }
                            }
                        },
                        enabled = !isRegistering,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("reg_submit_btn")
                    ) {
                        if (isRegistering) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Creating Account...")
                        } else {
                            Icon(Icons.Default.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Account", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // TAB 3: FORGOT PASSWORD
                if (activeTab == AuthTab.FORGOT) {
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Registered Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("forgot_email_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (forgotEmail.isBlank()) {
                                viewModel.showToast("warn", "Missing Email", "Enter your registered email.")
                                return@Button
                            }
                            isSendingReset = true
                            viewModel.requestPasswordReset(forgotEmail) { ok, tokenMsg ->
                                isSendingReset = false
                                if (ok) {
                                    val tokenRegex = "token generated: ([A-Z0-9]+)".toRegex()
                                    val match = tokenRegex.find(tokenMsg)
                                    if (match != null) {
                                        resetTokenInput = match.groupValues[1]
                                    }
                                    activeTab = AuthTab.RESET
                                }
                            }
                        },
                        enabled = !isSendingReset,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("forgot_submit_btn")
                    ) {
                        if (isSendingReset) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Send Reset Token", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { activeTab = AuthTab.LOGIN }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back to Login", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                // TAB 4: RESET PASSWORD
                if (activeTab == AuthTab.RESET) {
                    OutlinedTextField(
                        value = resetTokenInput,
                        onValueChange = { resetTokenInput = it },
                        label = { Text("Verification Token") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reset_token_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = resetNewPassword,
                        onValueChange = { resetNewPassword = it },
                        label = { Text("New Password (Min 6 chars)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reset_new_pwd_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = resetNewPassword2,
                        onValueChange = { resetNewPassword2 = it },
                        label = { Text("Confirm New Password") },
                        leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("reset_new_pwd2_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (resetTokenInput.isBlank() || resetNewPassword.isBlank() || resetNewPassword2.isBlank()) {
                                viewModel.showToast("warn", "Missing Fields", "Please complete all fields.")
                                return@Button
                            }
                            if (resetNewPassword != resetNewPassword2) {
                                viewModel.showToast("err", "Mismatch", "Passwords do not match.")
                                return@Button
                            }
                            if (resetNewPassword.length < 6) {
                                viewModel.showToast("warn", "Weak Password", "Must be at least 6 characters.")
                                return@Button
                            }
                            isResetting = true
                            viewModel.resetPassword(resetTokenInput, resetNewPassword) { ok ->
                                isResetting = false
                                if (ok) {
                                    activeTab = AuthTab.LOGIN
                                }
                            }
                        },
                        enabled = !isResetting,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("reset_submit_btn")
                    ) {
                        if (isResetting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reset Password", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { activeTab = AuthTab.LOGIN }) {
                        Text("Back to Login", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
