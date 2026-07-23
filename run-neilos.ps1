# run-neilos.ps1
Write-Host "🚀 Starting NeilOS JVM Project..." -ForegroundColor Cyan

# Check Java
$javaVersion = java -version 2>&1
if (-not $?) {
    Write-Host "❌ Java is not installed! Please install Java 17 or higher." -ForegroundColor Red
    exit 1
}

# Check Maven
$mavenVersion = mvn --version 2>&1
if (-not $?) {
    Write-Host "❌ Maven is not installed! Please install Apache Maven." -ForegroundColor Red
    exit 1
}

# Clean and run
Write-Host "📦 Building and running NeilOS..." -ForegroundColor Yellow
mvn clean compile exec:java -Dexec.mainClass="com.neilos.NeilOS"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ NeilOS started successfully!" -ForegroundColor Green
} else {
    Write-Host "❌ Failed to start NeilOS!" -ForegroundColor Red
}