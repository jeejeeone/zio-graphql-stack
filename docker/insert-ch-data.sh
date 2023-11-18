#!/bin/bash

# Name of your ClickHouse container
CONTAINER_NAME="docker-clickhouse-server-1"

# Path to your SQL file
SQL_FILE="init-ch.sql"

# Check if the SQL file exists
if [ ! -f "$SQL_FILE" ]; then
    echo "SQL file not found: $SQL_FILE"
    exit 1
fi

# Execute the SQL file in the ClickHouse container
docker exec -i "$CONTAINER_NAME" clickhouse-client --multiquery < "$SQL_FILE"
