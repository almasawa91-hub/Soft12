package com.aalmoghalis.muhasibsoft

import com.aalmoghalis.muhasibsoft.utils.PasswordUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordUtilsTest {
    @Test
    fun hashDoesNotContainPlaintextAndVerifiesCorrectPassword() {
        val password = "example-Password-92!"
        val stored = PasswordUtils.hash(password)
        assertFalse(stored.contains(password))
        assertTrue(PasswordUtils.isHashed(stored))
        assertTrue(PasswordUtils.verify(password, stored))
        assertFalse(PasswordUtils.verify("wrong-password", stored))
    }

    @Test
    fun eachHashUsesANewSalt() {
        assertNotEquals(PasswordUtils.hash("same-password"), PasswordUtils.hash("same-password"))
    }

    @Test
    fun legacyPlaintextCanBeVerifiedForMigration() {
        assertTrue(PasswordUtils.verify("legacy", "legacy"))
        assertFalse(PasswordUtils.verify("different", "legacy"))
        assertFalse(PasswordUtils.isHashed("legacy"))
    }
}
