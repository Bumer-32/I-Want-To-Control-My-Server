@echo off

cd /d "%~dp0"
goto :start

:genPyReqs
    python -m venv .gradle\python\venv
    call .gradle\python\venv\Scripts\activate.bat
    pip install --upgrade pipreqs
    python -m pipreqs.pipreqs --force build\python
    deactivate
    goto :eof

:runDev
    npm run dev
    goto :eof

:buildWeb
    npm run build -- --emptyOutDir
    goto :eof

:start
    goto %1
