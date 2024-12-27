@echo off

REM This script created to easy start different programs/script right from kotlin
REM and it was not designed for manual use
REM if you want to see it usage look at src/main/kotlin/ua/pp/lumivoid/ktor/api/dev/WebCompile.kt

cd /d "%~dp0"

goto :start

:npmInstall
echo Installing all npm dependencies
npm install
exit /b

:compileTS
echo Compiling TS
npx tsc --outDir "run/config/iwtcms/dev-web"
exit /b

:compileSASS
echo Compiling SASS
npx sass src/main/web/sass:run/config/iwtcms/dev-web/sass
exit /b

:start
for %%A in (%*) do goto :%%A