package com.kizuna.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12

    // In a real E2EE system, keys would be generated via Diffie-Hellman Key Exchange over the signaling channel.
    // For this demonstration, we are using a fixed AES key or generating one locally.
    private var activeSecretKey: SecretKey? = null

    fun initializeSession() {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        activeSecretKey = keyGen.generateKey()
    }

    // Set an external key negotiated via Diffie-Hellman
    fun setSessionKey(keyBytes: ByteArray) {
        activeSecretKey = SecretKeySpec(keyBytes, "AES")
    }

    fun encryptMessage(plaintext: String): String {
        val key = activeSecretKey ?: return plaintext // Fallback for pure signaling if no key set

        val cipher = Cipher.getInstance(ALGORITHM)
        val iv = ByteArray(IV_LENGTH_BYTE)
        SecureRandom().nextBytes(iv)
        val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)

        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec)
        val cipherText = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        val ivAndCipherText = iv + cipherText
        return Base64.encodeToString(ivAndCipherText, Base64.NO_WRAP)
    }

    fun decryptMessage(encryptedBase64: String): String {
        val key = activeSecretKey ?: return encryptedBase64

        return try {
            val decoded = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            val iv = decoded.copyOfRange(0, IV_LENGTH_BYTE)
            val cipherText = decoded.copyOfRange(IV_LENGTH_BYTE, decoded.size)

            val cipher = Cipher.getInstance(ALGORITHM)
            val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec)

            String(cipher.doFinal(cipherText), Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: Could not decrypt message"
        }
    }
}
