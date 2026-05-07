#!/usr/bin/env bash
source "$(dirname "$0")/config.sh"

execute_command() {
  local cmd="$1"
  rm -f "$TMP_OUTPUT"

  printf '%s\r\n' "$cmd" | socat \
    -t 1 \
    STDIO \
    EXEC:"docker attach $CONTAINER_NAME",pty,raw \
    | sed -r 's/\x1B\[([0-9]{1,3}(;[0-9]{1,2};?)?[mGKHFJ]|[0-9]{1,3})//g' \
    | sed -e 's/\r//g' -e 's/\x0d//g' \
    | grep -v '^\s*$' \
    | sed -e 's/\r//g' -e 's/\x0d//g' -e 's/\^M//g' -e 's/\^J//g' \
    > "$TMP_OUTPUT" 2>/dev/null || true
}
