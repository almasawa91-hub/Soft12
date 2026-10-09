package com.aalmoghalis.muhasibsoft.utils

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Salted PBKDF2 password hashing for locally stored application accounts.
 * Stored format: pbkdf2-sha256$iterations$saltHex$hashHex
 */
object PasswordUtils {
    private const val PREFIX = "pbkdf2-sha256"
    private const val ITERATIONS = 120_000
    private const val SALT_BYTES = 16
    private const val KEY_BITS = 256
    private val random = SecureRandom()

    fun hash(password: String): String {
        require(password.isNotEmpty()) { "Password must not be empty" }
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        val digest = derive(password, salt, ITERATIONS)
        return "$PREFIX\$$ITERATIONS\$${salt.toHex()}\$${digest.toHex()}"
    }

    /** Supports legacy plaintext records to permit migration after successful verification. */
    fun verify(password: String, storedValue: String): Boolean {
        if (!isHashed(storedValue)) return password == storedValue
        return try {
            val parts = storedValue.split('$')
            if (parts.size != 4 || parts[0] != PREFIX) return false
            val iterations = parts[1].toIntOrNull() ?: return false
            if (iterations < 1_000 || iterations > 2_000_000) return false
            val salt = parts[2].hexToBytes()
            val expected = parts[3].hexToBytes()
            MessageDigest.isEqual(expected, derive(password, salt, iterations))
        } catch (_: Exception) {
            false
        }
    }

    fun isHashed(value: String): Boolean = value.startsWith("$PREFIX\$")

    private fun derive(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { byte -> "%02x".format(byte.toInt() and 0xff) }

    private fun String.hexToBytes(): ByteArray {
        require(length % 2 == 0 && all { it in "0123456789abcdefABCDEF" })
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}
