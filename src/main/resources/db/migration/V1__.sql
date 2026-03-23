CREATE TABLE user
(
    id         BINARY(16)   NOT NULL,
    login      VARCHAR(64)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

ALTER TABLE user
    ADD CONSTRAINT uc_user_login UNIQUE (login);