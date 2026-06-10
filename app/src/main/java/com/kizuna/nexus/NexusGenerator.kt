package com.kizuna.nexus

import java.security.SecureRandom

object NexusGenerator {
    private const val ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    private const val NEXUS_LENGTH = 6
    private val secureRandom = SecureRandom()

    fun generateNexusCode(): String {
        return (1..NEXUS_LENGTH)
            .map { ALPHANUMERIC_CHARS[secureRandom.nextInt(ALPHANUMERIC_CHARS.length)] }
            .joinToString("")
    }
}
