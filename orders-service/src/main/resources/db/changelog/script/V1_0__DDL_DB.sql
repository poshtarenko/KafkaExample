--liquibase formatted sql

--changeset poshtarenko:1
CREATE TABLE Orders
(
    id                   BIGSERIAL PRIMARY KEY,
    product              VARCHAR(128)   NOT NULL,
    category             VARCHAR(128)   NOT NULL,
    price                NUMERIC(10, 2) NOT NULL,
    customer_name        VARCHAR(128)   NOT NULL,
    delivery_destination VARCHAR(128)   NOT NULL,
    status               VARCHAR(128)   NOT NULL,
    is_completed         BOOLEAN        NOT NULL,
    completed_at         TIMESTAMP
);
