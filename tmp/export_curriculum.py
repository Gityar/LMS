import re
import json

with open('app/src/main/java/com/example/data/db/CurriculumSeedLessonsM1M2.kt') as f:
    text1 = f.read()
with open('app/src/main/java/com/example/data/db/CurriculumSeedLessonsM3M4.kt') as f:
    text2 = f.read()
with open('app/src/main/java/com/example/data/db/CurriculumSeedLessonsM5M6.kt') as f:
    text3 = f.read()

combined_lessons = text1 + '\n' + text2 + '\n' + text3

lessons = []
pattern = re.compile(
    r'LessonEntity\(\s*lessonId\s*=\s*"([^"]+)",\s*courseId\s*=\s*"([^"]+)",\s*moduleNumber\s*=\s*(\d+),\s*lessonTitle\s*=\s*"([^"]+)",\s*contentType\s*=\s*"([^"]+)",\s*durationMins\s*=\s*(\d+),\s*orderIndex\s*=\s*(\d+),\s*contentBody\s*=\s*"""(.*?)"""\.trimIndent\(\),\s*videoUrl\s*=\s*"([^"]*)",\s*resourceUrl\s*=\s*"([^"]*)"',
    re.DOTALL
)

for m in pattern.finditer(combined_lessons):
    body = m.group(8).strip()
    # clean up Kotlin string interpolation escapes like \${'$'}
    body = body.replace(r"${'$'}{'$'}", "$$").replace(r"${'$'}", "$")
    lessons.append({
        'id': m.group(1),
        'courseId': m.group(2),
        'moduleNumber': int(m.group(3)),
        'title': m.group(4),
        'type': 'clinical' if m.group(5) == 'Text' else m.group(5).lower(),
        'duration': int(m.group(6)),
        'mediaUrl': m.group(9),
        'resourceUrl': m.group(10),
        'body': body
    })

with open('app/src/main/java/com/example/data/db/CurriculumSeedAssessments.kt') as f:
    text_asm = f.read()

asm_pattern = re.compile(
    r'AssessmentEntity\(\s*assessmentId\s*=\s*"([^"]+)",\s*courseId\s*=\s*"([^"]+)",\s*moduleNumber\s*=\s*(\d+),\s*assessmentTitle\s*=\s*"([^"]+)",\s*description\s*=\s*"([^"]+)",\s*passingScore\s*=\s*(\d+),\s*timeLimitMins\s*=\s*(\d+)',
    re.DOTALL
)
assessments = []
for m in asm_pattern.finditer(text_asm):
    assessments.append({
        'id': m.group(1),
        'courseId': m.group(2),
        'title': m.group(4),
        'instructions': m.group(5),
        'passingScore': int(m.group(6)),
        'timeLimit': int(m.group(7)),
        'type': 'certification' if 'OSCE' in m.group(1) or 'FINAL' in m.group(1) else 'module_quiz'
    })

asm_map = {
    'asm1.assessmentId': 'ASM-PCHNM-QZ1',
    'asm2.assessmentId': 'ASM-PCHNM-NRP',
    'asmMid.assessmentId': 'ASM-PCHNM-MID',
    'asmOsce.assessmentId': 'ASM-PCHNM-OSCE',
    'asmFinal.assessmentId': 'ASM-PCHNM-FINAL',
}
qst_pattern = re.compile(
    r'QuestionEntity\(\s*questionId\s*=\s*"([^"]+)",\s*assessmentId\s*=\s*([a-zA-Z0-9_\.]+),\s*questionType\s*=\s*"MCQ",\s*questionText\s*=\s*"([^"]+)",\s*optionA\s*=\s*"([^"]+)",\s*optionB\s*=\s*"([^"]+)",\s*optionC\s*=\s*"([^"]+)",\s*optionD\s*=\s*"([^"]+)",\s*correctAnswer\s*=\s*"([ABCD])",\s*points\s*=\s*(\d+),\s*explanation\s*=\s*"([^"]+)"',
    re.DOTALL
)

questions = []
ans_map = {'A': 0, 'B': 1, 'C': 2, 'D': 3}
for m in qst_pattern.finditer(text_asm):
    questions.append({
        'id': m.group(1),
        'assessmentId': asm_map.get(m.group(2), m.group(2).replace('"', '')),
        'q': m.group(3),
        'type': 'single',
        'options': [m.group(4), m.group(5), m.group(6), m.group(7)],
        'correct': ans_map.get(m.group(8), 0),
        'points': int(m.group(9)),
        'difficulty': 'Clinical Standard',
        'explanation': m.group(10)
    })

