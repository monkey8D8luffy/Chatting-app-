package com.kizuna.network

import android.util.Log
import com.kizuna.Config
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.*
import java.util.concurrent.TimeUnit

class WebSocketManager {
    private var webSocket: WebSocket? = null

    private val _messages = MutableSharedFlow<String>(replay = 1)
    val messages: SharedFlow<String> = _messages

    fun connect(nexusId: String) {
        val client = OkHttpManager.client
        val request = Request.Builder()
            .url("${Config.WEBSOCKET_URL}/$nexusId")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocketManager", "Connected to signaling server")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocketManager", "Received: $text")
                _messages.tryEmit(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocketManager", "WebSocket Error", t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocketManager", "Closed: $reason")
            }
        })
    }

    fun sendMessage(text: String) {
        webSocket?.send(text)
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
    }
}

object OkHttpManager {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS) // WebSockets require 0 timeout
            .build()
    }
}
