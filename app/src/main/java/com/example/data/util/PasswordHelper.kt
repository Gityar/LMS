package com.example.data.util

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object PasswordHelper {
    private const val SALT = "NeonatalLMS::v5"

    fun hashPassword(pwd: String): String {
        val raw = SALT + pwd
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(raw.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun genId(prefix: String): String {
        val rnd = UUID.randomUUID().toString().substring(0, 8).uppercase(Locale.ROOT)
        val ts = System.currentTimeMillis().toString(36).uppercase(Locale.ROOT)
        return "$prefix-$rnd-$ts"
    }

    fun genToken(): String {
        return UUID.randomUUID().toString().replace("-", "") +
                UUID.randomUUID().toString().replace("-", "")
    }

    fun nowIso(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        return sdf.format(Date())
    }

    fun formattedNow(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
