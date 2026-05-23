#!/bin/bash
# Initialize pgvector extension
echo "CREATE EXTENSION IF NOT EXISTS vector;" | psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"
