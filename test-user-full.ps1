$ErrorActionPreference = "Continue"
$BASE = "http://localhost:8080"

function Req {
    param($method, $path, $bodyJson, $label)
    $url = $BASE + $path
    $c = New-Object System.Net.WebClient
    $c.Headers.Add("Content-Type", "application/json")
    $c.Headers.Add("Authorization", "Bearer $script:TOKEN")
    $c.Encoding = [System.Text.Encoding]::UTF8
    try {
        if ($method -eq "GET") { $raw = $c.DownloadString($url) }
        elseif ($bodyJson) {
            $d = [System.Text.Encoding]::UTF8.GetBytes($bodyJson)
            $r = $c.UploadData($url, $method, $d)
            $raw = [System.Text.Encoding]::UTF8.GetString($r)
        } else {
            $r = $c.UploadData($url, $method, @())
            $raw = [System.Text.Encoding]::UTF8.GetString($r)
        }
        $parsed = $raw | ConvertFrom-Json
        Write-Host "[OK] $label" -ForegroundColor Green
        return ($parsed | ConvertTo-Json -Depth 5)
    } catch [System.Net.WebException] {
        $st = [int]$_.Exception.Response.StatusCode
        try {
            $sr = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            $errBody = $sr.ReadToEnd()
            Write-Host "[FAIL $st] $label : $errBody" -ForegroundColor Red
            return "ERROR [$st]: $errBody"
        } catch {
            Write-Host "[FAIL $st] $label" -ForegroundColor Red
            return "ERROR [$st]"
        }
    }
}

function Login {
    param($empNo, $pw)
    $loginBody = '{"employeeNo":"' + $empNo + '","password":"' + $pw + '"}'
    $c0 = New-Object System.Net.WebClient
    $c0.Headers.Add("Content-Type","application/json")
    $c0.Encoding = [System.Text.Encoding]::UTF8
    try {
        $raw = [System.Text.Encoding]::UTF8.GetString($c0.UploadData($BASE + "/api/auth/login","POST",[System.Text.Encoding]::UTF8.GetBytes($loginBody)))
        $data = $raw | ConvertFrom-Json
        $script:TOKEN = $data.token
        Write-Host "[LOGIN OK] name=$($data.name) role=$($data.role)" -ForegroundColor Cyan
        return $data
    } catch [System.Net.WebException] {
        $st = [int]$_.Exception.Response.StatusCode
        try {
            $sr2 = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            Write-Host "[LOGIN FAIL $st] $empNo : $($sr2.ReadToEnd())" -ForegroundColor Red
        } catch {
            Write-Host "[LOGIN FAIL $st] $empNo" -ForegroundColor Red
        }
        return $null
    }
}

# === PRE-CHECK: admin data ===
Write-Host "===== PRE-CHECK (ADMIN) =====" -ForegroundColor Yellow
Login "ADMIN001" "ADMIN001"
Write-Host "--- Course 1 lectures ---"; Req "GET" "/api/admin/courses/1/lectures" $null "course1-lectures"
Write-Host "--- Course 1 rounds ---"; Req "GET" "/api/admin/courses/1/rounds" $null "course1-rounds"
Write-Host "--- Lecture 1 videos ---"; Req "GET" "/api/admin/lectures/1/videos" $null "lecture1-videos"
Write-Host "--- Lecture 1 quiz ---"; Req "GET" "/api/admin/lectures/1/quiz" $null "lecture1-quiz"
Write-Host "--- Course 1 exam ---"; Req "GET" "/api/admin/courses/1/exam" $null "course1-exam"

# === USER TEST: B-10002 ===
Write-Host "`n===== USER FULL TEST: B-10002 =====" -ForegroundColor Magenta
$uLogin = Login "B-10002" "B-10002"
if (-not $uLogin) { Write-Host "Login failed. Exit."; exit 1 }

Write-Host "`n[U-1] Dashboard"
Req "GET" "/api/user/dashboard" $null "U-1-dashboard"

Write-Host "`n[U-2] Mypage"
Req "GET" "/api/user/mypage" $null "U-2-mypage"

Write-Host "`n[U-3] Course List"
Req "GET" "/api/user/courses" $null "U-3-course-list"

Write-Host "`n[U-4] Course Detail courseId=1"
Req "GET" "/api/user/courses/1" $null "U-4-course-detail"

