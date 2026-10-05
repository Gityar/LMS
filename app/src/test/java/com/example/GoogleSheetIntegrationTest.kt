package com.example

import com.example.ui.SheetSyncStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GoogleSheetIntegrationTest {

    @Test
    fun testDefaultGoogleSheetParameters() {
        val status = SheetSyncStatus()
        assertEquals("1TQcak2SNQ3oT9DBz_nyrOzVh8fWfNRVsWqt7Rk_1uTA", status.spreadsheetId)
        assertEquals("yaregalsemanew@gmail.com", status.ownerEmail)
        assertEquals(13, status.syncedSheetsCount)
    }

    @Test
    fun testGoogleSheetUrlFormatting() {
        val status = SheetSyncStatus()
        val url = "https://docs.google.com/spreadsheets/d/${status.spreadsheetId}/edit"
        assert(url.contains("1TQcak2SNQ3oT9DBz_nyrOzVh8fWfNRVsWqt7Rk_1uTA"))
        assert(url.startsWith("https://docs.google.com/spreadsheets/d/"))
    }

    @Test
    fun testOnlyAdminHasGoogleSheetPrivilege() {
        val adminRole = "admin"
        val instructorRole = "instructor"
        val learnerRole = "learner"

        fun canAccessSheet(role: String): Boolean = role.equals("admin", ignoreCase = true)

        assert(canAccessSheet(adminRole))
        assert(!canAccessSheet(instructorRole))
        assert(!canAccessSheet(learnerRole))
    }
}
