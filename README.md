# Kizuna

**Kizuna** is a hyper-private, peer-to-peer social communication Android app. Built with prioritizing zero-latency communication, absolute privacy, and a premium modern aesthetic, Kizuna is designed to be completely serverless for user data, relying only on signaling servers for connection negotiation.

## Core Architecture
*   **100% Native Jetpack Compose:** Built using a single-activity, single-module architecture without any XML or Java.
*   **True P2P WebRTC Calling:** Media (video/audio) is routed purely over WebRTC direct connections. WebSockets are strictly used only for SDP/ICE signaling.
*   **End-to-End Encryption (E2EE):** All chat messages are encrypted locally on the device (using AES-GCM) before transmission over the WebSocket. The server cannot read the message contents.
*   **Sovereign Authentication:** Utilizes the Android Credential Manager API ("Sign in with Google") to onboard users securely without requiring a centralized backend database.
*   **Nexus Protocol:** Users connect via a 6-character Alphanumeric code (Nexus), securing sessions uniquely per instance.

## Tech Stack
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose
*   **Networking:** OkHttp (WebSockets)
*   **Media:** WebRTC Android SDK (`io.getstream:stream-webrtc-android`)
*   **Auth:** Android Credential Manager (`androidx.credentials`)

## UI / UX Design Language
Kizuna strictly adheres to a deep dark and liquid glass design philosophy:
*   **Deep Dark Mode:** The app utilizes a pure dark theme (`0xFF050505`).
*   **Liquid Glass Morph Effects:** UI components (chat bubbles, modals, text fields) avoid flat Material colors. They are built using custom `glassmorphic` modifiers that blend translucent backgrounds, soft white borders, and heavy blur effects (`Modifier.blur(10.dp)`).
*   **Fluid Animations:** Seamlessly integrated `AnimatedVisibility` and liquid morphing animations between states.

## Setup & Running

1. **Clone the repository.**
2. **Setup Server Client ID:**
   In `app/src/main/java/com/kizuna/auth/AuthManager.kt`, replace `"YOUR_SERVER_CLIENT_ID"` with your actual Google OAuth Client ID for Android.
3. **Setup Signaling Server IP:**
   In `app/src/main/java/com/kizuna/network/WebSocketManager.kt`, replace `"ws://YOUR_PYTHON_SERVER_IP:8000/ws"` with the address of your external Python/FastAPI WebSocket signaling server.
4. **Build and Run:**
   The project uses Gradle 8.8. Run `./gradlew assembleDebug` to build the debug APK, or open the project in Android Studio and hit Run.

## Anti-Goals (What This Project Avoids)
*   No Firebase, Firestore, or server-side proprietary user databases.
*   No heavy media traffic routed through centralized WebSockets.
*   No third-party tracking, analytics, or bloated UI dependencies.
