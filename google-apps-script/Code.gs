/**
 * ============================================================================
 * NEONATAL NURSING LMS - GOOGLE APPS SCRIPT BACKEND (Code.gs)
 * Spreadsheet ID: 1TQcak2SNQ3oT9DBz_nyrOzVh8fWfNRVsWqt7Rk_1uTA
 *
 * FULL CRUD & MULTI-FORMAT CURRICULUM SYNC ENGINE
 * Supports rich courses, modular lessons (video/PDF/case/simulation/text),
 * multiple assessments, and flexible multi-format question banks.
 * ============================================================================
 */

var SPREADSHEET_ID = "1TQcak2SNQ3oT9DBz_nyrOzVh8fWfNRVsWqt7Rk_1uTA";

/**
 * 13 REQUIRED SCHEMAS & EXTENDED COLUMN HEADERS
 * Includes support for rich content types, media URLs, checklists, and question types.
 */
var SCHEMAS = {
  "Users": [
    "User_ID", "Full_Name", "Email", "Password_Hash", "Role", "Registration_Date", "Account_Status", "Last_Login"
  ],
  "Courses": [
    "Course_ID", "Course_Title", "Category", "Instructor_ID", "Instructor_Name", "Description", "Status", "Created_Date", "Total_Modules", "Difficulty", "CME_Credits", "Thumbnail_URL"
  ],
  "Lessons": [
    "Lesson_ID", "Course_ID", "Module_Number", "Lesson_Title", "Duration_Minutes", "Lesson_Type", "Body_Content", "Media_URL", "Resource_URL", "Checklist_JSON"
  ],
  "Enrollments": [
    "Enrollment_ID", "Learner_ID", "Learner_Name", "Course_ID", "Enrollment_Date", "Completion_Status", "Progress_Percentage", "Last_Accessed"
  ],
  "Activity_Logs": [
    "Log_ID", "User_ID", "Action", "Timestamp", "IP_Address", "Device_Info"
  ],
  "Password_Resets": [
    "Reset_ID", "User_ID", "Email", "Reset_Token", "Request_Date", "Expiry_Date", "Status"
  ],
  "Lesson_Content": [
    "Content_ID", "Lesson_ID", "Content_Type", "Content_Data", "Order_Index"
  ],
  "Lesson_Resources": [
    "Resource_ID", "Lesson_ID", "Resource_Name", "Resource_URL", "Resource_Type"
  ],
  "Assignments": [
    "Assignment_ID", "Course_ID", "Title", "Instructions", "Due_Date", "Max_Score"
  ],
  "Assignment_Submissions": [
    "Submission_ID", "Assignment_ID", "Learner_ID", "Submission_Date", "Submission_Text", "Score", "Feedback"
  ],
  "Assessments": [
    "Assessment_ID", "Course_ID", "Title", "Passing_Score", "Total_Questions", "Time_Limit_Min", "Assessment_Type", "Instructions"
  ],
  "Questions": [
    "Question_ID", "Assessment_ID", "Question_Text", "Question_Type", "Options_JSON", "Correct_Option_Index", "Explanation", "Points", "Difficulty", "Media_URL", "Option_A", "Option_B", "Option_C", "Option_D"
  ],
  "Assessment_Attempts": [
    "Attempt_ID", "Assessment_ID", "Learner_ID", "Learner_Name", "Course_ID", "Attempt_Number", "Score", "Percentage", "Passed", "Submitted_Date", "Answers_JSON"
  ],
  "Lesson_Feedback": [
    "Feedback_ID", "Lesson_ID", "Learner_ID", "Learner_Name", "Rating", "Comment", "Timestamp"
  ]
};

/**
 * One-click Setup: Creates or updates all 13 sheet tabs with bold headers.
 * Safely preserves existing data rows while ensuring all extended headers exist.
 */
function setupAllSheets() {
  var ss = SpreadsheetApp.openById(SPREADSHEET_ID);
  
  for (var sheetName in SCHEMAS) {
    var sheet = ss.getSheetByName(sheetName);
    var expectedHeaders = SCHEMAS[sheetName];
    
    if (!sheet) {
      sheet = ss.insertSheet(sheetName);
    }
    
    if (sheet.getLastRow() === 0) {
      sheet.appendRow(expectedHeaders);
      formatHeaderRange(sheet, expectedHeaders.length);
    } else {
      // Sync headers non-destructively: add any missing columns to row 1
      var existingHeaders = sheet.getRange(1, 1, 1, Math.max(sheet.getLastColumn(), 1)).getValues()[0];
      for (var i = 0; i < expectedHeaders.length; i++) {
        var colName = expectedHeaders[i];
        if (existingHeaders.indexOf(colName) === -1) {
          var nextCol = sheet.getLastColumn() + 1;
          sheet.getRange(1, nextCol).setValue(colName);
          existingHeaders.push(colName);
        }
      }
      formatHeaderRange(sheet, sheet.getLastColumn());
    }
  }
}

