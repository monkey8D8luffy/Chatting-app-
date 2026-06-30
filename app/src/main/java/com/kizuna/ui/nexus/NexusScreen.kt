package com.kizuna.ui.nexus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizuna.nexus.NexusGenerator
import com.kizuna.ui.components.glassmorphic
import com.kizuna.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusScreen(
    onConnectClicked: (String) -> Unit
) {
    var myNexusCode by remember { mutableStateOf(NexusGenerator.generateNexusCode()) }
    var targetNexusCode by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .glassmorphic()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "YOUR NEXUS",
                color = TextSecondary,
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )

            Text(
                text = myNexusCode,
                color = Color.White,
                fontSize = 42.sp, // Reduced slightly to avoid clipping on tiny phones
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { myNexusCode = NexusGenerator.generateNexusCode() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("REGENERATE", color = TextSecondary, fontSize = 12.sp)
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

            OutlinedTextField(
                value = targetNexusCode,
                onValueChange = {
                    // Only allow exactly 6 alphanumeric chars, filter out spaces or symbols.
                    val filtered = it.filter { char -> char.isLetterOrDigit() }.take(6)
                    targetNexusCode = filtered
                },
                label = { Text("Target Nexus", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(
                    onGo = {
                        if (targetNexusCode.length == 6) {
                            keyboardController?.hide()
                            onConnectClicked(targetNexusCode)
                        }
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (targetNexusCode.length == 6) {
                        keyboardController?.hide()
                        onConnectClicked(targetNexusCode)
                    }
                },
                enabled = targetNexusCode.length == 6,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.1f),
                    disabledContainerColor = Color.White.copy(alpha = 0.05f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "CONNECT",
                    color = if (targetNexusCode.length == 6) Color.White else TextSecondary
                )
            }
        }
    }
}
