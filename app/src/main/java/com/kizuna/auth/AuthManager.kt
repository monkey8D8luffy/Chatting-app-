package com.kizuna.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signInWithGoogle(): String? {
        return withContext(Dispatchers.IO) {
            try {
                // In a real app, pass the actual serverClientId
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId("YOUR_SERVER_CLIENT_ID") // Needs to be replaced for production
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result: GetCredentialResponse = credentialManager.getCredential(context, request)
                val credential = result.credential

                // Usually returns the user's ID token or email identifier
                Log.d("AuthManager", "Signed in with: ${credential.type}")
                return@withContext credential.type
            } catch (e: Exception) {
                Log.e("AuthManager", "Sign-in failed", e)
                null
            }
        }
    }
}
