package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.LmsDao
import com.example.data.model.*
import com.example.data.util.PasswordHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        CourseEntity::class,
        LessonEntity::class,
        EnrollmentEntity::class,
        ResourceEntity::class,
        AssignmentEntity::class,
        SubmissionEntity::class,
        AssessmentEntity::class,
        QuestionEntity::class,
        AttemptEntity::class,
        ActivityLogEntity::class,
        PasswordResetEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lmsDao(): LmsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "neonatal_lms.db"
                )
                    .addCallback(DatabaseSeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseSeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedInitialData(database.lmsDao())
                }
            }
        }
    }
}

suspend fun seedInitialData(dao: LmsDao) {
    val now = PasswordHelper.nowIso()

    // 1. Ensure User Accounts and Privileges are set up (Preserve existing if already present)
    var admin = dao.getUserByEmail("yaregalsemanew@gmail.com")
    if (admin == null) {
        admin = UserEntity(
            userId = "USR-ADMIN-01",
            fullName = "Yaregal Semanew",
            email = "yaregalsemanew@gmail.com",
            passwordHash = PasswordHelper.hashPassword("Admin@2026"),
            role = "admin",
            registrationDate = now,
            accountStatus = "active"
        )
        dao.insertUser(admin)
    }

    var instructor = dao.getUserByEmail("almaz@neonatal.et")
    if (instructor == null) {
        instructor = UserEntity(
            userId = "USR-INST-02",
            fullName = "Sister Almaz",
            email = "almaz@neonatal.et",
            passwordHash = PasswordHelper.hashPassword("Instructor@2026"),
            role = "instructor",
            registrationDate = now,
            accountStatus = "active"
        )
        dao.insertUser(instructor)
    }

    var instructor2 = dao.getUserByEmail("nurse.sarah@hospital.org")
    if (instructor2 == null) {
        instructor2 = UserEntity(
            userId = "USR-INST-03",
            fullName = "Sarah Johnson, NNP",
            email = "nurse.sarah@hospital.org",
            passwordHash = PasswordHelper.hashPassword("Instructor@2026"),
            role = "instructor",
            registrationDate = now,
            accountStatus = "active"
        )
        dao.insertUser(instructor2)
    }

    var learner = dao.getUserByEmail("nurse.tigist@hospital.et")
    if (learner == null) {
        learner = UserEntity(
            userId = "USR-LEARN-04",
            fullName = "Nurse Tigist Mengistu",
            email = "nurse.tigist@hospital.et",
            passwordHash = PasswordHelper.hashPassword("Learner@2026"),
            role = "learner",
            registrationDate = now,
            accountStatus = "active"
        )
        dao.insertUser(learner)
    }

    var learner2 = dao.getUserByEmail("learner@hospital.org")
    if (learner2 == null) {
        learner2 = UserEntity(
            userId = "USR-LEARN-05",
            fullName = "Alex Smith, RN",
            email = "learner@hospital.org",
            passwordHash = PasswordHelper.hashPassword("Learner@2026"),
            role = "learner",
            registrationDate = now,
            accountStatus = "active"
        )
        dao.insertUser(learner2)
    }

    // 2. Check if the new curriculum is already seeded
    val hasNewCurriculum = dao.getCourseById("CRS-PCHNM-M1") != null
    if (!hasNewCurriculum) {
        // Remove all previous courses, lessons, assignments, assessments, and questions
        dao.clearAllCourses()
        dao.clearAllLessons()
        dao.clearAllResources()
        dao.clearAllAssignments()
        dao.clearAllSubmissions()
        dao.clearAllAssessments()
        dao.clearAllQuestions()

        // Seed the new Wollo University PCHNM-3073 curriculum
        seedCurriculumData(dao, admin, instructor, instructor2, learner, learner2, now)
    }
}
