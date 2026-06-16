-- liquibase formatted sql
-- changeset Nurmagomedov Rasul:0001-create-table-users

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE ,
    password VARCHAR(60) NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 18),
    role VARCHAR(20)
);

--rollback DROP TABLE users;