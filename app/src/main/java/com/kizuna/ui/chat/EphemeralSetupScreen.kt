package com.kizuna.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizuna.ui.components.WatermarkBackground
import com.kizuna.ui.components.glassmorphic
import com.kizuna.ui.theme.PureDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EphemeralSetupScreen(onGenerateRoom: (String, Int) -> Unit) {
    var nickname by remember { mutableStateOf("") }
    var ttlMinutes by remember { mutableStateOf(10) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val onGenerateRoomAction = {
        if (nickname.isNotBlank()) {
            keyboardController?.hide()
            onGenerateRoom(nickname, ttlMinutes)
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
                text = "Ephemeral Room",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Highly secure. Self-destructing. Anonymous.",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = nickname,
                onValueChange = { nickname = it },
                label = { Text("Temporary Nickname", color = Color.White.copy(alpha = 0.7f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = CircleShape,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onGenerateRoomAction() }),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            Text(
                text = "Time-to-Live (TTL): $ttlMinutes mins",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Slider(
                value = ttlMinutes.toFloat(),
                onValueChange = { ttlMinutes = it.toInt() },
                valueRange = 1f..60f,
                steps = 59,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White.copy(alpha = 0.8f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            )

            Button(
                onClick = { onGenerateRoomAction() },
                enabled = nickname.isNotBlank(),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Generate Room", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
