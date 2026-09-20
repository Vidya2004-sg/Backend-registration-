@echo off
setlocal
if exist "C:\Program Files\Java\jdk-21.0.10" set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
if exist "%~dp0.env" (
    for /f "usebackq eol=# tokens=*" %%A in ("%~dp0.env") do set "%%A"
) else (
    echo [run-user.cmd] WARNING: backend\.env not found - DATABASE_URL must already be exported.
)
cd /d "%~dp0user-service"
if not exist "C:\Users\admin\AppData\Local\Temp\auth-app" mkdir "C:\Users\admin\AppData\Local\Temp\auth-app"
mvn spring-boot:run