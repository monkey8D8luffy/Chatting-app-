package com.kizuna.ui.call

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.kizuna.webrtc.WebRTCManager
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoTrack

@Composable
fun CallScreen(
    webRTCManager: WebRTCManager,
    isCaller: Boolean,
    onEndCall: () -> Unit
) {
    val context = LocalContext.current
    var remoteVideoTrack by remember { mutableStateOf<VideoTrack?>(null) }

    LaunchedEffect(Unit) {
        webRTCManager.initialize()
        if (isCaller) {
            webRTCManager.createOffer()
        }

        webRTCManager.remoteVideoTrack.collect { track ->
            remoteVideoTrack = track
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webRTCManager.disconnect()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Remote View
        AndroidView(
            factory = { ctx ->
                SurfaceViewRenderer(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    init(webRTCManager.getEglBaseContext(), null)
                    setEnableHardwareScaler(true)
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { renderer ->
                remoteVideoTrack?.addSink(renderer)
            }
        )

        // Local View PIP
        AndroidView(
            factory = { ctx ->
                SurfaceViewRenderer(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    init(webRTCManager.getEglBaseContext(), null)
                    setEnableHardwareScaler(true)
                    setZOrderMediaOverlay(true)
                    webRTCManager.startLocalVideo(ctx, this)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .width(120.dp)
                .height(160.dp)
        )

        // End Call Button
        IconButton(
            onClick = onEndCall,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
                .size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CallEnd,
                contentDescription = "End Call",
                tint = Color.Red,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