function formatHeaderRange(sheet, numCols) {
  if (numCols < 1) return;
  var headerRange = sheet.getRange(1, 1, 1, numCols);
  headerRange.setFontWeight("bold");
  headerRange.setBackground("#F1F5F9");
  headerRange.setFontColor("#1E293B");
}

/**
 * Diagnostic & Authorization Helper
 */
function testSendEmail() {
  var testEmail = "yaregalsemanew@gmail.com";
  MailApp.sendEmail({
    to: testEmail,
    subject: "✅ Neonatal Nursing LMS - Email Dispatch Test",
    htmlBody: "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
      "<h3>Email service is working!</h3>" +
      "<p>Your Google Apps Script backend has authorized MailApp and can successfully dispatch emails.</p>" +
      "</div>"
  });
  Logger.log("Test email successfully dispatched to " + testEmail);
}

/**
 * Helper: Find or create target sheet
 */
function getTargetSheet(ss, sheetKey) {
  var keyLower = String(sheetKey || "").toLowerCase();
  var sheets = ss.getSheets();
  for (var i = 0; i < sheets.length; i++) {
    if (sheets[i].getName().toLowerCase() === keyLower) {
      return sheets[i];
    }
  }
  // Try exact lookup from schema map
  for (var name in SCHEMAS) {
    if (name.toLowerCase() === keyLower) {
      var s = ss.getSheetByName(name);
      if (!s) {
        s = ss.insertSheet(name);
        s.appendRow(SCHEMAS[name]);
        formatHeaderRange(s, SCHEMAS[name].length);
      }
      return s;
    }
  }
  return ss.getSheetByName("Courses") || ss.getSheets()[0];
}

/**
 * HTTP GET - Used to read table records or verify connection
 */
function doGet(e) {
  try {
    var ss = SpreadsheetApp.openById(SPREADSHEET_ID);
    var action = (e && e.parameter && e.parameter.action) ? e.parameter.action : "ping";
    
    if (action === "ping") {
      return jsonResponse({
        status: "success",
        message: "Neonatal Nursing LMS API v3 (Multi-Format Curriculum Engine) is running",
        timestamp: new Date().toISOString()
      });
    }
    
    var sheet = getTargetSheet(ss, action);
    var data = sheet.getDataRange().getValues();
    
    if (data.length <= 1) {
      return jsonResponse({ status: "success", data: [] });
    }
    
    var headers = data[0];
    var rows = [];
    for (var r = 1; r < data.length; r++) {
      var item = {};
      for (var c = 0; c < headers.length; c++) {
        item[headers[c]] = data[r][c];
      }
      rows.push(item);
    }
    
    return jsonResponse({ status: "success", data: rows });
  } catch (err) {
    return jsonResponse({ status: "error", message: err.toString() });
  }
}

/**
 * HTTP POST - Handles all write, update, delete, and sync operations
 */
