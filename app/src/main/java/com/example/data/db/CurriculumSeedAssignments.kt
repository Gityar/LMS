package com.example.data.db

import com.example.data.dao.LmsDao
import com.example.data.model.ActivityLogEntity
import com.example.data.model.AssignmentEntity
import com.example.data.model.EnrollmentEntity
import com.example.data.model.ResourceEntity
import com.example.data.model.UserEntity

suspend fun seedCurriculumAssignmentsAndResources(
    dao: LmsDao,
    admin: UserEntity,
    instructor: UserEntity,
    instructor2: UserEntity,
    learner: UserEntity,
    learner2: UserEntity,
    now: String
) {
    // -------------------------------------------------------------
    // CLINICAL ASSIGNMENTS (PBL & PORTFOLIO)
    // -------------------------------------------------------------
    val asg1 = AssignmentEntity(
        assignmentId = "ASG-PCHNM-PBL1",
        courseId = "CRS-PCHNM-M3",
        moduleNumber = 1,
        assignmentTitle = "PBL 1 Case Analysis: Severe Birth Trauma & Brachial Plexus Injury",
        instructions = "Review the Week 3 clinical case of Baby Boy Mulu: 4.2 kg infant delivered after difficult vacuum extraction presenting with fluctuant scalp swelling crossing sutures, tachycardia, dropping hematocrit (28%), and limp right arm in 'waiter's tip' posture. Submit a 2-page clinical care plan detailing emergency shock resuscitation, UVC fluid expansion, monitoring parameters for subgaleal hemorrhage, and physical therapy positioning for Erb's palsy.",
        dueDate = "2026-11-15",
        allowedFileTypes = "PDF, DOCX, TXT",
        status = "active",
        createdBy = instructor.email,
        createdDate = now
    )

    val asg2 = AssignmentEntity(
        assignmentId = "ASG-PCHNM-PBL2",
        courseId = "CRS-PCHNM-M3",
        moduleNumber = 3,
        assignmentTitle = "PBL 2 Case Analysis: HIE & Therapeutic Hypothermia Protocol",
        instructions = "Draft an evidence-based institutional protocol for managing an infant born with severe hypoxic-ischemic encephalopathy (HIE Stage 2/3). Include criteria for therapeutic hypothermia eligibility (<6 hrs of life, pH <7.0 or BE ≥16), target temperature monitoring (33.5°C), management of secondary neonatal seizures with Phenobarbitone (20 mg/kg), and the slow rewarming schedule (0.5°C/hr).",
        dueDate = "2026-11-30",
        allowedFileTypes = "PDF, DOCX, TXT",
        status = "active",
        createdBy = instructor.email,
        createdDate = now
    )

    val asg3 = AssignmentEntity(
        assignmentId = "ASG-PCHNM-DISCH",
        courseId = "CRS-PCHNM-M4",
        moduleNumber = 3,
        assignmentTitle = "Family-Centred Discharge & Danger-Sign Counselling Plan",
        instructions = "Design a family-centred discharge and follow-up teaching plan for a preterm/LBW infant (birth weight 1.6 kg, discharged at 1.9 kg) that integrates Kangaroo Mother Care (KMC), exclusive cup/breastfeeding, EPI immunization scheduling, and danger-sign recognition (fast breathing, hypothermia, convulsions, jaundice) for parents.",
        dueDate = "2026-12-15",
        allowedFileTypes = "PDF, DOCX, TXT",
        status = "active",
        createdBy = instructor2.email,
        createdDate = now
    )

    val asg4 = AssignmentEntity(
        assignmentId = "ASG-PCHNM-LOG",
        courseId = "CRS-PCHNM-M5",
        moduleNumber = 1,
        assignmentTitle = "Verified Clinical Logbook & Reflective Journal Submission",
        instructions = "Upload your complete, preceptor-signed 160-Hour Clinical Practicum Logbook (120 hrs NICU, 40 hrs Postnatal Ward). Must verify procedural competency in CPAP assembly, resuscitation drills, phototherapy irradiance calculation, UVC assistance, and weekly Pendleton model reflective journals.",
        dueDate = "2026-12-28",
        allowedFileTypes = "PDF, DOCX, TXT",
        status = "active",
        createdBy = admin.email,
        createdDate = now
    )

    dao.insertAssignment(asg1)
    dao.insertAssignment(asg2)
    dao.insertAssignment(asg3)
    dao.insertAssignment(asg4)

    // -------------------------------------------------------------
    // CURRICULUM CLINICAL RESOURCES
    // -------------------------------------------------------------
    val res1 = ResourceEntity(
        resourceId = "RES-PCHNM-NRP-CHART",
        lessonId = "LES-PCHNM-203",
        resourceType = "PDF",
        fileName = "NRP_8th_Edition_Algorithm_Summary.pdf",
        mimeType = "application/pdf",
        fileId = "DOC-NRP-8TH",
        fileUrl = "https://www.aap.org/nrp-algorithm",
        uploadedBy = instructor.email,
        uploadedDate = now
    )

    val res2 = ResourceEntity(
        resourceId = "RES-PCHNM-SPO2-CARD",
        lessonId = "LES-PCHNM-203",
        resourceType = "PDF",
        fileName = "Targeted_Preductal_SpO2_Pocket_Reference.pdf",
        mimeType = "application/pdf",
        fileId = "DOC-SPO2-CARD",
        fileUrl = "https://gityar.github.io/MyPortfolio",
        uploadedBy = instructor2.email,
        uploadedDate = now
    )

    val res3 = ResourceEntity(
        resourceId = "RES-PCHNM-FLUID-CHART",
        lessonId = "LES-PCHNM-404",
        resourceType = "PDF",
        fileName = "Neonatal_Fluid_and_Electrolyte_Protocol_Chart.pdf",
        mimeType = "application/pdf",
        fileId = "DOC-FLUID-CHART",
        fileUrl = "https://www.who.int",
        uploadedBy = instructor2.email,
        uploadedDate = now
    )

    val res4 = ResourceEntity(
        resourceId = "RES-PCHNM-OSCE-RUBRIC",
        lessonId = "LES-PCHNM-602",
        resourceType = "PDF",
        fileName = "Master_OSCE_Scoring_Rubric_20_Points.pdf",
        mimeType = "application/pdf",
        fileId = "DOC-OSCE-RUBRIC",
        fileUrl = "https://gityar.github.io/MyPortfolio",
        uploadedBy = admin.email,
        uploadedDate = now
    )

    dao.insertResource(res1)
    dao.insertResource(res2)
    dao.insertResource(res3)
    dao.insertResource(res4)

    // -------------------------------------------------------------
    // SAMPLE ENROLLMENTS FOR LEARNERS
    // -------------------------------------------------------------
    val enr1 = EnrollmentEntity(
        enrollmentId = "ENR-PCHNM-01",
        learnerId = learner.userId,
        learnerName = learner.fullName,
        courseId = "CRS-PCHNM-M1",
        enrollmentDate = now,
        completionStatus = "Completed",
        progressPercentage = 100,
        lastAccessed = now
    )
    val enr2 = EnrollmentEntity(
        enrollmentId = "ENR-PCHNM-02",
        learnerId = learner.userId,
        learnerName = learner.fullName,
        courseId = "CRS-PCHNM-M2",
        enrollmentDate = now,
        completionStatus = "In Progress",
        progressPercentage = 65,
        lastAccessed = now
    )
    val enr3 = EnrollmentEntity(
        enrollmentId = "ENR-PCHNM-03",
        learnerId = learner2.userId,
        learnerName = learner2.fullName,
        courseId = "CRS-PCHNM-M2",
        enrollmentDate = now,
        completionStatus = "In Progress",
        progressPercentage = 40,
        lastAccessed = now
    )

    dao.insertEnrollment(enr1)
    dao.insertEnrollment(enr2)
    dao.insertEnrollment(enr3)

    // Log Activity
    val log = ActivityLogEntity(
        activityId = "ACT-CURRICULUM-SYNC",
        userEmail = admin.email,
        actionType = "CURRICULUM_SYNC",
        timestamp = now,
        ipAddress = "Android-Device",
        statusMessage = "Synchronized Wollo University PCHNM-3073 6-module neonatal nursing curriculum and master OSCE rubrics."
    )
    dao.insertLog(log)
}
