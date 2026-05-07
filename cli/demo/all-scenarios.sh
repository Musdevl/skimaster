#!/usr/bin/env bash

set -o pipefail

SCENARIO_DIR="scenarios"
RUNNER="runner/run-demo.sh"

if [ ! -d "$SCENARIO_DIR" ]; then
  echo "Unable to find folder: '$SCENARIO_DIR'"
  exit 1
fi

if [ ! -x "$RUNNER" ]; then
  echo "Cannot exec Runner '$RUNNER' script"
  exit 1
fi

echo "Scan of scenarios in '$SCENARIO_DIR'..."
echo

mapfile -t scenarios < <(find "$SCENARIO_DIR" -type f -name "*.demo" | sort)

if [ ${#scenarios[@]} -eq 0 ]; then
  echo "No .demo files found"
  exit 0
fi

success_count=0
fail_count=0

for scenario in "${scenarios[@]}"; do
  echo "========================================"
  echo "Execution: $scenario"
  echo "----------------------------------------"

  "$RUNNER" "$scenario"
  exit_code=$?

  echo "----------------------------------------"

  if [ $exit_code -eq 0 ]; then
    echo "SUCCESS: $scenario"
    ((success_count++))
  else
    echo "FAILURE: $scenario (code=$exit_code)"
    ((fail_count++))
  fi

  echo "========================================"
  echo
done

echo "Summary:"
echo "   Success : $success_count"
echo "   Failures : $fail_count"
echo "   Total  : ${#scenarios[@]}"

if [ $fail_count -ne 0 ]; then
  exit 1
else
  exit 0
fi
