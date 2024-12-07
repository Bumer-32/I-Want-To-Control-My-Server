#!/bin/bash
cd "$(dirname "$0")"
python3 -m venv .gradle/python/venv
source .gradle/python/venv/bin/activate
pip install --upgrade pipreqs
python3 -m pipreqs.pipreqs --force build/python
deactivate
