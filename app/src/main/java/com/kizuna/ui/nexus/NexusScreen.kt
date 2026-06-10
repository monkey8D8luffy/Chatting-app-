package com.kizuna.ui.nexus

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .glassmorphic(blurRadius = 20.dp)
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
                fontSize = 48.sp,
                letterSpacing = 8.sp,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { myNexusCode = NexusGenerator.generateNexusCode() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("REGENERATE", color = TextSecondary, fontSize = 12.sp)
            }

            Divider(color = Color.White.copy(alpha = 0.2f))

            OutlinedTextField(
                value = targetNexusCode,
                onValueChange = { if (it.length <= 6) targetNexusCode = it },
                label = { Text("Enter Target Nexus", color = TextSecondary) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (targetNexusCode.length == 6) {
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
