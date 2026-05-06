# Setup SonarCloud Configuration for a New Repository
# Usage: .\setup-sonarcloud.ps1 -ProjectKey "project-key" -ProjectName "Project Name"

param(
    [Parameter(Mandatory=$true)]
    [string]$ProjectKey,
    
    [Parameter(Mandatory=$true)]
    [string]$ProjectName,
    
    [string]$Organization = "fabrica-escuela",
    [string]$SourceRepo = "c:\Users\Dan\Desktop\Fabrica escuela QA 2026 1\financial_managment_app"
)

function Copy-SonarCloudConfig {
    param(
        [string]$ProjectKey,
        [string]$ProjectName,
        [string]$Organization
    )
    
    # Create .github/workflows directory if it doesn't exist
    if (-not (Test-Path ".github/workflows")) {
        New-Item -ItemType Directory -Path ".github/workflows" -Force | Out-Null
        Write-Host "✓ Created .github/workflows directory"
    }
    
    # Copy and configure sonar-project.properties
    $sonarProps = @"
# SonarCloud Configuration
# Identificador único del proyecto en SonarCloud
sonar.projectKey=$ProjectKey

# Organización en SonarCloud (usa la organization key, no el nombre visible)
sonar.organization=$Organization

# URL de SonarCloud
sonar.host.url=https://sonarcloud.io

# Nombre del proyecto
sonar.projectName=$ProjectName

# Descripción del proyecto
sonar.projectDescription=$ProjectName - Análisis continuo de calidad de código

# Versión del proyecto
sonar.projectVersion=1.0.0

# Ruta base del proyecto
sonar.sources=src/main/java,src/main/resources

# Ruta de pruebas
sonar.tests=src/test/java

# Exclusiones
sonar.exclusions=**/*Test.java,**/test/**,**/tests/**,**/config/**

# Lenguaje
sonar.language=java

# Encoding
sonar.sourceEncoding=UTF-8

# Coverage (si uses JaCoCo)
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml

# Calidad de puerta (Quality Gate)
sonar.qualitygate.wait=true
"@
    
    $sonarProps | Out-File -FilePath "sonar-project.properties" -Encoding UTF8
    Write-Host "✓ Created sonar-project.properties"
    
    # Copy and configure GitHub Actions workflow
    $workflow = @"
name: SonarCloud Analysis

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    name: Build and Analyze
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Cache SonarCloud packages
        uses: actions/cache@v3
        with:
          path: ~/.sonar/cache
          key: `${{ runner.os }}-sonar
          restore-keys: `${{ runner.os }}-sonar

      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: `${{ runner.os }}-m2-`${{ hashFiles('**/pom.xml') }}
          restore-keys: `${{ runner.os }}-m2

      - name: Build and analyze
        env:
          GITHUB_TOKEN: `${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: `${{ secrets.SONAR_TOKEN }}
        run: mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=$ProjectKey -Dsonar.organization=$Organization

      - name: Upload test results
        if: failure()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: target/surefire-reports/
"@
    
    $workflow | Out-File -FilePath ".github/workflows/sonarcloud.yml" -Encoding UTF8
    Write-Host "✓ Created .github/workflows/sonarcloud.yml"
}

function Update-PomXml {
    param(
        [string]$ProjectKey,
        [string]$Organization
    )
    
    if (-not (Test-Path "pom.xml")) {
        Write-Host "⚠ pom.xml not found in current directory"
        return
    }
    
    [xml]$pom = Get-Content "pom.xml"
    $ns = @{pom = "http://maven.apache.org/POM/4.0.0"}
    
    # Find or create properties element
    $properties = $pom.SelectSingleNode("//pom:properties", $ns)
    if ($null -eq $properties) {
        $properties = $pom.CreateElement("properties", $ns.pom)
        $pom.project.InsertAfter($properties, $pom.project.SelectSingleNode("//pom:scm", $ns))
    }
    
    # Update or create sonar properties
    $projectKeyNode = $properties.SelectSingleNode("pom:sonar.projectKey", $ns)
    if ($null -eq $projectKeyNode) {
        $projectKeyNode = $pom.CreateElement("sonar.projectKey", $ns.pom)
        $properties.AppendChild($projectKeyNode) | Out-Null
    }
    $projectKeyNode.InnerText = $ProjectKey
    
    $orgNode = $properties.SelectSingleNode("pom:sonar.organization", $ns)
    if ($null -eq $orgNode) {
        $orgNode = $pom.CreateElement("sonar.organization", $ns.pom)
        $properties.AppendChild($orgNode) | Out-Null
    }
    $orgNode.InnerText = $Organization
    
    $pom.Save("pom.xml")
    Write-Host "✓ Updated pom.xml with SonarCloud properties"
}

# Main execution
Write-Host "🔧 Setting up SonarCloud for: $ProjectName (Key: $ProjectKey)`n"

Copy-SonarCloudConfig -ProjectKey $ProjectKey -ProjectName $ProjectName -Organization $Organization

# Only try to update pom.xml if it exists
if (Test-Path "pom.xml") {
    Update-PomXml -ProjectKey $ProjectKey -Organization $Organization
}

Write-Host "`n✅ SonarCloud configuration completed!`n"
Write-Host "Next steps:"
Write-Host "1. Verify pom.xml, sonar-project.properties, and .github/workflows/sonarcloud.yml"
Write-Host "2. Create a new branch: git checkout -b feature/sonarcloud-config"
Write-Host "3. Stage changes: git add ."
Write-Host "4. Commit: git commit -m 'feat: Configure SonarCloud integration'"
Write-Host "5. Push: git push origin feature/sonarcloud-config"
Write-Host "6. Create PR on GitHub"
Write-Host "`nDon't forget to add GitHub secrets if not already set:"
Write-Host "  - SONAR_TOKEN: Your SonarCloud token"
Write-Host "  - SONAR_ORG: $Organization"
