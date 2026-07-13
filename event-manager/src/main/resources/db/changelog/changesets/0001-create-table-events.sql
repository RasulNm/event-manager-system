-- liquibase formatted sql
-- changeset Nurmagomedov Rasul:0001-create-table-events

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    owner_id BIGINT NOT NULL,
    max_places INTEGER NOT NULL,
    occupied_places INTEGER NOT NULL,
    date TIMESTAMP NOT NULL,
    cost INTEGER NOT NULL CHECK (cost >= 1),
    duration INTEGER NOT NULL CHECK (duration > 0),
    location_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'WAIT_START'
);

--rollback DROP TABLE events;