-- liquibase formatted sql
-- changeset Nurmagomedov Rasul:0001-create-table-registrations

CREATE TABLE registrations
(
    id         BIGSERIAL PRIMARY KEY,
    event_id   BIGINT    NOT NULL,
    user_id    BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_registrations_to_events
        FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,

    CONSTRAINT uk_event_user_registration
        UNIQUE (event_id, user_id)
);

--rollback DROP TABLE registrations;