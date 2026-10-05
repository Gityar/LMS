package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ToastMessage

@Composable
fun ToastOverlay(
    toasts: List<ToastMessage>,
    onDismiss: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.widthIn(max = 380.dp)
        ) {
            toasts.forEach { toast ->
                ToastItem(toast = toast, onDismiss = { onDismiss(toast.id) })
            }
        }
    }
}

@Composable
fun ToastItem(
    toast: ToastMessage,
    onDismiss: () -> Unit
) {
    val (borderColor, iconBg, iconColor, icon) = when (toast.type) {
        "ok" -> Quadruple(
            Color(0xFF10B981),
            Color(0xFFD1FAE5),
            Color(0xFF059669),
            Icons.Default.Check
        )
        "err" -> Quadruple(
            Color(0xFFEF4444),
            Color(0xFFFEE2E2),
            Color(0xFFDC2626),
            Icons.Default.Close
        )
        "warn" -> Quadruple(
            Color(0xFFF59E0B),
            Color(0xFFFEF3C7),
            Color(0xFFD97706),
            Icons.Default.Warning
        )
        else -> Quadruple(
            Color(0xFF4F46E5),
            Color(0xFFE0E7FF),
            Color(0xFF4F46E5),
            Icons.Default.Info
        )
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp))
            .border(width = 1.dp, color = Color(0xFFE2E8F0), shape = RoundedCornerShape(12.dp))
            .testTag("toast_item_${toast.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colored indicator stripe
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(borderColor, RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Icon
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toast.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = toast.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