courses = [
    {
        'id': 'CRS-PCHNM-M1',
        'title': 'Module 1: Foundations of Neonatology, Physiology & Classification',
        'category': 'Foundational Theory',
        'instructor': 'Sister Almaz',
        'desc': 'Wollo University PCHNM-3073. Focuses on neonatal mortality epidemiology (global, regional, national), transition from fetal to extrauterine life, cardiorespiratory & metabolic adaptation, brown adipose tissue thermogenesis, neonatal reflexes, and gestational age/birth weight classification.',
        'modules': 3,
        'status': 'published',
        'difficulty': 'Foundational',
        'cmeCredits': '4.0 CPD Hours',
        'thumbnail': 'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?w=600&auto=format&fit=crop&q=80'
    },
    {
        'id': 'CRS-PCHNM-M2',
        'title': 'Module 2: Immediate Care, Assessment & Neonatal Resuscitation (NRP)',
        'category': 'Emergency & Resuscitation',
        'instructor': 'Dr. Dawit',
        'desc': 'Delivery room triage, the 11-step Essential Newborn Care (ENC), APGAR scoring system, The Golden Minute, NRP clinical algorithm, Positive Pressure Ventilation (PPV), MR. SOPA ventilation corrective sequence, 3:1 chest compressions, IV epinephrine via UVC, and targeted pre-ductal SpO2 standards.',
        'modules': 6,
        'status': 'published',
        'difficulty': 'Intermediate',
        'cmeCredits': '5.0 CPD Hours',
        'thumbnail': 'https://images.unsplash.com/photo-1516549655169-df83a0774514?w=600&auto=format&fit=crop&q=80'
    },
    {
        'id': 'CRS-PCHNM-M3',
        'title': 'Module 3: Birth Injuries, Respiratory, Haematological & Metabolic Disorders',
        'category': 'Clinical Pathology',
        'instructor': 'Sister Almaz',
        'desc': 'Covers soft tissue, skeletal and neurological birth trauma (caput succedaneum vs cephalohematoma, subgaleal haemorrhage, Erb\'s & Klumpke\'s palsies), RDS, TTN, MAS, ABG analysis, neonatal jaundice, phototherapy, double volume exchange transfusion, seizures, HIE Sarnat staging, therapeutic hypothermia, and hypoglycemia.',
        'modules': 6,
        'status': 'published',
        'difficulty': 'Advanced',
        'cmeCredits': '6.0 CPD Hours',
        'thumbnail': 'https://images.unsplash.com/photo-1579684385127-1ef15d508118?w=600&auto=format&fit=crop&q=80'
    },
    {
        'id': 'CRS-PCHNM-M4',
        'title': 'Module 4: Infections, Congenital Anomalies & High-Risk Nutrition',
        'category': 'High-Risk Care',
        'instructor': 'Dr. Dawit',
        'desc': 'Early- and late-onset neonatal sepsis, meningitis, tetanus, nosocomial infection control, Necrotizing Enterocolitis (NEC), Congenital Anomalies (CHD, NTD, Down syndrome, TEF, CDH, omphalocele, gastroschisis), prematurity management, fluid tables, Total Parenteral Nutrition (TPN), Kangaroo Mother Care (KMC), and EPI immunization.',
        'modules': 6,
        'status': 'published',
        'difficulty': 'Advanced Specialist',
        'cmeCredits': '6.0 CPD Hours',
        'thumbnail': 'https://images.unsplash.com/photo-1581594693702-fbdc51b2763b?w=600&auto=format&fit=crop&q=80'
    },
    {
        'id': 'CRS-PCHNM-M5',
        'title': 'Module 5: Guided Clinical Practice & Practicum (160 Hours)',
        'category': 'Clinical Practicum',
        'instructor': 'Sister Almaz',
        'desc': 'Hands-on guided clinical rotation representing 70% of course weight: 3 weeks in the NICU (120 hrs) covering oxygen therapy, bubble CPAP titration, intubation assist, IV infusions, exchange transfusion, and verified clinical logbooks; 1 week in Maternal/Postnatal Ward (40 hrs) covering essential newborn care, APGAR, and breastfeeding counselling.',
        'modules': 3,
        'status': 'published',
        'difficulty': 'Clinical Residency',
        'cmeCredits': '8.0 CPD Hours',
        'thumbnail': 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=600&auto=format&fit=crop&q=80'
    },
    {
        'id': 'CRS-PCHNM-M6',
        'title': 'Module 6: Final Assessments, OSCE Evaluation & Continuous Improvement',
        'category': 'Summative & Evaluation',
        'instructor': 'Yaregal Semanew',
        'desc': 'Summative competency validation: Midterm written exam (10%), Master Objective Structured Clinical Examination (OSCE, 20-point rubric across 10 domains, pass mark 16/20 = 80%), Final Comprehensive Written Examination (40%), Portfolio Defence, Three-tier Feedback Strategy with the Pendleton Model, and WCAG 2.1 AA / UDL Course Audit.',
        'modules': 5,
        'status': 'published',
        'difficulty': 'Mastery Certification',
        'cmeCredits': '6.0 CPD Hours',
        'thumbnail': 'https://images.unsplash.com/photo-1532938911079-1b06ac7ceec7?w=600&auto=format&fit=crop&q=80'
    }
]

enrollments = [
    { 'id': 'ENR-PCHNM-01', 'courseId': 'CRS-PCHNM-M1', 'userId': 'USR-NURSE-01', 'progress': 100, 'enrolledDate': '2026-10-01' },
    { 'id': 'ENR-PCHNM-02', 'courseId': 'CRS-PCHNM-M2', 'userId': 'USR-NURSE-01', 'progress': 65, 'enrolledDate': '2026-10-01' },
    { 'id': 'ENR-PCHNM-03', 'courseId': 'CRS-PCHNM-M2', 'userId': 'USR-NURSE-02', 'progress': 40, 'enrolledDate': '2026-10-08' }
]

attempts = [
    { 'id': 'ATT-PCHNM-01', 'userId': 'USR-NURSE-01', 'learnerName': 'Nurse Tigist Mengistu', 'courseId': 'CRS-PCHNM-M6', 'courseTitle': 'Objective Structured Clinical Examination (OSCE — 10-Minute Master Station)', 'score': 18, 'total': 20, 'percentage': 90, 'passed': True, 'date': '2026-10-15' }
]

output_data = {
    'courses': courses,
    'lessons': lessons,
    'assessments': assessments,
    'questions': questions,
    'enrollments': enrollments,
    'attempts': attempts
}

with open('/tmp/curriculum_export.json', 'w') as out:
    json.dump(output_data, out, indent=2)

print(f"Exported successfully! courses={len(courses)}, lessons={len(lessons)}, assessments={len(assessments)}, questions={len(questions)}")