function doPost(e) {
  try {
    var postData = "";
    if (e && e.postData && e.postData.contents) {
      postData = e.postData.contents;
    }
    
    var req = {};
    if (postData) {
      try {
        req = JSON.parse(postData);
      } catch (parseErr) {
        req = {};
      }
    }
    
    var action = req.action || (e && e.parameter && e.parameter.action) || "";
    var ss = SpreadsheetApp.openById(SPREADSHEET_ID);
    
    // ------------------------------------------------------------------------
    // 1. PASSWORD RESET EMAIL
    // ------------------------------------------------------------------------
    if (action === "sendPasswordResetEmail") {
      var email = req.email || "";
      var userName = req.userName || "Clinician";
      var code = req.code || "";
      var resetLink = req.resetLink || "";
      
      if (!email || !code) {
        return jsonResponse({ status: "error", message: "Email and code are required." });
      }
      
      MailApp.sendEmail({
        to: email,
        subject: "🔐 Neonatal Nursing LMS - Verification Code: " + code,
        htmlBody: 
          "<div style='font-family: Arial, sans-serif; padding: 24px; color: #1e293b; max-width: 520px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 16px;'>" +
            "<h2 style='color: #4338ca; margin: 0 0 12px 0;'>👶 Neonatal Nursing Clinical LMS</h2>" +
            "<p style='font-size: 14px;'>Hello <strong>" + userName + "</strong>,</p>" +
            "<p style='font-size: 13px; line-height: 1.5;'>We received a request to reset your password. Use the verification code below:</p>" +
            "<div style='background-color: #f1f5f9; padding: 16px; text-align: center; border-radius: 12px; margin: 24px 0; border: 1px solid #cbd5e1;'>" +
              "<span style='font-family: monospace; font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #312e81;'>" + code + "</span>" +
            "</div>" +
            "<p style='font-size: 13px; text-align: center; margin: 16px 0;'>" +
              "<a href='" + resetLink + "' style='background-color: #4f46e5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 13px; display: inline-block;'>One-Click Password Reset</a>" +
            "</p>" +
            "<p style='font-size: 11px; color: #64748b; margin-top: 24px; border-top: 1px solid #f1f5f9; padding-top: 12px;'>" +
              "Expires in 15 minutes. If you did not request this, you can safely ignore this email." +
            "</p>" +
          "</div>"
      });
      
      var resetSheet = getTargetSheet(ss, "Password_Resets");
      resetSheet.appendRow([
        "RST-" + code,
        req.userId || "USR-UNKNOWN",
        email,
        code,
        new Date().toISOString(),
        new Date(Date.now() + 15 * 60 * 1000).toISOString(),
        "sent"
      ]);
      
      return jsonResponse({ status: "success", message: "Verification email sent successfully." });
    }
    
    // ------------------------------------------------------------------------
    // 2. UPDATE USER PASSWORD
    // ------------------------------------------------------------------------
    if (action === "updateUserPassword") {
      var targetEmail = String(req.email || "").toLowerCase();
      var newPassword = req.newPassword || "";
      var userSheet = getTargetSheet(ss, "Users");
      var userData = userSheet.getDataRange().getValues();
      var headers = userData[0];
      var emailCol = headers.indexOf("Email");
      var passCol = headers.indexOf("Password_Hash");
      
      if (emailCol !== -1 && passCol !== -1) {
        for (var row = 1; row < userData.length; row++) {
          if (String(userData[row][emailCol]).toLowerCase() === targetEmail) {
            userSheet.getRange(row + 1, passCol + 1).setValue(newPassword);
            break;
          }
        }
      }
      return jsonResponse({ status: "success", message: "Password updated in Users sheet." });
    }
    
    // ------------------------------------------------------------------------
    // 3. UPSERT RECORD BY KEY (Generic Upsert)
    // ------------------------------------------------------------------------
    if (action === "upsertRecord") {
      var sheet = getTargetSheet(ss, req.sheetKey || "Courses");
      var keyColName = req.keyColumn || "Course_ID";
      var record = req.record || {};
      var keyValue = record[keyColName];
      
      upsertSingleRecord(sheet, keyColName, keyValue, record);
      return jsonResponse({ status: "success", message: "Record upserted successfully" });
    }
    
    // ------------------------------------------------------------------------
    // 4. DELETE RECORD BY KEY
    // ------------------------------------------------------------------------
    if (action === "deleteRecord") {
      var sheet = getTargetSheet(ss, req.sheetKey || "Courses");
      var keyColName = req.keyColumn || "Course_ID";
      var keyValue = String(req.keyValue || "");
      
      var data = sheet.getDataRange().getValues();
      var headers = data[0];
      var colIdx = headers.indexOf(keyColName);
      
      if (colIdx !== -1) {
        for (var r = data.length - 1; r >= 1; r--) {
          if (String(data[r][colIdx]) === keyValue) {
            sheet.deleteRow(r + 1);
            break;
          }
        }
      }
      return jsonResponse({ status: "success", message: "Record deleted if existed" });
    }
    
    // ------------------------------------------------------------------------
    // 5. APPEND RECORD (General logs, feedback, attempts)
    // ------------------------------------------------------------------------
    if (action === "appendRecord") {
      var targetSheet = getTargetSheet(ss, req.sheetKey || "Users");
      var headers = targetSheet.getRange(1, 1, 1, targetSheet.getLastColumn() || 1).getValues()[0];
      var records = req.records || [];
      
      for (var i = 0; i < records.length; i++) {
        var rec = records[i];
        var row = [];
        for (var h = 0; h < headers.length; h++) {
          row.push(rec[headers[h]] !== undefined ? rec[headers[h]] : "");
        }
        targetSheet.appendRow(row);
      }
      return jsonResponse({ status: "success", count: records.length });
    }
    
    // ------------------------------------------------------------------------
    // 6. SYNC COURSES (Full replace or update)
    // ------------------------------------------------------------------------
    if (action === "syncCourses") {
      var courseSheet = getTargetSheet(ss, "Courses");
      syncTableRows(courseSheet, "Courses", req.records || []);
      return jsonResponse({ status: "success", synced: (req.records || []).length });
    }
    
    // ------------------------------------------------------------------------
    // 7. SYNC ALL CURRICULUM (Courses, Lessons, Assessments, Questions)
    // One-click batch sync from web app or admin dashboard
    // ------------------------------------------------------------------------
    if (action === "syncAllCurriculum") {
      var payload = req.curriculum || {};
      var syncedInfo = {};
      
      if (payload.courses) {
        var cSheet = getTargetSheet(ss, "Courses");
        syncTableRows(cSheet, "Courses", payload.courses);
        syncedInfo.courses = payload.courses.length;
      }
      
      if (payload.lessons) {
        var lSheet = getTargetSheet(ss, "Lessons");
        syncTableRows(lSheet, "Lessons", payload.lessons);
        syncedInfo.lessons = payload.lessons.length;
      }
      
      if (payload.assessments) {
        var aSheet = getTargetSheet(ss, "Assessments");
        syncTableRows(aSheet, "Assessments", payload.assessments);
        syncedInfo.assessments = payload.assessments.length;
      }
      
      if (payload.questions) {
        var qSheet = getTargetSheet(ss, "Questions");
        syncTableRows(qSheet, "Questions", payload.questions);
        syncedInfo.questions = payload.questions.length;
      }
      
      return jsonResponse({
        status: "success",
        message: "Full curriculum synced successfully",
        details: syncedInfo
      });
    }
    
    return jsonResponse({ status: "success", message: "Action processed: " + action });
  } catch (postErr) {
    return jsonResponse({ status: "error", message: postErr.toString() });
  }
}

