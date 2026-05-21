$BASE = "http://localhost:8080"

# 관리자로 로그인
$loginBody = [System.Text.Encoding]::UTF8.GetBytes('{"employeeNo":"ADMIN001","password":"ADMIN001"}')
$wc = New-Object System.Net.WebClient
$wc.Headers.Add("Content-Type","application/json")
$raw = [System.Text.Encoding]::UTF8.GetString($wc.UploadData("$BASE/api/auth/login","POST",$loginBody))
$TOKEN = ($raw | ConvertFrom-Json).token
Write-Host "Admin token OK"

# 직원 목록 조회
$wc2 = New-Object System.Net.WebClient
$wc2.Headers.Add("Authorization","Bearer $TOKEN")
$empRaw = $wc2.DownloadString("$BASE/api/admin/employees")
$employees = $empRaw | ConvertFrom-Json
$employees | ForEach-Object { Write-Host "ID=$($_.employeeId) No=$($_.employeeNo) Name=$($_.name) Role=$($_.role)" }
