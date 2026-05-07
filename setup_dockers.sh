#!/bin/bash

docker compose down -v && yes | docker system prune && docker compose up --build -d
