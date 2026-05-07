#!/usr/bin/env bash

RED="\033[31m"
GREEN="\033[32m"
BLUE="\033[34m"
YELLOW="\033[33m"
RESET="\033[0m"

print_title() {
  echo -e "\n${BLUE}=== $1 ===${RESET}\n"
}

print_command() {
  echo -e "${YELLOW}> $1${RESET}"
}

print_output() {
  sed 's/\r//g; s/\x0d//g; s/\^M//g; s/\^J//g' "$TMP_OUTPUT" | sed 's/^/  /'
  echo
}

print_success() {
  echo -e "${GREEN}OK: $1${RESET}"
}

print_error() {
  echo -e "${RED}FAIL: $1${RESET}"
}
