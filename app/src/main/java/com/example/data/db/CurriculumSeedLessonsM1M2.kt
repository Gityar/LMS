package com.example.data.db

import com.example.data.dao.LmsDao
import com.example.data.model.LessonEntity

suspend fun seedCurriculumModule1Lessons(dao: LmsDao) {
    val l101 = LessonEntity(
        lessonId = "LES-PCHNM-101",
        courseId = "CRS-PCHNM-M1",
        moduleNumber = 1,
        lessonTitle = "Unit 1.1.1: Introduction to Neonatology, Roles & Global Epidemiology",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 1,
        contentBody = """
# Unit 1.1.1: Introduction to Neonatology, Roles & Global Epidemiology

## 1. Global & Regional Neonatal Mortality Situation
- **Global burden (2021):** 2.3 million children died in the first month of life — approximately 6,400 newborn deaths every day.
- While under-5 child mortality has declined dramatically since 1990, neonatal deaths now account for **47%** of all under-5 deaths globally.
- **Sub-Saharan Africa:** Bears the highest neonatal mortality rate in the world at **27 deaths per 1,000 live births**. A child born in Sub-Saharan Africa is **10 times more likely** to die in the first month than a child in a high-income country.
- **Ethiopia Context:** Under-5 mortality declined by 71% (from 204 to 59 per 1,000 live births between 1990 and 2019). However, this reduction was concentrated in post-neonatal children. Consequently, the proportion of under-5 deaths occurring in the neonatal period rose from **43% to 48%**.
- More than **80% of newborn deaths** are caused by preventable and treatable conditions:
  1. Neonatal Sepsis and Severe Infections
  2. Complications of Prematurity and Low Birth Weight
  3. Intrapartum-related events (Birth Asphyxia)

## 2. The Professional Mandate of the Neonatal Nurse
The primary mandate of neonatal nursing training is to reduce neonatal morbidity and mortality through:
- **Evidence-Based Clinical Practice:** Applying standard resuscitation protocols, aseptic technique, and timely pharmacological interventions.
- **Compassionate & Family-Centred Care:** Fostering maternal-infant bonding, respectful communication, and parental empowerment.
- **Thermal Regulation:** Maintaining the warm chain and preventing hypothermia through kangaroo mother care and incubators.
- **Reflective Practice:** Maintaining clinical logbooks, participating in simulation debriefs, and lifelong learning.

## 3. Reflection Discussion Prompt
*Reflect on the neonatal mortality drivers in your region. What infrastructure, equipment, or skill gaps contribute most significantly to preventable newborn deaths at primary and referral health facilities?*
        """.trimIndent(),
        videoUrl = "https://www.youtube.com/watch?v=kYV3VlZ13A0",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l102 = LessonEntity(
        lessonId = "LES-PCHNM-102",
        courseId = "CRS-PCHNM-M1",
        moduleNumber = 1,
        lessonTitle = "Unit 1.1.2: Fetal-to-Neonatal Transition & Physiological Adaptation",
        contentType = "Text",
        durationMins = 35,
        orderIndex = 2,
        contentBody = """
# Unit 1.1.2: Fetal-to-Neonatal Transition & Physiological Adaptation

Successful transition from intrauterine aquatic existence to extrauterine terrestrial life requires rapid, coordinated adaptations across multiple organ systems.

## 1. Cardiorespiratory Transition
- **In Utero:** The placenta functions as the organ of respiration. Pulmonary vascular resistance (PVR) is high, pulmonary blood flow is low (~10% of combined cardiac output), and systemic vascular resistance (SVR) is low. Blood shunts right-to-left through the **foramen ovale** and **ductus arteriosus**.
- **At Birth:**
  1. Clamping of the umbilical cord removes the low-resistance placental circuit, sharply increasing SVR.
  2. The initial breath generates negative intrathoracic pressures (-30 to -60 cm H2O), expanding alveoli and displacing fetal lung liquid into lymphatic and capillary beds.
  3. Increased alveolar oxygen tension (PaO2) triggers pulmonary arteriolar vasodilation, causing a rapid **fall in pulmonary vascular resistance (PVR)**.
  4. Pulmonary venous return to the left atrium surges, elevating left atrial pressure above right atrial pressure, functionally closing the **foramen ovale**.
  5. Oxygenated blood and loss of circulating prostaglandin E2 cause smooth muscle constriction of the **ductus arteriosus**.

## 2. Thermoregulatory Adaptation & Brown Fat Metabolism
- Newborns cannot generate heat through shivering. They rely exclusively on **non-shivering thermogenesis** mediated by **Brown Adipose Tissue (BAT)**.
- Brown fat is localized around the scapulae, axillae, mediastinum, and perirenal areas.
- Cold stress triggers norepinephrine release, activating mitochondrial **uncoupling protein-1 (UCP-1)**, uncoupling oxidative phosphorylation to release heat directly instead of synthesizing ATP.
- **Vulnerabilities:** Preterm infants have depleted brown fat stores, thin skin, high trans-epidermal water loss, and a large surface area-to-mass ratio.

## 3. Metabolic Adaptation & Glucose Homeostasis
- Abrupt cessation of maternal glucose transfer necessitates glycogenolysis and gluconeogenesis within 2 hours of life.
- **Diagnostic thresholds:** Whole blood glucose < 40 mg/dL in the first 4 hours of life, or < 45 mg/dL after 4 hours, indicates hypoglycemia requiring intervention.

## 4. Neonatal Reflexes & Behavioral States
- **Primitive Reflexes:** Moro (startle), rooting, sucking, palmar/plantar grasp, stepping, asymmetric tonic neck ("fencing").
- **Brazelton Behavioral States:** State 1 (Deep sleep), State 2 (Light sleep), State 3 (Drowsy), State 4 (Quiet alert — optimal for bonding/feeding), State 5 (Active alert), State 6 (Crying).
        """.trimIndent(),
        videoUrl = "https://www.youtube.com/watch?v=Zf_C1jBv5_E",
        resourceUrl = "https://www.who.int/publications/i/item/9789241599887"
    )

    val l103 = LessonEntity(
        lessonId = "LES-PCHNM-103",
        courseId = "CRS-PCHNM-M1",
        moduleNumber = 1,
        lessonTitle = "Unit 1.2.1: Neonatal Classification by Gestational Age & Birth Weight",
        contentType = "Text",
        durationMins = 25,
        orderIndex = 3,
        contentBody = """
# Unit 1.2.1: Neonatal Classification by Gestational Age & Birth Weight

Accurate clinical classification stratifies mortality risk, guides incubator settings, and directs fluid and caloric titration.

## 1. Classification by Gestational Age (GA)
- **Preterm:** Born before 37 completed weeks (< 259 days).
  - Extremely Preterm: < 28 weeks
  - Very Preterm: 28 to < 32 weeks
  - Moderate to Late Preterm: 32 to < 37 weeks
- **Term:** 37 completed weeks up to 41 weeks and 6 days (37 – 41+6 weeks).
- **Post-term:** 42 completed weeks or more (≥ 294 days).

## 2. Classification by Birth Weight
- **Macrosomia:** Birth weight ≥ 4,000 grams.
- **Normal Birth Weight:** 2,500 – 3,999 grams.
- **Low Birth Weight (LBW):** Birth weight < 2,500 grams (1,500 – 2,499g).
- **Very Low Birth Weight (VLBW):** Birth weight < 1,500 grams (1,000 – 1,499g).
- **Extremely Low Birth Weight (ELBW):** Birth weight < 1,000 grams.

## 3. Classification by Weight-for-Gestational-Age (Fenton / Lubchenco Growth Charts)
- **Small for Gestational Age (SGA):** Birth weight < 10th percentile for GA. High risk for hypoglycemia, polycythemia, and perinatal asphyxia.
- **Appropriate for Gestational Age (AGA):** Birth weight between 10th and 90th percentile for GA.
- **Large for Gestational Age (LGA):** Birth weight > 90th percentile for GA. High risk for birth trauma, shoulder dystocia, hypoglycemia (infant of diabetic mother), and polycythemia.

## 4. Clinical Estimation of Gestational Age
- First day of the Last Menstrual Period (LMP) — Naegele's rule.
- Early obstetric ultrasound (Crown-Rump Length in 1st trimester).
- **New Ballard Score:** Neuromuscular maturity (posture, square window, arm recoil, popliteal angle, scarf sign, heel to ear) and Physical maturity (skin, lanugo, plantar surface, breast, eye/ear, genitals).
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.aap.org"
    )

    dao.insertLesson(l101)
    dao.insertLesson(l102)
    dao.insertLesson(l103)
}

suspend fun seedCurriculumModule2Lessons(dao: LmsDao) {
    val l201 = LessonEntity(
        lessonId = "LES-PCHNM-201",
        courseId = "CRS-PCHNM-M2",
        moduleNumber = 2,
        lessonTitle = "Unit 2.1.1: Assessment and 11-Step Essential Newborn Care at Birth",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 1,
        contentBody = """
# Unit 2.1.1: Assessment and 11-Step Essential Newborn Care at Birth

Essential Newborn Care (ENC) is a standardized bundle of evidence-based interventions administered to all infants at birth to optimize survival and transition.

## The 11 Standardized Steps of Essential Newborn Care:
1. **Immediate Drying & Tactile Stimulation:** Dry whole body immediately with a clean, warm towel on mother's abdomen; remove wet linen to prevent evaporative heat loss.
2. **Evaluate Breathing:** Assess if baby is crying vigorously. If apnoeic or gasping, call for immediate help, clamp/cut cord rapidly, and transfer to radiant warmer for resuscitation.
3. **Umbilical Cord Care:** Delay cord clamping for 1–3 minutes in vigorous infants to increase neonatal blood volume and iron stores. Clamp and cut with sterile scissors.
4. **Keep Warm (Thermal Protection):** Direct skin-to-skin contact on mother's chest; cover baby with pre-warmed dry cloth and bonnet. Maintain delivery room at 25°C–28°C.
5. **Early Initiation of Breastfeeding:** Assist mother to initiate exclusive breastfeeding within the **first 1 hour of life**. Colostrum provides immunoglobulins (IgA) and prevents hypoglycemia.
6. **Eye Prophylaxis:** Apply **1% Tetracycline eye ointment** across lower conjunctival sacs within 1 hour of birth to prevent ophthalmia neonatorum (gonococcal/chlamydial).
7. **Chlorhexidine Cord Care:** Apply **7.1% chlorhexidine digluconate (delivering 4% free chlorhexidine)** to the cord stump to prevent omphalitis and sepsis.
8. **Vitamin K1 Prophylaxis:** Administer Vitamin K1 IM in the anterolateral thigh to prevent Vitamin K Deficiency Bleeding (VKDB):
   - **Term & Late Preterm (≥ 34 weeks):** 1.0 mg IM
   - **Preterm (< 34 weeks):** 0.5 mg IM
9. **Identification Bands:** Securely place two verified identification bands (wrist and ankle) containing mother's name, infant gender, date, and time of birth.
10. **Anthropometric Measurements:** Weigh infant on a calibrated, pre-warmed scale; measure head circumference (HC) and crown-heel length once stabilized.
11. **Clinical Recording & Monitoring:** Document all findings in the delivery register and neonatal chart.

## APGAR Scoring System (1 and 5 Minutes)
- **A**ppearance (Color): 0 = blue/pale all over, 1 = acrocyanosis (pink body, blue extremities), 2 = completely pink
- **P**ulse (Heart Rate): 0 = absent, 1 = < 100 bpm, 2 = ≥ 100 bpm
- **G**rimace (Reflex): 0 = no response, 1 = grimace only, 2 = vigorous cry, cough, sneeze
- **A**ctivity (Tone): 0 = limp/flaccid, 1 = some flexion of extremities, 2 = active motion
- **R**espiration: 0 = absent, 1 = slow, irregular, weak gasp, 2 = good strong cry
*Scores: 7–10 Normal/Vigorous; 4–6 Moderate depression; 0–3 Severe depression.*
        """.trimIndent(),
        videoUrl = "https://www.youtube.com/watch?v=kYV3VlZ13A0",
        resourceUrl = "https://www.who.int"
    )

    val l202 = LessonEntity(
        lessonId = "LES-PCHNM-202",
        courseId = "CRS-PCHNM-M2",
        moduleNumber = 2,
        lessonTitle = "Unit 2.1.2: Clinical Skill Lab — Delivery Triage & Assessment Setup",
        contentType = "Text",
        durationMins = 25,
        orderIndex = 2,
        contentBody = """
# Unit 2.1.2: Clinical Skill Lab — Delivery Triage & Assessment Setup

## 1. Skill Lab Delivery Station Setup
- **Warmth:** Radiant warmer pre-heated to 100% manual power; pre-warmed sterile towels; polyethylene occlusive plastic wrap for infants < 32 weeks.
- **Airway & Suction:** Wall suction calibrated to **80–100 mmHg** (never exceed 100 mmHg in neonates); 8F, 10F, and 12F suction catheters; bulb syringe.
- **Breathing & Ventilation:** Flow-inflating bag, self-inflating bag (240–450 mL) with pop-off valve (30–40 cm H2O), or T-piece resuscitator; cushioned anatomical round/preterm masks (sizes 0/1); blended air/oxygen supply with flowmeter set to 8–10 L/min.
- **Auscultation & Monitoring:** Neonatal stethoscope, pulse oximeter with neonatal sensor placed on **right hand or wrist (pre-ductal)**.
- **Intubation & Vascular Access:** Laryngoscope with straight Miller blades (size 00 for ELBW, 0 for preterm, 1 for term); un-cuffed ETTs (2.5, 3.0, 3.5 mm); stylets; Umbilical Venous Catheterization (UVC) tray with 3.5F and 5F catheters, scalpel, and purse-string suture.

## 2. Pre-Birth Delivery Room Triage
Every birth requires at least one designated professional trained in neonatal resuscitation. Identify four pre-birth questions:
1. Expected gestational age?
2. Is the amniotic fluid clear or meconium-stained?
3. How many babies are expected?
4. Are there maternal risk factors (preeclampsia, chorioamnionitis, maternal diabetes, non-reassuring fetal heart rate)?
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.aap.org/nrp"
    )

    val l203 = LessonEntity(
        lessonId = "LES-PCHNM-203",
        courseId = "CRS-PCHNM-M2",
        moduleNumber = 2,
        lessonTitle = "Unit 2.2.1: The Golden Minute & NRP Clinical Algorithm",
        contentType = "Text",
        durationMins = 35,
        orderIndex = 3,
        contentBody = """
# Unit 2.2.1: The Golden Minute & NRP Clinical Algorithm

The **Golden Minute** represents the first 60 seconds of life during which initial steps, rapid evaluation, and initiation of positive pressure ventilation (if indicated) must be completed.

## 1. Rapid Assessment at Birth (3 Questions):
1. **Term gestation?**
2. **Good muscle tone?**
3. **Breathing or crying?**
- **YES to all 3:** Proceed to standard Essential Newborn Care (keep warm with mother, dry, position, clear airway if needed, promote skin-to-skin).
- **NO to any:** Bring infant to radiant warmer immediately for initial steps.

## 2. Initial Steps (Completed in First 30 Seconds):
1. **Warm:** Place under pre-heated radiant warmer.
2. **Position:** Head in the neutral **"sniffing" position** (slight extension). Avoid hyperextension or flexion.
3. **Clear Airway:** Suction **mouth first, then nose ("M before N")** using bulb syringe or suction catheter only if secretions are obstructing breathing.
4. **Dry:** Thoroughly dry the infant and discard wet linen immediately.
5. **Stimulate:** Provide tactile stimulation by gently rubbing the back or flicking the soles 2–3 times.

## 3. Evaluation of Heart Rate & Breathing (30–60 Seconds):
- Auscultate apical heartbeat with neonatal stethoscope for 6 seconds and multiply by 10.
- **If HR < 100 bpm, or infant is apnoeic or gasping:**
  - **Initiate Positive Pressure Ventilation (PPV) immediately within the Golden Minute.**
  - Rate: **40 to 60 breaths per minute** (cadence: "Breathe, two, three; breathe, two, three").
  - Starting FiO2: **21% (Room air)** for ≥ 35 weeks; **21%–30%** for < 35 weeks.
  - Attach pre-ductal SpO2 sensor to **right wrist**.

## 4. Targeted Pre-Ductal SpO2 Standards (Right Hand/Wrist):
- **1 minute:** 60% – 65%
- **2 minutes:** 65% – 70%
- **3 minutes:** 70% – 75%
- **4 minutes:** 75% – 80%
- **5 minutes:** 80% – 85%
- **10 minutes:** 85% – 95%
*(Caution: Targeting adult saturations 95%–100% in the first minutes causes hyperoxia and free-radical tissue damage).*

## 5. MR. SOPA Ventilation Corrective Sequence
If heart rate does not increase and chest is not moving adequately:
- **M - Mask adjustment:** Reapply with two-point top-bottom seal.
- **R - Reposition airway:** Re-establish neutral sniffing posture. *(Try PPV)*
- **S - Suction mouth & nose:** Clear obstruction.
- **O - Open mouth:** Lift mandible gently forward. *(Try PPV)*
- **P - Pressure increase:** Increment PIP by 5–10 cm H2O (max 40 cm H2O). *(Try PPV)*
- **A - Alternative airway:** Insert Endotracheal Tube (ETT) or Laryngeal Mask Airway (LMA).

## 6. Coordinated Chest Compressions (3:1 Ratio)
- **Indication:** Heart rate remains **< 60 bpm** despite 30 seconds of effective PPV with chest movement.
- **Technique:** Two-thumb encircling-hands technique on lower third of sternum (just below inter-mammary line).
- **Ratio:** **3 compressions to 1 ventilation** (90 compressions + 30 breaths = 120 events/minute).
- **Oxygen:** Increase FiO2 to **100%**.

## 7. Emergency Medication: Epinephrine
- **Indication:** HR remains < 60 bpm after effective chest compressions and 100% oxygen PPV.
- **Dose:** **0.02 mg/kg** (0.2 mL/kg of 1:10,000 solution) administered IV via **Umbilical Venous Catheter (UVC)** followed by 0.5–1 mL normal saline flush.
        """.trimIndent(),
        videoUrl = "https://www.youtube.com/watch?v=kYV3VlZ13A0",
        resourceUrl = "https://www.aap.org/nrp"
    )

    val l204 = LessonEntity(
        lessonId = "LES-PCHNM-204",
        courseId = "CRS-PCHNM-M2",
        moduleNumber = 2,
        lessonTitle = "Unit 2.2.2: Clinical Calculations & Resuscitation Dosage Formulations",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 4,
        contentBody = """
# Unit 2.2.2: Clinical Calculations & Resuscitation Dosage Formulations

Precision in fluid rate calculation and drug dilution is vital in neonatal critical care to avoid hypervolemia, intracranial hemorrhage, and fluid overload.

## 1. Neonatal Infusion Fluid Rate Formula
$$\text{Total Hourly Rate (mL/hr)} = \frac{\text{Weight (kg)} \times \text{Desired Daily Fluid (mL/kg/day)}}{24\text{ hours}}$$

### Worked Clinical Examples:
- **Case 1:** A 2.0 kg neonate requires 80 mL/kg/day of 10% Dextrose in Water on Day 2 of life.
  $$\text{Rate} = \frac{2.0\text{ kg} \times 80\text{ mL/kg/day}}{24\text{ hours}} = \frac{160}{24} = \mathbf{6.67\text{ mL/hr}}$$
- **Case 2:** A 1.8 kg preterm infant requires 100 mL/kg/day intravenous fluid.
  $$\text{Rate} = \frac{1.8\text{ kg} \times 100\text{ mL/kg/day}}{24\text{ hours}} = \frac{180}{24} = \mathbf{7.5\text{ mL/hr}}$$

## 2. Emergency Epinephrine Resuscitation Dosing
- **Concentration:** 1:10,000 (0.1 mg/mL).
- **Recommended IV/UVC dose:** **0.02 mg/kg** = **0.2 mL/kg** of 1:10,000 solution.
- **Worked example:** For a 3.0 kg term infant with asystole / persistent bradycardia:
  $$\text{Volume} = 3.0\text{ kg} \times 0.2\text{ mL/kg} = \mathbf{0.6\text{ mL}}\text{ of 1:10,000 Epinephrine}$$
  Follow immediately with 1.0 mL 0.9% Normal Saline flush to ensure rapid central delivery via UVC.

## 3. Glucose Infusion Rate (GIR) Calculation
$$\text{GIR (mg/kg/min)} = \frac{\text{Rate (mL/hr)} \times \text{Dextrose Concentration (\%)} \times 10}{60 \times \text{Weight (kg)}} = \frac{\text{Rate (mL/hr)} \times \% \text{Dextrose}}{6 \times \text{Weight (kg)}}$$
- **Normal baseline GIR:** 4 – 6 mg/kg/min.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    val l205 = LessonEntity(
        lessonId = "LES-PCHNM-205",
        courseId = "CRS-PCHNM-M2",
        moduleNumber = 2,
        lessonTitle = "Unit 2.2.3: Interactive Self-Assessment — NRP Clinical Scenarios",
        contentType = "Text",
        durationMins = 20,
        orderIndex = 5,
        contentBody = """
# Unit 2.2.3: Interactive Self-Assessment — NRP Clinical Scenarios

Review these core delivery room scenarios from the curriculum with evidence-based rationales:

### Question 1: Immediate Action After Initial Steps
- **Scenario:** A full-term infant is delivered limp and apnoeic. After drying and positioning in sniffing position for 30 seconds, the infant remains gasping with a heart rate of 75 bpm. What is the immediate next nursing action?
- **Correct Action:** **Initiate Positive Pressure Ventilation (PPV)**.
- **Rationale:** PPV is indicated if heart rate is below 100 bpm or if infant is gasping/apnoeic after initial steps. Chest compressions are only initiated if heart rate remains below 60 bpm after 30 seconds of effective PPV. Epinephrine follows compressions, not initial PPV.

### Question 2: Oxygen Saturation Target at 5 Minutes
- **Scenario:** A term newborn is receiving PPV with a pre-ductal saturation probe on the right hand. What SpO2 range should the resuscitation team expect at 5 minutes of life?
- **Correct Range:** **80% – 85%**.
- **Rationale:** Normal intra-to-extrauterine transition requires time. At 1 min SpO2 is 60-65%; at 5 min it is 80-85%; only by 10 min does it reach 85-95%. Targeting 95-100% earlier causes dangerous hyperoxia.

### Question 3: Infusion Rate Calculation
- **Scenario:** A 1.8 kg neonate requires 100 mL/kg/day of intravenous fluid. What hourly rate should be set on the infusion pump?
- **Correct Setting:** **7.5 mL/hr** ((1.8 * 100) / 24 = 180 / 24 = 7.5 mL/hr).

### Question 4: Compression-to-Ventilation Ratio
- **Scenario:** A newborn's heart rate remains 50 bpm after 30 seconds of effective PPV with good mask seal and chest rise. Which compression-to-ventilation ratio should the nurse use?
- **Correct Ratio:** **3:1 Ratio** (90 compressions and 30 breaths = 120 events/min).
- **Rationale:** 15:2 and 30:2 are adult and pediatric resuscitation ratios and are strictly unsafe in the newborn whose primary arrest etiology is respiratory asphyxia.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://www.aap.org/nrp"
    )

    val l206 = LessonEntity(
        lessonId = "LES-PCHNM-206",
        courseId = "CRS-PCHNM-M2",
        moduleNumber = 2,
        lessonTitle = "Unit 2.2.4: Skill Lab Simulation Protocol & Resuscitation Drills",
        contentType = "Text",
        durationMins = 30,
        orderIndex = 6,
        contentBody = """
# Unit 2.2.4: Skill Lab Simulation Protocol & Resuscitation Drills

## 1. Preparation Checklist (Before Scenario Commences)
- [ ] Radiant warmer pre-heated to active mode; warm blankets and sterile wrap available.
- [ ] Suction device tested and calibrated to 80–100 mmHg; 10F/12F catheters at hand.
- [ ] Self-inflating bag of correct size (240–450 mL) with tight mask seal verified; blender connected and set to 21% FiO2.
- [ ] Pre-ductal SpO2 probe, stethoscope, and emergency umbilical venous access kit ready.
- [ ] Hand hygiene performed according to WHO 5 Moments and sterile PPE donned.

## 2. Team Communication Protocol
- **Role Assignment:** Roles assigned aloud prior to delivery:
  1. Team Leader (Coordinates decisions, monitors elapsed time).
  2. Airway Lead (Positions head, manages mask, performs PPV/intubation).
  3. Compressor (Performs 3:1 compressions, monitors umbilical pulse).
  4. Medication & Equipment Nurse (Calculates doses, prepares UVC, flushes lines).
  5. Recorder / Timekeeper (Calls out 30s intervals, logs vitals and interventions).
- **Closed-Loop Communication:** Orders repeated aloud by the recipient before execution and confirmed upon completion.
- **Time Announcements:** Elapsed time called out at 30s, 60s, and every subsequent minute.

## 3. OSCE Station Briefing
- Station Duration: **10 minutes**.
- Maximum Score: **20 points**. Pass mark: **80% (16 points)**.
- Candidate conducts complete sequence from preparation to initial care, PPV, MR. SOPA, compressions, UVC access, parental counselling, and debriefing.
        """.trimIndent(),
        videoUrl = "",
        resourceUrl = "https://gityar.github.io/MyPortfolio"
    )

    dao.insertLesson(l201)
    dao.insertLesson(l202)
    dao.insertLesson(l203)
    dao.insertLesson(l204)
    dao.insertLesson(l205)
    dao.insertLesson(l206)
}