Write-Host "`n[U-5] Lectures in course 1"
Req "GET" "/api/user/courses/1/lectures" $null "U-5-lectures"

Write-Host "`n[U-6] My Courses"
Req "GET" "/api/user/my-courses" $null "U-6-my-courses"

Write-Host "`n[U-7] Enrollments all"
Req "GET" "/api/user/enrollments" $null "U-7-enrollments"

Write-Host "`n[U-8] In-progress enrollments"
Req "GET" "/api/user/enrollments/in-progress" $null "U-8-in-progress"

Write-Host "`n[U-9] History"
Req "GET" "/api/user/enrollments/history" $null "U-9-history"

Write-Host "`n[U-10] Schedule"
Req "GET" "/api/user/enrollments/schedule" $null "U-10-schedule"

Write-Host "`n[U-11] Calendar"
Req "GET" "/api/user/calendar" $null "U-11-calendar"

Write-Host "`n[U-12] Videos in course 1"
Req "GET" "/api/user/courses/1/videos" $null "U-12-course-videos"

Write-Host "`n[U-13] Videos lecture 1"
Req "GET" "/api/user/lectures/1/videos" $null "U-13-lecture-videos"

Write-Host "`n[U-14] Notices list"
Req "GET" "/api/user/notices" $null "U-14-notices"

Write-Host "`n[U-15] Notice detail 1"
Req "GET" "/api/user/notices/1" $null "U-15-notice-detail"

Write-Host "`n[U-16] Certificates (before)"
Req "GET" "/api/user/certificates" $null "U-16-certs-before"

Write-Host "`n[U-17] Quiz lectureId=1"
Req "GET" "/api/user/quizzes/1" $null "U-17-quiz"

Write-Host "`n[U-18] Exam courseId=1"
Req "GET" "/api/user/exams/1" $null "U-18-exam"

Write-Host "`n[U-19] QnA questions"
Req "GET" "/api/user/qna/questions" $null "U-19-qna"

# === COMPLETION via admin ===
Write-Host "`n===== FORCE COMPLETE (admin) =====" -ForegroundColor Yellow
Login "ADMIN001" "ADMIN001"
Req "PATCH" "/api/admin/enrollments/1/progress?progress=100" $null "force-progress-100"
Req "PUT" "/api/admin/enrollments/1/status?status=DONE" $null "force-status-DONE"

# === POST-COMPLETION: certificates ===
Write-Host "`n===== POST-COMPLETION: B-10002 =====" -ForegroundColor Magenta
Login "B-10002" "B-10002"

Write-Host "`n[U-20] Certificates (after completion)"
$certsJson = Req "GET" "/api/user/certificates" $null "U-20-certs-after"
Write-Host $certsJson

# parse certId
$certId = 1
try {
    $certs = $certsJson | ConvertFrom-Json
    if ($certs -is [System.Array] -and $certs.Count -gt 0) {
        if ($certs[0].certificateId) { $certId = $certs[0].certificateId }
        elseif ($certs[0].id) { $certId = $certs[0].id }
    } elseif ($certs.data -and $certs.data.Count -gt 0) {
        if ($certs.data[0].certificateId) { $certId = $certs.data[0].certificateId }
    }
} catch {}

Write-Host "`n[U-21] Certificate detail certId=$certId"
Req "GET" "/api/user/certificates/$certId" $null "U-21-cert-detail"

Write-Host "`n[U-22] Certificate download certId=$certId"
$dlC = New-Object System.Net.WebClient
$dlC.Headers.Add("Authorization", "Bearer $script:TOKEN")
try {
    $dlBytes = $dlC.DownloadData("$BASE/api/certificate/download/$certId")
    Write-Host "[OK] Certificate download: $($dlBytes.Length) bytes (PDF)" -ForegroundColor Green
} catch [System.Net.WebException] {
    $st = [int]$_.Exception.Response.StatusCode
    $sr = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    Write-Host "[FAIL $st] cert-download: $($sr.ReadToEnd())" -ForegroundColor Red
}

# === ENROLL new course ===
Write-Host "`n[U-23] Enroll roundId=4 (Spring Boot)"
Req "POST" "/api/user/enrollments?roundId=4" $null "U-23-enroll"

Write-Host "`n[U-24] My-courses after enroll"
Req "GET" "/api/user/my-courses" $null "U-24-my-courses-after"

Write-Host "`n===== ALL USER TESTS DONE =====" -ForegroundColor Cyan
