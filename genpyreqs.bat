@echo off
cd /d "%~dp0"
python -m venv .gradle\python\venv
call .gradle\python\venv\Scripts\activate.bat
pip install --upgrade pipreqs
python -m pipreqs.pipreqs --force build\python
deactivate
