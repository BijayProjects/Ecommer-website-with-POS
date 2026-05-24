# ============================================================
# Curator POS - Desktop Deployment Script
# Builds the fat JAR and creates a native CuratorPOS.exe
# on your Windows Desktop.
# ============================================================

$ErrorActionPreference = "Stop"
$JAVA_HOME_JBR = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.4\jbr"
$MVN = ".\apache-maven-3.9.6\bin\mvn.cmd"
$CS_FILE = ".\pos_launcher.cs"
$DESKTOP = [Environment]::GetFolderPath("Desktop")
$OUTPUT_DIR = "$DESKTOP\CuratorPOS"
$CSC = "C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe"

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  Curator POS - Building Executable   " -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Build the fat JAR
Write-Host "[1/4] Compiling and packaging fat JAR..." -ForegroundColor Yellow
$env:JAVA_HOME = $JAVA_HOME_JBR
& $MVN clean package "-Dmaven.test.skip=true" 2>&1 | Tee-Object -Variable mvnOutput
$jarFile = ".\target\CuratorPOS.jar"
if (-not (Test-Path $jarFile)) {
    Write-Host "ERROR: JAR build failed! Check Maven output above." -ForegroundColor Red
    exit 1
}
Write-Host "  -> JAR built successfully." -ForegroundColor Green

# Step 2: Compile the native .exe wrapper
Write-Host "[2/4] Compiling native Windows launcher (.exe)..." -ForegroundColor Yellow
$exeOutput = ".\target\CuratorPOS.exe"
& $CSC "/target:winexe" "/r:System.Windows.Forms.dll" "/out:$exeOutput" $CS_FILE
if (-not (Test-Path $exeOutput)) {
    Write-Host "ERROR: EXE compilation failed!" -ForegroundColor Red
    exit 1
}
Write-Host "  -> EXE compiled successfully." -ForegroundColor Green

# Step 3: Create desktop output folder
Write-Host "[3/4] Creating deployment folder on Desktop..." -ForegroundColor Yellow
if (Test-Path $OUTPUT_DIR) { Remove-Item $OUTPUT_DIR -Recurse -Force }
New-Item -ItemType Directory $OUTPUT_DIR | Out-Null

# Copy exe and jar
Copy-Item $exeOutput $OUTPUT_DIR
Copy-Item $jarFile $OUTPUT_DIR
Write-Host "  -> Files copied to $OUTPUT_DIR" -ForegroundColor Green

# Step 4: Desktop shortcut
Write-Host "[4/4] Creating desktop shortcut..." -ForegroundColor Yellow
$WScript = New-Object -ComObject WScript.Shell
$Shortcut = $WScript.CreateShortcut("$DESKTOP\Curator POS.lnk")
$Shortcut.TargetPath = "$OUTPUT_DIR\CuratorPOS.exe"
$Shortcut.WorkingDirectory = "$OUTPUT_DIR"
$Shortcut.Description = "Curator Business Suite - POS Terminal"
$Shortcut.Save()
Write-Host "  -> Shortcut created on Desktop." -ForegroundColor Green

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  DONE! Launch 'Curator POS' from     " -ForegroundColor Cyan
Write-Host "  your Desktop shortcut.              " -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "NOTE: CuratorPOS.jar must stay in the CuratorPOS folder." -ForegroundColor DarkGray
