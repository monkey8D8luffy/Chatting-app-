package com.kizuna

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kizuna.network.WebSocketManager
import com.kizuna.ui.auth.AuthScreen
import com.kizuna.ui.call.CallScreen
import com.kizuna.ui.chat.ChatScreen
import com.kizuna.ui.nexus.NexusScreen
import com.kizuna.ui.theme.KizunaTheme
import com.kizuna.webrtc.WebRTCManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KizunaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KizunaApp()
                }
            }
        }
    }
}

@Composable
fun KizunaApp() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Shared singletons for app lifespan
    val webSocketManager = remember { WebSocketManager() }
    val webRTCManager = remember { WebRTCManager(context) }

    var activeNexusId by remember { mutableStateOf("") }
    var isCaller by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate("nexus") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("nexus") {
            NexusScreen(
                onConnectClicked = { targetId ->
                    activeNexusId = targetId
                    navController.navigate("chat")
                }
            )
        }

        composable("chat") {
            ChatScreen(
                nexusId = activeNexusId,
                webSocketManager = webSocketManager,
                onStartCallClicked = {
                    isCaller = true
                    navController.navigate("call")
                }
            )
        }

        composable("call") {
            CallScreen(
                webRTCManager = webRTCManager,
                isCaller = isCaller,
                onEndCall = {
                    navController.popBackStack()
                }
            )
        }
    }
}
