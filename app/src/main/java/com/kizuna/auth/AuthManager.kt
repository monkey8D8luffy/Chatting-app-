package com.kizuna.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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

                // For demonstration: Since we don't have a valid ID token from the dummy clientId,
                // we'll just return a success string or bypass real Firebase Auth if needed.
                // Normally you would extract the ID token and use:
                // val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                // auth.signInWithCredential(firebaseCredential).await()

                Log.d("AuthManager", "Signed in with: ${credential.type}")
                return@withContext credential.type
            } catch (e: Exception) {
                Log.e("AuthManager", "Sign-in failed", e)
                null
            }
        }
    }

    suspend fun saveUserProfile(uid: String, displayName: String, avatarUrl: String, friendCode: String): Boolean {
        return try {
            val userMap = hashMapOf(
                "uid" to uid,
                "displayName" to displayName,
                "avatarUrl" to avatarUrl,
                "friendCode" to friendCode
            )
            firestore.collection("users").document(uid).set(userMap).await()
            true
        } catch (e: Exception) {
            Log.e("AuthManager", "Error saving user profile", e)
            false
        }
    }
}
