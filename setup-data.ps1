$ErrorActionPreference = "Stop"
$BASE = "http://localhost:8080"

function Req {
    param($method, $path, $bodyJson)
    $url = $BASE + $path
    $c = New-Object System.Net.WebClient
    $c.Headers.Add("Content-Type", "application/json")
    if ($script:TOKEN) { $c.Headers.Add("Authorization", "Bearer $script:TOKEN") }
    $c.Encoding = [System.Text.Encoding]::UTF8
    try {
        if ($method -eq "GET") { return $c.DownloadString($url) }
        $d = if ($bodyJson) { [System.Text.Encoding]::UTF8.GetBytes($bodyJson) } else { @() }
        return [System.Text.Encoding]::UTF8.GetString($c.UploadData($url, $method, $d))
    } catch {
        $sr = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $err = $sr.ReadToEnd()
        throw "Request $method $path failed: $err"
    }
}

# 1) Login
Write-Host "Logging in as ADMIN001..."
$loginRes = Req "POST" "/api/auth/login" '{"employeeNo":"ADMIN001","password":"ADMIN001"}'
$script:TOKEN = ($loginRes | ConvertFrom-Json).token

# 2) Ensure user B-10002
Write-Host "Checking employee B-10002..."
$emps = Req "GET" "/api/admin/employees" | ConvertFrom-Json
if (-not ($emps | Where-Object { $_.employeeNo -eq "B-10002" })) {
    Write-Host "Creating employee B-10002..."
    $empBody = '{"employeeNo":"B-10002","name":"Test User","email":"b10002@example.com","deptId":1,"role":"EMPLOYEE","password":"password123"}'
    Req "POST" "/api/admin/employees" $empBody
}

# 3) Course 1: lecture, round, rounds up to 4
Write-Host "Setting up Course 1..."
$course = Req "GET" "/api/admin/courses/1" | ConvertFrom-Json

# Lecture
$lectures = Req "GET" "/api/admin/courses/1/lectures" | ConvertFrom-Json
if ($lectures.Count -eq 0) {
    Write-Host "Creating lecture for course 1..."
    Req "POST" "/api/admin/courses/1/lectures" '{"title":"Lecture 1","content":"Content 1"}'
}

# Rounds
$rounds = Req "GET" "/api/admin/courses/1/rounds" | ConvertFrom-Json
$maxRound = 0
if ($rounds.Count -gt 0) { $maxRound = ($rounds | Measure-Object -Property roundId -Maximum).Maximum }

while ($maxRound -lt 4) {
    $next = $maxRound + 1
    Write-Host "Creating round $next for course 1..."
    $roundBody = '{"roundId":' + $next + ',"startDate":"2024-01-01","endDate":"2024-12-31"}'
    # Note: Backend might auto-generate roundId, adjust if POST /rounds doesn't take roundId
    try { Req "POST" "/api/admin/courses/1/rounds" $roundBody } catch { Write-Host "Failed to create round $next: $_" }
    $rounds = Req "GET" "/api/admin/courses/1/rounds" | ConvertFrom-Json
    $newMax = ($rounds | Measure-Object -Property roundId -Maximum).Maximum
    if ($newMax -le $maxRound) { break } # Prevent infinite loop if creation fails to increment ID
    $maxRound = $newMax
}

# 4) Lecture 1 quiz
Write-Host "Checking Lecture 1 quiz..."
try {
    Req "GET" "/api/admin/lectures/1/quizzes"
} catch {
    Write-Host "Creating quiz for lecture 1..."
    Req "POST" "/api/admin/lectures/1/quizzes" '[{"question":"Q1","answer":"A1"}]'
}

# 5) Course 1 exam
Write-Host "Checking Course 1 exam..."
try {
    Req "GET" "/api/admin/courses/1/exams"
} catch {
    Write-Host "Creating exam for course 1..."
    Req "POST" "/api/admin/courses/1/exams" '[{"question":"EQ1","answer":"EA1"}]'
}

# 6) Notice
Write-Host "Checking notices..."
$notices = Req "GET" "/api/admin/notices" | ConvertFrom-Json
if ($notices.Count -eq 0) {
    Write-Host "Creating a notice..."
    Req "POST" "/api/admin/notices" '{"title":"Welcome","content":"Hello"}'
}

# 7) Check HTML status
Write-Host "Checking HTML pages..."
$c = New-Object System.Net.WebClient
foreach ($p in @("/admin2-test.html", "/user-test.html")) {
    try {
        $code = [int]($c.DownloadString($BASE + $p) | Out-Null; $c.ResponseHeaders["Status"] -split ' ')[0]
        Write-Host "$p : Accessible"
    } catch {
        Write-Host "$p : $($_.Exception.Message)"
    }
}
