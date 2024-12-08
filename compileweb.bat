@echo off

REM This script added just for amenities in vscode and webstorm (live server easier to launch in extern folder because typescript and sass needs compiling)
REM Better to use it with this extention: https://marketplace.visualstudio.com/items?itemName=wk-j.save-and-run
REM Or with Prepros: https://prepros.io

cd /d "%~dp0"

REM check is webc exists (for multiple scripts launches)
if exist webc\ (
    exit /b
)

REM compile first in webc, because after compilation ts live server updates the page, and this occurs even BEFORE sass compilation, due to which the page remains without style
xcopy src\main\web webc /E /I /Q /Y
copy package-lock.json webc
copy package.json webc
cd webc
call npm install
call npx tsc --init
call npx tsc --rootDir . --module es2015
call npx sass style.scss style.css

cd ..
del /S /Q web
xcopy webc web /E /I /Q /Y

rmdir /S /Q webc
