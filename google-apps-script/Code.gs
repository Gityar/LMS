/**
 * ============================================================================
 * NEONATAL NURSING LMS - GOOGLE APPS SCRIPT BACKEND (Code.gs)
 * Spreadsheet ID: 1TQcak2SNQ3oT9DBz_nyrOzVh8fWfNRVsWqt7Rk_1uTA
 * ============================================================================
 */

var SPREADSHEET_ID = "1TQcak2SNQ3oT9DBz_nyrOzVh8fWfNRVsWqt7Rk_1uTA";

/**
 * 13 REQUIRED SCHEMAS & COLUMN HEADERS
 */
var SCHEMAS = {
  "Users": ["User_ID", "Full_Name", "Email", "Password_Hash", "Role", "Registration_Date", "Account_Status", "Last_Login"],
  "Courses": ["Course_ID", "Course_Title", "Category", "Instructor_ID", "Instructor_Name", "Description", "Status", "Created_Date", "Total_Modules"],
  "Lessons": ["Lesson_ID", "Course_ID", "Module_Number", "Lesson_Title", "Duration_Minutes", "Lesson_Type", "Body_Content"],
  "Enrollments": ["Enrollment_ID", "Learner_ID", "Learner_Name", "Course_ID", "Enrollment_Date", "Completion_Status", "Progress_Percentage", "Last_Accessed"],
  "Activity_Logs": ["Log_ID", "User_ID", "Action", "Timestamp", "IP_Address", "Device_Info"],
  "Password_Resets": ["Reset_ID", "User_ID", "Email", "Reset_Token", "Request_Date", "Expiry_Date", "Status"],
  "Lesson_Content": ["Content_ID", "Lesson_ID", "Content_Type", "Content_Data", "Order_Index"],
  "Lesson_Resources": ["Resource_ID", "Lesson_ID", "Resource_Name", "Resource_URL", "Resource_Type"],
  "Assignments": ["Assignment_ID", "Course_ID", "Title", "Instructions", "Due_Date", "Max_Score"],
  "Assignment_Submissions": ["Submission_ID", "Assignment_ID", "Learner_ID", "Submission_Date", "Submission_Text", "Score", "Feedback"],
  "Assessments": ["Assessment_ID", "Course_ID", "Title", "Passing_Score", "Total_Questions"],
  "Questions": ["Question_ID", "Assessment_ID", "Question_Text", "Option_A", "Option_B", "Option_C", "Option_D", "Correct_Option_Index", "Explanation"],
  "Assessment_Attempts": ["Attempt_ID", "Assessment_ID", "Learner_ID", "Learner_Name", "Course_ID", "Attempt_Number", "Score", "Percentage", "Passed", "Submitted_Date", "Answers_JSON"]
};

/**
 * One-click Setup: Creates all 13 sheet tabs with bold headers if missing
 */
function setupAllSheets() {
  var ss = SpreadsheetApp.openById(SPREADSHEET_ID);
  
  for (var sheetName in SCHEMAS) {
    var sheet = ss.getSheetByName(sheetName);
    var headers = SCHEMAS[sheetName];
    
    if (!sheet) {
      sheet = ss.insertSheet(sheetName);
    }
    
    if (sheet.getLastRow() === 0) {
      sheet.appendRow(headers);
      var headerRange = sheet.getRange(1, 1, 1, headers.length);
      headerRange.setFontWeight("bold");
      headerRange.setBackground("#F1F5F9");
      headerRange.setFontColor("#1E293B");
    }
  }
}