/**
 * Replaces table data rows while preserving headers
 */
function syncTableRows(sheet, schemaName, records) {
  var headers = SCHEMAS[schemaName] || sheet.getRange(1, 1, 1, sheet.getLastColumn()).getValues()[0];
  
  // Clear existing rows (keep header row)
  var lastRow = sheet.getLastRow();
  if (lastRow > 1) {
    sheet.deleteRows(2, lastRow - 1);
  }
  
  if (!records || records.length === 0) return;
  
  var newRows = [];
  for (var i = 0; i < records.length; i++) {
    var rec = records[i];
    var row = [];
    for (var h = 0; h < headers.length; h++) {
      var colName = headers[h];
      var val = rec[colName] !== undefined ? rec[colName] : (rec[colName.toLowerCase()] !== undefined ? rec[colName.toLowerCase()] : "");
      if (typeof val === "object") {
        val = JSON.stringify(val);
      }
      row.push(val);
    }
    newRows.push(row);
  }
  
  if (newRows.length > 0) {
    sheet.getRange(2, 1, newRows.length, headers.length).setValues(newRows);
  }
}

/**
 * Upserts a single record into a sheet based on primary key
 */
function upsertSingleRecord(sheet, keyColName, keyValue, record) {
  var data = sheet.getDataRange().getValues();
  var headers = data[0];
  var keyColIdx = headers.indexOf(keyColName);
  
  var rowValues = [];
  for (var h = 0; h < headers.length; h++) {
    var colName = headers[h];
    var val = record[colName] !== undefined ? record[colName] : (record[colName.toLowerCase()] !== undefined ? record[colName.toLowerCase()] : "");
    if (typeof val === "object") val = JSON.stringify(val);
    rowValues.push(val);
  }
  
  if (keyColIdx !== -1) {
    for (var r = 1; r < data.length; r++) {
      if (String(data[r][keyColIdx]) === String(keyValue)) {
        sheet.getRange(r + 1, 1, 1, headers.length).setValues([rowValues]);
        return;
      }
    }
  }
  
  // Not found, append
  sheet.appendRow(rowValues);
}

/**
 * JSON Response Formatter with proper MimeType
 */
function jsonResponse(obj) {
  return ContentService.createTextOutput(JSON.stringify(obj))
    .setMimeType(ContentService.MimeType.JSON);
}
