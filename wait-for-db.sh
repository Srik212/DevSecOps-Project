#!/bin/sh
set -e

host="$1"
shift
cmd="$@"

echo "Waiting for PostgreSQL at $host..."

until pg_isready -h "$host" -U "postgres"; do
  >&2 echo "Postgres is unavailable - sleeping"
  sleep 1
done

echo "Postgres is up - initializing database..."
python init_db.py

>&2 echo "Starting app..."
exec $cmd

