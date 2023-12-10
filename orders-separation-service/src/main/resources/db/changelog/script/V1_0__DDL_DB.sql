--liquibase formatted sql

--changeset poshtarenko:1
CREATE TABLE Delivery
(
    id            BIGSERIAL PRIMARY KEY,
    order_id      BIGINT       NOT NULL,
    product       VARCHAR(128) NOT NULL,
    customer_name VARCHAR(128) NOT NULL,
    destination   VARCHAR(128) NOT NULL,
    status        VARCHAR(128) NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    delivered_at  TIMESTAMP
);
