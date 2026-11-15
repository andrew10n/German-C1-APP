$ErrorActionPreference = 'Continue'
$root = Resolve-Path (Join-Path $PSScriptRoot '..\..')
$apk = Join-Path $root 'app\build\outputs\apk\debug\app-debug.apk'
$adbCandidates = @(
    (Get-Command adb -ErrorAction SilentlyContinue).Source,
    'C:\Android\Sdk\platform-tools\adb.exe',
    (Join-Path $root '.work\android-sdk\platform-tools\adb.exe')
) | Where-Object { $_ -and (Test-Path $_) } | Select-Object -Unique
Write-Output "Workspace: $root"
Write-Output "APK present: $(Test-Path $apk)"
if ($adbCandidates.Count -eq 0) {
    Write-Output 'ADB: not found'
    exit 0
}
$adb = $adbCandidates[0]
Write-Output "ADB: $adb"
& $adb version
Write-Output 'Devices:'
& $adb devices -l
Write-Output 'No device result is a validation blocker, not a passing device test.'
