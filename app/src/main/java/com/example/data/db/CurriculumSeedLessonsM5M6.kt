package com.example.data.db

import com.example.data.dao.LmsDao
import com.example.data.model.LessonEntity

suspend fun seedCurriculumModule5Lessons(dao: LmsDao) {
    val l501 = LessonEntity(
        lessonId = "LES-PCHNM-501",
        courseId = "CRS-PCHNM-M5",
        moduleNumber = 5,
        lessonTitle = "Unit 5.1.1: NICU Practicum (120 Hours) — Advanced Critical Interventions",
        contentType = "Text",
        durationMins = 40,
        orderIndex = 1,
        contentBody = """
# Unit 5.1.1: NICU Practicum (120 Hours) — Advanced Critical Interventions

The NICU Practicum comprises 3 weeks (120 clinical contact hours) under direct preceptor supervision.

## Clinical Competencies Required in the NICU:
1. **Oxygen Therapy & CPAP Management:**
   - Setting up bubble CPAP with heated humidification (37°C) and water column pressure (5–7 cm H2O).
   - Securing nasal prongs with proper bonnet and chin strap without columellar pressure necrosis.
2. **Endotracheal Intubation Assistance:**
   - Pre-oxygenation, positioning, laryngoscope blade selection, ETT depth calculation:
     ${'$'}{'$'}\text{ETT Depth (cm at lip)} = 6 + \text{Weight in kg}${'$'}{'$'}
3. **Intravenous Fluid Calculation & Infusion Pumps:**
   - Precision programming of micro-infusion and syringe pumps; checking for air bubbles to prevent fatal air embolism.
4. **Phototherapy Irradiance Monitoring:**
   - Calibrating radiometers, positioning phototherapy units at 35–45 cm distance, monitoring infant temperature every 3 hours.
5. **Exchange Transfusion Assistance:**
   - Preparing heparinized saline flushes, maintaining the waste and donor blood log, warming blood.
6. **Arterial & Capillary Blood Gas Sampling:**
   - Performing Allen test prior to radial artery puncture; proper heel warm-up and lateral plantar border lancing.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l502 = LessonEntity(
        lessonId = "LES-PCHNM-502",
        courseId = "CRS-PCHNM-M5",
        moduleNumber = 5,
        lessonTitle = "Unit 5.2.1: Postnatal & Community Rotation (40 Hours) — Essential Care & Counselling",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 2,
        contentBody = """
# Unit 5.2.1: Postnatal & Community Rotation (40 Hours) — Essential Care & Counselling

The Postnatal Ward rotation comprises 1 week (40 clinical contact hours) focused on mother-infant dyad care and community transition.

## Clinical Focus Areas:
1. **Essential Newborn Care Execution:** Providing standard immediate care, cord antisepsis, thermal preservation, and vitamin K injection.
2. **Breastfeeding Counselling (LATCH Assessment):**
   - Latch: Full mouth over areola, flanged lips.
   - Audible swallowing: Clear rhythmic swallow sounds.
   - Type of nipple: Everted vs flat/inverted.
   - Comfort: Pain-free latch.
   - Hold: C-hold, football, or cross-cradle position.
3. **Danger Sign Identification for Families:**
   - Educating parents to seek immediate emergency care if infant exhibits:
     - Inability to suck or feed
     - Fast breathing (> 60 breaths/min) or chest in-drawing
     - Fever (> 37.5°C) or cold body (< 36.5°C)
     - Convulsions or abnormal twitching
     - Yellow palms or soles of feet
     - Red, swollen, or foul-smelling cord stump.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.who.int"
    )

    val l503 = LessonEntity(
        lessonId = "LES-PCHNM-503",
        courseId = "CRS-PCHNM-M5",
        moduleNumber = 5,
        lessonTitle = "Unit 5.3.1: Clinical Logbook & Procedural Competency Verification",
        contentType = "Text",
        durationMins = 25,
        orderIndex = 3,
        contentBody = """
# Unit 5.3.1: Clinical Logbook & Procedural Competency Verification

## Clinical Logbook Requirements
Students must complete and record evidence for the following minimum procedure counts:
- [ ] Neonatal Resuscitation Drill / Golden Minute: Minimum **5 sessions**
- [ ] Bubble CPAP Assembly and Maintenance: Minimum **5 sessions**
- [ ] IV Fluid Rate and GIR Calculations: Minimum **10 verified cases**
- [ ] Phototherapy and Bilirubin Monitoring: Minimum **5 patients**
- [ ] Kangaroo Mother Care Positioning and Counselling: Minimum **5 mother-infant dyads**
- [ ] Nasogastric Tube Placement and Tube Feeding: Minimum **5 verified procedures**
- [ ] EPI Immunization (BCG / Hep B / OPV): Minimum **5 verified administrations**
- [ ] Parent Discharge Education & Danger Sign Counselling: Minimum **3 sessions**

Every entry requires the date, hospital registration number, procedure details, reflection on challenges, and clinical preceptor signature.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    dao.insertLesson(l501)
    dao.insertLesson(l502)
    dao.insertLesson(l503)
}

suspend fun seedCurriculumModule6Lessons(dao: LmsDao) {
    val l601 = LessonEntity(
        lessonId = "LES-PCHNM-601",
        courseId = "CRS-PCHNM-M6",
        moduleNumber = 6,
        lessonTitle = "Unit 6.1.1: Midterm Theory Examination Review & Structure (Modules 1–3)",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 1,
        contentBody = """
# Unit 6.1.1: Midterm Theory Examination Review & Structure (Modules 1–3)

The Midterm Written Examination is scheduled at Week 9 and carries **10% of the course grade**.

## Examination Blueprint:
- **Module 1 (Foundations & Physiology):** 30% weight — fetal adaptation, mortality indicators, GA and weight classifications.
- **Module 2 (Immediate Care & NRP):** 40% weight — 11-step ENC, Golden Minute, PPV, MR. SOPA, 3:1 compressions, epinephrine dosing, pre-ductal SpO2 targets.
- **Module 3 (Pathology & Crises):** 30% weight — birth trauma differential, RDS vs TTN vs MAS, ABG analysis, phototherapy/exchange transfusion, seizure management.

Review high-yield clinical decision points, formulas, and rationales before attempting the examination.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l602 = LessonEntity(
        lessonId = "LES-PCHNM-602",
        courseId = "CRS-PCHNM-M6",
        moduleNumber = 6,
        lessonTitle = "Unit 6.1.2: Master OSCE Station — 10-Minute Resuscitation & 10-Domain Rubric",
        contentType = "Text",
        durationMins = 40,
        orderIndex = 2,
        contentBody = """
# Unit 6.1.2: Master OSCE Station — 10-Minute Resuscitation & 10-Domain Rubric

The Objective Structured Clinical Examination (OSCE) carries **20% of the total course grade**. The pass mark is **80% (16 / 20 points)**.

## Master OSCE Scoring Rubric (10 Domains × 2 Points):
1. **Preparation & Infection Control (2 pts):** Performs hand hygiene, dons PPE, checks radiant warmer, suction (80-100 mmHg), and PPV equipment prior to delivery.
2. **Initial Thermal & Airway Management (2 pts):** Dries neonate, discards wet linen, positions head in sniffing position, clears airway appropriately within 30 seconds.
3. **Rapid Assessment & APGAR (2 pts):** Accurately assesses heart rate by apical auscultation and breathing within 30 seconds; calculates APGAR.
4. **PPV Execution & Technique (2 pts):** Initiates PPV at 40–60 breaths/min with airtight mask seal, observing chest rise within 30 seconds of birth.
5. **Corrective Measures (MR. SOPA) (2 pts):** Systematically applies MR. SOPA sequence when chest rise is absent during PPV.
6. **Chest Compressions & Coordination (2 pts):** Delivers 3:1 compressions (90 compressions : 30 breaths) synchronized with 100% oxygen when HR < 60 bpm.
7. **Medication Administration & Access (2 pts):** Correctly calculates epinephrine dosage (0.02 mg/kg) and identifies umbilical venous catheter (UVC) route.
8. **Team Communication & Reassessment (2 pts):** Communicates clear, concise updates; re-evaluates heart rate and SpO2 every 30 seconds.
9. **Family Counselling & Empathy (2 pts):** Delivers clear, compassionate, and culturally sensitive clinical updates to parents post-resuscitation.
10. **Documentation, Post-Resuscitation Care & Debriefing (2 pts):** Initiates NICU transfer protocols, maintains thermal chain, completes accurate documentation, and articulates a structured Pendleton debrief.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l603 = LessonEntity(
        lessonId = "LES-PCHNM-603",
        courseId = "CRS-PCHNM-M6",
        moduleNumber = 6,
        lessonTitle = "Unit 6.1.3: Final Comprehensive Theory Examination & Clinical Portfolio Defence",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 3,
        contentBody = """
# Unit 6.1.3: Final Comprehensive Theory Examination & Clinical Portfolio Defence

## 1. Final Comprehensive Examination (40% Weight)
- Comprehensive evaluation administered through the LMS at the conclusion of Week 16.
- Tests clinical synthesis across all 9 Course Learning Outcomes (CO1 through CO9).
- Clinical problem-solving scenarios, emergency dosing calculations, and diagnostic decision-making.

## 2. Clinical Portfolio Defence (Weeks 14–16)
- Submission and oral defence of the maintained Clinical Portfolio before a faculty review panel.
- Assesses professional accountability, evidence-based nursing reflections, verified logbook completion, and multidisciplinary communication.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l604 = LessonEntity(
        lessonId = "LES-PCHNM-604",
        courseId = "CRS-PCHNM-M6",
        moduleNumber = 6,
        lessonTitle = "Unit 6.2.1: Three-Tier Feedback Strategy & The Pendleton Debriefing Framework",
        contentType = "Text",
        durationMins = 25,
        orderIndex = 4,
        contentBody = """
# Unit 6.2.1: Three-Tier Feedback Strategy & The Pendleton Debriefing Framework

Constructive and structured feedback prevents clinical error propagation and builds professional confidence.

## The Three-Tier Feedback Framework:
1. **Tier 1: Immediate Clinical Debriefing — The Pendleton Model:**
   - Carried out immediately after every simulation drill, resuscitation, or bedside procedure:
     - **Step 1:** The learner states what they did well.
     - **Step 2:** The instructor reinforces and confirms what went well.
     - **Step 3:** The learner identifies areas for improvement.
     - **Step 4:** The instructor provides constructive coaching and actionable correction.
2. **Tier 2: Automated LMS Diagnostic Feedback:**
   - Online formative quizzes return instant item-level feedback explaining why an answer is correct or incorrect, linking directly to clinical protocols.
3. **Tier 3: Mid-Rotation Formative Clinical Review:**
   - Conducted at Week 11 midway through the clinical practicum. Preceptors review logbooks and conduct one-on-one reviews to bridge gaps prior to the final OSCE.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l605 = LessonEntity(
        lessonId = "LES-PCHNM-605",
        courseId = "CRS-PCHNM-M6",
        moduleNumber = 6,
        lessonTitle = "Unit 6.2.2: Universal Design for Learning (UDL) & WCAG 2.1 Level AA Compliance Report",
        contentType = "Text",
        durationMins = 25,
        orderIndex = 5,
        contentBody = """
# Unit 6.2.2: Universal Design for Learning (UDL) & WCAG 2.1 Level AA Compliance Report

## 1. Web Content Accessibility Guidelines (WCAG 2.1 Level AA) Compliance:
- **Contrast (1.4.3):** Body text achieves contrast ratio of at least 7.1:1 (dark charcoal on off-white/surface). Deep navy headings provide clear visual hierarchy.
- **Non-Text Content (1.1.1):** All instructional diagrams (NRP flowchart, APGAR scoring tables) carry structured textual descriptions and alternative text.
- **Info and Relationships (1.3.1):** Semantic typography headings, scoped table headers, and Unicode mathematical representations.
- **Keyboard Navigation (2.1.1):** All interactive elements and self-assessment disclosures are fully operable via keyboard and touch gestures.
- **Captions & Transcripts (1.2.2 & 1.2.5):** Synchronized video demonstrations and downloadable full-text transcripts.

## 2. Universal Design for Learning (UDL) Alignment:
- **Multiple Means of Representation:** Content provided through multimodal formats (narrated lectures, structured clinical algorithms, interactive calculators, video tutorials).
- **Multiple Means of Action & Expression:** Mastery demonstrated via written case analyses, oral PBL presentations, high-fidelity OSCE performance, and reflective clinical logbooks.
- **Multiple Means of Engagement:** Collaborative learning via Think-Pair-Share, interprofessional team drills, self-paced diagnostic quizzes, and bedside clinical rounds.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    dao.insertLesson(l601)
    dao.insertLesson(l602)
    dao.insertLesson(l603)
    dao.insertLesson(l604)
    dao.insertLesson(l605)
}
