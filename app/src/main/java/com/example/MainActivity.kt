package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.LmsViewModel
import com.example.ui.MainLmsScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.components.ToastOverlay
import com.example.ui.theme.NeonatalLmsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeonatalLmsTheme {
                val viewModel: LmsViewModel = viewModel()
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val toasts by viewModel.toasts.collectAsStateWithLifecycle()

                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (currentUser == null) {
                            AuthScreen(viewModel = viewModel)
                        } else {
                            MainLmsScreen(viewModel = viewModel)
                        }

                        // Ensure ToastOverlay can be seen on both Auth and Main
                        if (currentUser == null) {
                            ToastOverlay(
                                toasts = toasts,
                                onDismiss = { id -> viewModel.dismissToast(id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
