package com.kizuna.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizuna.network.WebSocketManager
import com.kizuna.security.CryptoManager
import com.kizuna.ui.components.WatermarkBackground
import com.kizuna.ui.components.glassmorphic
import com.kizuna.ui.theme.PureDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(val id: String, val text: String, val isMine: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    nexusId: String,
    webSocketManager: WebSocketManager,
    onStartCallClicked: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        CryptoManager.initializeSession()
        webSocketManager.connect(nexusId)

        webSocketManager.messages.collect { encryptedMsg ->
            val decrypted = CryptoManager.decryptMessage(encryptedMsg)
            messages.add(ChatMessage(System.currentTimeMillis().toString(), decrypted, false))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webSocketManager.disconnect()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureDark)
            .systemBarsPadding()
            .imePadding()
    ) {
        TopAppBar(
            title = { Text(text = "Nexus: $nexusId", color = Color.White) },
            actions = {
                IconButton(onClick = onStartCallClicked) {
                    Icon(imageVector = Icons.Default.Videocam, contentDescription = "Start Video Call", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            WatermarkBackground()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message...", color = Color.White.copy(alpha = 0.5f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White.copy(alpha = 0.2f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
                shape = CircleShape,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (messageText.isNotBlank()) {
                        sendMessage(messageText, webSocketManager, messages)
                        keyboardController?.hide()
                        messageText = ""
                    }
                })
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        sendMessage(messageText, webSocketManager, messages)
                        keyboardController?.hide()
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .glassmorphic(shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

private fun sendMessage(text: String, webSocketManager: WebSocketManager, messages: MutableList<ChatMessage>) {
    messages.add(ChatMessage(System.currentTimeMillis().toString(), text, true))
    val encrypted = CryptoManager.encryptMessage(text)
    webSocketManager.sendMessage(encrypted)
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (message.isMine) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.id.toLongOrNull() ?: System.currentTimeMillis()))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .glassmorphic(shape = shape)
                .padding(12.dp)
        ) {
            Column {
                Text(text = message.text, color = Color.White)
                Text(
                    text = time,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}
