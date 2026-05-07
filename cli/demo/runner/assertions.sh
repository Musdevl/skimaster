#!/usr/bin/env bash
source "$(dirname "$0")/renderer.sh"
source "$(dirname "$0")/config.sh"

assert_output() {
  local expected="$1"

  if [[ "$expected" == *\** ]]; then
    local regex
    regex=$(printf '%s' "$expected" \
      | sed -e 's/\./\\./g' \
            -e 's/\[/\\[/g' \
            -e 's/\]/\\]/g' \
            -e 's/(/\\(/g' \
            -e 's/)/\\)/g' \
            -e 's/\^/\\^/g' \
            -e 's/\$/\\$/g' \
            -e 's/+/\\+/g' \
            -e 's/?/\\?/g' \
            -e 's/|/\\|/g' \
            -e 's/{/\\{/g' \
            -e 's/}/\\}/g' \
    )
    regex="${regex//\*/.+}"

    if grep -qE "$regex" "$TMP_OUTPUT"; then
      print_success "$expected"
    else
      print_error "Expected '$expected' not found"
      echo "--- OUTPUT ---"
      cat "$TMP_OUTPUT"
      exit 1
    fi
  else
    if grep -qF "$expected" "$TMP_OUTPUT"; then
      print_success "$expected"
    else
      print_error "Expected '$expected' not found"
      echo "--- OUTPUT ---"
      cat "$TMP_OUTPUT"
      exit 1
    fi
  fi
}
