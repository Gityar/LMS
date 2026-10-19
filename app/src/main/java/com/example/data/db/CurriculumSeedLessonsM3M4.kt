package com.example.data.db

import com.example.data.dao.LmsDao
import com.example.data.model.LessonEntity

suspend fun seedCurriculumModule3Lessons(dao: LmsDao) {
    val l301 = LessonEntity(
        lessonId = "LES-PCHNM-301",
        courseId = "CRS-PCHNM-M3",
        moduleNumber = 3,
        lessonTitle = "Unit 3.1.1: Soft Tissue, Skeletal & Neurological Birth Trauma",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 1,
        contentBody = """
# Unit 3.1.1: Soft Tissue, Skeletal & Neurological Birth Trauma

## 1. Differential Diagnosis of Extracranial Swelling:
1. **Caput Succedaneum:**
   - Edema of subcutaneous tissue above the periosteum.
   - **Crosses suture lines**; poorly defined margins; present at birth.
   - Resolves spontaneously in 2 to 3 days without therapy.
2. **Cephalohematoma:**
   - Subperiosteal hemorrhage confined to individual cranial bones.
   - **Does NOT cross suture lines**; bounded by periosteal attachments; expands over hours/days.
   - Complications: Hyperbilirubinemia, anemia.
   - **Management:** Observation. **Incision and needle aspiration are strictly contraindicated** due to high risk of introducing fatal infection.
3. **Subgaleal Hemorrhage (True Emergency):**
   - Rupture of emissary veins leading to blood collection under the epicranial aponeurosis.
   - **Crosses suture lines and fontanelles**, extending from orbital ridges to the nape of the neck.
   - Can sequester up to 50% of the newborn's blood volume, causing rapid hypovolemic shock, pallor, tachycardia, falling hematocrit, and coagulopathy.
   - **Management:** Immediate ICU admission, continuous head circumference measurement, urgent packed red blood cell / whole blood transfusion.

## 2. Brachial Plexus Injuries
- **Erb-Duchenne Palsy (C5–C6):** Most common. Upper trunk lesion resulting in "waiter's tip" posture (arm adducted, internally rotated, elbow extended, forearm pronated, wrist flexed). Moro reflex absent on affected side; grasp reflex intact.
- **Klumpke's Palsy (C7–T1):** Lower trunk lesion causing claw hand deformity, absent palmar grasp, and potential Horner's syndrome (ptosis, miosis, anhidrosis).
- **Management:** Immobilization for 7–10 days post-injury, followed by gentle passive range-of-motion physical therapy.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.who.int"
    )

    val l302 = LessonEntity(
        lessonId = "LES-PCHNM-302",
        courseId = "CRS-PCHNM-M3",
        moduleNumber = 3,
        lessonTitle = "Unit 3.1.2: Problem-Based Learning (PBL 1) — Severe Birth Trauma Case Session",
        contentType = "Text",
        durationMins = 35,
        orderIndex = 2,
        contentBody = """
# Unit 3.1.2: Problem-Based Learning (PBL 1) — Severe Birth Trauma Case Session

## Case Presentation: Baby boy of Mulu
A 4.2 kg male infant is born to a 28-year-old primigravida after prolonged second-stage labor and difficult vacuum extraction for fetal distress.

### Clinical Findings at 4 Hours of Life:
- Neurological: Lethargic, weak high-pitched cry, right arm flaccid and internally rotated.
- Head Exam: Progressive fluctuant, ballotable swelling extending across sagittal and coronal sutures down to the supraorbital ridges. Head circumference increased by 2.5 cm over 3 hours.
- Vital Signs: HR 185 bpm, RR 68 bpm with mild subcostal retractions, BP 52/28 mmHg (MAP 36 mmHg), Temperature 35.8°C.
- Laboratory: Hematocrit 28% (down from cord blood 48%), Blood glucose 42 mg/dL.

### Problem-Solving Framework:
1. **Clinical Diagnosis:** Massive Subgaleal Hemorrhage complicated by Hypovolemic Shock, secondary Right Erb-Duchenne palsy, and hypothermia.
2. **Immediate Resuscitation Priorities:**
   - Airway & Breathing: Supplemental oxygen to maintain pre-ductal SpO2 90%–95%.
   - Vascular Access: Emergency Umbilical Venous Catheter (UVC) placement.
   - Volume Resuscitation: Immediate 0.9% Normal Saline bolus 10–20 mL/kg over 10–20 minutes, followed by urgent cross-matched O-negative Packed Red Blood Cells (10–15 mL/kg).
   - Thermal & Metabolic: Radiant warmer servo-controlled to 36.5°C; 10% Dextrose IV infusion at GIR 6 mg/kg/min.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l303 = LessonEntity(
        lessonId = "LES-PCHNM-303",
        courseId = "CRS-PCHNM-M3",
        moduleNumber = 3,
        lessonTitle = "Unit 3.2.1: Respiratory Disorders: RDS, TTN, MAS & ABG Management",
        contentType = "Text",
        durationMins = 35,
        orderIndex = 3,
        contentBody = """
# Unit 3.2.1: Respiratory Disorders: RDS, TTN, MAS & ABG Management

Respiratory distress affects up to 7% of term and 80% of extremely preterm neonates.

## 1. Respiratory Distress Syndrome (RDS / Hyaline Membrane Disease)
- **Pathophysiology:** Primary deficiency of pulmonary surfactant produced by Type II pneumocytes. Causes high alveolar surface tension, diffuse micro-atelectasis, ventilation-perfusion mismatch, and hypoxemia.
- **Chest X-ray:** Diffuse, bilateral reticulogranular ("ground-glass") pattern with prominent air bronchograms and hypo-expansion.
- **Management:** Early bubble CPAP (5–7 cm H2O), exogenous surfactant replacement (poractant alfa 200 mg/kg via LISA/InSurE technique), thermoregulation.

## 2. Transient Tachypnea of the Newborn (TTN / "Wet Lung")
- **Pathophysiology:** Delayed resorption and clearance of fetal alveolar lung fluid. Common after elective cesarean section without labor.
- **Chest X-ray:** Hyperinflation, prominent perihilar vascular markings, and fluid in the interlobar fissures.
- **Management:** Supportive humidified oxygen; resolves spontaneously within 24 to 72 hours.

## 3. Meconium Aspiration Syndrome (MAS)
- **Pathophysiology:** Intrauterine hypoxia triggers anal sphincter relaxation and gasping in utero. Aspirated meconium causes airway obstruction (ball-valve effect -> air leak / pneumothorax), chemical pneumonitis, surfactant inactivation, and secondary bacterial pneumonia.
- **Chest X-ray:** Patchy, asymmetric coarse infiltrates, hyperinflation, and flattening of diaphragms.

## 4. Arterial Blood Gas (ABG) Reference Values in Neonates
- **pH:** 7.35 – 7.45
- **PaO2:** 50 – 70 mmHg
- **PaCO2:** 35 – 45 mmHg
- **HCO3-:** 20 – 26 mEq/L
- **Base Excess (BE):** -4 to +4 mEq/L
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.aap.org"
    )

    val l304 = LessonEntity(
        lessonId = "LES-PCHNM-304",
        courseId = "CRS-PCHNM-M3",
        moduleNumber = 3,
        lessonTitle = "Unit 3.3.1: Neonatal Jaundice, Hyperbilirubinemia & Exchange Transfusion",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 4,
        contentBody = """
# Unit 3.3.1: Neonatal Jaundice, Hyperbilirubinemia & Exchange Transfusion

Jaundice is visible when Total Serum Bilirubin (TSB) exceeds 5–7 mg/dL (85–120 µmol/L) in a cephalocaudal progression (Kramer's zones).

## 1. Physiological vs. Pathological Jaundice
| Characteristic | Physiological Jaundice | Pathological Jaundice |
| :--- | :--- | :--- |
| **Onset** | After 24 hours of life | **Within first 24 hours of life** |
| **TSB Rise Rate** | < 5 mg/dL/day (< 85 µmol/L/d) | **> 5 mg/dL/day (> 85 µmol/L/d)** |
| **Peak TSB (Term)** | < 12 mg/dL | **> 12 mg/dL** |
| **Peak TSB (Preterm)**| < 15 mg/dL | **> 15 mg/dL** |
| **Duration (Term)** | Resolves by Day 7–10 | Persists ≥ 14 days |
| **Conjugated Bilirubin**| < 2 mg/dL | **> 2 mg/dL or > 15% of total** |

## 2. Phototherapy Clinical Protocol
- **Mechanism:** Bilirubin absorbs blue light at **420 – 470 nm**, undergoing photo-isomerization to water-soluble **lumirubin**, excreted in bile and urine without hepatic conjugation.
- **Nursing Care:** Cover eyes with opaque shields; keep infant naked except minimal diaper; turn infant every 2–3 hours; increase fluid intake by **20–30 mL/kg/day** to compensate for insensible fluid loss; monitor irradiance (standard: 8–10 µW/cm2/nm; intensive: ≥ 30 µW/cm2/nm).

## 3. Double-Volume Exchange Transfusion (DVET)
- **Calculation:** Volume = **2 × Circulating Blood Volume** (${'$'}{'$'}2 \times 85\text{ mL/kg} = \mathbf{170\text{ mL/kg}}${'$'}{'$'}).
- **Selection of Blood:** Fresh whole blood (< 5 days old), irradiated and cross-matched:
  - Rh incompatibility: ABO compatible with infant, Rh-negative (same as mother).
  - ABO incompatibility: O group, Rh compatible with infant.
- **Procedure:** Push-pull via UVC using 5–20 mL aliquots over 90–120 minutes. Administer 1 mL 10% Calcium Gluconate for every 100 mL blood exchanged.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.who.int"
    )

    val l305 = LessonEntity(
        lessonId = "LES-PCHNM-305",
        courseId = "CRS-PCHNM-M3",
        moduleNumber = 3,
        lessonTitle = "Unit 3.3.2: Neurological Crises, Seizures & Metabolic Emergencies",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 5,
        contentBody = """
# Unit 3.3.2: Neurological Crises, Seizures & Metabolic Emergencies

## 1. Neonatal Seizure Classification
1. **Subtle Seizures (50% of all cases):** Eye deviation, repetitive blinking, oral-buccal smacking, tongue protrusion, bicycling/pedaling movements, paroxysmal apnea with bradycardia.
2. **Clonic Seizures:** Rhythmic jerking (1–3 Hz), focal or multifocal. Cannot be suppressed by gentle limb restraint (differentiates from benign neonatal jitteriness).
3. **Tonic Seizures:** Sustained rigid extension or posturing of limbs or trunk.
4. **Myoclonic Seizures:** Rapid, lightening-fast single or multiple flexor jerks.

## 2. Anticonvulsant Management Protocol
- **First-Line Drug:** **Phenobarbitone (Phenobarbital)**.
  - Loading dose: **20 mg/kg IV** infused over 10–15 minutes.
  - If seizures persist after 15 minutes: give additional 5–10 mg/kg boluses up to maximum **40 mg/kg**.
  - Maintenance dose: **3.5 – 5 mg/kg/day** in 2 divided doses starting 12–24 hours after loading.
- **Second-Line Drug:** **Phenytoin** (20 mg/kg loading in normal saline with ECG monitoring) or **Levetiracetam** (40–60 mg/kg).

## 3. Metabolic Emergencies: Hypoglycemia & Hypocalcemia
- **Symptomatic Hypoglycemia (< 40 mg/dL):**
  - Immediate IV bolus: **2 mL/kg of 10% Dextrose in Water (DW)** over 1 minute.
  - Follow with maintenance IV glucose infusion at **GIR 6 – 8 mg/kg/min** (approx 90 mL/kg/day of 10% DW).
- **Symptomatic Hypocalcemia (Total serum Ca < 7.0 mg/dL or Ionized Ca < 4.0 mg/dL):**
  - **10% Calcium Gluconate:** 2 mL/kg diluted 1:1 with sterile water IV over 10 minutes under continuous cardiac monitoring (risk of bradycardia / arrhythmia).
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l306 = LessonEntity(
        lessonId = "LES-PCHNM-306",
        courseId = "CRS-PCHNM-M3",
        moduleNumber = 3,
        lessonTitle = "Unit 3.3.3: Problem-Based Learning (PBL 2) — HIE & Therapeutic Hypothermia Protocols",
        contentType = "Text",
        durationMins = 35,
        orderIndex = 6,
        contentBody = """
# Unit 3.3.3: Problem-Based Learning (PBL 2) — HIE & Therapeutic Hypothermia Protocols

## 1. Hypoxic-Ischemic Encephalopathy (HIE) Sarnat Staging
- **Stage 1 (Mild):** Hyper-alert, irritable, tachycardia, dilated pupils, exaggerated Moro, normal EEG. Good prognosis (>98% normal outcome).
- **Stage 2 (Moderate):** Lethargic, hypotonic, miosis, seizures, weak/absent Moro, suppressed EEG. 20%–35% mortality or neurodevelopmental disability.
- **Stage 3 (Severe):** Stupor/coma, flaccid, absent reflexes, non-reactive pupils, status epilepticus, flat/burst-suppression EEG. High mortality; severe disability.

## 2. Therapeutic Hypothermia Protocol
- **Eligibility Criteria:**
  1. Gestational age ≥ 36 completed weeks and birth weight ≥ 1,800 grams.
  2. Age < 6 hours of life at initiation.
  3. Evidence of perinatal asphyxia: Cord/arterial pH < 7.00 or base deficit ≥ 16 mEq/L, or 10-minute APGAR ≤ 5, or ongoing resuscitation at 10 minutes.
  4. Moderate to severe encephalopathy (Sarnat Stage 2 or 3).
- **Target Temperature:** Core rectal/esophageal temperature **33.5°C (range 33.0°C – 34.0°C)**.
- **Duration:** Maintained continuously for **72 hours**.
- **Rewarming Phase:** Slow, controlled rewarming at **0.5°C per hour** until normothermia (36.5°C) is restored. (Rapid rewarming triggers hypotension and seizures).
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.aap.org"
    )

    dao.insertLesson(l301)
    dao.insertLesson(l302)
    dao.insertLesson(l303)
    dao.insertLesson(l304)
    dao.insertLesson(l305)
    dao.insertLesson(l306)
}

suspend fun seedCurriculumModule4Lessons(dao: LmsDao) {
    val l401 = LessonEntity(
        lessonId = "LES-PCHNM-401",
        courseId = "CRS-PCHNM-M4",
        moduleNumber = 4,
        lessonTitle = "Unit 4.1.1: Neonatal Sepsis, Meningitis & Nosocomial Infection Prevention",
        contentType = "Text",
        durationMins = 35,
        orderIndex = 1,
        contentBody = """
# Unit 4.1.1: Neonatal Sepsis, Meningitis & Nosocomial Infection Prevention

Neonatal sepsis is a clinical syndrome of systemic bacteremia in the first 28 days of life and remains a leading cause of preventable neonatal death in developing countries.

## 1. Classification of Neonatal Sepsis
1. **Early-Onset Sepsis (EOS):**
   - Onset within the first **72 hours of life**.
   - Transmission: Vertical transmission from maternal genital tract during labor and delivery.
   - Pathogens: Klebsiella pneumoniae, Escherichia coli, Group B Streptococcus (GBS), Listeria monocytogenes.
   - Risk factors: Prolonged rupture of membranes (PROM > 18 hrs), maternal chorioamnionitis, intrapartum maternal fever (> 38°C).
2. **Late-Onset Sepsis (LOS):**
   - Onset between **72 hours and 28 days of life**.
   - Transmission: Hospital-acquired (nosocomial) via health worker hands, central lines, catheters, or community contact.
   - Pathogens: Staphylococcus aureus, Coagulase-Negative Staphylococci (CoNS), Klebsiella, Pseudomonas, Acinetobacter.

## 2. Empirical Antibiotic Regimens
- **First-Line for Neonatal Sepsis:**
  - **Ampicillin:** 100 mg/kg/day divided IV every 12 hours.
  - **Gentamicin:** 4 – 5 mg/kg IV once every 24 hours (monitor renal function).
- **Neonatal Meningitis (Confirmed or High Suspicion):**
  - **High-Dose Ampicillin:** 200 mg/kg/day divided q6–8h PLUS **Cefotaxime:** 100–150 mg/kg/day divided q8–12h.
  - Duration: Minimum 21 days for Gram-negative meningitis; 14 days for Listeria or GBS.

## 3. Infection Prevention & Hand Hygiene Standards
- **WHO 5 Moments of Hand Hygiene:** Hand washing with soap and clean running water (40–60s) or alcohol-based hand rub (20–30s).
- Evidence demonstrates that institutional hand washing compliance **reduces neonatal mortality by 44%**.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.who.int"
    )

    val l402 = LessonEntity(
        lessonId = "LES-PCHNM-402",
        courseId = "CRS-PCHNM-M4",
        moduleNumber = 4,
        lessonTitle = "Unit 4.1.2: Necrotizing Enterocolitis (NEC) — Pathophysiology & Management",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 2,
        contentBody = """
# Unit 4.1.2: Necrotizing Enterocolitis (NEC) — Pathophysiology & Management

Necrotizing Enterocolitis is an acquired gastrointestinal necrosis predominantly affecting premature, formula-fed infants in the NICU.

## 1. Pathophysiology Triad
- Premature immature intestinal mucosal barrier.
- Enteral formula feeding (bypassing protective maternal breast milk oligosaccharides and secretory IgA).
- Bacterial colonisation with virulent flora causing mucosal injury, bacterial translocation, intramural gas formation (**pneumatosis intestinalis**), and full-thickness perforation.

## 2. Modified Bell's Staging Criteria
- **Stage I (Suspected NEC):** Feeding intolerance, gastric residuals, mild abdominal distension, heme-positive stools. Normal or ileus on abdominal radiograph.
- **Stage II (Definite NEC):** Prominent abdominal distension, absent bowel sounds, marked tenderness, grossly bloody stools. Abdominal X-ray demonstrates **Pneumatosis Intestinalis** (gas in the bowel wall) and portal venous gas.
- **Stage III (Advanced NEC):** Septic shock, severe acidosis, coagulopathy (DIC), peritonitis. Abdominal X-ray demonstrates **Pneumoperitoneum** (free air under diaphragm indicating bowel perforation).

## 3. Nursing & Medical Management
- **Immediate NPO (Nil Per Os):** Decompress stomach with a continuous double-lumen Replogle tube to low intermittent suction.
- **Parenteral Nutrition & Fluids:** Maintain hydration with TPN.
- **Broad-Spectrum IV Antibiotics:** Ampicillin, Gentamicin, and Metronidazole (for anaerobic coverage).
- **Surgical Intervention:** Peritoneal drain placement or exploratory laparotomy with bowel resection and enterostomy for Stage III perforation.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.aap.org"
    )

    val l403 = LessonEntity(
        lessonId = "LES-PCHNM-403",
        courseId = "CRS-PCHNM-M4",
        moduleNumber = 4,
        lessonTitle = "Unit 4.2.1: Congenital Malformations & Surgical Emergencies",
        contentType = "Text",
        durationMins = 35,
        orderIndex = 3,
        contentBody = """
# Unit 4.2.1: Congenital Malformations & Surgical Emergencies

## 1. Neural Tube Defects (NTD): Myelomeningocele
- Protrusion of spinal cord neural placode and meninges through a defective vertebral arch.
- **Immediate Nursing Actions:**
  - Cover the defect immediately with a **sterile, warm normal saline-soaked non-adherent gauze**.
  - Position infant in **prone or lateral position**; avoid pressure on sac.
  - Strictly avoid latex-containing equipment (high risk of latex sensitization).
  - Administer prophylactic IV broad-spectrum antibiotics to prevent ascending meningitis.

## 2. Esophageal Atresia (EA) with Tracheoesophageal Fistula (TEF)
- Esophagus ends in a blind pouch, usually with a fistula connecting trachea to lower esophagus.
- **Clinical Signs:** Excessive frothy oral secretions, choking, coughing, and cyanosis on initial feeding attempt.
- **Diagnosis:** Inability to pass a 10F firm catheter beyond 10–12 cm from the lips. X-ray confirms coiled tube in upper esophageal pouch.
- **Management:** Keep NPO, place in **upright (30°–45°) posture**, continuous low suction in the pouch via Replogle catheter to prevent pulmonary aspiration, urgent surgical repair.

## 3. Congenital Diaphragmatic Hernia (CDH)
- Defect in the pleuroperitoneal membrane (most commonly left-sided Bochdalek hernia), allowing abdominal viscera to herniate into thorax, causing severe pulmonary hypoplasia.
- **Signs:** Scaphoid abdomen, barrel chest, shifted heart sounds, severe respiratory distress.
- **CRITICAL CONTRAINDICATION:** **NEVER perform bag-valve-mask ventilation** (inflates stomach and intestines in thorax, causing fatal lung compression). Immediate endotracheal intubation in delivery room.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.who.int"
    )

    val l404 = LessonEntity(
        lessonId = "LES-PCHNM-404",
        courseId = "CRS-PCHNM-M4",
        moduleNumber = 4,
        lessonTitle = "Unit 4.3.1: Care of Preterm Neonates & Total Parenteral Nutrition (TPN)",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 4,
        contentBody = """
# Unit 4.3.1: Care of Preterm Neonates & Total Parenteral Nutrition (TPN)

## 1. Daily Fluid Requirements by Birth Weight & Day of Life (mL/kg/day)
| Birth Weight | Day 1 | Day 2 | Days 3–6 | ≥ Day 7 |
| :--- | :--- | :--- | :--- | :--- |
| **< 750 g** | 100–140 | 120–160 | 140–200 | 140–160 |
| **750 – 1,000 g** | 100–120 | 120–140 | 130–180 | 140–160 |
| **1,000 – 1,500 g**| 80–100 | 100–120 | 120–160 | 150 |
| **1,500 – 2,500 g**| 60–80 | 80–100 | 120–160 | 150 |
| **> 2,500 g** | 60–80 | 80–100 | 90–150 | 150 |

## 2. Total Parenteral Nutrition (TPN) Principles
- Indicated for ELBW and VLBW infants unable to tolerate enteral feeding due to gut immaturity.
- **Amino Acids:** Start at 1.5–2.0 g/kg/day on Day 1, advance by 0.5–1.0 g/kg/day up to **3.5–4.0 g/kg/day**.
- **Lipids (Intralipid 20%):** Start at 1.0 g/kg/day, titrate up to 3.0 g/kg/day (monitor serum triglycerides).
- **Electrolytes:** Sodium 3–4 mEq/kg/day, Potassium 1–2 mEq/kg/day (added once urine output established).

## 3. Minimal Enteral Nutrition (Trophic Priming)
- Begin within first 24–48 hours with **maternal colostrum at 10–20 mL/kg/day** in small aliquot feeds (1–2 mL q2–3h).
- Purpose is trophic maturation of the intestinal brush border without overburdening metabolic capacity.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l405 = LessonEntity(
        lessonId = "LES-PCHNM-405",
        courseId = "CRS-PCHNM-M4",
        moduleNumber = 4,
        lessonTitle = "Unit 4.3.2: Kangaroo Mother Care (KMC) & Developmental Care in NICU",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 5,
        contentBody = """
# Unit 4.3.2: Kangaroo Mother Care (KMC) & Developmental Care in NICU

Kangaroo Mother Care is an evidence-based clinical strategy of early, continuous, and prolonged skin-to-skin contact between mother and low birth weight infant.

## 1. The Core Components of KMC
1. **KMC Position:** Infant placed prone and upright between mother's breasts. Head turned to one side in slightly extended posture to maintain airway patency; hips and arms flexed in a "frog" posture. Top of support binder adjusted to ear level.
2. **KMC Nutrition:** Exclusive breastfeeding or expressed breast milk feeding via cup or NG tube.
3. **KMC Discharge & Support:** Early discharge in stable KMC position with regular clinical follow-up.

## 2. Evidence-Based Clinical Benefits
- **Mortality Reduction:** Reduces neonatal mortality in LBW infants by **40%**.
- **Infection Prevention:** Reduces incidence of severe sepsis and nosocomial infections by 65%.
- **Thermal Synchrony:** Mother's breast tissue automatically adjusts temperature to warm a hypothermic infant.
- **Neurodevelopment:** Promotes deep quiet sleep, stable heart rate, and improved weight gain (15–20 g/kg/day).

## 3. Discontinuation Criteria for KMC
- Infant reaches **40 weeks post-menstrual age** or weight reaches **≥ 2,500 grams**.
- Infant becomes restless, vigorously kicks limbs out of the binder, and cries when wrapped.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.who.int"
    )

    val l406 = LessonEntity(
        lessonId = "LES-PCHNM-406",
        courseId = "CRS-PCHNM-M4",
        moduleNumber = 4,
        lessonTitle = "Unit 4.3.3: Skill Lab — Feeding Positioning, Tube Feeding & EPI Immunization",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 6,
        contentBody = """
# Unit 4.3.3: Skill Lab — Feeding Positioning, Tube Feeding & EPI Immunization

## 1. Nasogastric (NG) / Orogastric (OG) Tube Placement
- **Measurement (NEX method):** Measure from the tip of the **N**ose to the **E**arlobe, then to the **X**iphoid process (for preterm infants, midpoint between xiphoid and umbilicus).
- **Confirmation of Placement:**
  - Aspirate gastric contents and test with pH indicator strip (pH ≤ 5.5).
  - Auscultate whoosh of 1–2 mL air over epigastrium while listening with stethoscope.
- **Administration:** Feed by gentle gravity flow (barrel of syringe elevated 15–20 cm above infant). Never force milk under plunger pressure.

## 2. Cup Feeding Technique
- Hold infant in semi-upright seated posture on lap.
- Place small rim of medicinal feeding cup gently against lower lip.
- Tip cup slightly so milk touches lower lip. **Let infant lap or sip milk using tongue**. Never pour milk directly into the mouth (severe choking and aspiration risk).

## 3. Expanded Program on Immunization (EPI) at Birth
- **BCG Vaccine (Bacillus Calmette-Guérin):**
  - Dose: **0.05 mL** reconstituted vaccine.
  - Route: **Intradermal (ID)** on right upper deltoid.
  - Technique: 26G needle, bevel facing upward at 10°–15° angle; inject slowly to produce a distinct 5–6 mm pale wheal.
- **Hepatitis B Birth Dose:**
  - Dose: **0.5 mL (10 µg)**.
  - Route: **Intramuscular (IM)** on the anterolateral thigh (vastus lateralis) within 24 hours of birth.
- **Oral Polio Vaccine (OPV 0):**
  - Dose: **2 drops** orally on tongue.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.who.int"
    )

    dao.insertLesson(l401)
    dao.insertLesson(l402)
    dao.insertLesson(l403)
    dao.insertLesson(l404)
    dao.insertLesson(l405)
    dao.insertLesson(l406)
}
