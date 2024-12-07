REM This script added just for amenities in vscode (live server easier to launch in extern folder because typescript and sass needs compiling)
REM Better to use it with this extention: https://marketplace.visualstudio.com/items?itemName=wk-j.save-and-run

REM @echo off
cd /d "%~dp0"

REM compile first in webc, because after compilation ts live server updates the page, and this occurs even BEFORE sass compilation, due to which the page remains without style
xcopy src\main\web webc /E /I /Q /Y
copy package-lock.json webc
copy package.json webc
cd webc
call npm install
call npx tsc --init
call npx tsc --rootDir .
call npx sass style.scss style.css

cd ..
del /S /Q web
xcopy webc web /E /I /Q /Y

rmdir /S /Q webc
