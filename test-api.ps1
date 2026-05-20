$ErrorActionPreference = "Continue"
$BASE = "http://localhost:8080"

function Req {
    param($method, $path, $bodyJson)
    $url = $BASE + $path
    $c = New-Object System.Net.WebClient
    $c.Headers.Add("Content-Type", "application/json")
    $c.Headers.Add("Authorization", "Bearer $script:TOKEN")
    try {
        if ($method -eq "GET") {
            $raw = $c.DownloadString($url)
        } elseif ($bodyJson) {
            $d = [System.Text.Encoding]::UTF8.GetBytes($bodyJson)
            $r = $c.UploadData($url, $method, $d)
            $raw = [System.Text.Encoding]::UTF8.GetString($r)
        } else {
            $r = $c.UploadData($url, $method, @())
            $raw = [System.Text.Encoding]::UTF8.GetString($r)
        }
        try { return ($raw | ConvertFrom-Json | ConvertTo-Json -Depth 4) } catch { return $raw }
    } catch [System.Net.WebException] {
        $st = [int]$_.Exception.Response.StatusCode
        try {
            $sr = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            return "ERROR [$st]: " + $sr.ReadToEnd()
        } catch { return "ERROR [$st]" }
    }
}

# 로그인
$loginBody = '{"employeeNo":"ADMIN001","password":"ADMIN001"}'
$c0 = New-Object System.Net.WebClient
$c0.Headers.Add("Content-Type","application/json")
$rawLogin = [System.Text.Encoding]::UTF8.GetString($c0.UploadData($BASE + "/api/auth/login","POST",[System.Text.Encoding]::UTF8.GetBytes($loginBody)))
$loginData = $rawLogin | ConvertFrom-Json
$script:TOKEN = $loginData.token
Write-Host "=== 로그인 성공: name=$($loginData.name) role=$($loginData.role) ==="

Write-Host "`n===== [1] 관리자 대시보드 ====="
Req "GET" "/api/admin/dashboard"

Write-Host "`n===== [2] 부서 목록 ====="
Req "GET" "/api/admin/departments"

Write-Host "`n===== [3] 직원 목록 ====="
Req "GET" "/api/admin/employees"

Write-Host "`n===== [4] 강좌 목록 ====="
Req "GET" "/api/admin/courses"

Write-Host "`n===== [5] 수강 현황 ====="
Req "GET" "/api/admin/enrollments"

Write-Host "`n===== [6] 공지사항 목록 ====="
Req "GET" "/api/admin/notices"

Write-Host "`n===== [7] 강좌 등록 테스트 ====="
$courseBody = '{"title":"테스트 강좌","category":"직무교육","targetRole":1,"durationMin":60,"description":"테스트 강좌 설명"}'
Req "POST" "/api/admin/courses" $courseBody

Write-Host "`n===== [8] 수강 통계 ====="
Req "GET" "/api/admin/enrollments/statistics"

Write-Host "`n===== [9] 사용자 강좌 목록 (인증 없이) ====="
$c9 = New-Object System.Net.WebClient
try { $c9.DownloadString($BASE + "/api/user/courses") | ConvertFrom-Json | ConvertTo-Json -Depth 3 } catch { "ERROR: $($_.Exception.Message)" }

Write-Host "`n===== [10] 사용자 대시보드 (토큰 필요) ====="
Req "GET" "/api/user/dashboard"

Write-Host "`n===== [11] 마이페이지 ====="
Req "GET" "/api/user/mypage"

Write-Host "`n===== [12] 내 수강 목록 ====="
Req "GET" "/api/user/my-courses"

Write-Host "`n===== [13] 공지사항 (사용자) ====="
Req "GET" "/api/user/notices"

Write-Host "`n===== 테스트 완료 ====="
