-- liquibase formatted sql
-- changeset Nurmagomedov Rasul:0001-create-table-locations

CREATE TABLE locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    address VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity >= 5),
    description VARCHAR(200)
);

--rollback DROP TABLE locations;