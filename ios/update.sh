#!/usr/bin/env bash


set -e

CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
CORE_DIR="${CURRENT_DIR}/core"


swift package --package-path "${CORE_DIR}" update
swift package --package-path "${CORE_DIR}" generate-xcodeproj
