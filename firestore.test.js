const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read courses or lessons", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("courses").get());
  await assertFails(unauthDb.collection("lessons").get());
});

test("Authenticated user: can read courses and lessons", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("courses").doc("CRS-NRP-01").set({
      courseId: "CRS-NRP-01",
      courseTitle: "Neonatal Resuscitation Program",
      category: "Resuscitation",
      isActive: true,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("courses").doc("CRS-NRP-01").get());
});

test("Authenticated user: can create their own enrollment", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("enrollments").doc("ENR-101").set({
      enrollmentId: "ENR-101",
      userId: ALICE_UID,
      courseId: "CRS-NRP-01",
      status: "enrolled",
      progressPercent: 0,
      createdAt: new Date(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("enrollments").doc("ENR-TEST-01").set({
      userId: ALICE_UID,
      courseId: "CRS-NRP-01",
      status: "enrolled",
      progressPercent: 25.0,
      createdAt: null,
      lastAccessed: null,
    })
  );
});

test("Authenticated user: cannot read another user's enrollment", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("enrollments").doc("ENR-BOB").set({
      enrollmentId: "ENR-BOB",
      userId: BOB_UID,
      courseId: "CRS-NRP-01",
      status: "enrolled",
      progressPercent: 50,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("enrollments").doc("ENR-BOB").get());
});

test("Authenticated user: can record their own OSCE attempt", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("attempts").doc("ATT-101").set({
      attemptId: "ATT-101",
      userId: ALICE_UID,
      learnerName: "Nurse Alice",
      courseId: "CRS-NRP-01",
      assessmentId: "LES-03",
      score: 6,
      total: 6,
      percentage: 100,
      passed: true,
      type: "osce",
      createdAt: new Date(),
    })
  );
});

test("Audit logs: immutable (cannot update or delete)", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("activityLogs").doc("LOG-01").set({
      logId: "LOG-01",
      userId: ALICE_UID,
      action: "USER_LOGIN",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("activityLogs").doc("LOG-01").delete());
  await assertFails(
    aliceDb.collection("activityLogs").doc("LOG-01").update({
      action: "ALTERED",
    })
  );
});
