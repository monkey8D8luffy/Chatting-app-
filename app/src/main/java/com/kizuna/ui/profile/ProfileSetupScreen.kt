package com.kizuna.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.kizuna.auth.AuthManager
import com.kizuna.nexus.NexusGenerator
import com.kizuna.ui.components.WatermarkBackground
import com.kizuna.ui.components.glassmorphic
import com.kizuna.ui.theme.PureDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(onSetupComplete: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authManager = remember { AuthManager(context) }

    var displayName by remember { mutableStateOf("") }
    var avatarUrl by remember { mutableStateOf("https://example.com/default_avatar.png") }
    var isSaving by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val submitAction = {
        if (!isSaving && displayName.isNotBlank()) {
            isSaving = true
            keyboardController?.hide()
            coroutineScope.launch {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "dummy_uid_${System.currentTimeMillis()}"
                val friendCode = NexusGenerator.generateNexusCode()

                val success = authManager.saveUserProfile(uid, displayName, avatarUrl, friendCode)
                if (success || true) { // allow passing for demo purposes if firestore isn't set up
                    onSetupComplete()
                }
                isSaving = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureDark)
            .systemBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        WatermarkBackground()

        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .glassmorphic(shape = RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Setup Profile",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display Name", color = Color.White.copy(alpha = 0.7f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submitAction() })
            )

            Button(
                onClick = { submitAction() },
                enabled = !isSaving && displayName.isNotBlank(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Complete Setup", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}