/**
 * Helper: Find or create sheet
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
        message: "Neonatal Nursing LMS API is running",
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
 * HTTP POST - Handles all writing, syncing, and sending password reset emails
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
    // 1. SEND PASSWORD RESET EMAIL (Protected 6-digit OTP & Direct Link)
    // ------------------------------------------------------------------------
    if (action === "sendPasswordResetEmail") {
      var email = req.email || "";
      var userName = req.userName || "Clinician";
      var code = req.code || "";
      var resetLink = req.resetLink || "";
      
      if (!email || !code) {
        return jsonResponse({ status: "error", message: "Email and code are required." });
      }
      
      // Dispatch email via native Google Apps Script MailApp
      MailApp.sendEmail({
        to: email,
        subject: "🔐 Neonatal Nursing LMS - Verification Code: " + code,
        htmlBody: 
          "<div style='font-family: Arial, sans-serif; padding: 24px; color: #1e293b; max-width: 520px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 16px;'>" +
            "<div style='display: flex; align-items: center; gap: 8px; margin-bottom: 12px;'>" +
              "<h2 style='color: #4338ca; margin: 0;'>👶 Neonatal Nursing Clinical LMS</h2>" +
            "</div>" +
            "<p style='font-size: 14px;'>Hello <strong>" + userName + "</strong>,</p>" +
            "<p style='font-size: 13px; line-height: 1.5;'>We received a request to reset your password. Use the secure 6-digit verification code below to authorize the change:</p>" +
            "<div style='background-color: #f1f5f9; padding: 16px; text-align: center; border-radius: 12px; margin: 24px 0; border: 1px solid #cbd5e1;'>" +
              "<span style='font-family: monospace; font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #312e81;'>" + code + "</span>" +
            "</div>" +
            "<p style='font-size: 13px; text-align: center; margin: 16px 0;'>" +
              "<a href='" + resetLink + "' style='background-color: #4f46e5; color: white; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: bold; font-size: 13px; display: inline-block;'>One-Click Password Reset</a>" +
            "</p>" +
            "<p style='font-size: 11px; color: #64748b; line-height: 1.4; border-top: 1px solid #f1f5f9; padding-top: 12px; margin-top: 24px;'>" +
              "This verification code expires in 15 minutes. If you did not request a password reset, your account remains secure and no action is required." +
            "</p>" +
          "</div>"
      });
      
      // Log to Password_Resets sheet
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
      
      var emailCol = 2; // Email column (0-indexed)
      var passCol = 3;  // Password_Hash column
      
      for (var row = 1; row < userData.length; row++) {
        if (String(userData[row][emailCol]).toLowerCase() === targetEmail) {
          userSheet.getRange(row + 1, passCol + 1).setValue(newPassword);
          break;
        }
      }
      
      return jsonResponse({ status: "success", message: "Password updated in Users sheet." });
    }
    
    // ------------------------------------------------------------------------
    // 3. APPEND RECORD (Users, Enrollments, Attempts, etc.)
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
    // 4. SYNC COURSES (Replace or update courses)
    // ------------------------------------------------------------------------
    if (action === "syncCourses") {
      var courseSheet = getTargetSheet(ss, "Courses");
      var courseHeaders = SCHEMAS["Courses"];
      
      // Clear data but keep headers
      var lastRow = courseSheet.getLastRow();
      if (lastRow > 1) {
        courseSheet.deleteRows(2, lastRow - 1);
      }
      
      var courseList = req.records || [];
      for (var c = 0; c < courseList.length; c++) {
        var cr = courseList[c];
        courseSheet.appendRow([
          cr.Course_ID || "",
          cr.Course_Title || "",
          cr.Category || "",
          cr.Instructor_ID || "INST-01",
          cr.Instructor_Name || "",
          cr.Description || "",
          cr.Status || "published",
          cr.Created_Date || new Date().toISOString(),
          cr.Total_Modules || 3
        ]);
      }
      
      return jsonResponse({ status: "success", synced: courseList.length });
    }
    
    return jsonResponse({ status: "success", message: "Action processed: " + action });
  } catch (postErr) {
    return jsonResponse({ status: "error", message: postErr.toString() });
  }
}

/**
 * JSON Response Formatter with proper MimeType
 */
function jsonResponse(obj) {
  return ContentService.createTextOutput(JSON.stringify(obj))
    .setMimeType(ContentService.MimeType.JSON);
}
