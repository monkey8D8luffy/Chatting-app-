package com.kizuna.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizuna.auth.AuthManager
import com.kizuna.ui.components.glassmorphic
import com.kizuna.ui.components.WatermarkBackground
import com.kizuna.ui.theme.PureDark
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authManager = remember { AuthManager(context) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureDark)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        WatermarkBackground()

        if (!isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .glassmorphic()
                    .padding(vertical = 48.dp, horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "KIZUNA",
                    color = Color.White,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            val result = authManager.signInWithGoogle()
                            onAuthSuccess()
                            isLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(text = "Sign in with Google", color = Color.White, fontSize = 16.sp)
                }
            }
        } else {
            Text(text = "Authenticating...", color = Color.White)
        }
    }
}
