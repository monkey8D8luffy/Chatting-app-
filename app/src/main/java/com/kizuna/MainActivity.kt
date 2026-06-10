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
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.kizuna.network.WebSocketManager
import com.kizuna.ui.auth.AuthScreen
import com.kizuna.ui.call.CallScreen
import com.kizuna.ui.chat.ChatScreen
import com.kizuna.ui.chat.EphemeralChatScreen
import com.kizuna.ui.chat.EphemeralSetupScreen
import com.kizuna.ui.hub.HubScreen
import com.kizuna.ui.profile.ProfileSetupScreen
import com.kizuna.ui.theme.KizunaTheme
import com.kizuna.webrtc.WebRTCManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:1234567890:android:abcdef123456")
                    .setProjectId("kizuna-test-project")
                    .setApiKey("test-api-key")
                    .build()
                FirebaseApp.initializeApp(this, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

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

    // Ephemeral state
    var ephemeralNickname by remember { mutableStateOf("") }
    var ephemeralTtl by remember { mutableStateOf(10) }
    var ephemeralRoomCode by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "auth") {
        composable("auth") {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate("profile_setup") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("profile_setup") {
            ProfileSetupScreen(
                onSetupComplete = {
                    navController.navigate("hub") {
                        popUpTo("profile_setup") { inclusive = true }
                    }
                }
            )
        }

        composable("hub") {
            HubScreen(
                onNavigateToChat = { targetId ->
                    activeNexusId = targetId
                    navController.navigate("chat")
                },
                onNavigateToProfile = {
                    navController.navigate("ephemeral_setup") // Demo linking ephemeral from profile icon for now
                }
            )
        }

        composable("ephemeral_setup") {
            EphemeralSetupScreen(
                onGenerateRoom = { nickname, ttl ->
                    ephemeralNickname = nickname
                    ephemeralTtl = ttl
                    ephemeralRoomCode = "EPH-" + (1000..9999).random() // Mock generator
                    navController.navigate("ephemeral_chat")
                }
            )
        }

        composable("ephemeral_chat") {
            EphemeralChatScreen(
                roomCode = ephemeralRoomCode,
                nickname = ephemeralNickname,
                ttlMinutes = ephemeralTtl,
                onRoomClosed = {
                    navController.popBackStack("hub", inclusive = false)
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
