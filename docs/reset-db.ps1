# Réinitialise le schéma et les données de covoiturage_db dans le conteneur postgres-db
$ErrorActionPreference = "Stop"

$sql = Join-Path $PSScriptRoot "schema.sql"
Get-Content $sql -Raw | docker exec -i postgres-db psql -U postgres -d covoiturage_db

Write-Host "Schema recreate. Lance maintenant: .\mvnw.cmd spring-boot:run"
