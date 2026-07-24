CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users
(
    id            UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    email         VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE daily_readiness
(
    id                   UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    user_id              UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    record_date          DATE NOT NULL,
    sleep_score          INT  NOT NULL CHECK (sleep_score BETWEEN 1 AND 5),
    soreness_score       INT  NOT NULL CHECK (soreness_score BETWEEN 1 AND 5),
    stress_score         INT  NOT NULL CHECK (stress_score BETWEEN 1 AND 5),
    readiness_percentage INT  NOT NULL CHECK (readiness_percentage BETWEEN 0 AND 100),
    created_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_daily_readiness UNIQUE (user_id, record_date)
);

CREATE TABLE workout_sessions
(
    id               UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    user_id          UUID                     NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    session_date     TIMESTAMP WITH TIME ZONE NOT NULL,
    category         VARCHAR(50)              NOT NULL CHECK (category IN ('STRENGTH', 'CARDIO', 'CONTACT_SPORT')),
    activity_name    VARCHAR(100)             NOT NULL,
    duration_minutes INT                      NOT NULL CHECK (duration_minutes > 0),
    rpe              INT                      NOT NULL CHECK (rpe BETWEEN 1 AND 10),
    internal_load    INT                      NOT NULL, -- Calculado (duration * rpe)
    notes            TEXT,

    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE strength_details
(
    id            UUID PRIMARY KEY         DEFAULT gen_random_uuid(),
    session_id    UUID          NOT NULL REFERENCES workout_sessions (id) ON DELETE CASCADE,
    exercise_name VARCHAR(255)  NOT NULL,
    sets          INT           NOT NULL CHECK (sets > 0),
    reps          INT           NOT NULL CHECK (reps > 0),
    weight_kg     NUMERIC(5, 2) NOT NULL CHECK (weight_kg >= 0),
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_workout_sessions_user_date ON workout_sessions (user_id, session_date);
CREATE INDEX idx_daily_readiness_user_date ON daily_readiness (user_id, record_date);