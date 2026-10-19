package com.example.data.db

import com.example.data.dao.LmsDao
import com.example.data.model.AssessmentEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.UserEntity

suspend fun seedCurriculumAssessments(
    dao: LmsDao,
    admin: UserEntity,
    instructor: UserEntity,
    instructor2: UserEntity,
    now: String
) {
    // 1. Module 1 Quiz
    val asm1 = AssessmentEntity(
        assessmentId = "ASM-PCHNM-QZ1",
        courseId = "CRS-PCHNM-M1",
        moduleNumber = 1,
        assessmentTitle = "Module 1 Knowledge Check: Neonatal Transition & Classification",
        description = "Formative quiz assessing understanding of global neonatal mortality, transitional cardiorespiratory adaptation, brown fat thermogenesis, and neonatal classifications.",
        passingScore = 80,
        timeLimitMins = 20,
        attemptsAllowed = 3,
        status = "active",
        createdBy = instructor.email,
        createdDate = now
    )
    dao.insertAssessment(asm1)

    val q1_1 = QuestionEntity(
        questionId = "QST-PCHNM-101",
        assessmentId = asm1.assessmentId,
        questionType = "MCQ",
        questionText = "Globally, what percentage of all under-5 child deaths occur during the first month of life (neonatal period)?",
        optionA = "25%",
        optionB = "35%",
        optionC = "47%",
        optionD = "65%",
        correctAnswer = "C",
        points = 25,
        explanation = "According to WHO/UNICEF 2021 data, neonatal deaths account for approximately 47% of all under-5 child deaths globally (2.3 million deaths annually).",
        orderIndex = 1
    )
    val q1_2 = QuestionEntity(
        questionId = "QST-PCHNM-102",
        assessmentId = asm1.assessmentId,
        questionType = "MCQ",
        questionText = "Which physiological change is responsible for the functional closure of the foramen ovale at birth?",
        optionA = "Increase in right atrial pressure due to umbilical vein constriction",
        optionB = "Rise in left atrial pressure exceeding right atrial pressure as pulmonary venous return increases",
        optionC = "Direct contraction of smooth muscle fibers stimulated by bradykinin",
        optionD = "Increase in pulmonary vascular resistance immediately after the first breath",
        correctAnswer = "B",
        points = 25,
        explanation = "With lung expansion and decreased PVR, pulmonary venous blood flow to the left atrium surges, raising left atrial pressure above right atrial pressure and closing the foramen ovale flap.",
        orderIndex = 2
    )
    val q1_3 = QuestionEntity(
        questionId = "QST-PCHNM-103",
        assessmentId = asm1.assessmentId,
        questionType = "MCQ",
        questionText = "How is an infant born at 31 completed weeks of gestation with a birth weight of 1,200 grams classified?",
        optionA = "Late preterm, Low Birth Weight (LBW)",
        optionB = "Very preterm, Very Low Birth Weight (VLBW)",
        optionC = "Extremely preterm, Extremely Low Birth Weight (ELBW)",
        optionD = "Moderate preterm, Normal birth weight",
        correctAnswer = "B",
        points = 25,
        explanation = "Gestation between 28 and <32 weeks is classified as Very Preterm; a birth weight between 1,000g and 1,499g is classified as Very Low Birth Weight (VLBW).",
        orderIndex = 3
    )
    val q1_4 = QuestionEntity(
        questionId = "QST-PCHNM-104",
        assessmentId = asm1.assessmentId,
        questionType = "MCQ",
        questionText = "What is the primary mechanism of heat production in newborn infants exposed to cold stress?",
        optionA = "Vigorous shivering thermogenesis of major skeletal muscles",
        optionB = "Non-shivering thermogenesis via brown adipose tissue (BAT) metabolism",
        optionC = "Peripheral cutaneous vasodilation to increase blood flow",
        optionD = "Immediate acceleration of gluconeogenesis in the brain",
        correctAnswer = "B",
        points = 25,
        explanation = "Newborns do not shiver. They produce heat via norepinephrine-mediated lipolysis in brown adipose tissue containing uncoupling protein-1 (UCP-1).",
        orderIndex = 4
    )
    dao.insertQuestion(q1_1)
    dao.insertQuestion(q1_2)
    dao.insertQuestion(q1_3)
    dao.insertQuestion(q1_4)

    // 2. Module 2 NRP Assessment
    val asm2 = AssessmentEntity(
        assessmentId = "ASM-PCHNM-NRP",
        courseId = "CRS-PCHNM-M2",
        moduleNumber = 2,
        assessmentTitle = "Module 2: NRP Clinical Scenarios & Calculations Assessment",
        description = "Core interactive assessment covering the Golden Minute, MR. SOPA steps, 3:1 compression ratios, dosage calculations, and targeted pre-ductal SpO2 standards.",
        passingScore = 80,
        timeLimitMins = 25,
        attemptsAllowed = 3,
        status = "active",
        createdBy = instructor2.email,
        createdDate = now
    )
    dao.insertAssessment(asm2)

    val q2_1 = QuestionEntity(
        questionId = "QST-PCHNM-201",
        assessmentId = asm2.assessmentId,
        questionType = "MCQ",
        questionText = "A full-term infant is delivered limp and apnoeic. After drying and positioning for 30 seconds, the infant remains gasping with a heart rate of 75 bpm. What is the immediate next nursing action?",
        optionA = "Begin chest compressions at a 3:1 ratio",
        optionB = "Administer 100% free-flow oxygen via mask",
        optionC = "Initiate Positive Pressure Ventilation (PPV) at 40-60 breaths/min",
        optionD = "Administer IV epinephrine via umbilical venous catheter",
        correctAnswer = "C",
        points = 25,
        explanation = "PPV is indicated if heart rate is < 100 bpm or if the infant is gasping or apnoeic after initial steps. Chest compressions are only initiated if HR remains < 60 bpm after 30 seconds of effective PPV.",
        orderIndex = 1
    )
    val q2_2 = QuestionEntity(
        questionId = "QST-PCHNM-202",
        assessmentId = asm2.assessmentId,
        questionType = "MCQ",
        questionText = "A term newborn is receiving PPV with a pre-ductal saturation probe on the right hand. What target SpO2 range should the team expect at 5 minutes of life?",
        optionA = "60% – 65%",
        optionB = "65% – 70%",
        optionC = "80% – 85%",
        optionD = "95% – 100%",
        correctAnswer = "C",
        points = 25,
        explanation = "According to NRP guidelines, the target pre-ductal SpO2 at 5 minutes of life is 80%–85%. Targeting 95%–100% too early causes hyperoxic tissue injury.",
        orderIndex = 2
    )
    val q2_3 = QuestionEntity(
        questionId = "QST-PCHNM-203",
        assessmentId = asm2.assessmentId,
        questionType = "MCQ",
        questionText = "A 1.8 kg neonate requires 100 mL/kg/day of intravenous fluid. What hourly rate should be set on the infusion pump?",
        optionA = "5.4 mL/hr",
        optionB = "7.5 mL/hr",
        optionC = "9.0 mL/hr",
        optionD = "18.0 mL/hr",
        correctAnswer = "B",
        points = 25,
        explanation = "Rate = (Weight * Desired Fluid) / 24 hours = (1.8 kg * 100 mL/kg/day) / 24 = 180 / 24 = 7.5 mL/hr.",
        orderIndex = 3
    )
    val q2_4 = QuestionEntity(
        questionId = "QST-PCHNM-204",
        assessmentId = asm2.assessmentId,
        questionType = "MCQ",
        questionText = "A newborn's heart rate remains 50 bpm after 30 seconds of effective PPV with good mask seal and chest rise. Which compression-to-ventilation ratio should the nurse use?",
        optionA = "15:2 ratio",
        optionB = "30:2 ratio",
        optionC = "3:1 ratio (90 compressions : 30 breaths/min)",
        optionD = "5:1 ratio",
        correctAnswer = "C",
        points = 25,
        explanation = "Neonatal resuscitation uses a 3:1 ratio — 90 compressions and 30 breaths per minute, giving 120 total events per minute. 15:2 and 30:2 are adult/pediatric ratios and are unsafe in newborns.",
        orderIndex = 4
    )
    dao.insertQuestion(q2_1)
    dao.insertQuestion(q2_2)
    dao.insertQuestion(q2_3)
    dao.insertQuestion(q2_4)

    // 3. Midterm Theory Examination (Module 6 / Modules 1-3)
    val asmMid = AssessmentEntity(
        assessmentId = "ASM-PCHNM-MID",
        courseId = "CRS-PCHNM-M6",
        moduleNumber = 1,
        assessmentTitle = "Midterm Theory Examination (PCHNM-3073 Modules 1–3)",
        description = "Summative proctored theory examination (10% of course grade) testing clinical competencies across Foundations, Delivery Room Resuscitation, and Clinical Pathologies.",
        passingScore = 80,
        timeLimitMins = 45,
        attemptsAllowed = 2,
        status = "active",
        createdBy = admin.email,
        createdDate = now
    )
    dao.insertAssessment(asmMid)

    val qm_1 = QuestionEntity(
        questionId = "QST-PCHNM-M01",
        assessmentId = asmMid.assessmentId,
        questionType = "MCQ",
        questionText = "In distinguishing a cephalohematoma from a caput succedaneum, which physical examination finding is pathognomonic for cephalohematoma?",
        optionA = "The swelling crosses the sagittal and lambdoid sutures",
        optionB = "The swelling is sharply limited by the cranial suture lines",
        optionC = "The overlying skin is always ecchymotic and warm",
        optionD = "The swelling resolves completely within 12 hours of delivery",
        correctAnswer = "B",
        points = 20,
        explanation = "Cephalohematoma is subperiosteal blood accumulation, so it cannot cross cranial suture lines. Caput succedaneum is subcutaneous edema that freely crosses sutures.",
        orderIndex = 1
    )
    val qm_2 = QuestionEntity(
        questionId = "QST-PCHNM-M02",
        assessmentId = asmMid.assessmentId,
        questionType = "MCQ",
        questionText = "What is the recommended initial drug, dose, and route for neonatal seizures that fail to terminate spontaneously?",
        optionA = "Phenytoin 10 mg/kg orally",
        optionB = "Diazepam 0.5 mg/kg rectally",
        optionC = "Phenobarbitone 20 mg/kg IV infused over 10–15 minutes",
        optionD = "Lorazepam 0.5 mg/kg rapid IV push",
        correctAnswer = "C",
        points = 20,
        explanation = "Phenobarbitone (Phenobarbital) 20 mg/kg IV over 10–15 minutes is the first-line anticonvulsant of choice in neonatal seizures.",
        orderIndex = 2
    )
    val qm_3 = QuestionEntity(
        questionId = "QST-PCHNM-M03",
        assessmentId = asmMid.assessmentId,
        questionType = "MCQ",
        questionText = "For an infant with severe Hypoxic-Ischemic Encephalopathy (HIE) eligible for therapeutic hypothermia, what is the target core temperature and treatment duration?",
        optionA = "30.0°C – 32.0°C for 24 hours",
        optionB = "33.0°C – 34.0°C (target 33.5°C) for 72 hours",
        optionC = "35.0°C – 36.0°C for 48 hours",
        optionD = "32.0°C – 33.0°C for 12 hours",
        correctAnswer = "B",
        points = 20,
        explanation = "Therapeutic hypothermia protocols require core cooling to 33.0°C–34.0°C (target 33.5°C) maintained for 72 hours, followed by slow rewarming at 0.5°C per hour.",
        orderIndex = 3
    )
    val qm_4 = QuestionEntity(
        questionId = "QST-PCHNM-M04",
        assessmentId = asmMid.assessmentId,
        questionType = "MCQ",
        questionText = "What total blood volume is required to perform a Double-Volume Exchange Transfusion in a 3.0 kg neonate with Rh hemolytic disease?",
        optionA = "255 mL",
        optionB = "340 mL",
        optionC = "510 mL",
        optionD = "850 mL",
        correctAnswer = "C",
        points = 20,
        explanation = "Double-volume exchange calculation: 2 * 85 mL/kg * 3.0 kg = 170 mL/kg * 3.0 kg = 510 mL.",
        orderIndex = 4
    )
    val qm_5 = QuestionEntity(
        questionId = "QST-PCHNM-M05",
        assessmentId = asmMid.assessmentId,
        questionType = "MCQ",
        questionText = "When administering epinephrine for neonatal cardiac arrest via an Umbilical Venous Catheter (UVC), what is the correct concentration and dosage?",
        optionA = "1:1,000 solution at 0.1 mg/kg",
        optionB = "1:10,000 solution at 0.02 mg/kg (0.2 mL/kg)",
        optionC = "1:10,000 solution at 0.1 mg/kg (1.0 mL/kg)",
        optionD = "1:100,000 solution at 0.005 mg/kg",
        correctAnswer = "B",
        points = 20,
        explanation = "The NRP standard IV/UVC epinephrine dose is 0.02 mg/kg of the 1:10,000 (0.1 mg/mL) dilution, equivalent to 0.2 mL/kg, followed by normal saline flush.",
        orderIndex = 5
    )
    dao.insertQuestion(qm_1)
    dao.insertQuestion(qm_2)
    dao.insertQuestion(qm_3)
    dao.insertQuestion(qm_4)
    dao.insertQuestion(qm_5)

    // 4. Master OSCE Assessment (Module 6)
    val asmOsce = AssessmentEntity(
        assessmentId = "ASM-PCHNM-OSCE",
        courseId = "CRS-PCHNM-M6",
        moduleNumber = 2,
        assessmentTitle = "Objective Structured Clinical Examination (OSCE — 10-Minute Master Station)",
        description = "High-fidelity simulated neonatal resuscitation station. 10 assessment domains scored 0–2 points each (Total 20 points). Pass mark: 80% (16 points). Accompanying accredited certificate awarded upon successful completion.",
        passingScore = 80,
        timeLimitMins = 10,
        attemptsAllowed = 3,
        status = "active",
        createdBy = admin.email,
        createdDate = now
    )
    dao.insertAssessment(asmOsce)

    val qo_1 = QuestionEntity(
        questionId = "QST-OSCE-01",
        assessmentId = asmOsce.assessmentId,
        questionType = "MCQ",
        questionText = "OSCE Domain 1: In delivery preparation and infection control, what steps earn full Excellent (2 pts) marks?",
        optionA = "Washing hands with water alone and turning on the radiant warmer after delivery",
        optionB = "Performing standard hand hygiene, donning PPE, pre-warming radiant warmer, and systematically testing suction and PPV bag/mask before delivery",
        optionC = "Wearing gloves only without checking suction pressure or blender concentration",
        optionD = "Delegating all equipment verification to the nurse after the infant is born",
        correctAnswer = "B",
        points = 20,
        explanation = "Domain 1 requires prior hand hygiene, complete PPE, radiant warmer preheating, suction check (80-100 mmHg), and bag-mask verification prior to birth.",
        orderIndex = 1
    )
    val qo_2 = QuestionEntity(
        questionId = "QST-OSCE-02",
        assessmentId = asmOsce.assessmentId,
        questionType = "MCQ",
        questionText = "OSCE Domain 4 & 5: When PPV fails to achieve chest rise and heart rate remains 55 bpm, what is the correct sequential action?",
        optionA = "Immediately start chest compressions before checking the mask or airway",
        optionB = "Systematically perform MR. SOPA (Mask adjustment, Reposition head, Suction mouth/nose, Open mouth, Pressure increase, Alternative airway)",
        optionC = "Administer high-dose atropine via endotracheal tube",
        optionD = "Switch off the oxygen blender and increase room temperature",
        correctAnswer = "B",
        points = 20,
        explanation = "When PPV is ineffective, MR. SOPA corrective steps must be methodically applied before advancing to chest compressions.",
        orderIndex = 2
    )
    val qo_3 = QuestionEntity(
        questionId = "QST-OSCE-03",
        assessmentId = asmOsce.assessmentId,
        questionType = "MCQ",
        questionText = "OSCE Domain 6: How should chest compressions be coordinated with ventilations in neonatal resuscitation?",
        optionA = "15 compressions to 2 breaths asynchronous",
        optionB = "3 compressions to 1 ventilation (90 compressions and 30 breaths per minute) synchronized with 100% oxygen",
        optionC = "Continuous rapid compressions at 120/min with asynchronous bagging",
        optionD = "5 compressions followed by a 5-second pause for breathing",
        correctAnswer = "B",
        points = 20,
        explanation = "NRP mandates the two-thumb technique at a 3:1 ratio (90 compressions and 30 breaths = 120 events/min) using 100% oxygen.",
        orderIndex = 3
    )
    val qo_4 = QuestionEntity(
        questionId = "QST-OSCE-04",
        assessmentId = asmOsce.assessmentId,
        questionType = "MCQ",
        questionText = "OSCE Domain 9: What constitutes Excellent (2 pts) communication during post-resuscitation family counselling?",
        optionA = "Informing parents of infant death risk using complex medical jargon and dismissing their questions",
        optionB = "Providing clear, honest, compassionate, and culturally sensitive updates to parents, using understandable language and active listening",
        optionC = "Instructing parents that they cannot see the infant until tomorrow without explanation",
        optionD = "Minimizing the clinical severity by falsely claiming the baby was completely healthy",
        correctAnswer = "B",
        points = 20,
        explanation = "Domain 9 evaluates warmth, empathy, cultural sensitivity, clear understandable language, and supportive presence.",
        orderIndex = 4
    )
    val qo_5 = QuestionEntity(
        questionId = "QST-OSCE-05",
        assessmentId = asmOsce.assessmentId,
        questionType = "MCQ",
        questionText = "OSCE Domain 10: In the Pendleton Model for post-resuscitation clinical debriefing, what is Step 1?",
        optionA = "The instructor immediately criticizes the student's mistakes",
        optionB = "The learner states what they did well during the resuscitation",
        optionC = "The team leader assigns disciplinary action for timing delays",
        optionD = "The nurse writes an incident report before discussing the case",
        correctAnswer = "B",
        points = 20,
        explanation = "Step 1 of the Pendleton debriefing model empowers the learner to articulate what went well, reinforcing positive actions before coaching improvements.",
        orderIndex = 5
    )
    dao.insertQuestion(qo_1)
    dao.insertQuestion(qo_2)
    dao.insertQuestion(qo_3)
    dao.insertQuestion(qo_4)
    dao.insertQuestion(qo_5)

    // 5. Final Comprehensive Examination (Module 6)
    val asmFinal = AssessmentEntity(
        assessmentId = "ASM-PCHNM-FINAL",
        courseId = "CRS-PCHNM-M6",
        moduleNumber = 3,
        assessmentTitle = "Final Comprehensive Theory Examination (PCHNM-3073 All Modules)",
        description = "Exit summative examination (40% of course grade) testing clinical synthesis across all 9 Course Learning Outcomes (CO1 to CO9).",
        passingScore = 80,
        timeLimitMins = 60,
        attemptsAllowed = 2,
        status = "active",
        createdBy = admin.email,
        createdDate = now
    )
    dao.insertAssessment(asmFinal)

    val qf_1 = QuestionEntity(
        questionId = "QST-FINAL-01",
        assessmentId = asmFinal.assessmentId,
        questionType = "MCQ",
        questionText = "Which clinical sign is a critical contraindication to Bag-Valve-Mask positive pressure ventilation in a neonate with respiratory distress?",
        optionA = "Heart rate of 80 bpm with central cyanosis",
        optionB = "Scaphoid abdomen with suspected Congenital Diaphragmatic Hernia (CDH)",
        optionC = "Acrocyanosis with strong crying",
        optionD = "Birth weight less than 1,500 grams",
        correctAnswer = "B",
        points = 25,
        explanation = "In Congenital Diaphragmatic Hernia (CDH), mask ventilation forces air into the herniated stomach and intestines in the chest, worsening lung hypoplasia and causing fatal pneumothorax. Immediate endotracheal intubation is required.",
        orderIndex = 1
    )
    val qf_2 = QuestionEntity(
        questionId = "QST-FINAL-02",
        assessmentId = asmFinal.assessmentId,
        questionType = "MCQ",
        questionText = "What is the recommended empirical first-line antibiotic combination for suspected neonatal early-onset sepsis (EOS)?",
        optionA = "Ceftriaxone and Metronidazole",
        optionB = "Ampicillin and Gentamicin",
        optionC = "Vancomycin and Meropenem",
        optionD = "Ciprofloxacin and Erythromycin",
        correctAnswer = "B",
        points = 25,
        explanation = "Ampicillin (for Gram-positive organisms and Listeria) plus Gentamicin (for Gram-negative enteric bacilli) is the global and national first-line regimen for neonatal EOS.",
        orderIndex = 2
    )
    val qf_3 = QuestionEntity(
        questionId = "QST-FINAL-03",
        assessmentId = asmFinal.assessmentId,
        questionType = "MCQ",
        questionText = "At what gestational age and weight should Kangaroo Mother Care (KMC) generally be discontinued as the infant transitions out of kangaroo wraps?",
        optionA = "At 34 weeks gestation or weight of 1,800 grams",
        optionB = "At 40 weeks post-menstrual age or weight ≥ 2,500 grams (when the infant wriggles uncomfortably)",
        optionC = "Immediately when the infant reaches 14 days of life",
        optionD = "Only when the mother returns to work",
        correctAnswer = "B",
        points = 25,
        explanation = "KMC is typically discontinued when the infant reaches term equivalent (40 weeks) or ≥ 2,500g and begins fussing or wriggling out of the binder.",
        orderIndex = 3
    )
    val qf_4 = QuestionEntity(
        questionId = "QST-FINAL-04",
        assessmentId = asmFinal.assessmentId,
        questionType = "MCQ",
        questionText = "What is the correct dose, needle route, and anatomical site for administering BCG immunization at birth?",
        optionA = "0.5 mL subcutaneous in the abdomen",
        optionB = "0.05 mL intradermal in the right upper deltoid using a 26G needle to form a wheal",
        optionC = "0.1 mL intramuscular in the anterolateral thigh",
        optionD = "1.0 mL orally on the tongue",
        correctAnswer = "B",
        points = 25,
        explanation = "BCG is given as a 0.05 mL intradermal injection into the right upper deltoid with a fine 26G needle bevel up, producing a characteristic 5–6 mm wheal.",
        orderIndex = 4
    )
    dao.insertQuestion(qf_1)
    dao.insertQuestion(qf_2)
    dao.insertQuestion(qf_3)
    dao.insertQuestion(qf_4)
}
