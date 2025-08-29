-- Flyway migration: create games table
CREATE TABLE IF NOT EXISTS games (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    gender VARCHAR(32),
    platform VARCHAR(32),
    year INT,
    status VARCHAR(32)
);
