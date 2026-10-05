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
    if (dao.getUserCount() > 0) return

    val now = PasswordHelper.nowIso()

    // 1. Admin User (from Google Apps Script constants)
    val admin = UserEntity(
        userId = "USR-ADMIN-01",
        fullName = "Yaregal Semanew",
        email = "yaregalsemanew@gmail.com",
        passwordHash = PasswordHelper.hashPassword("Admin@2026"),
        role = "admin",
        registrationDate = now,
        accountStatus = "active"
    )
    dao.insertUser(admin)

    // 2. Instructor User
    val instructor = UserEntity(
        userId = "USR-INST-02",
        fullName = "Sarah Johnson, NNP",
        email = "nurse.sarah@hospital.org",
        passwordHash = PasswordHelper.hashPassword("Instructor@2026"),
        role = "instructor",
        registrationDate = now,
        accountStatus = "active"
    )
    dao.insertUser(instructor)

    // 3. Learner User
    val learner = UserEntity(
        userId = "USR-LEARN-03",
        fullName = "Alex Smith, RN",
        email = "learner@hospital.org",
        passwordHash = PasswordHelper.hashPassword("Learner@2026"),
        role = "learner",
        registrationDate = now,
        accountStatus = "active"
    )
    dao.insertUser(learner)

    // Seed Courses
    val c1 = CourseEntity(
        courseId = "CRS-NRP-101",
        courseTitle = "Neonatal Resuscitation Program (NRP)",
        category = "Emergency Care",
        instructorId = instructor.userId,
        instructorName = instructor.fullName,
        description = "Evidence-based clinical protocol for newborn resuscitation in delivery rooms and NICUs. Covers initial thermal stabilization, airway clearance, positive pressure ventilation (PPV), endotracheal intubation, chest compressions, and emergency umbilical vein catheterization.",
        status = "active",
        createdDate = now,
        totalModules = 4
    )
    val c2 = CourseEntity(
        courseId = "CRS-THERMO-102",
        courseTitle = "NICU Thermoregulation & Neutral Thermal Zone",
        category = "Clinical Care",
        instructorId = instructor.userId,
        instructorName = instructor.fullName,
        description = "Core competencies in maintaining optimal thermal environments for very low birth weight (VLBW) infants. Learn radiative, conductive, evaporative, and convective heat loss prevention, servo-control incubators, and kangaroo mother care (KMC).",
        status = "active",
        createdDate = now,
        totalModules = 3
    )
    val c3 = CourseEntity(
        courseId = "CRS-JAUND-103",
        courseTitle = "Neonatal Hyperbilirubinemia & Phototherapy",
        category = "Diagnostics & Therapy",
        instructorId = admin.userId,
        instructorName = admin.fullName,
        description = "Clinical screening, transcutaneous bilirubinometry (TcB), total serum bilirubin (TSB) nomograms, phototherapy irradiance calculation, and exchange transfusion thresholds to prevent acute bilirubin encephalopathy.",
        status = "active",
        createdDate = now,
        totalModules = 3
    )
    val c4 = CourseEntity(
        courseId = "CRS-VENT-104",
        courseTitle = "Neonatal Respiratory Distress & CPAP Management",
        category = "Critical Care",
        instructorId = instructor.userId,
        instructorName = instructor.fullName,
        description = "Pathophysiology of Respiratory Distress Syndrome (RDS), transient tachypnea of the newborn (TTN), bubble CPAP titration, non-invasive ventilation, surfactant administration (LISA/MIST techniques), and blood gas interpretation.",
        status = "active",
        createdDate = now,
        totalModules = 4
    )
    dao.insertCourse(c1)
    dao.insertCourse(c2)
    dao.insertCourse(c3)
    dao.insertCourse(c4)

    // Seed Lessons for Course 1
    val l1 = LessonEntity(
        lessonId = "LES-NRP-01",
        courseId = c1.courseId,
        moduleNumber = 1,
        lessonTitle = "Initial Assessment & Delivery Room Preparation",
        contentType = "Text",
        durationMins = 25,
        orderIndex = 1,
        contentBody = """
# Module 1: Initial Assessment & Delivery Room Preparation

## 1. Preparation Before Birth
Every birth requires at least one skilled professional whose only responsibility is the management of the newborn. If risk factors are identified, at least two qualified individuals should be present.

### Four Pre-Birth Inquiries:
1. What is the expected gestational age?
2. Is the amniotic fluid clear?
3. How many babies are expected?
4. Are there any additional risk factors (e.g., chorioamnionitis, maternal diabetes, fetal distress)?

## 2. Equipment Checklist (The NRP Equipment Tray)
- **Warmth:** Radiant warmer preheated, dry warm towels, plastic wrap for <32 weeks, thermal mattress.
- **Airway:** Bulb syringe, 10F/12F suction catheters connected to wall suction (80-100 mmHg), meconium aspirator.
- **Auscultation & Monitoring:** Neonatal stethoscope, pulse oximeter with neonatal sensor, 3-lead ECG monitor.
- **Ventilation:** Flow-inflating or T-piece resuscitator, neonatal masks (term and preterm), blended oxygen source.
- **Intubation:** Laryngoscope with size 00, 0, and 1 straight blades, endotracheal tubes (2.5, 3.0, 3.5 mm ID), stylets.

## 3. The Golden Minute
Within the first 60 seconds after birth:
1. Warm and dry the infant.
2. Position head and neck in the "sniffing" position to open the airway.
3. Clear secretions ONLY if copious or obstructing breathing.
4. Stimulate by gently rubbing the back or flicking the soles of the feet.
5. Evaluate breathing (apnea/gasping) and heart rate (<100 bpm indicates positive pressure ventilation is needed immediately).
        """.trimIndent(),
        videoUrl = "https://www.youtube.com/watch?v=kYV3VlZ13A0",
        resourceUrl = "https://www.aap.org/nrp"
    )

    val l2 = LessonEntity(
        lessonId = "LES-NRP-02",
        courseId = c1.courseId,
        moduleNumber = 2,
        lessonTitle = "Positive Pressure Ventilation (PPV) & MR. SOPA",
        contentType = "Text",
        durationMins = 35,
        orderIndex = 2,
        contentBody = """
# Module 2: Positive Pressure Ventilation (PPV)

PPV is the single most important and effective step in cardiopulmonary resuscitation of the compromised newborn.

## Indications for PPV
- Apnea or gasping respirations.
- Heart rate less than 100 bpm, even with spontaneous breathing.
- Persistent central cyanosis or low SpO2 despite supplemental free-flow oxygen.

## Ventilation Parameters
- **Rate:** 40 to 60 breaths per minute (rhythm: "Breathe, two, three; breathe, two, three").
- **Initial Pressure:** Peak Inspiratory Pressure (PIP) 20-25 cm H2O. PEEP 5 cm H2O.
- **Oxygen Concentration:**
  - >=35 weeks: Start with 21% (Room Air).
  - <35 weeks: Start with 21% - 30% oxygen. Titrate based on pre-ductal target saturation table.

## Ventilation Corrective Steps (MR. SOPA)
If the heart rate does not increase and the chest is not moving, perform MR. SOPA:
- **M - Mask Adjustment:** Reapply mask and ensure an airtight seal.
- **R - Reposition Airway:** Adjust head to neutral "sniffing" position.
- *(Try PPV and check chest movement)*
- **S - Suction Mouth & Nose:** Clear secretions with bulb syringe or suction catheter.
- **O - Open Mouth:** Open mouth slightly and lift jaw forward.
- *(Try PPV and check chest movement)*
- **P - Pressure Increase:** Increase PIP in increments of 5-10 cm H2O (maximum 40 cm H2O).
- *(Try PPV and check chest movement)*
- **A - Alternative Airway:** Insert endotracheal tube (ETT) or laryngeal mask airway (LMA).
        """.trimIndent(),
        videoUrl = "https://www.youtube.com/watch?v=Zf_C1jBv5_E",
        resourceUrl = "https://www.resus.org.uk/library/2021-resuscitation-guidelines/newborn-resuscitation-and-support-transition-infants"
    )

    val l3 = LessonEntity(
        lessonId = "LES-THERMO-01",
        courseId = c2.courseId,
        moduleNumber = 1,
        lessonTitle = "Mechanisms of Heat Loss in Preterm Infants",
        contentType = "Text",
        durationMins = 20,
        orderIndex = 1,
        contentBody = """
# Mechanisms of Heat Loss in Preterm Infants

Preterm neonates have a high ratio of surface area to body mass, very thin skin with high trans-epidermal water loss, and minimal brown adipose tissue (BAT) for non-shivering thermogenesis.

## Four Physical Mechanisms:
1. **Evaporation:** Wet skin post-birth or high ambient dry air. Prevented by occlusive polyethylene wraps without drying preterm infants <32 weeks.
2. **Radiation:** Heat radiant transfer to cold incubator walls or nearby windows. Prevented by double-walled incubators and thermal incubator blankets.
3. **Convection:** Air currents from open doors, fans, or drafty rooms. Keep incubator portholes closed.
4. **Conduction:** Direct contact with cold scale plates, cold stethoscope, or cold sheets. Pre-warm all surfaces before infant contact.

## Target Axillary Temperature:
- **Normal Range:** 36.5°C to 37.5°C (97.7°F to 99.5°F).
- **Hypothermia Classification:**
  - Cold Stress (Mild): 36.0°C – 36.4°C
  - Moderate: 32.0°C – 35.9°C
  - Severe: < 32.0°C
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.who.int/publications/i/item/9789241599887"
    )

    dao.insertLesson(l1)
    dao.insertLesson(l2)
    dao.insertLesson(l3)

    // Seed Resources
    val res1 = ResourceEntity(
        resourceId = "RES-NRP-FLOWCHART",
        lessonId = l1.lessonId,
        resourceType = "PDF",
        fileName = "NRP_8th_Edition_Algorithm_Summary.pdf",
        mimeType = "application/pdf",
        fileId = "DOC-NRP-8TH",
        fileUrl = "https://www.aap.org/nrp-algorithm",
        uploadedBy = instructor.email,
        uploadedDate = now
    )
    val res2 = ResourceEntity(
        resourceId = "RES-MRSOPA-POCKET",
        lessonId = l2.lessonId,
        resourceType = "PDF",
        fileName = "MR_SOPA_Emergency_Card.pdf",
        mimeType = "application/pdf",
        fileId = "DOC-MRSOPA-CARD",
        fileUrl = "https://pediatrics.aappublications.org",
        uploadedBy = instructor.email,
        uploadedDate = now
    )
    dao.insertResource(res1)
    dao.insertResource(res2)

    // Seed Enrollment for sample learner
    val enr1 = EnrollmentEntity(
        enrollmentId = "ENR-001",
        learnerId = learner.userId,
        learnerName = learner.fullName,
        courseId = c1.courseId,
        enrollmentDate = now,
        completionStatus = "In Progress",
        progressPercentage = 50,
        lastAccessed = now
    )
    dao.insertEnrollment(enr1)

    // Seed Assignment
    val asg1 = AssignmentEntity(
        assignmentId = "ASG-NRP-01",
        courseId = c1.courseId,
        moduleNumber = 2,
        assignmentTitle = "MR. SOPA Simulation & Case Study Analysis",
        instructions = "Review the provided delivery room case: 36-week newborn with meconium stained amniotic fluid, initial heart rate 72 bpm, inadequate chest rise on initial bag-mask ventilation. Detail each step of MR. SOPA in sequence and explain when you would escalate to chest compressions and epinephrine.",
        dueDate = "2026-10-15",
        allowedFileTypes = "PDF, DOCX, TXT",
        status = "active",
        createdBy = instructor.email,
        createdDate = now
    )
    dao.insertAssignment(asg1)

    // Seed Sample Submission
    val sub1 = SubmissionEntity(
        submissionId = "SUB-001",
        assignmentId = asg1.assignmentId,
        learnerId = learner.userId,
        learnerName = learner.fullName,
        courseId = c1.courseId,
        fileName = "Alex_Smith_MR_SOPA_CaseStudy.pdf",
        mimeType = "application/pdf",
        fileId = "FILE-SUB-01",
        fileUrl = "https://hospital.org/lms/submissions/Alex_Smith_CaseStudy.pdf",
        submittedDate = now,
        grade = "96 / 100",
        feedback = "Exceptional clinical reasoning! Correctly identified the exact threshold for starting 3:1 chest compressions when HR <60 bpm after 30s of effective PPV with chest movement.",
        gradedBy = instructor.email,
        gradedDate = now,
        status = "Graded"
    )
    dao.insertSubmission(sub1)

    // Seed Assessment & Questions
    val asm1 = AssessmentEntity(
        assessmentId = "ASM-NRP-QZ1",
        courseId = c1.courseId,
        moduleNumber = 1,
        assessmentTitle = "NRP Core Knowledge & Ventilation Assessment",
        description = "Comprehensive assessment covering delivery room Golden Minute priorities, SpO2 titration, and ventilation corrective steps.",
        passingScore = 80,
        timeLimitMins = 20,
        attemptsAllowed = 3,
        status = "active",
        createdBy = instructor.email,
        createdDate = now
    )
    dao.insertAssessment(asm1)

    val q1 = QuestionEntity(
        questionId = "QST-001",
        assessmentId = asm1.assessmentId,
        questionType = "MCQ",
        questionText = "What is the primary indicator of successful positive pressure ventilation (PPV) during newborn resuscitation?",
        optionA = "Prompt rise in heart rate above 100 bpm",
        optionB = "Immediate pink coloration of feet",
        optionC = "Spontaneous crying within 5 seconds",
        optionD = "Pre-ductal SpO2 reaching 98% in the first minute",
        correctAnswer = "A",
        points = 25,
        explanation = "The single most important and reliable indicator of successful PPV is a rapid increase in the infant's heart rate.",
        orderIndex = 1
    )
    val q2 = QuestionEntity(
        questionId = "QST-002",
        assessmentId = asm1.assessmentId,
        questionType = "MCQ",
        questionText = "When initiating PPV for a term newborn (>=35 weeks), what initial oxygen concentration should be used?",
        optionA = "100% Oxygen",
        optionB = "21% (Room Air)",
        optionC = "50% Oxygen",
        optionD = "30% Oxygen",
        correctAnswer = "B",
        points = 25,
        explanation = "Resuscitation of term and late preterm newborns should begin with 21% oxygen (room air) to prevent hyperoxic oxidative stress.",
        orderIndex = 2
    )
    val q3 = QuestionEntity(
        questionId = "QST-003",
        assessmentId = asm1.assessmentId,
        questionType = "MCQ",
        questionText = "In the MR. SOPA ventilation corrective sequence, what does the letter 'P' stand for?",
        optionA = "Position prone",
        optionB = "Pressure increase (by 5-10 cm H2O)",
        optionC = "Pure oxygen switch",
        optionD = "Pulse palpation",
        correctAnswer = "B",
        points = 25,
        explanation = "'P' stands for Pressure increase in increments of 5 to 10 cm H2O up to a maximum recommended PIP of 40 cm H2O.",
        orderIndex = 3
    )
    val q4 = QuestionEntity(
        questionId = "QST-004",
        assessmentId = asm1.assessmentId,
        questionType = "MCQ",
        questionText = "What is the normal target axillary temperature for a newborn infant according to WHO guidelines?",
        optionA = "35.5°C to 36.5°C",
        optionB = "36.5°C to 37.5°C",
        optionC = "37.5°C to 38.5°C",
        optionD = "36.0°C to 37.0°C",
        correctAnswer = "B",
        points = 25,
        explanation = "WHO defines the normal neonatal thermal range as 36.5°C to 37.5°C (97.7°F to 99.5°F).",
        orderIndex = 4
    )
    dao.insertQuestion(q1)
    dao.insertQuestion(q2)
    dao.insertQuestion(q3)
    dao.insertQuestion(q4)

    // Seed Activity Log
    val log1 = ActivityLogEntity(
        activityId = "ACT-INIT-01",
        userEmail = admin.email,
        actionType = "SYSTEM_INITIALIZE",
        timestamp = now,
        ipAddress = "Android-Device",
        statusMessage = "Neonatal Nursing LMS initialized with default admin and clinical curriculum."
    )
    dao.insertLog(log1)
}
