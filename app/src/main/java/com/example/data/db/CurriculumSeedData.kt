package com.example.data.db

import com.example.data.dao.LmsDao
import com.example.data.model.UserEntity

/**
 * Wollo University, College of Medicine and Health Sciences
 * Department of Pediatrics & Child Health Nursing
 * Course Code: PCHNM-3073 | Neonatal Nursing
 * Integrated Course Design & OpenEdX Studio Build Architecture
 * 10 ECTS / 6 Cr.hr | 16 Weeks | 270 Hours (76 Theory, 168 Practice, 26 Self-Study)
 * 6 Modules | 160 Clinical Hours | Blended Delivery
 */
suspend fun seedCurriculumData(
    dao: LmsDao,
    admin: UserEntity,
    instructor: UserEntity,
    instructor2: UserEntity,
    learner: UserEntity,
    learner2: UserEntity,
    now: String
) {
    // 1. Seed 6 Curriculum Modules (Courses)
    seedCurriculumCourses(dao, admin, instructor, instructor2, now)

    // 2. Seed Module Lessons
    seedCurriculumModule1Lessons(dao)
    seedCurriculumModule2Lessons(dao)
    seedCurriculumModule3Lessons(dao)
    seedCurriculumModule4Lessons(dao)
    seedCurriculumModule5Lessons(dao)
    seedCurriculumModule6Lessons(dao)

    // 3. Seed Assessments & Formative/Summative Questions
    seedCurriculumAssessments(dao, admin, instructor, instructor2, now)

    // 4. Seed Clinical Assignments, Resources, Sample Enrollments & Audit Logs
    seedCurriculumAssignmentsAndResources(dao, admin, instructor, instructor2, learner, learner2, now)
}
