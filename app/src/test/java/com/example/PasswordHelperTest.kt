package com.example

import com.example.data.util.PasswordHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PasswordHelperTest {

    @Test
    fun testPasswordHashingMatchesSalt() {
        val hash = PasswordHelper.hashPassword("Admin@2026")
        assertNotNull(hash)
        assertEquals(64, hash.length) // SHA-256 produces 64 hex characters
    }

    @Test
    fun testGenIdGeneratesExpectedPrefix() {
        val id = PasswordHelper.genId("CRS")
        assert(id.startsWith("CRS-"))
    }
}
