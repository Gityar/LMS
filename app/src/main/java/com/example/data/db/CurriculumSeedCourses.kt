package com.example.data.db

import com.example.data.dao.LmsDao
import com.example.data.model.CourseEntity
import com.example.data.model.UserEntity

suspend fun seedCurriculumCourses(
    dao: LmsDao,
    admin: UserEntity,
    instructor: UserEntity,
    instructor2: UserEntity,
    now: String
) {
    val m1 = CourseEntity(
        courseId = "CRS-PCHNM-M1",
        courseTitle = "Module 1: Foundations of Neonatology, Physiology & Classification",
        category = "Foundational Theory",
        instructorId = instructor.userId,
        instructorName = instructor.fullName,
        description = "Wollo University PCHNM-3073. Focuses on neonatal mortality epidemiology (global, regional, national), transition from fetal to extrauterine life, cardiorespiratory/metabolic adaptation, brown adipose tissue thermogenesis, neonatal reflexes, and gestational age/birth weight classification.",
        status = "active",
        createdDate = now,
        totalModules = 3
    )

    val m2 = CourseEntity(
        courseId = "CRS-PCHNM-M2",
        courseTitle = "Module 2: Immediate Care, Assessment & Neonatal Resuscitation (NRP)",
        category = "Emergency & Resuscitation",
        instructorId = instructor2.userId,
        instructorName = instructor2.fullName,
        description = "Delivery room triage, the 11-step Essential Newborn Care (ENC), APGAR scoring system, The Golden Minute, NRP clinical algorithm, Positive Pressure Ventilation (PPV), MR. SOPA ventilation corrective sequence, 3:1 chest compressions, IV epinephrine via UVC, and targeted pre-ductal SpO2 standards.",
        status = "active",
        createdDate = now,
        totalModules = 4
    )

    val m3 = CourseEntity(
        courseId = "CRS-PCHNM-M3",
        courseTitle = "Module 3: Birth Injuries, Respiratory, Haematological & Metabolic Disorders",
        category = "Clinical Pathology",
        instructorId = instructor.userId,
        instructorName = instructor.fullName,
        description = "Covers soft tissue, skeletal and neurological birth trauma (caput succedaneum vs cephalohematoma, subgaleal haemorrhage, Erb's & Klumpke's palsies), RDS, TTN, MAS, ABG analysis, neonatal jaundice, phototherapy, double volume exchange transfusion, seizures, HIE Sarnat staging, therapeutic hypothermia, and hypoglycemia.",
        status = "active",
        createdDate = now,
        totalModules = 6
    )

    val m4 = CourseEntity(
        courseId = "CRS-PCHNM-M4",
        courseTitle = "Module 4: Infections, Congenital Anomalies & High-Risk Nutrition",
        category = "High-Risk Care",
        instructorId = instructor2.userId,
        instructorName = instructor2.fullName,
        description = "Early- and late-onset neonatal sepsis, meningitis, tetanus, nosocomial infection control, Necrotizing Enterocolitis (NEC), Congenital Anomalies (CHD, NTD, Down syndrome, TEF, CDH, omphalocele, gastroschisis), prematurity management, fluid tables, Total Parenteral Nutrition (TPN), Kangaroo Mother Care (KMC), and EPI immunization.",
        status = "active",
        createdDate = now,
        totalModules = 6
    )

    val m5 = CourseEntity(
        courseId = "CRS-PCHNM-M5",
        courseTitle = "Module 5: Guided Clinical Practice & Practicum (160 Hours)",
        category = "Clinical Practicum",
        instructorId = instructor.userId,
        instructorName = instructor.fullName,
        description = "Hands-on guided clinical rotation representing 70% of course weight: 3 weeks in the NICU (120 hrs) covering oxygen therapy, bubble CPAP titration, intubation assist, IV infusions, exchange transfusion, and verified clinical logbooks; 1 week in Maternal/Postnatal Ward (40 hrs) covering essential newborn care, APGAR, and breastfeeding counselling.",
        status = "active",
        createdDate = now,
        totalModules = 3
    )

    val m6 = CourseEntity(
        courseId = "CRS-PCHNM-M6",
        courseTitle = "Module 6: Final Assessments, OSCE Evaluation & Continuous Improvement",
        category = "Summative & Evaluation",
        instructorId = admin.userId,
        instructorName = admin.fullName,
        description = "Summative competency validation: Midterm written exam (10%), Master Objective Structured Clinical Examination (OSCE, 20-point rubric across 10 domains, pass mark 16/20 = 80%), Final Comprehensive Written Examination (40%), Portfolio Defence, Three-tier Feedback Strategy with the Pendleton Model, and WCAG 2.1 AA / UDL Course Audit.",
        status = "active",
        createdDate = now,
        totalModules = 5
    )

    dao.insertCourse(m1)
    dao.insertCourse(m2)
    dao.insertCourse(m3)
    dao.insertCourse(m4)
    dao.insertCourse(m5)
    dao.insertCourse(m6)
}
