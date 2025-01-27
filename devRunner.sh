#!/bin/sh

# This script created to easy start different programs/script right from kotlin
# and it was not designed for manual use
# if you want to see it usage look at src/main/kotlin/ua/pp/lumivoid/ktor/api/dev/WebCompile.kt

cd "$(dirname "$0")"

npmInstall() {
  echo "Installing all npm dependencies"
  npm install
}

compileTS() {
  echo "Compiling TS"
  npx tsc --outDir "run/iwtcms/dev-web"
}

compileSASS() {
  echo "Compiling SASS"
  npx sass src/main/web/sass:run/iwtcms/dev-web/sass
}

for arg in "$@"; do
  case $arg in
    npmInstall)
      npmInstall
      ;;
    compileTS)
      compileTS
      ;;
    compileSASS)
      compileSASS
      ;;
    *)
      echo "Unknown argument: $arg"
      ;;
  esac
done
