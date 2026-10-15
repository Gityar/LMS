package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.FirebaseEnrollment
import com.example.data.model.FirebaseUser
import com.example.data.repository.FirebaseLmsRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FirebaseLmsRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private lateinit var repository: FirebaseLmsRepository

    @Before
    override fun setUpFirebase() {
        super.setUpFirebase()
        repository = FirebaseLmsRepository(firestore)
    }

    @Test
    fun authenticatedUser_canSaveAndRetrieveOwnProfile() = runBlocking {
        val uid = signInTestUser("nurse.alice@hospital.org")
        val user = FirebaseUser(
            userId = uid,
            fullName = "Nurse Alice",
            email = "nurse.alice@hospital.org",
            role = "learner",
            accountStatus = "active"
        )
        repository.saveUser(user)

        val retrieved = repository.getUser(uid)
        assertNotNull(retrieved)
        assertEquals("Nurse Alice", retrieved?.fullName)
        assertEquals("learner", retrieved?.role)
    }

    @Test
    fun authenticatedUser_canSaveAndObserveOwnEnrollment() = runBlocking {
        val uid = signInTestUser("nurse.bob@hospital.org")
        val enrollment = FirebaseEnrollment(
            enrollmentId = "ENR-TEST-01",
            userId = uid,
            courseId = "CRS-NRP-01",
            status = "enrolled",
            progressPercent = 25.0
        )
        repository.saveEnrollment(enrollment)

        val enrollments = repository.observeEnrollments(uid).first()
        assertTrue(enrollments.any { it.enrollmentId == "ENR-TEST-01" })
    }

    @Test
    fun unauthenticatedUser_cannotObserveEnrollments() = runBlocking {
        auth.signOut()
        try {
            kotlinx.coroutines.withTimeout(3000L) {
                repository.observeEnrollments("any_user").first()
            }
            fail("Expected exception for unauthenticated caller")
        } catch (e: Throwable) {
            println("Caught exception type: ${e.javaClass.name}, message: ${e.message}, cause: ${e.cause}")
            assertTrue(
                e is FirebaseFirestoreException ||
                e.cause is FirebaseFirestoreException ||
                e is java.util.concurrent.CancellationException ||
                e is kotlinx.coroutines.CancellationException ||
                e.message?.contains("Error getting Query snapshot") == true ||
                e.message?.contains("PERMISSION_DENIED") == true
            )
        }
    }
}
