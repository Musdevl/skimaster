#!/usr/bin/env bash

set -euo pipefail

BASE_DIR="$(dirname "$0")"

source "$BASE_DIR/config.sh"
source "$BASE_DIR/renderer.sh"
source "$BASE_DIR/transport.sh"
source "$BASE_DIR/assertions.sh"

run_demo() {
  local file="$1"

  print_title "Running demo: $file"

  while IFS= read -r line || [[ -n "$line" ]]; do
    # skip commentaires, lignes vides
    [[ -z "$line" || "$line" =~ ^# ]] && continue

    if [[ "$line" == \$* ]]; then
      print_title "${line#\$ }"
    fi

    if [[ "$line" == \>* ]]; then
      cmd="${line#> }"

      print_command "$cmd"
      execute_command "$cmd"
      print_output

      sleep "$DELAY_BETWEEN_CMD"

    elif [[ "$line" == EXPECT:* ]]; then
      expected="${line#EXPECT: }"
      assert_output "$expected"
    fi

  done < "$file"

  echo
  print_success "Demo completed successfully"
}

# entrypoint
if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <scenario>"
  exit 1
fi

clear
run_demo "$1"
