$candidates = @("B-10001:B-10001","B-10002:B-10002","A-20001:A-20001","B-10001:1234","B-10002:1234","A-20001:1234","B-10003:B-10003","A-20002:A-20002")
foreach ($item in $candidates) {
    $parts = $item.Split(":")
    $no = $parts[0]; $pw = $parts[1]
    $body = '{"employeeNo":"' + $no + '","password":"' + $pw + '"}'
    $wc = New-Object System.Net.WebClient
    $wc.Headers.Add("Content-Type","application/json")
    try {
        $raw = [System.Text.Encoding]::UTF8.GetString($wc.UploadData("http://localhost:8080/api/auth/login","POST",[System.Text.Encoding]::UTF8.GetBytes($body)))
        $d = $raw | ConvertFrom-Json
        Write-Host "SUCCESS: $no / $pw  name=$($d.name) role=$($d.role)" -ForegroundColor Green
    } catch [System.Net.WebException] {
        Write-Host "FAIL: $no / $pw" -ForegroundColor Red
    }
}
